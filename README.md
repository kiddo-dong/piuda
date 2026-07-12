<div align="center">

# 실:온 (Sil:On)

### 치매 환자를 돌보는 보호자와 간병인을 잇는 케어 커넥션 서비스

*간병인의 판단과 보호자의 신뢰 사이를 잇고, AI 상담사 **시온이**가 돌봄을 돕습니다.*

<!-- 👉 여기에 앱 소개 이미지(대표 화면/로고)를 넣어주세요 -->
<!-- 예: <img src="docs/images/intro.png" width="720" /> -->

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-6DB33F?logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/pgvector-0.8.0-4169E1?logo=postgresql&logoColor=white)

</div>

---

## 📑 목차
1. [서비스 소개](#-서비스-소개)
2. [핵심 기능](#-핵심-기능)
3. [기술 스택](#-기술-스택)
4. [시스템 아키텍처](#-시스템-아키텍처)
5. [데이터베이스 스키마 (ERD)](#-데이터베이스-스키마-erd)
6. [프로젝트 구조](#-프로젝트-구조)
7. [로컬 실행 방법](#-로컬-실행-방법)
8. [API 문서](#-api-문서)

---

## 🩺 서비스 소개

**실:온**은 치매 환자를 돌보는 **보호자(가족)** 와 **간병인**이 한 환자를 중심으로 정보를 공유하고 신뢰를 쌓도록 돕는 서비스입니다.

기존 돌봄 앱이 *"모든 일상을 빠짐없이 기록"* 하는 데 집중했다면, 실:온은 **"판단이 필요한 순간"과 "정보가 필요한 순간"** 에 집중합니다.

### 이런 문제를 해결합니다
- 🤝 **보호자–간병인 간 정보 단절** — 한 환자를 여러 명이 함께 돌보지만 정보가 흩어져 있음
- 🧭 **판단의 근거 부재** — 간병인이 단독으로 내린 결정이 나중에 오해·갈등으로 이어짐
- ❓ **막막한 돌봄 지식** — "이럴 땐 어떻게 해야 하지?"에 답해줄 곳이 없음

### 이렇게 해결합니다
- **케어 판단 기록** — 간병인의 단독 판단을 *상황·판단·근거* 로 남겨 신뢰 자산으로 전환
- **AI 케어 어드바이스(시온이)** — 환자 정보 + 전문 지식(RAG) 기반 맞춤 상담
- **환자 중심 공유 구조** — 보호자·간병인이 한 환자를 함께 돌보는 N:M 연결

<!-- 👉 여기에 서비스 소개용 화면 흐름 이미지를 넣어주세요 -->

---

## ✨ 핵심 기능

> 각 기능의 앱 화면 스크린샷은 아래 자리에 직접 넣어주세요.

### 📋 케어 판단 기록 (핵심 차별점)
간병인이 보호자의 사전 지시 없이 내린 단독 판단을 **상황 · 판단 · 근거** 3요소로 기록하는 보관소.
- 8개 카테고리(식사/이동/투약/위생/수면/행동/안전/기타)로 분류·검색
- 긴급도 3단계(일반/관찰필요/**즉시공유**) — 즉시공유 시 보호자에게 **실시간 FCM 푸시**
- 작성은 간병인만, 조회는 환자에 연결된 보호자·간병인 전원
- 캘린더 조회 시 판단 기록도 함께 표시

<!-- ![케어 판단 기록](docs/images/judgment.png) -->

### 🤖 AI 케어 어드바이스 — 시온이
- 치매 돌봄 전문 AI 상담사 **'시온이'** 와 세션 기반 대화
- **RAG 파이프라인**: 치매 케어 지식(PDF·JSON) → pgvector 유사도 검색 → GPT 응답
- 환자의 신상·의료 정보를 자동으로 대화 맥락에 주입 → 환자 맞춤 답변

<!-- ![AI 케어 어드바이스](docs/images/careadvice.png) -->

### 🧓 환자 관리
- 환자 등록 및 **초대코드**로 보호자·간병인 합류 (N:M 공동 돌봄)
- 환자별 신상·의료 정보(치매 유형, 복용 약물, 진정 방법, 배회 경로 등) 관리

<!-- ![환자 관리](docs/images/patient.png) -->

### 📅 케어 캘린더
- 병원 방문·방문요양 등 케어 일정 등록/관리, 담당자 지정
- 판단 기록 통합 조회

### 🖼 기억 갤러리
- 환자별 사진 갤러리 (S3 저장)

### 💬 커뮤니티
- 게시글·댓글·대댓글, 좋아요·스크랩, 8개 카테고리, 키워드 검색
- 댓글 채택(내공점수), 신고(자동 숨김/삭제)

<!-- ![커뮤니티](docs/images/community.png) -->

### 💬 실시간 채팅
- WebSocket(STOMP) 기반 1:1 실시간 채팅
- 텍스트·이미지·파일 전송, 읽음 처리, FCM 푸시 알림

### 👤 회원 / 인증
- 이메일 회원가입 + **소셜 로그인 3종(Google·Kakao·Line)** + 온보딩
- JWT 인증 (액세스 30분 / 리프레시 14일, Token Rotation)
- 역할: 보호자(PROTECTOR) / 간병인(CAREGIVER) / 의료진(MEDICAL_STAFF)

---

## 🛠 기술 스택

| 구분 | 기술 |
|------|------|
| **Language / Framework** | Java 21, Spring Boot 3.5.0 |
| **Security** | Spring Security, JWT (JJWT 0.13.0) |
| **Persistence** | Spring Data JPA, MySQL 8 |
| **AI / RAG** | Spring AI 1.0.0, OpenAI (gpt-4o-mini, text-embedding-3-small) |
| **Vector Store** | PostgreSQL 16 + pgvector 0.8.0 |
| **Realtime** | WebSocket (STOMP) |
| **Push** | Firebase Cloud Messaging (FCM) |
| **Storage** | AWS S3 |
| **Mapping / Util** | Lombok, MapStruct |
| **API Docs** | springdoc-openapi 2.8.8 (Swagger UI) |
| **Build** | Maven |

---

## 🏗 시스템 아키텍처

```
                    ┌─────────────┐
                    │   Client    │  (모바일 앱)
                    └──────┬──────┘
                REST │ WebSocket │ FCM
                    ┌──────▼──────────────────────────┐
                    │      Spring Boot (실:온 API)      │
                    │  ┌────────────────────────────┐  │
                    │  │ presentation → application  │  │
                    │  │           → domain          │  │
                    │  └────────────────────────────┘  │
                    └───┬───────────┬───────────┬──────┘
              JPA │           │ Spring AI │        │ SDK
            ┌───────▼───┐ ┌─────▼─────┐ ┌───▼────┐ ┌─▼─────┐
            │  MySQL    │ │ PostgreSQL │ │ OpenAI │ │ AWS S3│
            │(운영 DB)  │ │  pgvector  │ │  API   │ │ ·FCM  │
            └───────────┘ └────────────┘ └────────┘ └───────┘
```

- **데이터소스 이원화**: 운영 데이터는 MySQL(JPA), RAG 벡터는 PostgreSQL·pgvector로 분리
- **도메인형 계층 구조**: 각 도메인이 `presentation → application → domain` 3계층을 동일하게 가짐

### RAG 파이프라인 (AI 케어 어드바이스)
```
[앱 시작 시] knowledge/*.pdf·*.json → 청크 분할 → 임베딩(1536차원) → pgvector 저장
[질문 수신 시] 질문 → 임베딩 → 유사도 검색(Top-3) + 환자 정보 + 대화 이력 → GPT → 응답
```

---

## 🗂 데이터베이스 스키마 (ERD)

<!-- 👉 여기에 ERD 이미지를 넣어주세요 (ERDCloud/dbdiagram.io로 생성) -->
<!-- 예: <img src="docs/images/erd.png" width="900" /> -->

- **MySQL** (`dementia_project`): 회원·환자·판단기록·커뮤니티·채팅 등 운영 데이터
- **PostgreSQL** (`piuda_vector`): `vector_store` — RAG 지식 임베딩

주요 관계
- `users` ⇄ `patients` : **N:M** (중간 테이블 `patient_members` — 보호자·간병인 공동 돌봄)
- `patients` 1 : N `care_judgment_logs` / `care_calendars` / `memory_galleries` / `care_advice_sessions`
- `posts` 1 : N `comments` / `post_images`, `users` 1 : N `posts`

---

## 📁 프로젝트 구조

```
project.piuda
├── PiudaApplication.java
├── domain/                      # 도메인별 패키지 (각 도메인 = 3계층)
│   ├── user/                    # 회원 · 인증(JWT)
│   ├── auth/                    # 소셜 로그인 (Google·Kakao·Line)
│   ├── patient/                 # 환자 등록 · 초대
│   ├── patientmemory/           # 환자 신상 · 의료 정보
│   ├── calendar/                # 케어 캘린더
│   ├── carejudgment/            # ⭐ 케어 판단 기록
│   ├── careadvice/              # AI 케어 어드바이스 (시온이 · RAG)
│   ├── memorygallery/           # 기억 갤러리 (사진)
│   ├── community/               # 게시글 · 댓글 · 스크랩
│   ├── chat/                    # 1:1 실시간 채팅
│   ├── report/                  # 신고 · 자동 숨김/삭제
│   └── admin/                   # 관리자 통계 · 관리
│       ├── presentation/        #   Controller (HTTP 요청 처리)
│       ├── application/          #   Service + dto (비즈니스 로직)
│       └── domain/              #   Entity + Repository (영속성)
└── global/                      # 전 도메인 공통 인프라
    ├── security/                # JWT 필터 · Spring Security · WebSocket 인가
    ├── config/                  # VectorStore · WebSocket · S3 · Swagger
    ├── infrastructure/          # S3 · FCM · RAG · 소셜 클라이언트
    └── exception/               # 전역 예외 처리
```

---

## 🚀 로컬 실행 방법

### 사전 요구사항
- **JDK 21**
- **MySQL** 로컬 3306 포트, `dementia_project` 스키마 생성
- **PostgreSQL 16** 로컬 5432 포트, `piuda_vector` 스키마 + `vector` 확장 활성화

### 환경변수 (IDE Run Configuration 또는 셸)
```bash
JWT_SECRET=...                       # HS256용 시크릿 (32바이트 이상 권장)
MYSQL_PASSWORD=...
PGVECTOR_PASSWORD=...
OPENAI_API_KEY=...
AWS_ACCESS_KEY=...
AWS_SECRET_KEY=...
GOOGLE_CLIENT_ID=...
LINE_CLIENT_ID=...
FCM_SERVICE_ACCOUNT_KEY_PATH=...     # (선택) 없으면 푸시 비활성화
```

### 빌드 & 실행
```bash
# 빌드
./mvnw clean package -DskipTests

# 로컬 실행
./mvnw spring-boot:run

# 전체 테스트
./mvnw test
```

> RAG 지식 파일(`src/main/resources/knowledge/*.pdf|*.json`)은 `.gitignore` 처리되어 있어 환경마다 직접 배치가 필요합니다.

---

## 📖 API 문서

애플리케이션 실행 후 Swagger UI에서 전체 API를 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui.html
```

### 인증
- 대부분의 엔드포인트는 `Authorization: Bearer <accessToken>` 헤더 필요
- 인증 불필요: 회원가입/로그인/토큰재발급, 소셜 로그인, 게시글 조회

---

<div align="center">

**실:온 (Sil:On)** · 치매 케어 커넥션 서비스

</div>
