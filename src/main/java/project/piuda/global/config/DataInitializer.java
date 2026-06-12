package project.piuda.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.calendar.domain.CareCalendar;
import project.piuda.domain.calendar.domain.CareCalendarRepository;
import project.piuda.domain.calendar.domain.CalendarCategory;
import project.piuda.domain.calendar.domain.CalendarType;
import project.piuda.domain.careadvice.domain.*;
import project.piuda.domain.caregiverdiary.domain.CaregiverDiary;
import project.piuda.domain.caregiverdiary.domain.CaregiverDiaryRepository;
import project.piuda.domain.caregiverdiary.domain.MoodType;
import project.piuda.domain.community.domain.*;
import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.DailyLogRepository;
import project.piuda.domain.dailylog.domain.HealthTrend;
import project.piuda.domain.memorygallery.domain.MemoryGallery;
import project.piuda.domain.memorygallery.domain.MemoryGalleryRepository;
import project.piuda.domain.patient.domain.*;
import project.piuda.domain.patientmemory.domain.PatientMemory;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.user.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final PatientMemoryRepository patientMemoryRepository;
    private final DailyLogRepository dailyLogRepository;
    private final CareCalendarRepository careCalendarRepository;
    private final MemoryGalleryRepository memoryGalleryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CareAdviceSessionRepository careAdviceSessionRepository;
    private final CareAdviceMessageRepository careAdviceMessageRepository;
    private final CaregiverDiaryRepository caregiverDiaryRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("[DataInitializer] 이미 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("[DataInitializer] 더미 데이터 삽입 시작...");

        // ── 1. 사용자 ────────────────────────────────────────────
        User protector = userRepository.save(User.builder()
                .email("protector@test.com")
                .password(passwordEncoder.encode("test1234"))
                .name("김보호")
                .nickname("보호자김씨")
                .phone("010-1111-2222")
                .introduction("어머니를 모시고 있는 보호자입니다.")
                .role(Role.PROTECTOR)
                .build());

        User caregiver = userRepository.save(User.builder()
                .email("caregiver@test.com")
                .password(passwordEncoder.encode("test1234"))
                .name("이간병")
                .nickname("간병인이씨")
                .phone("010-3333-4444")
                .introduction("10년 경력 전문 간병인입니다.")
                .role(Role.CAREGIVER)
                .build());

        User medical = userRepository.save(User.builder()
                .email("medical@test.com")
                .password(passwordEncoder.encode("test1234"))
                .name("박의사")
                .nickname("의료진박씨")
                .phone("010-5555-6666")
                .introduction("신경과 전문의입니다.")
                .role(Role.MEDICAL_STAFF)
                .build());

        caregiverProfileRepository.save(CaregiverProfile.builder()
                .user(caregiver)
                .experienceYears(10)
                .gender(project.piuda.domain.user.domain.Gender.FEMALE)
                .birthDate(LocalDate.of(1985, 4, 20))
                .build());

        // ── 2. 환자 ────────────────────────────────────────────
        Patient patient1 = patientRepository.save(Patient.builder()
                .name("김할머니")
                .birthDate(LocalDate.of(1942, 3, 15))
                .gender(project.piuda.domain.patient.domain.Gender.FEMALE)
                .dementiaStage(DementiaStage.CDR_2)
                .build());

        Patient patient2 = patientRepository.save(Patient.builder()
                .name("이할아버지")
                .birthDate(LocalDate.of(1938, 7, 22))
                .gender(project.piuda.domain.patient.domain.Gender.MALE)
                .dementiaStage(DementiaStage.CDR_1)
                .build());

        // 환자-사용자 연결
        patientMemberRepository.save(PatientMember.builder()
                .patient(patient1).user(protector).relationship("자녀").build());
        patientMemberRepository.save(PatientMember.builder()
                .patient(patient1).user(caregiver).relationship("간병인").build());
        patientMemberRepository.save(PatientMember.builder()
                .patient(patient1).user(medical).relationship("담당의").build());
        patientMemberRepository.save(PatientMember.builder()
                .patient(patient2).user(protector).relationship("배우자").build());

        // ── 3. 환자 메모리 ────────────────────────────────────────
        patientMemoryRepository.save(PatientMemory.builder()
                .patient(patient1)
                .bloodType("A")
                .longTermCareGrade(2)
                .dementiaType("알츠하이머형 치매")
                .comorbidities("고혈압, 당뇨")
                .contraindications("항응고제 주의")
                .medicationInfo("아리셉트 5mg 1일 1회 취침 전, 혈압약 1일 1회")
                .primaryDoctorInfo("서울대병원 신경과 최민준 교수")
                .likes("트로트 음악, 화투, 손주들과 대화")
                .dislikes("큰 소리, 낯선 사람")
                .soothingWords("괜찮아요, 여기 있을게요, 같이 있어요")
                .ineffectiveWords("왜 또 그러세요, 아까 말씀드렸잖아요")
                .sundowningInfo("오후 4시 이후 불안감 증가, 집에 가겠다고 하심")
                .repetitiveBehaviors("같은 질문 반복(밥 먹었냐), 물건 감추기")
                .wanderingRoute("현관 → 1층 엘리베이터 → 건물 앞 벤치")
                .emergencyContacts("딸 김보호 010-1111-2222")
                .preferredHospital("서울대학교병원")
                .specialNotes("좋아하는 색상은 분홍색, 화투 치면 기분이 좋아지심")
                .build());

        patientMemoryRepository.save(PatientMemory.builder()
                .patient(patient2)
                .bloodType("O")
                .longTermCareGrade(3)
                .dementiaType("혈관성 치매")
                .comorbidities("뇌졸중 후유증, 고지혈증")
                .medicationInfo("혈전용해제 1일 1회, 고지혈증약 1일 1회 취침 전")
                .likes("바둑, 뉴스 시청, 산책")
                .dislikes("병원 가는 것")
                .soothingWords("천천히 하세요, 잘 하고 계세요")
                .emergencyContacts("배우자 010-1111-2222")
                .build());

        // ── 4. 간병 일지 + 케어 캘린더 ──────────────────────────
        DailyLog log1 = dailyLogRepository.save(DailyLog.builder()
                .patient(patient1)
                .writer(caregiver)
                .logDate(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .physicalHygiene(true)
                .physicalBath(true)
                .physicalMealHelp(true)
                .physicalToiletHelp(true)
                .physicalTotalMinutes(120)
                .cognitiveStimulationMinutes(30)
                .cognitiveLifeTogetherMinutes(60)
                .emotionalCommunicationMinutes(30)
                .householdMealClean(true)
                .householdTotalMinutes(60)
                .physicalFunctionTrend(HealthTrend.MAINTAINED)
                .mealFunctionTrend(HealthTrend.MAINTAINED)
                .specialNotes("오늘 트로트 틀어드리니 기분이 좋아지심. 점심 식사 잘 하심.")
                .build());

        careCalendarRepository.save(CareCalendar.builder()
                .patient(patient1)
                .writer(caregiver)
                .dailyLog(log1)
                .title("이간병님의 하루 일지 작성 완료")
                .calendarType(CalendarType.DAILY_LOG)
                .startTime(LocalDate.now().minusDays(1).atTime(9, 0))
                .endTime(LocalDate.now().minusDays(1).atTime(17, 0))
                .build());

        careCalendarRepository.save(CareCalendar.builder()
                .patient(patient1)
                .writer(protector)
                .assignee(caregiver)
                .title("정기 병원 방문")
                .content("서울대병원 신경과 정기 진료")
                .calendarType(CalendarType.SCHEDULE)
                .category(CalendarCategory.OUTING)
                .startTime(LocalDateTime.now().plusDays(3).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(3).withHour(13).withMinute(0))
                .build());

        // ── 5. 메모리 갤러리 ────────────────────────────────────
        memoryGalleryRepository.save(MemoryGallery.builder()
                .patient(patient1)
                .writer(protector)
                .imageUrl("https://piuda-dummy.s3.amazonaws.com/gallery/family_photo_1.jpg")
                .memo("어머니 생신 때 찍은 가족사진 (2024년 3월)")
                .build());

        memoryGalleryRepository.save(MemoryGallery.builder()
                .patient(patient1)
                .writer(caregiver)
                .imageUrl("https://piuda-dummy.s3.amazonaws.com/gallery/daily_walk.jpg")
                .memo("오늘 산책 중 공원에서")
                .build());

        // ── 6. 커뮤니티 게시글 + 댓글 ──────────────────────────
        Post post1 = postRepository.save(Post.builder()
                .writer(protector)
                .title("치매 어르신 수면 문제 어떻게 해결하셨나요?")
                .content("어머니가 밤에 자꾸 일어나셔서 힘드네요. 석양증후군인 것 같은데 경험 있으신 분 조언 부탁드립니다.")
                .category(PostCategory.QNA)
                .build());

        Post post2 = postRepository.save(Post.builder()
                .writer(caregiver)
                .title("치매 어르신 식사 거부 시 대처법 공유합니다")
                .content("식사 거부가 심할 때 좋아하는 음악을 틀어드리면 효과가 있었어요. 좋아하는 반찬 위주로 소량씩 자주 드리는 것도 도움이 됩니다.")
                .category(PostCategory.CAREGIVER_TIPS)
                .build());

        Post post3 = postRepository.save(Post.builder()
                .writer(medical)
                .title("치매 초기 진단 후 가족분들께 드리는 안내")
                .content("치매 초기 진단을 받으셨다면 당황하지 마시고 전문의와 충분히 상담하세요. 조기 치료와 인지 활동이 진행을 늦추는 데 중요합니다.")
                .category(PostCategory.INFO)
                .build());

        Post post4 = postRepository.save(Post.builder()
                .writer(protector)
                .title("오늘 어머니가 제 이름을 기억하셨어요")
                .content("몇 달 만에 제 이름을 부르셨는데 너무 감동이었습니다. 작은 순간이지만 큰 힘이 되네요.")
                .category(PostCategory.EMOTION)
                .build());

        commentRepository.save(Comment.builder()
                .post(post1)
                .writer(caregiver)
                .content("저는 낮에 충분히 활동량을 늘리고, 저녁에는 조명을 어둡게 해드렸더니 효과가 있었어요.")
                .build());

        Comment parentComment = commentRepository.save(Comment.builder()
                .post(post1)
                .writer(medical)
                .content("석양증후군은 규칙적인 생활 패턴 유지가 핵심입니다. 필요하다면 전문의와 상담해 약물 치료도 고려해 보세요.")
                .build());

        commentRepository.save(Comment.builder()
                .post(post1)
                .writer(protector)
                .parentComment(parentComment)
                .content("감사합니다. 한번 병원에 여쭤봐야겠네요.")
                .build());

        commentRepository.save(Comment.builder()
                .post(post2)
                .writer(protector)
                .content("좋은 정보 감사합니다! 어머니가 트로트를 좋아하시는데 시도해봐야겠어요.")
                .build());

        // ── 7. AI 케어 어드바이스 세션 + 메시지 ─────────────────
        CareAdviceSession session = careAdviceSessionRepository.save(
                CareAdviceSession.builder()
                        .user(protector)
                        .patient(patient1)
                        .build());

        careAdviceMessageRepository.save(CareAdviceMessage.builder()
                .session(session)
                .role(MessageRole.USER)
                .content("어머니가 요즘 밤에 자꾸 집에 가겠다고 하시는데 어떻게 대처해야 할까요?")
                .build());

        careAdviceMessageRepository.save(CareAdviceMessage.builder()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content("치매 어르신이 '집에 가겠다'고 하시는 것은 심리적 불안감의 표현인 경우가 많습니다. "
                        + "억지로 막기보다는 공감해 드리고 '조금 있다 같이 가요'처럼 안심시켜 드리는 것이 좋습니다. "
                        + "어머니께서 좋아하시는 트로트를 틀어드리거나 화투를 함께 하시면서 관심을 돌려드리는 것도 효과적입니다.")
                .build());

        careAdviceMessageRepository.save(CareAdviceMessage.builder()
                .session(session)
                .role(MessageRole.USER)
                .content("어머니가 식사를 잘 안 하시려고 할 때는 어떻게 해야 하나요?")
                .build());

        careAdviceMessageRepository.save(CareAdviceMessage.builder()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content("식사 거부 시에는 좋아하시는 음식 위주로 소량씩 자주 드리는 방법이 효과적입니다. "
                        + "식사 시간에 좋아하시는 음악을 틀어드리면 분위기가 좋아져 도움이 될 수 있어요. "
                        + "억지로 드시게 하기보다 자연스럽게 식사 자리로 유도해 드리세요.")
                .build());

        // ── 8. 간병일기 ─────────────────────────────────────────
        caregiverDiaryRepository.save(CaregiverDiary.builder()
                .user(caregiver)
                .title("오늘 하루를 돌아보며")
                .content("어르신이 오늘 유독 힘드셨는지 계속 우셨다. 내가 위로해드리려 했지만 역부족이었다. "
                        + "집에 가겠다는 말을 수십 번 하셔서 나도 지쳤지만, 퇴근 전 잠깐 웃어주실 때 그 힘으로 버텼다.")
                .mood(MoodType.TIRED)
                .build());

        caregiverDiaryRepository.save(CaregiverDiary.builder()
                .user(caregiver)
                .title("감사한 하루")
                .content("오늘은 어르신이 내 이름을 기억해주셨다. '이 선생님'이라고 부르실 때 눈물이 날 뻔했다. "
                        + "이런 순간들이 있어서 이 일을 계속할 수 있는 것 같다.")
                .mood(MoodType.GRATEFUL)
                .build());

        caregiverDiaryRepository.save(CaregiverDiary.builder()
                .user(caregiver)
                .title("트로트 틀어드린 날")
                .content("좋아하시는 트로트를 틀어드렸더니 노래를 따라 부르시며 기분이 많이 좋아지셨다. "
                        + "음악이 이렇게 큰 힘이 될 줄이야. 앞으로도 자주 틀어드려야겠다.")
                .mood(MoodType.HAPPY)
                .build());

        caregiverDiaryRepository.save(CaregiverDiary.builder()
                .user(protector)
                .title("엄마 곁에 있어서 다행이야")
                .content("오늘 퇴근 후 엄마한테 들렀는데 날 못 알아보시다가 나중에 기억하셨다. "
                        + "엄마가 아직 나를 기억해주시는 게 너무 감사하다. 더 자주 찾아뵙자.")
                .mood(MoodType.HOPEFUL)
                .build());

        log.info("[DataInitializer] 더미 데이터 삽입 완료.");
        log.info("[DataInitializer] 테스트 계정: protector@test.com / caregiver@test.com / medical@test.com (비밀번호: test1234)");
    }
}
