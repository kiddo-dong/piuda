# Piuda API 명세서

> Base URL: `http://localhost:8080/api/v1`  
> 인증이 필요한 API는 모두 `Authorization: Bearer {JWT토큰}` 헤더 필요

---

## 공통 에러 응답

```json
{ "message": "에러 메시지" }
```

| HTTP 상태 | 의미 |
|-----------|------|
| 400 | 잘못된 입력값 또는 비즈니스 규칙 위반 |
| 401 | 인증 토큰 없음 또는 만료 |
| 403 | 접근 권한 없음 |
| 404 | 리소스 없음 |
| 409 | 중복 데이터 (이미 존재) |
| 413 | 파일 크기 초과 (10MB 제한) |
| 500 | 서버 내부 오류 |

---

## 1. 사용자 (User)

### 회원가입
```
POST /users/signup
인증: 불필요
```
**Request Body**
```json
{
  "email": "user@example.com",
  "password": "1234",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "role": "PROTECTOR",
  "experienceYears": 3,
  "introduction": "성실한 간병인입니다."
}
```
> `role`: `PROTECTOR` | `CAREGIVER`  
> `experienceYears`, `introduction`: 간병인(CAREGIVER)만 사용

**Response** `200 OK`

---

### 로그인
```
POST /users/login
인증: 불필요
```
**Request Body**
```json
{
  "email": "user@example.com",
  "password": "1234"
}
```
**Response** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### 내 정보 조회
```
GET /users/me
인증: 필요
```
**Response** `200 OK`
```json
{
  "userId": 1,
  "email": "user@example.com",
  "name": "홍길동",
  "phone": "010-1234-5678",
  "role": "PROTECTOR",
  "score": 100
}
```

---

### 내 정보 수정
```
PUT /users/me
인증: 필요
```
**Request Body**
```json
{
  "name": "홍길동(수정)",
  "phone": "010-9999-8888",
  "password": "newpassword"
}
```
> 변경하지 않을 필드는 생략 또는 빈 문자열

**Response** `200 OK`

---

### 회원 탈퇴
```
DELETE /users/me
인증: 필요
```
**Response** `200 OK`

---

### 내공점수 랭킹 조회
```
GET /users/ranking?limit=10
인증: 필요
```
> `limit`: 조회할 인원 수 (기본값 10)

**Response** `200 OK`
```json
[
  {
    "rank": 1,
    "userId": 5,
    "name": "김간병인",
    "role": "CAREGIVER",
    "score": 180
  }
]
```

---

## 2. 환자 (Patient)

### 환자 등록
```
POST /patients
인증: 필요
```
**Request Body**
```json
{
  "name": "홍환자",
  "birthDate": "1940-05-15",
  "gender": "MALE",
  "dementiaStage": "경증"
}
```
> `gender`: `MALE` | `FEMALE`

**Response** `200 OK`
```json
{
  "id": 1,
  "name": "홍환자",
  "birthDate": "1940-05-15",
  "gender": "MALE",
  "dementiaStage": "경증",
  "inviteCode": "A1B2C3D4"
}
```

---

### 초대코드로 환자 합류
```
POST /patients/join
인증: 필요
```
**Request Body**
```json
{
  "inviteCode": "A1B2C3D4"
}
```
**Response** `200 OK` — 환자 정보 반환

---

### 내 환자 목록 조회
```
GET /patients/my
인증: 필요
```
**Response** `200 OK` — 환자 배열

---

### 환자 상세 조회
```
GET /patients/{patientId}
인증: 필요 (케어팀 구성원만)
```
**Response** `200 OK` — 환자 정보 (inviteCode 포함)

---

### 환자 정보 수정
```
PUT /patients/{patientId}
인증: 필요 (케어팀 구성원만)
```
**Request Body** — 환자 등록과 동일

**Response** `200 OK`

---

### 환자 삭제
```
DELETE /patients/{patientId}
인증: 필요 (케어팀 구성원만)
```
**Response** `200 OK`

---

### IoT 디바이스 연동
```
POST /patients/{patientId}/devices?deviceSerial=ESP32-001
인증: 필요
```
**Response** `200 OK`

---

### IoT 디바이스 연동 해제
```
DELETE /patients/{patientId}/devices
인증: 필요
```
**Response** `200 OK`

---

## 3. 환자 신상 정보 (Patient Memory)

### 환자 메모리 조회
```
GET /patients/{patientId}/patient-memory
인증: 필요
```
**Response** `200 OK`
```json
{
  "bloodType": "A",
  "longTermCareGrade": 2,
  "dementiaType": "알츠하이머",
  "comorbidities": "고혈압, 당뇨",
  "medicationInfo": "혈압약 1일 1회",
  "contraindications": "없음",
  "likes": "트로트, 화투",
  "dislikes": "큰 소리",
  "soothingWords": "잘 하셨어요",
  "ineffectiveWords": "왜 또 그러세요",
  "sundowningInfo": "저녁 6시 이후 불안 증가",
  "repetitiveBehaviors": "집에 가겠다고 반복",
  "wanderingRoute": "현관 → 거실 반복",
  "emergencyContacts": "아들 010-xxxx-xxxx",
  "preferredHospital": "서울대병원",
  "specialNotes": "물 자주 드셔야 함"
}
```

---

### 환자 메모리 수정
```
PUT /patients/{patientId}/patient-memory
인증: 필요
```
**Request Body** — 조회 응답과 동일 구조

**Response** `200 OK`

---

## 4. 하루 일지 (Daily Log)

### 일지 등록
```
POST /patients/{patientId}/daily-logs
인증: 필요
Content-Type: multipart/form-data
```
| Part | 타입 | 설명 |
|------|------|------|
| data | application/json | 일지 데이터 (아래 JSON) |
| image | file | 첨부 이미지 (선택, jpg/png/gif/webp, 최대 10MB) |

**data JSON**
```json
{
  "logDate": "2026-05-30",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "physicalHygiene": true,
  "physicalBath": false,
  "physicalMealHelp": true,
  "physicalPositionChange": false,
  "physicalMobilityHelp": true,
  "physicalToiletHelp": false,
  "physicalTotalMinutes": 60,
  "cognitiveStimulationMinutes": 30,
  "cognitiveLifeTogetherMinutes": 20,
  "cognitiveBehaviorManagementMinutes": 10,
  "emotionalCommunicationMinutes": 0,
  "householdMealClean": true,
  "householdPersonalHelp": false,
  "householdTotalMinutes": 30,
  "physicalFunctionTrend": "STABLE",
  "mealFunctionTrend": "IMPROVING",
  "bowelIncontinenceCount": 0,
  "urineIncontinenceCount": 1,
  "specialNotes": "특이사항 없음"
}
```
> `physicalFunctionTrend`, `mealFunctionTrend`: `IMPROVING` | `STABLE` | `DECLINING`  
> `emotionalCommunicationMinutes`: 간병인(CAREGIVER)만 0 초과 값 입력 가능

**Response** `200 OK` — 생성된 logId (숫자)

---

### 일지 목록 조회
```
GET /patients/{patientId}/daily-logs
인증: 필요 (케어팀 구성원만)
```
**Response** `200 OK` — 일지 배열 (최신순)

---

### 일지 상세 조회
```
GET /daily-logs/{logId}
인증: 필요
```
**Response** `200 OK` — 일지 상세

---

### 일지 수정
```
PUT /daily-logs/{logId}
인증: 필요
Content-Type: multipart/form-data
```
| Part | 타입 | 설명 |
|------|------|------|
| data | application/json | 수정할 일지 데이터 |
| image | file | 새 이미지 (없으면 기존 이미지 유지) |

**Response** `200 OK`

---

### 일지 삭제
```
DELETE /daily-logs/{logId}
인증: 필요
```
> 삭제 시 연동된 캘린더 항목도 함께 삭제됨

**Response** `200 OK`

---

## 5. 케어 캘린더 (Care Calendar)

### 일정 등록
```
POST /patients/{patientId}/calendars
인증: 필요
```
**Request Body**
```json
{
  "title": "병원 방문",
  "content": "정기 검진",
  "assigneeId": null,
  "category": "VISIT",
  "startTime": "2026-06-01T10:00:00",
  "endTime": "2026-06-01T12:00:00"
}
```
> `category`: `OUTING` | `VISIT` | `SUPPLY` | `EVENT` | `ETC`  
> `assigneeId`: 담당자로 지정할 사용자 ID (선택)

**Response** `200 OK` — 생성된 calendarId (숫자)

---

### 일정 목록 조회
```
GET /patients/{patientId}/calendars
인증: 필요
```
**Response** `200 OK` — 일정 배열 (시작 시간 오름차순)

---

### 일정 수정
```
PUT /calendars/{calendarId}
인증: 필요 (작성자만)
```
**Request Body** — 일정 등록과 동일

**Response** `200 OK`

---

### 일정 삭제
```
DELETE /calendars/{calendarId}
인증: 필요 (작성자만)
```
**Response** `200 OK`

---

## 6. 기억 갤러리 (Memory Gallery)

### 사진 업로드
```
POST /patients/{patientId}/gallery
인증: 필요
Content-Type: multipart/form-data
```
| Part | 타입 | 설명 |
|------|------|------|
| image | file | 업로드할 이미지 (jpg/png/gif/webp, 최대 10MB) |
| memo | text | 메모 (선택) |

**Response** `200 OK`

---

### 갤러리 조회
```
GET /patients/{patientId}/gallery
인증: 필요 (케어팀 구성원만)
```
**Response** `200 OK`
```json
[
  {
    "type": "IMAGE",
    "url": "https://s3.amazonaws.com/...",
    "writerName": "김간병인",
    "recordedAt": "2026-05-30T09:00:00"
  },
  {
    "type": "AUDIO",
    "url": "https://s3.amazonaws.com/...",
    "recordedAt": "2026-05-29T14:30:00"
  }
]
```
> 일지 첨부사진 + 직접 업로드 사진 + IoT 음성 통합, 최신순

---

### 갤러리 항목 삭제
```
DELETE /patients/{patientId}/gallery/{galleryId}
인증: 필요 (케어팀 구성원만)
```
**Response** `200 OK`

---

## 7. 커뮤니티 - 게시글 (Post)

### 게시글 작성
```
POST /posts
인증: 필요
Content-Type: multipart/form-data
```
| Part | 타입 | 설명 |
|------|------|------|
| data | application/json | 게시글 데이터 |
| image | file | 첨부 이미지 (선택) |

**data JSON**
```json
{
  "title": "치매 환자 식사 도움 팁",
  "content": "이런 방법이 효과적이었어요.",
  "category": "TIP"
}
```
> `category`: `TIP` | `QUESTION` | `SHARE` | `ETC`

**Response** `200 OK`
```json
{ "postId": 1 }
```

---

### 게시글 목록 조회
```
GET /posts?category=TIP
인증: 필요
```
> `category` 파라미터 생략 시 전체 조회

**Response** `200 OK`
```json
[
  {
    "postId": 1,
    "writerName": "홍보호자",
    "writerRole": "PROTECTOR",
    "title": "치매 환자 식사 도움 팁",
    "content": "...",
    "category": "TIP",
    "imageUrl": null,
    "likeCount": 5,
    "likedByMe": false,
    "createdAt": "2026-05-30T10:00:00",
    "updatedAt": "2026-05-30T10:00:00"
  }
]
```

---

### 게시글 상세 조회
```
GET /posts/{postId}
인증: 필요
```
**Response** `200 OK` — 게시글 상세

---

### 게시글 수정
```
PUT /posts/{postId}
인증: 필요 (작성자만)
Content-Type: multipart/form-data
```
**Response** `200 OK`

---

### 게시글 삭제
```
DELETE /posts/{postId}
인증: 필요 (작성자만)
```
**Response** `200 OK`

---

### 게시글 좋아요 토글
```
POST /posts/{postId}/likes
인증: 필요
```
**Response** `200 OK`
```json
{ "liked": true }
```
> 이미 좋아요 누른 상태면 취소, `liked: false` 반환

---

## 8. 커뮤니티 - 댓글 (Comment)

### 댓글 작성
```
POST /posts/{postId}/comments
인증: 필요
```
**Request Body**
```json
{ "content": "좋은 정보 감사합니다!" }
```
**Response** `200 OK`
```json
{ "commentId": 1 }
```

---

### 댓글 목록 조회
```
GET /posts/{postId}/comments
인증: 필요
```
**Response** `200 OK` — 댓글 배열 (등록 순)

---

### 댓글 수정
```
PUT /comments/{commentId}
인증: 필요 (작성자만)
```
**Request Body**
```json
{ "content": "수정된 댓글입니다." }
```
**Response** `200 OK`

---

### 댓글 삭제
```
DELETE /comments/{commentId}
인증: 필요 (작성자만)
```
**Response** `200 OK`

---

### 댓글 채택
```
POST /posts/{postId}/comments/{commentId}/adopt
인증: 필요 (게시글 작성자만)
```
> 게시글당 1개만 채택 가능, 채택 시 댓글 작성자에게 내공점수 +10

**Response** `200 OK`

---

### 댓글 채택 취소
```
DELETE /posts/{postId}/comments/{commentId}/adopt
인증: 필요 (게시글 작성자만)
```
> 채택 취소 시 내공점수 -10

**Response** `200 OK`

---

## 9. AI 케어 조언 (Care Advice)

### 세션 생성
```
POST /patients/{patientId}/care-advice/sessions
인증: 필요
```
**Response** `200 OK`
```json
{ "sessionId": 1, "createdAt": "2026-05-30T10:00:00" }
```

---

### 메시지 전송 (AI 답변 요청)
```
POST /care-advice/sessions/{sessionId}/messages
인증: 필요
```
**Request Body**
```json
{ "content": "치매 환자가 밥을 안 먹으려 할 때 어떻게 해야 하나요?" }
```
**Response** `200 OK`
```json
{
  "userMessage": {
    "role": "USER",
    "content": "치매 환자가 밥을 안 먹으려 할 때 어떻게 해야 하나요?",
    "createdAt": "2026-05-30T10:00:00"
  },
  "assistantMessage": {
    "role": "ASSISTANT",
    "content": "환자분의 상황을 고려할 때...",
    "createdAt": "2026-05-30T10:00:01"
  }
}
```

---

### 세션 목록 조회
```
GET /patients/{patientId}/care-advice/sessions
인증: 필요
```
**Response** `200 OK` — 세션 배열 (최신순)

---

### 메시지 목록 조회
```
GET /care-advice/sessions/{sessionId}/messages
인증: 필요 (세션 생성자만)
```
**Response** `200 OK` — 메시지 배열 (시간순)

---

### 세션 삭제
```
DELETE /care-advice/sessions/{sessionId}
인증: 필요 (세션 생성자만)
```
**Response** `200 OK`

---

## 10. IoT 디바이스 (Device)

### 디바이스 등록
```
POST /devices
인증: 불필요
```
**Request Body**
```json
{ "deviceSerial": "ESP32-001" }
```
**Response** `200 OK`

---

### 음성 기록 업로드
```
POST /devices/{deviceSerial}/voice
인증: 불필요
```
**Request Body**
```json
{ "audioUrl": "https://s3.amazonaws.com/piuda/voice/record.mp3" }
```
**Response** `200 OK`
