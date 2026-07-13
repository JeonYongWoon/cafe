# 📊 jeonscafe 데이터베이스 ERD 설계서

본 문서는 커피숍 주문 시스템(`jeonscafe`)의 데이터 모델링 결과를 보여주는 ERD 설계서입니다. 모든 테이블명은 프로젝트 컨벤션에 따라 **복수형 명사**로 정의되었습니다.

---

## 1. 🗺️ ERD (Entity Relationship Diagram)

```mermaid
erDiagram
    members ||--o{ orders : "places"
    members ||--o{ point_histories : "has"
    orders ||--|{ order_items : "contains"
    menus ||--o{ order_items : "is_ordered_in"
    orders ||--o| point_histories : "linked_to"

    members {
        bigint id PK "회원 식별값"
        varchar username "회원 로그인 아이디"
        varchar password "회원 비밀번호"
        bigint point_balance "회원 잔여 포인트"
        bigint version "낙관적 락 데이터 버전"
        datetime created_at "가입 일시"
    }

    menus {
        bigint id PK "메뉴 식별값"
        varchar name "메뉴명"
        bigint price "가격"
        varchar status "판매 상태 (AVAILABLE, SOLD_OUT, DISCONTINUED)"
        varchar image_url "메뉴 이미지 경로"
        datetime created_at "등록 일시"
    }

    orders {
        bigint id PK "주문 식별값"
        bigint member_id FK "주문한 회원 식별값"
        bigint total_price "총 결제 포인트"
        varchar status "주문 상태 (RECEIVED, PREPARING, READY_FOR_PICKUP, COMPLETED)"
        datetime created_at "주문 및 결제 일시"
        datetime updated_at "최종 상태 변경 일시"
    }

    order_items {
        bigint id PK "주문 상품 식별값"
        bigint order_id FK "연관된 주문 식별값"
        bigint menu_id FK "주문한 메뉴 식별값"
        varchar temperature "온도 옵션 (HOT / ICE)"
        int quantity "수량"
        bigint price "주문 시점의 메뉴 개별 가격"
    }

    point_histories {
        bigint id PK "포인트 내역 식별값"
        bigint member_id FK "포인트가 변동된 회원 식별값"
        bigint order_id FK "연관 주문 식별값 (선택)"
        bigint amount "변동 포인트 금액 (충전 +, 사용 -)"
        varchar type "변동 유형 (CHARGE, USE)"
        datetime created_at "변동 일시"
    }
```

---

## 2. 🗂️ 테이블 상세 명세

### ① `members` (회원 테이블)
* **`id` (PK)**: 회원 식별 고유키
* **`point_balance`**: 잔여 포인트 정보
* **`version`**: **[P1 반영]** 동시성 제어(Lost Update)를 위한 낙관적 락 데이터 버전 컬럼. 중복 결제 및 다중 요청에 의한 잔액 꼬임 현상을 방지합니다.

### ② `menus` (커피 메뉴 테이블)
* **`id` (PK)**: 메뉴 식별 고유키
* **`status`**: **[P2 반영]** 상품 판매 상태 관리 컬럼 (`AVAILABLE`[판매중], `SOLD_OUT`[품절], `DISCONTINUED`[단종]). 
  * 메뉴가 더 이상 제공되지 않더라도 실제 DB에서 `DELETE`하지 않고 상태값 변경을 처리하여 과거 주문 정보(`order_items`)와의 외래키(FK) 참조 무결성을 안전하게 지켜냅니다.

### ③ `orders` (주문 정보 테이블)
* **`id` (PK)**: 주문 식별 고유키 (주문 번호)
* **`updated_at`**: **[P2 반영]** 최종 상태 변경 일시. 
  * 주문 상태(`RECEIVED` -> `PREPARING` -> `READY_FOR_PICKUP` -> `COMPLETED`)의 상태 추이 및 변경 시간 히스토리를 정밀하게 추적하고 분석하기 위함입니다.

### ④ `order_items` (주문 상품 상세 테이블)
* **`id` (PK)**: 주문 개별 상품 일련번호
* **`price`**: 주문 시점의 커피 가격 기록 (이후 `menus` 테이블에서 판매 가격이 바뀌어도 과거 결제 이력 보존용)

### ⑤ `point_histories` (포인트 충전/사용 이력 테이블)
* **`id` (PK)**: 포인트 변동 이력 고유키
* **`order_id` (FK, Null 허용)**: **[P3 반영]** 포인트가 차감되었을 때 연관된 주문 번호(`orders.id`)를 저장합니다.
  * 고객의 포인트 사용(차감) 내역이 어떤 결제 건에 해당되는지 역추적 조회를 수월하게 도와줍니다. (단, 포인트 충전 시에는 `Null` 처리)

---

## 3. ⚡ 인덱스(Index) 설계 전략 (성능 최적화)

### `idx_orders_created_at` (orders 테이블의 created_at 컬럼)
* **[P3 반영]**: 인기 메뉴 목록 조회 API (`GET /menu/popular`) 호출 시, **"최근 7일간 주문이 가장 많은 상위 3개 메뉴"**를 집계해야 합니다.
* 데이터가 무수히 쌓였을 때 전체 데이터를 다 훑는 풀 스캔(Full Scan) 현상을 방지하기 위해 `orders.created_at` 컬럼에 단일 인덱스(Index)를 적용하여 날짜 조건절 검색 속도 및 조인 성능을 최적화합니다.
