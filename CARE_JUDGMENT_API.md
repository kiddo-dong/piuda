# 케어 판단 기록 API (프론트 연동 문서)

간병인이 보호자의 사전 지시 없이 내린 단독 판단을 **상황·판단·근거**로 남기는 보관소.

- **저장 기준**: 환자(patient)에 종속
- **작성**: 간병인(CAREGIVER)만
- **조회**: 해당 환자에 연결된 사용자 전원 (보호자·간병인·의료진)
- **수정·삭제**: 작성한 간병인 본인만
- **즉시공유(IMMEDIATE)**: 작성 시 보호자에게 FCM 푸시 발송

> 모든 요청 공통 헤더: `Authorization: Bearer <accessToken>`
> base: `/api/v1/patients/{patientId}/care-judgments`

---

## 1. 판단 기록 작성

```
POST /api/v1/patients/{patientId}/care-judgments
Content-Type: application/json
```

**Request Body**
```json
{
  "category": "MEAL",
  "situation": "식사를 거부하셨어요",
  "action": "죽으로 바꿔서 드렸습니다",
  "rationale": "치아 상태가 안 좋아 보여서요",
  "urgency": "IMMEDIATE"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `category` | enum | ✅ | 상황 카테고리 (아래 표) |
| `situation` | string(≤500) | ✅ | 어떤 상황이었나요 |
| `action` | string(≤500) | ✅ | 어떻게 했나요 (판단) |
| `rationale` | string(≤500) | ✅ | 왜 그렇게 했나요 (근거) |
| `urgency` | enum | ✅ | 공유 긴급도 (아래 표) |

**Response** `200 OK`
```json
5
```
→ 생성된 기록 ID (Long)

---

## 2. 보관소 조회 (목록)

```
GET /api/v1/patients/{patientId}/care-judgments
GET /api/v1/patients/{patientId}/care-judgments?category=MEAL
```
- `category` 쿼리파라미터로 필터링 (미지정 시 전체)
- 최신순 정렬

**Response** `200 OK`
```json
{
  "immediateShareCount": 1,
  "logs": [
    {
      "id": 5,
      "patientId": 1,
      "writerName": "김간병",
      "category": "MEAL",
      "situation": "식사를 거부하셨어요",
      "action": "죽으로 바꿔서 드렸습니다",
      "rationale": "치아 상태가 안 좋아 보여서요",
      "urgency": "IMMEDIATE",
      "createdAt": "2026-06-30T14:20:00",
      "updatedAt": null
    }
  ]
}
```
> `immediateShareCount`: 카테고리 필터와 무관하게 **환자 전체 기준 즉시공유(IMMEDIATE) 건수**. 목업의 "🔔 즉시공유 N건" 표기에 사용.

---

## 3. 단건 조회

```
GET /api/v1/patients/{patientId}/care-judgments/{logId}
```
**Response** `200 OK` — 위 `logs[]` 항목과 동일한 단일 객체

---

## 4. 수정 (작성자 본인만)

```
PUT /api/v1/patients/{patientId}/care-judgments/{logId}
Content-Type: application/json
```
Request Body는 **작성(POST)과 동일**.

**Response** `200 OK`

---

## 5. 삭제 (작성자 본인만)

```
DELETE /api/v1/patients/{patientId}/care-judgments/{logId}
```
**Response** `200 OK`

---

## Enum 목록

### category (JudgmentCategory)
| 값 | 의미 |
|----|------|
| `MEAL` | 식사 |
| `MOVEMENT` | 이동 |
| `MEDICATION` | 투약 |
| `HYGIENE` | 위생·배변 |
| `SLEEP` | 수면·야간 |
| `BEHAVIOR` | 정서·행동 |
| `SAFETY` | 안전·낙상 |
| `ETC` | 기타 |

### urgency (UrgencyLevel)
| 값 | 의미 | 동작 |
|----|------|------|
| `NORMAL` | 일반 | 기록만 |
| `NEEDS_OBSERVATION` | 관찰필요 | 기록만 |
| `IMMEDIATE` | 즉시공유 | **보호자에게 FCM 푸시** |

> ⚠️ enum 값은 **정확히 대문자 문자열**로 보내야 합니다. (`"MEAL"`, `"IMMEDIATE"` 등)

---

## 에러 응답 (공통 포맷)
```json
{ "message": "..." }
```

| 상황 | HTTP | message |
|------|------|---------|
| 간병인이 아님 (작성) | `403` | "케어 판단 기록은 간병인만 작성할 수 있습니다." |
| 환자에 연결 안 됨 | `403` | "해당 환자에 대한 접근 권한이 없습니다." |
| 작성자 본인 아님 (수정/삭제) | `403` | "본인이 작성한 기록만 수정·삭제할 수 있습니다." |
| 다른 환자의 기록 | `403` | "해당 환자의 판단 기록이 아닙니다." |
| 없는 기록/환자/사용자 | `404` | "존재하지 않는 판단 기록입니다." 등 |
| 필드 누락/형식 오류 | `400` | "어떤 상황이었는지 입력해주세요." 등 |

---

## 캘린더 통합 조회 (참고)

판단 기록은 **캘린더 조회 API에도 함께 반환**됩니다 (저장은 분리, 조회만 통합).

```
GET /api/v1/patients/{patientId}/calendars
```
```json
{
  "calendars":    [ { ...수동 일정 } ],
  "judgmentLogs": [ { ...판단 기록 (위와 동일한 객체) } ]
}
```
→ `judgmentLogs`의 각 항목을 `createdAt` 날짜에 마커로 표시하면 됩니다.

---

## 권장 화면 흐름

**간병인 (작성)**
```
카테고리 선택 → 상황/판단/근거 입력 → 긴급도 선택 → POST
  → 200 (생성된 id) → 목록 갱신
```

**보호자 (보관소)**
```
GET (전체 또는 category 필터)
  → immediateShareCount로 "🔔 즉시공유 N건" 배지
  → logs를 카드 리스트로 (카테고리 배지 + createdAt + situation/action + 이유(rationale))
```

---

## 필수 체크리스트 (저장 안 될 때 확인)
1. **간병인(CAREGIVER) 계정으로 로그인** 했는가 → 아니면 403
2. 그 간병인이 **해당 환자에 연결(초대코드 합류)** 돼 있는가 → 아니면 403
3. `category` / `urgency` 값이 **정확한 대문자 enum** 인가 → 아니면 400
4. `situation` / `action` / `rationale` 이 **빈 값이 아닌가** → 아니면 400
5. `Content-Type: application/json` 헤더 + **JSON body**로 보냈는가
