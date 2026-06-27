# Piuda API 문서

**Base URL:** `http://localhost:8080`  
**인증:** 인증이 필요한 모든 API는 `Authorization: Bearer <accessToken>` 헤더 필요  
**Swagger UI:** `/swagger-ui.html`

---

## 목차
1. [인증 공통](#1-인증-공통)
2. [회원 (User)](#2-회원-user)
3. [소셜 로그인 (Auth)](#3-소셜-로그인-auth)
4. [환자 (Patient)](#4-환자-patient)
5. [환자 신상/의료 정보 (PatientMemory)](#5-환자-신상의료-정보-patientmemory)
6. [기억 갤러리 (MemoryGallery)](#6-기억-갤러리-memorygallery)
8. [케어 캘린더 (Calendar)](#8-케어-캘린더-calendar)
9. [간병일기 (CaregiverDiary)](#9-간병일기-caregiverdiary)
10. [AI 케어 어드바이스 (CareAdvice)](#10-ai-케어-어드바이스-careadvice)
11. [커뮤니티 게시글 (Post)](#11-커뮤니티-게시글-post)
12. [커뮤니티 댓글 (Comment)](#12-커뮤니티-댓글-comment)
13. [채팅 (Chat)](#13-채팅-chat)
14. [디바이스 (Device)](#14-디바이스-device)
15. [신고 (Report)](#15-신고-report)
16. [관리자 (Admin)](#16-관리자-admin)
17. [Enum 목록](#17-enum-목록)

---

## 1. 인증 공통

### 토큰 흐름
- **로그인/소셜 로그인** → `accessToken`(30분) + `refreshToken`(14일) 반환
- **액세스 토큰 만료 시** → `POST /api/v1/users/refresh`로 재발급 (Token Rotation: 기존 리프레시 토큰 무효화, 새 토큰 발급)
- **로그아웃** → `POST /api/v1/users/logout`으로 리프레시 토큰 삭제

### 인증 불필요 엔드포인트
`POST /signup`, `POST /login`, `POST /refresh`, `POST /devices`, `POST /devices/*/voice`, `GET /posts`, `GET /posts/*`, `GET /posts/*/comments`, `POST /auth/*`

---

## 2. 회원 (User)

### POST `/api/v1/users/signup`
회원가입 (multipart/form-data)

**Request** (form-data)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | ✅ | 이메일 형식, 255자 이하 |
| password | String | ✅ | 8자 이상, 영문+숫자 포함 |
| passwordConfirm | String | ✅ | 비밀번호 확인 |
| name | String | ✅ | 50자 이하 |
| nickname | String | ✅ | 2~20자, 한글/영문/숫자만 |
| role | String | ✅ | `PROTECTOR` / `CAREGIVER` / `MEDICAL_STAFF` |
| phone | String | - | `010-1234-5678` 형식 |
| introduction | String | - | 200자 이하 |
| experienceYears | Integer | - | 0~50 (CAREGIVER만) |
| gender | String | - | `MALE` / `FEMALE` |
| birthDate | String | - | `YYYY-MM-DD` |
| caregiverType | String | - | `CARE_WORKER` / `GENERAL` (CAREGIVER만) |
| image | File | - | 프로필 이미지 |

**Response** `200 OK`
```json
// 본문 없음
```

---

### POST `/api/v1/users/login`
로그인

**Request**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** `200 OK`
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci..."
}
```

---

### POST `/api/v1/users/refresh`
액세스 토큰 재발급

**Request**
```json
{
  "refreshToken": "eyJhbGci..."
}
```

**Response** `200 OK`
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci..."  // 새 리프레시 토큰으로 교체 필요
}
```

---

### POST `/api/v1/users/logout` 🔒
로그아웃 — 리프레시 토큰 삭제

**Response** `200 OK`

---

### POST `/api/v1/users/onboarding` 🔒
소셜 로그인 신규 사용자 온보딩

**Request**
```json
{
  "nickname": "피우다유저",
  "role": "CAREGIVER",
  "phone": "010-1234-5678",
  "experienceYears": 3,
  "gender": "MALE",
  "birthDate": "1990-01-01",
  "caregiverType": "CARE_WORKER"
}
```

**Response** `200 OK`
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci..."
}
```

---

### GET `/api/v1/users/me` 🔒
내 정보 조회

**Response** `200 OK`
```json
{
  "userId": 1,
  "email": "user@example.com",
  "name": "홍길동",
  "nickname": "피우다",
  "phone": "010-1234-5678",
  "profileImageUrl": "https://s3.../profiles/xxx.jpg",
  "introduction": "안녕하세요",
  "role": "CAREGIVER",
  "score": 120,
  "gender": "MALE",
  "birthDate": "1990-01-01",
  "experienceYears": 3,
  "caregiverType": "CARE_WORKER"
}
```

---

### PUT `/api/v1/users/me` 🔒
내 정보 수정 (multipart/form-data)

**Request** (form-data, 모든 필드 선택)

| 필드 | 타입 | 설명 |
|------|------|------|
| name | String | 이름 |
| nickname | String | 닉네임 |
| phone | String | 전화번호 |
| introduction | String | 자기소개 |
| currentPassword | String | 현재 비밀번호 (비밀번호 변경 시 필수) |
| password | String | 새 비밀번호 |
| gender | String | 성별 |
| birthDate | String | 생년월일 |
| experienceYears | Integer | 경력 연수 |
| caregiverType | String | 간병인 유형 |
| image | File | 프로필 이미지 |

**Response** `200 OK`

---

### DELETE `/api/v1/users/me` 🔒
회원 탈퇴 (모든 관련 데이터 cascade 삭제)

**Response** `200 OK`

---

### GET `/api/v1/users/check-nickname?nickname={nickname}`
닉네임 중복 확인

**Response** `200 OK`
```json
true   // 사용 가능
false  // 중복
```

---

### GET `/api/v1/users/ranking?limit={limit}`
내공점수 랭킹 조회

**Query Params**

| 파라미터 | 기본값 | 설명 |
|---------|--------|------|
| limit | 10 | 조회 개수 |

**Response** `200 OK`
```json
[
  {
    "rank": 1,
    "userId": 5,
    "name": "홍길동",
    "role": "CAREGIVER",
    "score": 340,
    "profileImageUrl": "https://..."
  }
]
```

---

### PUT `/api/v1/users/fcm-token` 🔒
FCM 토큰 등록/갱신

**Request**
```json
{
  "fcmToken": "firebase-token-value"
}
```

**Response** `200 OK`

---

### GET `/api/v1/users/{nickname}/profile` 🔒
공개 프로필 조회

**Response** `200 OK`
```json
{
  "nickname": "피우다",
  "profileImageUrl": "https://...",
  "role": "CAREGIVER",
  "introduction": "안녕하세요",
  "experienceYears": 3,
  "caregiverType": "CARE_WORKER",
  "score": 120,
  "joinedAt": "2024-01-15"
}
```

---

## 3. 소셜 로그인 (Auth)

### POST `/api/v1/auth/google`
### POST `/api/v1/auth/kakao`
### POST `/api/v1/auth/line`

**Request**
```json
{
  "token": "소셜_제공자_토큰"
}
```

**Response** `200 OK`
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "needsOnboarding": true  // true면 /onboarding으로 이동
}
```

> `needsOnboarding: true`이면 발급된 accessToken으로 `POST /api/v1/users/onboarding` 호출 필요

---

## 4. 환자 (Patient)

### POST `/api/v1/patients` 🔒
환자 등록 (PatientMemory 빈 레코드 자동 생성)

**Request**
```json
{
  "name": "김환자",
  "birthDate": "1940-05-20",
  "gender": "FEMALE",
  "dementiaStage": "MODERATE",
  "relationship": "모"
}
```

**Response** `200 OK` — 생성된 환자 정보 반환 (PatientResponse)

---

### POST `/api/v1/patients/join` 🔒
초대코드로 환자 합류

**Request**
```json
{
  "inviteCode": "ABC123",
  "relationship": "자"
}
```

**Response** `200 OK` — PatientResponse

---

### GET `/api/v1/patients/my` 🔒
내 환자 목록 조회

**Response** `200 OK`
```json
[
  {
    "id": 1,
    "name": "김환자",
    "birthDate": "1940-05-20",
    "gender": "FEMALE",
    "dementiaStage": "MODERATE",
    "inviteCode": "ABC123",
    "deviceSerial": "ESP32-001"
  }
]
```

---

### GET `/api/v1/patients/{patientId}` 🔒
환자 상세 조회

**Response** `200 OK` — PatientResponse

---

### PUT `/api/v1/patients/{patientId}` 🔒
환자 정보 수정

**Request**
```json
{
  "name": "김환자",
  "birthDate": "1940-05-20",
  "gender": "FEMALE",
  "dementiaStage": "SEVERE"
}
```

**Response** `200 OK` — PatientResponse

---

### DELETE `/api/v1/patients/{patientId}` 🔒
환자 삭제

**Response** `200 OK`

---

### POST `/api/v1/patients/{patientId}/devices` 🔒
환자에 디바이스 연동

**Request**
```json
{
  "deviceSerial": "ESP32-001"
}
```

**Response** `200 OK`

---

### DELETE `/api/v1/patients/{patientId}/devices` 🔒
환자 디바이스 연동 해제

**Response** `200 OK`

---

## 5. 환자 신상/의료 정보 (PatientMemory)

> 환자 등록 시 자동 생성되는 1:1 레코드. 직접 생성/삭제 불필요.

### GET `/api/v1/patients/{patientId}/patient-memory` 🔒
환자 신상/의료 정보 조회

**Response** `200 OK`
```json
{
  "id": 1,
  "patientId": 1,
  "bloodType": "A",
  "longTermCareGrade": 2,
  "dementiaType": "알츠하이머형",
  "comorbidities": "고혈압, 당뇨",
  "contraindications": "페니실린 알레르기",
  "medicationInfo": "아리셉트 5mg 1일 1회",
  "prnMedicationInfo": "필요시 수면제",
  "primaryDoctorInfo": "서울대병원 신경과 김의사 010-0000-0000",
  "likes": "트로트 음악, 화투",
  "dislikes": "큰 소리",
  "soothingWords": "잘하셨어요, 괜찮아요",
  "ineffectiveWords": "왜 그러세요",
  "sundowningInfo": "저녁 5시 이후 불안 증가",
  "repetitiveBehaviors": "집에 가고 싶다 반복",
  "wanderingRoute": "현관 → 거실 반복",
  "emergencyContacts": "자녀 010-1234-5678",
  "preferredHospital": "서울대병원",
  "specialNotes": "낙상 위험 있음",
  "updatedAt": "2024-06-15T10:30:00"
}
```

---

### PUT `/api/v1/patients/{patientId}/patient-memory` 🔒
환자 신상/의료 정보 수정 (모든 필드 선택)

**Request**
```json
{
  "bloodType": "A",
  "longTermCareGrade": 2,
  "dementiaType": "알츠하이머형",
  "medicationInfo": "아리셉트 5mg",
  "likes": "트로트 음악"
}
```

**Response** `200 OK`

---

## 6. 기억 갤러리 (MemoryGallery)

### POST `/api/v1/patients/{patientId}/gallery/photos` 🔒
사진 업로드 (multipart/form-data)

**Request** (form-data)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| image | File | ✅ | 이미지 파일 |
| memo | String | - | 사진 메모 |

**Response** `200 OK`

---

### GET `/api/v1/patients/{patientId}/gallery/photos` 🔒
사진 갤러리 조회 (최신순)

**Response** `200 OK`
```json
[
  {
    "galleryId": 5,
    "imageUrl": "https://...",
    "recordedAt": "2024-06-15T10:00:00",
    "writerName": "홍길동",
    "memo": "오늘 산책",
    "source": "GALLERY"
  }
]
```

---

### DELETE `/api/v1/patients/{patientId}/gallery/photos/{galleryId}` 🔒
사진 삭제 (직접 업로드한 사진만 가능)

**Response** `200 OK`

---

### GET `/api/v1/patients/{patientId}/gallery/audio` 🔒
음성 갤러리 조회

**Response** `200 OK`
```json
[
  {
    "audioId": 1,
    "audioUrl": "https://...",
    "recordedAt": "2024-06-15T14:00:00"
  }
]
```

---

### DELETE `/api/v1/patients/{patientId}/gallery/audio/{audioId}` 🔒
음성 기록 삭제

**Response** `200 OK`

---

## 8. 케어 캘린더 (Calendar)

### POST `/api/v1/patients/{patientId}/calendars` 🔒
수동 일정 등록

**Request**
```json
{
  "title": "병원 방문",
  "content": "서울대병원 신경과",
  "assigneeId": 3,
  "category": "VISIT",
  "startTime": "2024-06-20T10:00:00",
  "endTime": "2024-06-20T12:00:00"
}
```

**Response** `200 OK` — 생성된 calendarId (Long)

---

### GET `/api/v1/patients/{patientId}/calendars`
환자 캘린더 전체 조회 (수동 등록, 시작시간순)

**Response** `200 OK`
```json
[
  {
    "id": 1,
    "patientId": 1,
    "writerName": "홍길동",
    "assigneeName": "김간병",
    "dailyLogId": null,
    "title": "병원 방문",
    "content": "서울대병원 신경과",
    "calendarType": "SCHEDULE",
    "category": "VISIT",
    "startTime": "2024-06-20T10:00:00",
    "endTime": "2024-06-20T12:00:00"
  }
]
```

> `calendarType`: `SCHEDULE`(수동 등록)

---

### GET `/api/v1/calendars/{calendarId}`
일정 단건 조회

**Response** `200 OK` — CareCalendarResponse

---

### PUT `/api/v1/calendars/{calendarId}` 🔒
일정 수정 (`SCHEDULE` 타입, 작성자만)

**Request** — POST와 동일

**Response** `200 OK`

---

### DELETE `/api/v1/calendars/{calendarId}` 🔒
일정 삭제 (`SCHEDULE` 타입, 작성자만)

**Response** `200 OK`

---

## 9. 간병일기 (CaregiverDiary)

> 간병인 본인만 볼 수 있는 프라이빗 일기

### POST `/api/v1/diary` 🔒
일기 작성

**Request**
```json
{
  "title": "오늘의 하루",
  "content": "오늘은 힘들었지만...",
  "mood": "TIRED"
}
```

**Response** `200 OK` — 생성된 diaryId (Long)

---

### GET `/api/v1/diary` 🔒
내 일기 목록 조회 (최신순)

**Response** `200 OK`
```json
[
  {
    "id": 1,
    "title": "오늘의 하루",
    "content": "오늘은 힘들었지만...",
    "mood": "TIRED",
    "createdAt": "2024-06-15T22:00:00",
    "updatedAt": "2024-06-15T22:00:00"
  }
]
```

---

### GET `/api/v1/diary/{diaryId}` 🔒
일기 단건 조회 (작성자만)

**Response** `200 OK` — CaregiverDiaryResponse

---

### PUT `/api/v1/diary/{diaryId}` 🔒
일기 수정 (작성자만)

**Request** — POST와 동일

**Response** `200 OK`

---

### DELETE `/api/v1/diary/{diaryId}` 🔒
일기 삭제 (작성자만)

**Response** `200 OK`

---

## 10. AI 케어 어드바이스 (CareAdvice)

### POST `/api/v1/patients/{patientId}/care-advice/sessions` 🔒
새 대화 세션 시작

**Response** `200 OK`
```json
{
  "sessionId": 1,
  "patientId": 1,
  "createdAt": "2024-06-15T10:00:00"
}
```

---

### POST `/api/v1/care-advice/sessions/{sessionId}/messages` 🔒
메시지 전송 및 AI 응답 수신

**Request**
```json
{
  "content": "어머니가 저녁마다 집에 가겠다고 하시는데 어떻게 하면 좋을까요?"
}
```

**Response** `200 OK`
```json
{
  "userMessage": {
    "messageId": 1,
    "role": "USER",
    "content": "어머니가 저녁마다...",
    "createdAt": "2024-06-15T10:01:00"
  },
  "assistantMessage": {
    "messageId": 2,
    "role": "ASSISTANT",
    "content": "황혼증후군으로 보입니다...",
    "createdAt": "2024-06-15T10:01:03"
  },
  "ragUsed": true
}
```

---

### GET `/api/v1/patients/{patientId}/care-advice/sessions` 🔒
세션 목록 조회 (최신순)

**Response** `200 OK` — CareAdviceSessionResponse[]

---

### GET `/api/v1/care-advice/sessions/{sessionId}/messages` 🔒
세션 메시지 전체 조회 (시간순)

**Response** `200 OK` — CareAdviceMessageResponse[]

---

### DELETE `/api/v1/care-advice/sessions/{sessionId}` 🔒
세션 삭제

**Response** `200 OK`

---

## 11. 커뮤니티 게시글 (Post)

### POST `/api/v1/posts` 🔒
게시글 작성 (multipart/form-data, 이미지 최대 8장)

**Request** (form-data)

| 필드 | 타입 | 필수 |
|------|------|------|
| title | String | ✅ |
| content | String | ✅ |
| category | String | ✅ |
| images | File[] | - |

**Response** `200 OK` — 생성된 postId (Long)

---

### GET `/api/v1/posts`
게시글 목록 조회 (인증 선택)

**Query Params**

| 파라미터 | 기본값 | 설명 |
|---------|--------|------|
| category | - | PostCategory enum 필터 |
| keyword | - | 제목/내용 검색 |
| sortType | `LATEST` | `LATEST` / `VIEWS` / `LIKES` |
| cursor | - | LATEST 정렬 시 마지막 postId |
| page | 0 | VIEWS/LIKES 정렬 시 페이지 번호 |
| size | 10 | 페이지 크기 |

**Response** `200 OK`
```json
{
  "posts": [
    {
      "postId": 1,
      "writerNickname": "피우다",
      "writerProfileImageUrl": "https://...",
      "writerRole": "CAREGIVER",
      "title": "치매 환자 밥 먹이기 팁",
      "content": "...",
      "category": "CAREGIVER_TIPS",
      "imageUrls": ["https://..."],
      "likeCount": 12,
      "viewCount": 84,
      "likedByMe": false,
      "scrappedByMe": false,
      "hasAdopted": false,
      "createdAt": "2024-06-15T10:00:00",
      "updatedAt": "2024-06-15T10:00:00"
    }
  ],
  "hasNext": true,
  "nextCursor": 5   // LATEST만. VIEWS/LIKES는 nextPage
}
```

---

### GET `/api/v1/posts/{postId}`
게시글 단건 조회 (조회수 +1, 인증 선택)

**Response** `200 OK` — PostResponse

---

### PUT `/api/v1/posts/{postId}` 🔒
게시글 수정 (작성자만, 신고로 숨겨진 게시글 수정 불가)

**Response** `200 OK`

---

### DELETE `/api/v1/posts/{postId}` 🔒
게시글 삭제 (작성자만)

**Response** `200 OK`

---

### POST `/api/v1/posts/{postId}/likes` 🔒
좋아요 토글

**Response** `200 OK`
```json
true   // 좋아요 추가됨
false  // 좋아요 취소됨
```

---

### POST `/api/v1/posts/{postId}/scraps` 🔒
스크랩 토글

**Response** `200 OK`
```json
true   // 스크랩됨
false  // 스크랩 취소됨
```

---

### DELETE `/api/v1/posts/{postId}/scraps` 🔒
스크랩 명시적 취소

**Response** `200 OK`

---

### GET `/api/v1/posts/scraps` 🔒
내 스크랩 목록 조회

**Query Params**

| 파라미터 | 기본값 | 설명 |
|---------|--------|------|
| category | - | 카테고리 필터 |
| sortType | `LATEST` | `LATEST` / `VIEWS` / `LIKES` |
| page | 0 | 페이지 번호 |
| size | 10 | 페이지 크기 |

**Response** `200 OK`
```json
{
  "posts": [
    {
      "postId": 1,
      "writerNickname": "피우다",
      "writerProfileImageUrl": "https://...",
      "title": "...",
      "scrappedAt": "2024-06-15T11:00:00"
    }
  ],
  "hasNext": false,
  "nextPage": 1
}
```

---

## 12. 커뮤니티 댓글 (Comment)

### POST `/api/v1/posts/{postId}/comments` 🔒
댓글/대댓글 작성

**Request**
```json
{
  "content": "좋은 글이네요!",
  "parentCommentId": null   // 대댓글이면 부모 댓글 ID
}
```

**Response** `200 OK` — 생성된 commentId (Long)

---

### GET `/api/v1/posts/{postId}/comments`
댓글 목록 조회 (계층 구조, 인증 불필요)

**Response** `200 OK`
```json
[
  {
    "commentId": 1,
    "parentCommentId": null,
    "writerNickname": "피우다",
    "writerProfileImageUrl": "https://...",
    "writerRole": "CAREGIVER",
    "content": "좋은 글이네요!",
    "adopted": false,
    "hidden": false,
    "createdAt": "2024-06-15T11:00:00",
    "replies": [
      {
        "commentId": 2,
        "parentCommentId": 1,
        "writerNickname": "다른유저",
        "content": "저도요!",
        "replies": []
      }
    ]
  }
]
```

> `hidden: true`인 댓글은 content가 `"[신고로 인해 숨겨진 댓글입니다]"`로 반환

---

### PUT `/api/v1/comments/{commentId}` 🔒
댓글 수정 (작성자만)

**Request**
```json
{
  "content": "수정된 내용입니다"
}
```

**Response** `200 OK`

---

### DELETE `/api/v1/comments/{commentId}` 🔒
댓글 삭제 (작성자만, 대댓글도 자동 삭제)

**Response** `200 OK`

---

### POST `/api/v1/posts/{postId}/comments/{commentId}/adopt` 🔒
댓글 채택 (게시글 작성자만, 게시글당 1개)

**Response** `200 OK`

---

### DELETE `/api/v1/posts/{postId}/comments/{commentId}/adopt` 🔒
댓글 채택 취소 (게시글 작성자만)

**Response** `200 OK`

---

## 13. 채팅 (Chat)

### REST API

#### POST `/api/v1/chats` 🔒
채팅방 생성 또는 기존 방 반환

**Request**
```json
{
  "targetNickname": "상대방닉네임"
}
```

**Response** `200 OK`
```json
{
  "roomId": 1,
  "otherNickname": "상대방닉네임",
  "otherProfileImageUrl": "https://...",
  "lastMessage": "안녕하세요",
  "lastMessageAt": "2024-06-15T10:00:00",
  "unreadCount": 3
}
```

---

#### GET `/api/v1/chats` 🔒
내 채팅방 목록 조회 (최근 메시지순)

**Response** `200 OK` — ChatRoomResponse[]

---

#### GET `/api/v1/chats/{roomId}/messages` 🔒
메시지 내역 조회 (커서 페이징, 최신순)

**Query Params**

| 파라미터 | 기본값 | 설명 |
|---------|--------|------|
| cursor | - | 마지막으로 받은 messageId |
| size | 30 | 조회 개수 |

**Response** `200 OK`
```json
{
  "messages": [
    {
      "messageId": 10,
      "senderNickname": "피우다",
      "messageType": "TEXT",
      "content": "안녕하세요",
      "fileName": null,
      "read": true,
      "mine": true,
      "createdAt": "2024-06-15T10:00:00"
    }
  ],
  "hasNext": true,
  "nextCursor": 5
}
```

---

#### POST `/api/v1/chats/{roomId}/files` 🔒
이미지/파일 전송 (multipart/form-data)

**Request** (form-data)

| 필드 | 타입 | 설명 |
|------|------|------|
| files | File[] | 이미지 또는 파일 |

**Response** `200 OK` — ChatMessageResponse[]

---

#### PATCH `/api/v1/chats/{roomId}/read` 🔒
채팅방 메시지 읽음 처리

**Response** `200 OK`

---

#### DELETE `/api/v1/chats/{roomId}` 🔒
채팅방 나가기 — 채팅방과 모든 메시지를 삭제한다.

> ⚠️ 1:1 구조상 **상대방의 대화 내역도 함께 삭제**됩니다. (단순 나가기가 아닌 완전 삭제)

**Response** `200 OK`

| 상황 | HTTP | message |
|------|------|---------|
| 참여하지 않은 방 | `403` | "채팅방 접근 권한이 없습니다." |
| 존재하지 않는 방 | `404` | "존재하지 않는 채팅방입니다." |

---

### WebSocket (STOMP)

**연결 엔드포인트:** `ws://localhost:8080/ws`  
**인증:** WebSocket 연결 시 `Authorization` 헤더 포함

#### 메시지 전송
```
SEND /app/chat/{roomId}
```
```json
{
  "content": "안녕하세요",
  "messageType": "TEXT"
}
```

#### 메시지 수신 구독
```
SUBSCRIBE /topic/chat/{roomId}
```
→ ChatMessageResponse 수신

> 🔒 본인이 참여한 채팅방만 구독 가능합니다. 참여하지 않은 방을 구독하면 STOMP ERROR가 반환됩니다.

#### 읽음 이벤트 구독
```
SUBSCRIBE /topic/chat/{roomId}/read
```

#### 실시간 알림 구독
```
SUBSCRIBE /user/queue/notifications
```
```json
{
  "roomId": 1,
  "senderNickname": "피우다",
  "preview": "안녕하세요",
  "unreadCount": 2
}
```

---

## 14. 디바이스 (Device)

> 아래 디바이스 관련 API는 ESP32 펌웨어 전용이므로 앱에서 직접 호출하지 않음

### POST `/api/v1/devices`
디바이스 등록 (인증 불필요)

**Request**
```json
{
  "deviceSerial": "ESP32-001"
}
```

**Response** `200 OK` — deviceId (Long)

---

### GET `/api/v1/patients/{patientId}/device` 🔒
환자에 연결된 디바이스 조회

**Response** `200 OK`
```json
{
  "id": 1,
  "deviceSerial": "ESP32-001",
  "deviceStatus": "ACTIVE",
  "purchasedAt": "2024-01-01",
  "lastConnectedAt": "2024-06-15T09:00:00"
}
```

---

### PATCH `/api/v1/patients/{patientId}/device` 🔒
환자에 디바이스 연동

**Request**
```json
{
  "deviceSerial": "ESP32-001"
}
```

**Response** `200 OK`

---

### DELETE `/api/v1/patients/{patientId}/device` 🔒
환자 디바이스 연동 해제

**Response** `200 OK`

---

### POST `/api/v1/devices/{deviceSerial}/tts` 🔒
TTS 메시지 전송 (앱 → 디바이스)

**Request**
```json
{
  "text": "어머니, 약 드실 시간이에요."
}
```

**Response** `200 OK`

---

## 15. 신고 (Report)

### POST `/api/v1/posts/{postId}/reports` 🔒
게시글 신고

**Request**
```json
{
  "reason": "SPAM"
}
```

**Response** `200 OK`

---

### POST `/api/v1/comments/{commentId}/reports` 🔒
댓글 신고

**Request**
```json
{
  "reason": "ABUSE"
}
```

**Response** `200 OK`

> 신고 누적 시 자동 처리: 5개 이상 → `hidden=true` (내용 숨김), 10개 이상 → 자동 삭제

---

## 16. 관리자 (Admin)

> ADMIN 권한 계정만 접근 가능 (DB에서 role 컬럼을 'ADMIN'으로 직접 변경)

### GET `/api/v1/admin/stats` 🔒
전체 통계

**Response** `200 OK`
```json
{
  "totalUsers": 150,
  "protectorCount": 80,
  "caregiverCount": 60,
  "medicalStaffCount": 10,
  "totalPosts": 320,
  "totalDevices": 45
}
```

---

### GET `/api/v1/admin/users?page=0&size=20` 🔒
전체 회원 목록 (페이징)

---

### DELETE `/api/v1/admin/users/{userId}` 🔒
회원 강제 탈퇴

---

### GET `/api/v1/admin/posts?page=0&size=20` 🔒
전체 게시글 목록 (페이징)

---

### DELETE `/api/v1/admin/posts/{postId}` 🔒
게시글 강제 삭제

---

### GET `/api/v1/admin/reports?page=0&size=20` 🔒
신고 목록 조회 (PENDING 상태만)

**Response**
```json
[
  {
    "reportId": 1,
    "reporterNickname": "신고자",
    "targetType": "POST",
    "targetId": 5,
    "reason": "SPAM",
    "status": "PENDING",
    "createdAt": "2024-06-15T10:00:00"
  }
]
```

---

### PATCH `/api/v1/admin/reports/{reportId}/dismiss` 🔒
신고 기각 (숨겨진 게시글/댓글 복구)

---

## 17. Enum 목록

### Role
| 값 | 설명 |
|----|------|
| `PROTECTOR` | 보호자 |
| `CAREGIVER` | 간병인 |
| `MEDICAL_STAFF` | 의료진 |

### CaregiverType
| 값 | 설명 |
|----|------|
| `CARE_WORKER` | 요양보호사 |
| `GENERAL` | 일반 간병인 |

### Gender
| 값 | 설명 |
|----|------|
| `MALE` | 남성 |
| `FEMALE` | 여성 |

### DementiaStage
| 값 | 설명 |
|----|------|
| `MILD` | 경증 |
| `MODERATE` | 중등도 |
| `SEVERE` | 중증 |

### PostCategory
| 값 | 설명 |
|----|------|
| `QNA` | 질문/답변 |
| `INFO` | 정보 공유 |
| `CAREGIVER_TIPS` | 간병 팁 |
| `EMOTION` | 감정 나눔 |
| `STORY` | 이야기 |
| `ADVERTISEMENT` | 광고 |
| `ITEM_SALE` | 물품 판매 |
| `GROUP_BUY` | 공동구매 |

### MoodType (간병일기)
| 값 | 설명 |
|----|------|
| `HAPPY` | 행복 |
| `GRATEFUL` | 감사 |
| `TIRED` | 피곤 |
| `SAD` | 슬픔 |
| `ANXIOUS` | 불안 |
| `ANGRY` | 화남 |
| `LONELY` | 외로움 |
| `HOPEFUL` | 희망 |

### CalendarCategory
| 값 | 설명 |
|----|------|
| `OUTING` | 외출 |
| `VISIT` | 방문 |
| `SUPPLY` | 물품 |
| `EVENT` | 행사 |
| `ETC` | 기타 |

### CalendarType
| 값 | 설명 |
|----|------|
| `SCHEDULE` | 수동 등록 일정 |

### ReportReason
| 값 | 설명 |
|----|------|
| `SPAM` | 스팸 |
| `OBSCENE` | 음란물 |
| `ABUSE` | 욕설/비방 |
| `MISINFORMATION` | 허위정보 |
| `COPYRIGHT` | 저작권 침해 |
| `OTHER` | 기타 |

### MessageType (채팅)
| 값 | 설명 |
|----|------|
| `TEXT` | 텍스트 |
| `IMAGE` | 이미지 |
| `FILE` | 파일 |

---

## 공통 에러 응답

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "닉네임은 2자 이상 20자 이하이어야 합니다."
}
```

| 상태코드 | 의미 |
|---------|------|
| `400` | 잘못된 요청 (유효성 검사 실패) |
| `401` | 인증 필요 (토큰 없음/만료) |
| `403` | 권한 없음 |
| `404` | 리소스 없음 |
| `409` | 중복 충돌 (이미 존재하는 이메일/닉네임 등) |
| `500` | 서버 오류 |
