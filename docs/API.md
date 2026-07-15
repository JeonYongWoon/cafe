# jeonscafe REST API 상세 명세서

본 문서는 커피숍 주문 시스템(jeonscafe)에서 사용하는 REST API 인터페이스 설계 규격서입니다. 모든 API는 컨벤션에 따른 공통 JSON 응답 포맷을 준수합니다.

## 식별자 매핑 규칙 가이드
* 데이터베이스 테이블 설계상 기본키는 모두 id 컬럼으로 통일되어 있습니다.
* 단, API JSON 응답 내에서는 각 도메인의 유일 식별을 직관적으로 구분하기 위해 menuId, memberId, orderId, pointHistoryId 등으로 명시하여 반환합니다.

---

## 1. 커피 메뉴 목록 조회 API
* Endpoint: GET /menus
* 설명: 판매 가능 상태(AVAILABLE, SOLD_OUT)인 커피 메뉴의 목록을 조회합니다. (단종된 DISCONTINUED 상태의 메뉴는 노출되지 않습니다.)

### 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": [
    {
      "menuId": 1,
      "name": "아메리카노",
      "price": 4500,
      "status": "AVAILABLE",
      "imageUrl": "/images/americano.png"
    },
    {
      "menuId": 2,
      "name": "카페라떼",
      "price": 5000,
      "status": "SOLD_OUT",
      "imageUrl": "/images/cafelatte.png"
    }
  ],
  "error": null
}
```

---

## 2. 포인트 충전하기 API
* Endpoint: POST /points/charge
* 설명: 특정 사용자의 포인트를 충전합니다. 충전 완료 후 갱신된 잔여 포인트와 이력 식별키를 리턴합니다.

### 요청 JSON 바디
```json
{
  "memberId": 1,
  "amount": 10000
}
```

### 성공 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "pointBalance": 14500,
    "pointHistoryId": 1001
  },
  "error": null
}
```

### 실패 응답 예시 (HTTP Status: 400 Bad Request)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_CHARGE_AMOUNT",
    "message": "충전 금액은 최소 1,000원 이상이어야 합니다."
  }
}
```

---

## 3. 커피 주문 및 결제 API
* Endpoint: POST /orders
* 설명: 장바구니에 담긴 커피 상품들을 포인트 결제하고 주문을 접수합니다. 회원 포인트 잔액 검증(낙관적 락 체크)을 수반하며, 결제 완료 시 외부 플랫폼으로 실시간 데이터를 전송합니다.

### 요청 JSON 바디
```json
{
  "memberId": 1,
  "items": [
    {
      "menuId": 1,
      "temperature": "ICE",
      "quantity": 2
    },
    {
      "menuId": 2,
      "temperature": "HOT",
      "quantity": 1
    }
  ]
}
```

### 성공 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": {
    "orderId": 10023,
    "totalPrice": 14000,
    "status": "RECEIVED",
    "createdAt": "2026-07-13T16:20:00",
    "orderItems": [
      {
        "menuId": 1,
        "temperature": "ICE",
        "quantity": 2,
        "price": 4500
      },
      {
        "menuId": 2,
        "temperature": "HOT",
        "quantity": 1,
        "price": 5000
      }
    ]
  },
  "error": null
}
```

### 실패 응답 예시 (HTTP Status: 400 Bad Request)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INSUFFICIENT_POINT",
    "message": "보유 포인트 잔액이 부족하여 결제에 실패했습니다."
  }
}
```

---

## 4. 인기 메뉴 목록 조회 API
* Endpoint: GET /menus/popular?days=7
* 쿼리 파라미터: days (조회 기간 기준 일수, 기본값 7)
* 설명: 최근 7일(또는 days 파라미터 값 기준) 동안 주문 횟수가 가장 높은 상위 3가지 인기 메뉴와 누적 주문 건수를 반환합니다.

### 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": [
    {
      "menuId": 1,
      "name": "아메리카노",
      "price": 4500,
      "orderCount": 420
    },
    {
      "menuId": 3,
      "name": "돌체라떼",
      "price": 5500,
      "orderCount": 310
    },
    {
      "menuId": 2,
      "name": "카페라떼",
      "price": 5000,
      "orderCount": 280
    }
  ],
  "error": null
}
```

---

## 5. 회원가입 API
* Endpoint: POST /members
* 설명: 새로운 사용자를 등록합니다.

### 요청 JSON 바디
```json
{
  "username": "user123",
  "password": "securepassword"
}
```

### 성공 응답 예시 (HTTP Status: 201 Created)
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "username": "user123",
    "createdAt": "2026-07-13T16:30:00"
  },
  "error": null
}
```

### 실패 응답 예시 (HTTP Status: 400 Bad Request)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "DUPLICATE_USERNAME",
    "message": "이미 사용 중인 아이디입니다."
  }
}
```

---

## 6. 로그인 API
* Endpoint: POST /sessions
* 설명: 사용자 계정 및 비밀번호를 검증하여 인증 세션을 수립합니다.

### 요청 JSON 바디
```json
{
  "username": "user123",
  "password": "securepassword"
}
```

### 성공 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "username": "user123"
  },
  "error": null
}
```

### 실패 응답 예시 (HTTP Status: 400 Bad Request)
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "아이디 또는 비밀번호가 일치하지 않습니다."
  }
}
```

---

## 7. 회원 상세 조회 API
* Endpoint: GET /members/{memberId}
* 설명: 특정 회원의 상세 정보와 실시간 잔여 포인트를 조회합니다.

### 성공 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": {
    "memberId": 1,
    "username": "user123",
    "pointBalance": 10000
  },
  "error": null
}
```

---

## 8. 관리자 주문 목록 조회 API
* Endpoint: GET /admin/orders
* 설명: 관리자가 현재 접수된 모든 고객의 주문 목록을 최신순으로 조회합니다.

### 성공 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": [
    {
      "orderId": 10023,
      "username": "user123",
      "totalPrice": 14000,
      "status": "RECEIVED",
      "createdAt": "2026-07-13T16:20:00"
    }
  ],
  "error": null
}
```

---

## 9. 주문 상태 변경 API
* Endpoint: PATCH /orders/{orderId}/status
* 설명: 관리자가 주문의 제조 상태를 변경 처리합니다.
* 허용 가능한 상태값 범위:
  - RECEIVED (주문접수)
  - PREPARING (제조중)
  - READY_FOR_PICKUP (픽업대기)
  - COMPLETED (주문완료)

### 요청 JSON 바디
```json
{
  "status": "PREPARING"
}
```

### 성공 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": {
    "orderId": 10023,
    "status": "PREPARING",
    "updatedAt": "2026-07-13T16:32:00"
  },
  "error": null
}
```

---

## 10. 주문 상세 단건 조회 API
* Endpoint: GET /orders/{orderId}
* 설명: 특정 주문의 상세 결제 내역 및 주문 상품 목록을 조회합니다.

### 성공 응답 예시 (HTTP Status: 200 OK)
```json
{
  "success": true,
  "data": {
    "orderId": 10023,
    "memberId": 1,
    "totalPrice": 14000,
    "status": "RECEIVED",
    "createdAt": "2026-07-13T16:20:00",
    "orderItems": [
      {
        "menuId": 1,
        "name": "아메리카노",
        "temperature": "ICE",
        "quantity": 2,
        "price": 4500
      },
      {
        "menuId": 2,
        "name": "카페라떼",
        "temperature": "HOT",
        "quantity": 1,
        "price": 5000
      }
    ]
  },
  "error": null
}
```

### 실패 응답 예시 (HTTP Status: 400 Bad Request, 403 Forbidden)
```json
// 주문을 찾을 수 없는 경우
{
  "success": false,
  "data": null,
  "error": {
    "code": "ORDER_NOT_FOUND",
    "message": "해당 주문 내역을 찾을 수 없습니다."
  }
}

// 본인의 주문 내역이 아닌 경우
{
  "success": false,
  "data": null,
  "error": {
    "code": "UNAUTHORIZED_ORDER_ACCESS",
    "message": "본인의 주문 내역만 조회할 수 있습니다."
  }
}
```
