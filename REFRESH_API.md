# 액세스 토큰 재발급 API (프론트 연동 문서)

## 개요

액세스 토큰(30분 만료)이 만료되면, 리프레시 토큰(14일 만료)으로 새 토큰을 재발급받습니다.
**Token Rotation** 방식이라 재발급 시 `accessToken`과 `refreshToken` **둘 다 새로 발급**됩니다.
→ 응답으로 받은 두 토큰을 **모두 저장(기존 값 덮어쓰기)** 해야 합니다.

---

## 엔드포인트

```
POST /api/v1/users/refresh
```

- 인증 불필요 (Authorization 헤더 없이 호출)
- **반드시 JSON body로 전송** (쿼리파라미터 ❌)

### Request

```http
POST /api/v1/users/refresh
Content-Type: application/json

{
  "refreshToken": "저장해둔 리프레시 토큰 값"
}
```

> ⚠️ 다음 3가지를 반드시 지킬 것
> 1. `Content-Type: application/json` 헤더 필수
> 2. body에 `{ "refreshToken": "..." }` 담기
> 3. `?refreshToken=...` 쿼리파라미터로 보내지 말 것 (이 경우 `Required request body is missing` 에러)

### Response 200 (성공)

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "a1b2c3d4-...-새로운-리프레시-토큰"
}
```

→ `accessToken`, `refreshToken` **둘 다 새 값으로 저장**.

### 에러 응답

| 상황 | HTTP | message |
|------|------|---------|
| body 누락 / 형식 오류 | `400` | "요청 본문(JSON)이 없거나 형식이 올바르지 않습니다." |
| refreshToken 빈 값 | `400` | "리프레시 토큰을 입력해주세요." |
| 유효하지 않은 토큰 | `403` | "유효하지 않은 리프레시 토큰입니다." |
| 만료된 토큰 | `403` | "리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요." |

> `403`이 오면 리프레시 토큰도 만료/무효라는 뜻 → **저장된 토큰 삭제 후 로그인 화면으로 이동**.

---

## 권장 처리 흐름

```
API 요청 중 401(액세스 토큰 만료) 발생
  → POST /api/v1/users/refresh (저장된 refreshToken 으로)
     ├ 200: 새 accessToken/refreshToken 저장 → 원래 요청 재시도
     └ 403: 토큰 모두 삭제 → 로그인 화면 이동
```

### 예시 (axios interceptor)

```javascript
axios.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const { data } = await axios.post('/api/v1/users/refresh', {
          refreshToken: getRefreshToken(),          // 저장된 값
        });                                          // Content-Type: application/json 자동
        saveTokens(data.accessToken, data.refreshToken); // 둘 다 저장
        original.headers.Authorization = `Bearer ${data.accessToken}`;
        return axios(original);                      // 원래 요청 재시도
      } catch (e) {
        clearTokens();
        redirectToLogin();
        return Promise.reject(e);
      }
    }
    return Promise.reject(error);
  }
);
```

---

## 참고: 토큰 수명

| 토큰 | 만료 | 저장 위치 권장 |
|------|------|----------------|
| accessToken | 30분 | 메모리 / 헤더 |
| refreshToken | 14일 | 안전한 저장소 (Secure Storage 등) |
