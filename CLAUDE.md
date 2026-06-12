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

**실행 전제조건:**
- MySQL 서버가 로컬 3306 포트에서 실행 중이어야 하며, `dementia_project` 스키마가 존재해야 한다.
- PostgreSQL 서버가 로컬 5432 포트에서 실행 중이어야 하며, `piuda_vector` 스키마와 `vector` 확장이 활성화되어 있어야 한다.
- `spring.jpa.hibernate.ddl-auto=update` 설정 중.

**환경변수 (IDE Run Configuration에서 설정):**
```
MYSQL_PASSWORD=...
PGVECTOR_PASSWORD=...
OPENAI_API_KEY=...
AWS_ACCESS_KEY=...
AWS_SECRET_KEY=...
GOOGLE_CLIENT_ID=...
LINE_CLIENT_ID=...
```

## 기술 스택

- Java 21 / Spring Boot 3.5.0
- Spring Security + JWT (JJWT 0.13.0) — Stateless, 액세스 토큰 30분 / 리프레시 토큰 14일
- Spring Data JPA + MySQL (`dementia_project` 스키마)
- Spring AI 1.0.0 — OpenAI ChatModel + EmbeddingModel + PGVector RAG
- PostgreSQL 16 + pgvector 0.8.0 (`piuda_vector` 스키마) — 벡터 저장소
- AWS S3 (`spring-cloud-starter-aws 2.2.6`) — 이미지 업로드
- Lombok + MapStruct
- springdoc-openapi 2.8.8 — Swagger UI (`/swagger-ui.html`)

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
- `global/config/` — `VectorStoreConfig` (PGVector 빈 수동 구성)
- `global/infrastructure/` — `S3UploadService`, `RagChatClient`, `KnowledgeLoader`
- `global/exception/` — `GlobalExceptionHandler`

## 도메인 구성 및 주요 연관 흐름

| 도메인 | 역할 |
|--------|------|
| `user` | 회원가입/로그인. Role: `PROTECTOR`(보호자) / `CAREGIVER`(간병인) / `MEDICAL_STAFF`(의료진) |
| `patient` | 환자 등록. 보호자-환자 N:M 매핑은 `PatientMember` 중간 테이블 사용 |
| `device` | ESP32 IoT 디바이스 등록 및 환자 연동 |
| `dailylog` | 간병 일지 CRUD. 일지 생성 시 `CareCalendar`에 `DAILY_LOG` 타입 항목을 자동 생성 |
| `calendar` | 케어 일정 관리. `CalendarType`: `MANUAL`(직접 등록) / `DAILY_LOG`(자동 생성) |
| `patientmemory` | 환자 1인당 1개의 신상/의료 정보 레코드. 환자 등록 시 빈 레코드 자동 생성 |
| `memorygallery` | 환자별 사진 갤러리. S3 URL을 저장하며 Writer(User) 참조를 가짐 |
| `community` | 게시글/댓글 커뮤니티. `PostCategory`: QNA/INFO/CAREGIVER_TIPS/EMOTION/STORY/ADVERTISEMENT/ITEM_SALE/GROUP_BUY |
| `auth` | 소셜 로그인 (Google/Kakao/Line). 신규 사용자는 온보딩 필요 |
| `careadvice` | AI 케어 어드바이스. Spring AI + PGVector RAG, 세션 기반 대화 |
| `caregiverdiary` | 간병일기. 간병인 본인의 프라이빗 일기. `MoodType`: HAPPY/GRATEFUL/TIRED/SAD/ANXIOUS/ANGRY/LONELY/HOPEFUL |

### 핵심 비즈니스 규칙
- **환자 등록** (`PatientService.registerPatient`) 시 `PatientMemory` 빈 레코드를 함께 생성한다.
- **일지 등록** (`DailyLogService.createDailyLog`) 시 `CareCalendar` 항목이 자동 생성된다.
- `emotionalCommunicationMinutes` 필드는 `CAREGIVER`, `MEDICAL_STAFF` 역할만 기입 가능하다.
- 이미지는 각 도메인 엔드포인트에서 `multipart/form-data`로 직접 처리한다 (별도 이미지 업로드 API 없음).
- **게시글 스크랩**: `PostScrap` 엔티티로 관리. 토글(`POST /scraps`), 취소(`DELETE /scraps`), 목록 조회(`GET /scraps`) 지원.
- **게시글 정렬**: `SortType` — `LATEST`(커서 페이징), `VIEWS`/`LIKES`(오프셋 페이징).

## RAG 파이프라인 (careadvice)

```
[앱 시작 시]
resources/knowledge/*.pdf, *.json
  → PagePdfDocumentReader / JsonReader
  → TokenTextSplitter (청크 분할)
  → OpenAI text-embedding-3-small (1536차원 임베딩)
  → PGVector vector_store 테이블에 저장

[메시지 수신 시]
사용자 질문 → 임베딩 → PGVector 유사도 검색 (Top-3, threshold 0.6)
  → 검색된 지식 + 환자 정보(PatientMemory) + 대화 이력(최근 10개)
  → OpenAI gpt-4o-mini → 응답 생성 → MySQL에 저장
```

**지식 베이스 관리:**
- PDF/JSON 파일은 `.gitignore` 처리됨 — 환경마다 `resources/knowledge/`에 직접 배치 필요
- `knowledge.reload-on-startup=false` (기본값): vector_store가 비어있을 때만 인덱싱
- `knowledge.reload-on-startup=true`: 강제 재인덱싱 (변경 시 먼저 `TRUNCATE vector_store` 권장)
- `knowledge.pdf-start-page=5` (기본값): 총 페이지가 5 미만인 PDF는 자동으로 전체 읽기
- JSON 형식: `[{"title": "...", "content": "..."}]` — `content` 필드가 벡터화됨

**데이터소스 구성:**
- MySQL (JPA): `spring.datasource.*` — Spring Boot 자동 구성
- PostgreSQL (PGVector): `pgvector.datasource.*` — `VectorStoreConfig`에서 수동 구성 (`DataSource` 빈 미노출로 JPA와 충돌 방지)

## 인증

- 인증 불필요 엔드포인트: `POST /api/v1/users/login`, `POST /api/v1/users/signup`, `POST /api/v1/users/refresh`, `POST /api/v1/devices`, `POST /api/v1/devices/*/voice`, `GET /api/v1/posts`, `GET /api/v1/posts/*`, `GET /api/v1/posts/*/comments`, `POST /api/v1/auth/*`
- 나머지 모든 엔드포인트는 `Authorization: Bearer <JWT>` 헤더 필요
- JWT에는 `userId`, `email`, `role` 클레임이 포함된다

## 인증 토큰 흐름

- **로그인/소셜 로그인/온보딩** 시 `accessToken`(30분) + `refreshToken`(14일) 함께 발급
- **액세스 토큰 재발급**: `POST /api/v1/users/refresh?refreshToken=<값>` — 새 액세스/리프레시 토큰 반환 (Token Rotation)
- **로그아웃**: `POST /api/v1/users/logout` — DB의 리프레시 토큰 삭제
- 리프레시 토큰은 `refresh_tokens` 테이블(MySQL)에 저장. 사용자 1명당 1개 유지
