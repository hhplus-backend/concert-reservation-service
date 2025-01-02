# API 명세서

## 유저 토큰 발급 API

### 기본정보

| 메서드  | endpoint         |
|------|------------------|
| POST | /api/queue-token |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |

#### 본문

| 이름     | 타입     | 설명              | 필수 |
|--------|--------|-----------------|----|
| userId | string | 토큰을 발급받을 userId | O  |

### 응답

#### 본문

| 이름         | 타입      | 설명      | 필수 |
|------------|---------|---------|----|
| success    | boolean | 응답성공여부  |    |
| code       | string  | 응답코드    | O  |
| message    | string  | 응답코드메시지 | O  |
| timestamp  | string  | 응답일시    | O  |
| data       | json    | 응답데이터   | O  |
| queueToken | string  | 발급받은 토큰 | O  |

### 예제

#### 요청

```bash
curl --location --request POST 'localhost:8080/api/queue-token' \
--header 'Content-Type: application/json' \
--data '{
    "userId": "${userId}"
}'
```

#### 응답: 성공

```http request
HTTP/1.1 201 Created
Content-Type: application/json

{
    "success": true,
    "code": "SUCCESS_01",
    "message": "Success",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": {
      "queueToken": "abc123"
    }
}
```

#### 응답: 실패

- 유효하지 않은 요청

```http request
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid request data"
}
```

- 유효하지 않은 user id

```http request
HTTP/1.1 404 Notfound
Content-Type: application/json

{
    "code": "USER_ERROR_01",
    "message": "User not found",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "User not found"
}
```

- 토큰 발행 과정에서 에러 발생

```http request
HTTP/1.1 500 Internel server error
Content-Type: application/json

{
    "code": "TOKEN_ERROR_01",
    "message": "Token issue fail",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "toke issue fail"
}
```

## 유저 토큰 조회 API

### 기본정보

| 메서드 | endpoint         |
|-----|------------------|
| GET | /api/queue-token |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

### 응답

#### 본문

| 이름        | 타입      | 설명                    | 필수 |
|-----------|---------|-----------------------|----|
| success   | boolean | 응답성공여부                |    |
| code      | string  | 응답코드                  | O  |
| message   | string  | 응답코드메시지               | O  |
| timestamp | string  | 응답일시                  | O  |
| data      | json    | 응답데이터                 | O  |
| status    | string  | 활성화상태(ACTIVE,WAITING) | O  |

### 예제

#### 요청

```bash
curl --location --request GET 'localhost:8080/api/queue-token' \
--header 'USER-TOKEN: Bearer ${USER_TOKEN}'
```

#### 응답: 성공

```http request
HTTP/1.1 201 Created
Content-Type: application/json

{
    "success": true,
    "code": "SUCCESS_01",
    "message": "Success",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": {
      "status": "ACTIVE"
    }
}
```

#### 응답: 실패

- 유효하지 않은 토큰

```http request
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "user token is invalid or expired"
}
```

- 토큰 발행 과정에서 에러 발생

```http request
HTTP/1.1 500 Internel server error
Content-Type: application/json

{
    "code": "TOKEN_ERROR_01",
    "message": "TokenError",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "fetch token failed"
}
```

## 콘서트 목록 조회 API

### 기본정보

| 메서드 | endpoint     |
|-----|--------------|
| GET | /api/concert |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

### 응답

#### 본문

| 이름        | 타입      | 설명      | 필수 |
|-----------|---------|---------|----|
| success   | boolean | 응답성공여부  |    |
| code      | string  | 응답코드    | O  |
| message   | string  | 응답코드메시지 | O  |
| timestamp | string  | 응답일시    | O  |
| data      | json    | 응답데이터   | O  |
| concertId | Long    | 콘서트 ID  | O  |
| title     | String  | 콘서트 제목  | O  |

#### 요청

```bash
curl --location --request GET 'localhost:8080/api/concerts' \
--header 'USER-TOKEN: Bearer valid-token-1'
```

#### 응답: 성공

```http request
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "code": "SUCCESS_01",
  "message": "Success",
  "timestamp": "2025-01-03T10:15:00.000Z",
  "data": [
    {
      "concertId": 1,
      "title": "A 콘서트"
    },
    {
      "concertId": 2,
      "title": "B 콘서트"
    }
  ]
}
```

#### 응답: 실패

- 유효하지 않은 USER-TOKEN

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid or missing USER-TOKEN header"
}
```

## 특정 콘서트 조회 API

### 기본정보

| 메서드 | endpoint                 |
|-----|--------------------------|
| GET | /api/concert/{concertId} |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

#### 경로 변수

| 이름        | 타입     | 설명    | 필수 |
|-----------|--------|-------|----|
| concertId | number | 콘서트ID | O  |

### 응답

#### 본문

| 이름        | 타입      | 설명      | 필수 |
|-----------|---------|---------|----|
| success   | boolean | 응답성공여부  |    |
| code      | string  | 응답코드    | O  |
| message   | string  | 응답코드메시지 | O  |
| timestamp | string  | 응답일시    | O  |
| data      | json    | 응답데이터   | O  |
| concertId | Long    | 콘서트 ID  | O  |
| title     | String  | 콘서트 제목  | O  |

### 예제

#### 요청

```bash
curl --location --request GET 'localhost:8080/api/concerts/1' \
--header 'USER-TOKEN: Bearer valid-token-1'
```

#### 응답: 성공

```http request
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "code": "SUCCESS_01",
  "message": "Success",
  "timestamp": "2025-01-03T10:15:00.000Z",
  "data": {
    "concertId": 1,
    "title": "A 콘서트"
  }
}
```

#### 응답: 실패

- 유효하지 않은 USER-TOKEN

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid or missing USER-TOKEN header"
}
```

#### 응답: 실패

- 존재하지 않는 concertId

```http request
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "success": false,
  "code": "CONCERT_ERROR_01",
  "message": "Concert not found",
  "data": "Concert not found"
}
```

## 콘서트 스케줄 조회 API

### 기본정보

| 메서드 | endpoint                           |
|-----|------------------------------------|
| GET | /api/concert/{concertId}/schedules |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

#### 경로 변수

| 이름        | 타입     | 설명    | 필수 |
|-----------|--------|-------|----|
| concertId | number | 콘서트ID | O  |

### 응답

#### 본문

| 이름                       | 타입      | 설명          | 필수 |
|--------------------------|---------|-------------|----|
| success                  | boolean | 응답성공여부      |    |
| code                     | string  | 응답코드        | O  |
| message                  | string  | 응답코드메시지     | O  |
| timestamp                | string  | 응답일시        | O  |
| data                     | json    | 응답데이터       | O  |
| concertId                | Long    | 콘서트 아이디     | O  |
| title                    | String  | 콘서트 제목      | O  |
| schedules                | 배열      | 콘서트 스케줄 리스트 | O  |
| └─ scheduleId            | Long    | 스케줄 아이디     | O  |
| └─ performanceDate       | String  | 공연 날짜       | O  |
| └─ startTime             | String  | 공연 시작 시간    | O  |
| └─ endTime               | String  | 공연 종료 시간    | O  |
| └─ place                 | json    | 장소 정보       | O  |
| └─ └─ name               | String  | 장소명         | O  |
| └─ └─ availableSeatCount | int     | 남은 좌석 수     | O  |

### 예제

#### 요청

```bash
curl --location --request GET 'localhost:8080/api/concert/1/schedules' \
--header 'USER-TOKEN: Bearer valid-token-1'
```

#### 응답: 성공

```http request
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "code": "SUCCESS_01",
  "message": "Success",
  "timestamp": "2025-01-03T10:15:00.000Z",
  "data": {
    "concertId": 1,
    "title": "A 콘서트",
    "schedules": [
      {
        "scheduleId": 101,
        "performanceDate": "2025-01-01",
        "startTime": "18:00",
        "endTime": "20:00",
        "place": {
          "name": "LG아트센터",
          "availableSeatCount": 500
        }
      },
      {
        "scheduleId": 102,
        "performanceDate": "2025-01-02",
        "startTime": "19:00",
        "endTime": "21:00",
        "place": {
          "name": "우리금융아트홀",
          "availableSeatCount": 450
        }
      }
    ]
  }
}
```

#### 응답: 실패

- 유효하지 않은 USER-TOKEN

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid or missing USER-TOKEN header"
}
```

- 존재하지 않는 concertId

```http request
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "success": false,
  "code": "CONCERT_NOT_FOUND",
  "message": "Concert not found",
  "data": null
}
```

## 예약 가능 날짜/좌석 조회 API

### 기본정보

| 메서드 | endpoint                                              |
|-----|-------------------------------------------------------|
| GET | /api/concert/{concertId}/schedules/{scheduleId}/seats |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

#### 경로 변수

| 이름         | 타입     | 설명    | 필수 |
|------------|--------|-------|----|
| concertId  | number | 콘서트ID | O  |
| scheduleId | number | 스케쥴ID | O  |

#### 쿼리 파라미터

| 이름   | 타입     | 설명               | 필수 |
|------|--------|------------------|----|
| date | string | 콘서트일(YYYY-MM-DD) | O  |

### 응답

#### 본문

| 이름        | 타입           | 설명        | 필수 |
|-----------|--------------|-----------|----|
| success   | boolean      | 응답성공여부    |    |
| code      | string       | 응답코드      | O  |
| message   | string       | 응답코드메시지   | O  |
| timestamp | string       | 응답일시      | O  |
| data      | json         | 응답데이터     | O  |
| available | boolean      | 예약 가능 여부  | O  |
| seats     | list(number) | 좌석번호 list | O  |

### 예제

#### 요청

```bash
curl --location --request GET 'localhost:8080/api/concert/{concertId}/schedules/{scheduleId}/seats?date={date}' \
--header 'USER-TOKEN: ${USER_TOKEN}' \
--data ''
```

#### 응답: 성공

```http request
HTTP/1.1 200 Ok
Content-Type: application/json

{
    "success": true,
    "code": "SUCCESS_01",
    "message": "Success",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": {
        "available": true,
        "seats": [1, 2, 3, 4, 5]
    }
}
```

#### 응답: 실패

- 유효하지 않은 USER-TOKEN

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid or missing USER-TOKEN header"
}
```

- 유효하지 않은 concertId 또는 scheduleId

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid concertId or scheduleId"
}
``` 

- 데이터 조회 실패

```http request
HTTP/1.1 500 Internel server error
Content-Type: application/json

{
    "code": "SEAT_ERROR_01",
    "message": "SeatError",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "fetch concert seat is occurred error"
}
```

## 좌석 예약 요청 API

### 기본정보

| 메서드  | endpoint                                                                 |
|------|--------------------------------------------------------------------------|
| POST | /concert/{concertId}/schedules/{scheduleId}/reservations/{reservationId} |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

#### 경로 변수

| 이름            | 타입     | 설명    | 필수 |
|---------------|--------|-------|----|
| concertId     | number | 콘서트ID | O  |
| scheduleId    | number | 스케쥴ID | O  |
| reservationId | number | 예약ID  | O  |

#### 본문

| 이름     | 타입     | 설명   | 필수 |
|--------|--------|------|----|
| seatNo | number | 좌석번호 | O  |

### 응답

#### 본문

| 이름            | 타입      | 설명      | 필수 |
|---------------|---------|---------|----|
| success       | boolean | 응답성공여부  |    |
| code          | string  | 응답코드    | O  |
| message       | string  | 응답코드메시지 | O  |
| timestamp     | string  | 응답일시    | O  |
| data          | json    | 응답데이터   | O  |
| reservationId | number  | 예약ID    | O  |
| seatNo        | number  | 좌석번호    | O  |

### 예제

#### 요청

```bash
curl --location --request POST 'localhost:8080/api/concert/{concertId}/schedules/{scheduleId}/reservations/{reservationId}' \
--header 'USER-TOKEN: Bearer ${USER_TOKEN}' \
--header 'Content-Type: application/json' \
--data '{
    "seatNo": ${seatNo}
}'
```

#### 응답: 성공

```http request
HTTP/1.1 201 Created
Content-Type: application/json

{
    "success": true,
    "code": "SUCCESS_01",
    "message": "Success",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": {
        "reservationId": 1,
        "seatNo": 1
    }
}
```

#### 응답: 실패

- 유효하지 않은 USER-TOKEN

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid or missing USER-TOKEN header"
}
```

- 유효하지 않은 concertId 또는 scheduleId

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid concertId or scheduleId"
}
``` 

- 유효하지 않은 좌석 번호

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid seatNo"
}
``` 

```http request
HTTP/1.1 500 Internel server error
Content-Type: application/json

{
    "code": "RESERVATION_ERROR_01",
    "message": "ReservationError",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "reservation failed"
}
```

## 잔액 충전 API

### 기본정보

| 메서드  | endpoint     |
|------|--------------|
| POST | /api/balance |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

#### 본문

| 이름     | 타입     | 설명     | 필수 |
|--------|--------|--------|----|
| amount | number | 충전할 금액 | O  |

### 응답

#### 본문

| 이름        | 타입      | 설명      | 필수 |
|-----------|---------|---------|----|
| success   | boolean | 응답성공여부  |    |
| code      | string  | 응답코드    | O  |
| message   | string  | 응답코드메시지 | O  |
| timestamp | string  | 응답일시    | O  |
| data      | json    | 응답데이터   | O  |
| amount    | number  | 충전 후 잔고 | O  |

### 예제

#### 요청

```bash
curl --location --request POST 'localhost:8080/api/balance' \
--header 'USER-TOKEN: ${USER_TOKEN}' \
--header 'Content-Type: application/json' \
--data '{
    "amount": ${amount}
}'
```

#### 응답: 성공

```http request
HTTP/1.1 200 Ok
Content-Type: application/json

{
    "success": true,
    "code": "SUCCESS_01",
    "message": "Success",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": {
        "balance": 20000
    }
}
```

#### 응답: 실패

- 유효하지 않은 USER-TOKEN

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid or missing USER-TOKEN header"
}
```

- 유효하지 않은 금액

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid balance request data"
}
```

- 충전 실패

```http request
HTTP/1.1 500 Internel server error
Content-Type: application/json

{
    "code": "BALANCE_ERROR_02",
    "message": "BalanceChargeError",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "balance charge is failed"
}
```

## 잔액 조회 API

### 기본정보

| 메서드 | endpoint     |
|-----|--------------|
| GET | /api/balance |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

### 응답

#### 본문

| 이름        | 타입      | 설명      | 필수 |
|-----------|---------|---------|----|
| success   | boolean | 응답성공여부  |    |
| code      | string  | 응답코드    | O  |
| message   | string  | 응답코드메시지 | O  |
| timestamp | string  | 응답일시    | O  |
| data      | json    | 응답데이터   | O  |
| amount    | number  | 잔고      | O  |

### 예제

#### 요청

```bash
curl --location --request GET 'localhost:8080/api/balance' \
--header 'USER-TOKEN: ${USER_TOKEN}' \
--data ''
```

#### 응답: 성공

```http request
HTTP/1.1 200 Ok
Content-Type: application/json

{
    "success": true,
    "code": "SUCCESS_01",
    "message": "Success",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": {
        "balance": 15000
    }
}
```

#### 응답: 실패

- 유효하지 않은 USER-TOKEN

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid or missing USER-TOKEN header"
}
```

- 잔액 조회중 에러 발생

```http request
HTTP/1.1 500 Internel server error
Content-Type: application/json

{
    "code": "BALANCE_ERROR_01",
    "message": "BalanceError",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "fetch balance is failed"
}
```

## 결제 API

### 기본정보

| 메서드  | endpoint     |
|------|--------------|
| POST | /api/payment |

### 요청

#### 헤더

| 이름           | 설명               | 필수 |
|--------------|------------------|----|
| Content-Type | application/json | O  |
| USER-TOKEN   | Bearer 토큰 인증     | O  |

#### 본문

| 이름            | 타입     | 설명   | 필수 |
|---------------|--------|------|----|
| reservationId | number | 예약ID | O  |

### 응답

#### 본문

| 이름        | 타입      | 설명         | 필수 |
|-----------|---------|------------|----|
| success   | boolean | 응답성공여부     |    |
| code      | string  | 응답코드       | O  |
| message   | string  | 응답코드메시지    | O  |
| timestamp | string  | 응답일시       | O  |
| data      | json    | 응답데이터      | O  |
| status    | string  | 충전성공/실패 여부 | O  |

### 예제

#### 요청

```bash
curl --location --request POST 'localhost:8080/api/payment' \
--header 'USER-TOKEN: Bearer ${USER_TOKEN}' \
--header 'Content-Type: application/json' \
--data '{
    "reservationId": ${reservationId}
}'
```

#### 응답: 성공

```http request
HTTP/1.1 200 Ok
Content-Type: application/json

{
    "success": true,
    "code": "SUCCESS_01",
    "message": "Success",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": {
        "status": "SUCCESS"
    }
}
```

#### 응답: 실패

- 유효하지 않은 요청

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Invalid request data"
}
```

- 결제 자금 부족

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Insufficient funds for payment"
}
```

- 예약 ID invalid

```http request
HTTP/1.1 400 Bad request
Content-Type: application/json

{
    "code": "FAIL_01",
    "message": "Request is invalid",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "Insufficient funds for payment"
}
```

- 결제 중 오류

```http request
HTTP/1.1 500 Internel server error
Content-Type: application/json

{
    "code": "PAYMENT_ERROR_01",
    "message": "PaymentError",
    "timestamp": "2024-12-31T01:45:40.667Z",
    "data": "payment is failed"
}
```
