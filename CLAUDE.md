# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# 빌드
./mvnw clean package -DskipTests

# 로컬 실행
./mvnw spring-boot:run

# 전체 테스트
./mvnw test

# 단일 테스트 클래스 실행
./mvnw test -Dtest=ClassName

# 단일 테스트 메서드 실행
./mvnw test -Dtest=ClassName#methodName
```

**실행 전제조건:** MySQL 서버가 로컬 3306 포트에서 실행 중이어야 하며, `dementia_project` 스키마가 존재해야 한다.  
`spring.jpa.hibernate.ddl-auto=create` 설정으로 **서버 재시작 시마다 테이블이 재생성**된다(데이터 초기화 주의).

## 기술 스택

- Java 21 / Spring Boot 3.5.0
- Spring Security + JWT (JJWT 0.13.0) — Stateless, 토큰 유효시간 30분
- Spring Data JPA + MySQL (`dementia_project` 스키마)
- AWS S3 (`spring-cloud-starter-aws 2.2.6`) — 이미지 업로드
- Lombok + MapStruct

## 아키텍처

도메인별 패키지 분리 구조(`project.piuda.domain.<도메인>`). 각 도메인은 아래 레이어를 가진다:

```
domain/<name>/
  domain/        ← Entity + Repository (JPA)
  application/   ← Service + dto/Request + dto/Response
  presentation/  ← Controller
```

공통 인프라는 `global/` 하위에 위치한다:
- `global/security/` — JWT 필터(`JwtAuthenticationFilter`), Provider, CustomUserDetails
- `global/infrastructure/` — `S3UploadService`
- `global/exception/` — `GlobalExceptionHandler`

## 도메인 구성 및 주요 연관 흐름

| 도메인 | 역할 |
|--------|------|
| `user` | 회원가입/로그인. Role: `PROTECTOR`(보호자) / `CAREGIVER`(간병인) |
| `patient` | 환자 등록. 보호자-환자 N:M 매핑은 `PatientMember` 중간 테이블 사용 |
| `device` | ESP32 IoT 디바이스 등록 및 환자 연동 |
| `dailylog` | 간병 일지 CRUD. 일지 생성 시 `CareCalendar`에 `DAILY_LOG` 타입 항목을 자동 생성 |
| `calendar` | 케어 일정 관리. `CalendarType`: `MANUAL`(직접 등록) / `DAILY_LOG`(자동 생성) |
| `patientmemory` | 환자 1인당 1개의 신상/의료 정보 레코드. 환자 등록 시 빈 레코드 자동 생성 |
| `memorygallery` | 환자별 사진 갤러리. S3 URL을 저장하며 Writer(User) 참조를 가짐 |

### 핵심 비즈니스 규칙
- **환자 등록** (`PatientService.registerPatient`) 시 `PatientMemory` 빈 레코드를 함께 생성한다.
- **일지 등록** (`DailyLogService.createDailyLog`) 시 `CareCalendar` 항목이 자동 생성된다.
- `emotionalCommunicationMinutes` 필드는 `CAREGIVER` 역할만 기입 가능하다.
- 이미지는 `POST /api/v1/images/upload` (S3 업로드) → 반환된 URL을 각 도메인 요청에 포함하는 2단계 흐름이다.

## 인증

- 인증 불필요 엔드포인트: `POST /api/v1/users/login`, `POST /api/v1/users/signup`, `POST /api/v1/devices`
- 나머지 모든 엔드포인트는 `Authorization: Bearer <JWT>` 헤더 필요
- JWT에는 `userId`, `email`, `role` 클레임이 포함된다

## 환경 설정

`application.properties`에 AWS 자격증명, DB 비밀번호, JWT 시크릿이 평문으로 작성되어 있다. 실제 배포 시에는 환경변수나 외부 설정으로 분리 필요.
