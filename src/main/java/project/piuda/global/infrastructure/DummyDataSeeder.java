package project.piuda.global.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.calendar.domain.CalendarCategory;
import project.piuda.domain.calendar.domain.CalendarType;
import project.piuda.domain.calendar.domain.CareCalendar;
import project.piuda.domain.calendar.domain.CareCalendarRepository;
import project.piuda.domain.carejudgment.domain.CareJudgmentLog;
import project.piuda.domain.carejudgment.domain.CareJudgmentLogRepository;
import project.piuda.domain.carejudgment.domain.JudgmentCategory;
import project.piuda.domain.carejudgment.domain.UrgencyLevel;
import project.piuda.domain.community.domain.Comment;
import project.piuda.domain.community.domain.CommentRepository;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostCategory;
import project.piuda.domain.community.domain.PostImage;
import project.piuda.domain.community.domain.PostImageRepository;
import project.piuda.domain.community.domain.PostRepository;
import project.piuda.domain.patient.domain.*;
import project.piuda.domain.patientmemory.domain.PatientMemory;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.user.domain.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 중간 평가/데모용 더미 데이터 시더.
 * app.seed-dummy=true 일 때만 실행되며, 이미 시드된 경우(protector01 존재) 건너뛴다.
 * 평가 후에는 플래그를 끄면 된다. (운영 배포에는 절대 true로 두지 말 것)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DummyDataSeeder implements ApplicationRunner {

    private static final String COMMON_PASSWORD = "Test1234!";
    private static final String SEED_MARKER_EMAIL = "protector01@piuda.com";

    private final UserRepository userRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final PatientRepository patientRepository;
    private final PatientMemoryRepository patientMemoryRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final CareJudgmentLogRepository careJudgmentLogRepository;
    private final CareCalendarRepository careCalendarRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.seed-dummy:false}")
    private boolean seedEnabled;

    private final Random random = new Random(42);

    private static final String[] PATIENT_NAMES = {
            "김영자", "이순덕", "박말순", "최정례", "정복순", "한금례", "오분남", "윤옥자", "장귀례", "임순이"
    };
    private static final String[] SITUATIONS = {
            "식사를 거부하셨어요", "갑자기 밖에 나가려 하셨어요", "약을 안 드시려 하셨어요",
            "화장실을 못 찾으셨어요", "밤에 잠들지 못하고 배회하셨어요", "목욕을 강하게 거부하셨어요",
            "예정에 없던 손님이 오셨어요", "낙상 위험이 있어 보였어요", "감정이 격해지셨어요"
    };
    private static final String[] ACTIONS = {
            "죽으로 바꿔서 드렸습니다", "동행해서 짧게 산책하고 돌아왔습니다", "복용 시간을 늦춰 다시 시도했습니다",
            "화장실까지 손잡고 안내했습니다", "따뜻한 우유를 드리고 곁에 있었습니다", "부분 세정으로 대체했습니다",
            "보호자께 먼저 연락드리고 대응했습니다", "미끄럼 방지 매트를 깔고 이동을 도왔습니다", "좋아하시는 음악을 틀어드렸습니다"
    };
    private static final String[] RATIONALES = {
            "치아 상태가 안 좋아 보여서요", "무리하면 위험할 것 같아서요", "공복에 드시면 속이 불편해 보이셔서요",
            "실수하시면 위축되실 것 같아서요", "억지로 재우면 더 불안해하셔서요", "컨디션이 안 좋아 보이셔서요",
            "임의로 판단하기 어려운 상황이라서요", "지난번에 넘어질 뻔하셨어서요", "평소 진정에 도움이 되던 방법이라서요"
    };
    private static final String[] POST_TITLES = {
            "치매 초기 어머니 돌봄 팁 공유합니다", "야간 배회 어떻게 대처하세요?", "요양등급 신청 후기",
            "식사 거부하실 때 좋은 방법", "간병하면서 힘들 때 마음 다잡는 법", "좋은 요양보호사 구하는 법",
            "기저귀 추천 부탁드려요", "주간보호센터 다녀온 후기", "약 복용 거부 해결하신 분?", "간병 공동구매 하실 분"
    };

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedEnabled) return;
        if (userRepository.findByEmail(SEED_MARKER_EMAIL).isPresent()) {
            log.info("[SEED] 더미 데이터가 이미 존재합니다. 건너뜁니다.");
            return;
        }
        log.info("[SEED] 더미 데이터 생성 시작...");

        List<User> protectors = createUsers("protector", "보호자", Role.PROTECTOR, 12);
        List<User> caregivers = createUsers("caregiver", "간병인", Role.CAREGIVER, 15);
        List<User> medicals   = createUsers("medical", "의료진", Role.MEDICAL_STAFF, 3);
        createCaregiverProfiles(caregivers);

        List<PatientBundle> patients = createPatients(8, protectors, caregivers);
        seedJudgmentLogs(patients);
        seedCalendars(patients);
        seedCommunity(protectors, caregivers, medicals);

        log.info("[SEED] 완료 — 회원 {}명(보호자 {}, 간병인 {}, 의료진 {}), 환자 {}명. 공통 비밀번호: {}",
                protectors.size() + caregivers.size() + medicals.size(),
                protectors.size(), caregivers.size(), medicals.size(), patients.size(), COMMON_PASSWORD);
    }

    private List<User> createUsers(String emailPrefix, String nickPrefix, Role role, int count) {
        List<User> result = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String idx = String.format("%02d", i);
            User user = User.builder()
                    .email(emailPrefix + idx + "@piuda.com")
                    .password(passwordEncoder.encode(COMMON_PASSWORD))
                    .name(nickPrefix + idx)
                    .nickname(nickPrefix + idx)
                    .phone("010-" + String.format("%04d", random.nextInt(10000)) + "-" + String.format("%04d", random.nextInt(10000)))
                    .introduction(nickPrefix + " 데모 계정입니다.")
                    .role(role)
                    .build();
            user.addScore(random.nextInt(500)); // 랭킹 다양화 (기본 100 + 랜덤)
            result.add(userRepository.save(user));
        }
        return result;
    }

    private void createCaregiverProfiles(List<User> caregivers) {
        CaregiverType[] types = CaregiverType.values();
        for (User cg : caregivers) {
            caregiverProfileRepository.save(CaregiverProfile.builder()
                    .user(cg)
                    .experienceYears(random.nextInt(20))
                    .gender(random.nextBoolean()
                            ? project.piuda.domain.user.domain.Gender.MALE
                            : project.piuda.domain.user.domain.Gender.FEMALE)
                    .birthDate(LocalDate.of(1970 + random.nextInt(30), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                    .caregiverType(types[random.nextInt(types.length)])
                    .build());
        }
    }

    private List<PatientBundle> createPatients(int count, List<User> protectors, List<User> caregivers) {
        DementiaStage[] stages = DementiaStage.values();
        List<PatientBundle> bundles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Patient patient = patientRepository.save(Patient.builder()
                    .name(PATIENT_NAMES[i % PATIENT_NAMES.length])
                    .birthDate(LocalDate.of(1935 + random.nextInt(15), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                    .gender(random.nextBoolean() ? project.piuda.domain.patient.domain.Gender.MALE
                                                 : project.piuda.domain.patient.domain.Gender.FEMALE)
                    .dementiaStage(stages[random.nextInt(stages.length)])
                    .build());

            patientMemoryRepository.save(PatientMemory.builder()
                    .patient(patient)
                    .bloodType(new String[]{"A", "B", "O", "AB"}[random.nextInt(4)])
                    .longTermCareGrade(1 + random.nextInt(5))
                    .dementiaType(new String[]{"알츠하이머형", "혈관성", "루이소체", "전두측두엽"}[random.nextInt(4)])
                    .medicationInfo("아리셉트 5mg, 혈압약")
                    .comorbidities("고혈압, 당뇨")
                    .likes("트로트, 단 음식")
                    .dislikes("시끄러운 소리")
                    .build());

            // 보호자 1~2명 + 간병인 1~2명 매핑
            List<User> members = new ArrayList<>();
            User protector = protectors.get(i % protectors.size());
            patientMemberRepository.save(PatientMember.builder().patient(patient).user(protector).relationship("자녀").build());
            members.add(protector);

            List<User> assignedCaregivers = new ArrayList<>();
            int cgCount = 1 + random.nextInt(2);
            for (int c = 0; c < cgCount; c++) {
                User cg = caregivers.get((i * 2 + c) % caregivers.size());
                if (assignedCaregivers.contains(cg)) continue;
                patientMemberRepository.save(PatientMember.builder().patient(patient).user(cg).relationship("담당 간병인").build());
                assignedCaregivers.add(cg);
                members.add(cg);
            }
            bundles.add(new PatientBundle(patient, assignedCaregivers, members));
        }
        return bundles;
    }

    private void seedJudgmentLogs(List<PatientBundle> patients) {
        JudgmentCategory[] categories = JudgmentCategory.values();
        UrgencyLevel[] urgencies = UrgencyLevel.values();
        for (PatientBundle bundle : patients) {
            int logCount = 15 + random.nextInt(11); // 15~25건
            for (int i = 0; i < logCount; i++) {
                int pick = random.nextInt(SITUATIONS.length);
                CareJudgmentLog log = CareJudgmentLog.builder()
                        .patient(bundle.patient)
                        .writer(bundle.caregivers.get(random.nextInt(bundle.caregivers.size())))
                        .category(categories[random.nextInt(categories.length)])
                        .situation(SITUATIONS[pick])
                        .action(ACTIONS[pick])
                        .rationale(RATIONALES[pick])
                        .urgency(urgencies[random.nextInt(urgencies.length)])
                        .build();
                backdate(log, "createdAt", LocalDateTime.now().minusDays(random.nextInt(45)).minusHours(random.nextInt(24)));
                careJudgmentLogRepository.save(log);
            }
        }
    }

    private void seedCalendars(List<PatientBundle> patients) {
        CalendarCategory[] categories = CalendarCategory.values();
        String[] titles = {"병원 방문", "방문요양 오는 날", "치매약 처방", "미용실 동행", "가족 모임", "주간보호센터"};
        for (PatientBundle bundle : patients) {
            for (int i = 0; i < 8; i++) {
                LocalDateTime start = LocalDateTime.now().plusDays(random.nextInt(40) - 20)
                        .withHour(9 + random.nextInt(8)).withMinute(0);
                careCalendarRepository.save(CareCalendar.builder()
                        .patient(bundle.patient)
                        .writer(bundle.members.get(random.nextInt(bundle.members.size())))
                        .title(titles[random.nextInt(titles.length)])
                        .content("데모 일정")
                        .calendarType(CalendarType.SCHEDULE)
                        .category(categories[random.nextInt(categories.length)])
                        .startTime(start)
                        .endTime(start.plusHours(1 + random.nextInt(3)))
                        .build());
            }
        }
    }

    private void seedCommunity(List<User> protectors, List<User> caregivers, List<User> medicals) {
        List<User> all = new ArrayList<>();
        all.addAll(protectors);
        all.addAll(caregivers);
        all.addAll(medicals);
        PostCategory[] categories = PostCategory.values();

        for (int i = 0; i < 30; i++) {
            User writer = all.get(random.nextInt(all.size()));
            Post post = postRepository.save(Post.builder()
                    .writer(writer)
                    .title(POST_TITLES[i % POST_TITLES.length] + " (" + (i + 1) + ")")
                    .content("데모용 게시글 본문입니다. 치매 돌봄 관련 경험을 공유합니다.")
                    .category(categories[random.nextInt(categories.length)])
                    .build());

            // 약 40% 게시글에 이미지 1~3장 첨부 (picsum 랜덤 이미지)
            if (random.nextInt(10) < 4) {
                int imageCount = 1 + random.nextInt(3);
                for (int img = 0; img < imageCount; img++) {
                    String url = "https://picsum.photos/seed/piuda" + i + "_" + img + "/600/400";
                    postImageRepository.save(PostImage.builder().post(post).imageUrl(url).build());
                }
            }

            int commentCount = random.nextInt(5);
            for (int c = 0; c < commentCount; c++) {
                commentRepository.save(Comment.builder()
                        .post(post)
                        .writer(all.get(random.nextInt(all.size())))
                        .parentComment(null)
                        .content("도움이 되는 글이네요. 감사합니다!")
                        .build());
            }
        }
    }

    private void backdate(Object entity, String field, LocalDateTime value) {
        try {
            Field f = entity.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(entity, value);
        } catch (Exception e) {
            log.warn("[SEED] {} 백데이트 실패: {}", field, e.getMessage());
        }
    }

    private record PatientBundle(Patient patient, List<User> caregivers, List<User> members) {}
}
