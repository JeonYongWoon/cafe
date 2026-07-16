# jeonscafe 커피숍 주문 시스템

본 프로젝트는 스프링 부트(Spring Boot)와 JPA를 기반으로 구축된 분산 환경 친화적인 커피숍 주문 및 결제 시스템입니다. 안전한 포인트 기반 결제 시스템과 동시성 제어 기술을 특징으로 합니다.
---

## 프론트엔드 UI 예시

![JEONS CAFE 프론트엔드 UI](./docs/images/cafe_web_ui.png)
---

## 관련 문서 바로가기
- [요구사항 정의서 (REQUIREMENTS.md)](./docs/REQUIREMENTS.md)
- [데이터베이스 ERD 설계서 (ERD.md)](./docs/ERD.md)
- [REST API 상세 명세서 (API.md)](./docs/API.md)
- [프로젝트 공통 컨벤션 (CONVENTION.md)](./docs/CONVENTION.md)
- [와이어프레임 및 화면 설계서 (WIREFRAME.md)](./docs/WIREFRAME.md)
- [프로젝트 변경 이력 (CHANGELOG.md)](./CHANGELOG.md)
- [문서 조회 라우터 (document_router.md)](./docs/document_router.md)
- [메인 오케스트라 규칙 (orchestrator_rules.md)](./docs/orchestrator_rules.md)
- [서브에이전트 규칙 (subagent_rules.md)](./docs/subagent_rules.md)
- [아키텍처 결정 기록 (adr.md)](./docs/adr.md)
- [AI 실수 사전 (ai_common_mistakes.md)](./docs/ai_common_mistakes.md)
- [리뷰 및 검증 로그 템플릿 (review_log_template.md)](./docs/review_log_template.md)
- [안전 정지 리포트 템플릿 (safety_stop_template.md)](./docs/safety_stop_template.md)

---

## 기술 스택 (Tech Stack)
- **개발 언어**: Java 17
- **프레임워크**: Spring Boot 4.1.0, Spring Data JPA
- **데이터베이스**: H2 Database (인메모리 디비)
- **빌드 도구**: Gradle

---

## 주요 비즈니스 정책
- **포인트 결제**: 결제는 오직 포인트로만 처리되며 1원 = 1P로 1대1 매핑됩니다. 잔여 포인트를 확인하여 차감합니다.
- **동시성 제어**: 포인트 결제 및 차감 시 낙관적 락(Optimistic Lock)을 활용하여 포인트 잔액 꼬임 현상 및 중복 결제를 원천 방지합니다.
- **실시간 데이터 송신**: 주문이 완료되면 주문 정보가 외부 데이터 수집 플랫폼으로 실시간 전송됩니다.
- **메뉴 상태 관리**: 메뉴가 단종되더라도 과거 결제 이력 조회를 위해 DB에서 물리적으로 삭제하지 않고 상태값(DISCONTINUED)으로 제어합니다.

---

## 데이터베이스 설계 (ERD)

```mermaid
erDiagram
    members ||--o{ orders : "places"
    members ||--o{ point_histories : "has"
    orders ||--|{ order_items : "contains"
    menus ||--o{ order_items : "is_ordered_in"
    orders ||--o| point_histories : "linked_to"

    members {
        bigint id PK
        varchar username
        varchar password
        bigint point_balance
        bigint version
        datetime created_at
    }
    menus {
        bigint id PK
        varchar name
        bigint price
        varchar status
        varchar image_url
        datetime created_at
    }
    orders {
        bigint id PK
        bigint member_id FK
        bigint total_price
        varchar status
        datetime created_at
        datetime updated_at
    }
    order_items {
        bigint id PK
        bigint order_id FK
        bigint menu_id FK
        varchar temperature
        int quantity
        bigint price
    }
    point_histories {
        bigint id PK
        bigint member_id FK
        bigint order_id FK
        bigint amount
        varchar type
        datetime created_at
    }
```

---

## API 엔드포인트 명세 요약

| 분류 | 기능 | HTTP 메서드 및 URI | 설명 |
| :--- | :--- | :--- | :--- |
| 회원 | 회원가입 | `POST /members` | 신규 사용자 가입 |
| 회원 | 로그인 | `POST /sessions` | 로그인 세션 인증 |
| 회원 | 상세 정보 조회 | `GET /members/{memberId}` | 특정 회원 및 포인트 잔액 조회 |
| 메뉴 | 커피 메뉴 목록 | `GET /menus` | 판매 가능한 커피 목록 조회 |
| 메뉴 | 인기 메뉴 조회 | `GET /menus/popular` | 최근 7일간 최다 주문 상위 3개 메뉴 |
| 포인트 | 포인트 충전 | `POST /points/charge` | 사용자 포인트 금액 충전 |
| 주문 | 커피 주문 및 결제 | `POST /orders` | 포인트 차감 결제 및 주문 생성 |
| 주문 | 주문 상세 조회 | `GET /orders/{orderId}` | 주문 및 개별 품목 목록 단건 조회 |
| 관리자 | 주문 목록 조회 | `GET /admin/orders` | 전체 사용자 주문 최신순 조회 |
| 관리자 | 주문 상태 변경 | `PATCH /orders/{orderId}/status` | 주문 단계 상태 전이 처리 |

---

## 개발 가이드라인 및 컨벤션

### 1. Lombok 사용 제약 사항 (부작용 방지)
- **@Setter 사용 금지**: 객체의 핵심 데이터를 아무 제약 없이 수정하는 행위를 방지하기 위해 사용하지 않으며, 의미가 명확한 비즈니스 메서드를 통해 상태를 변경합니다.
- **@Data 사용 금지**: JPA 양방향 연관관계에서 무한 루프(StackOverflowError)를 방지하기 위해 필요한 개별 애너테이션(@Getter, @NoArgsConstructor 등)만 적용합니다.
- **@AllArgsConstructor 사용 제한**: 매개변수 대입 순서 오염으로 인한 오동작 방지를 위해 필수 인자 생성자(@RequiredArgsConstructor)나 Builder 패턴을 사용합니다.

### 2. 네이밍 규칙
- **패키지**: 소문자 단수형 (예: com.example.jeonscafe.order)
- **클래스/인터페이스**: PascalCase (예: OrderService)
- **메서드/변수**: camelCase (예: chargePoint)
- **데이터베이스 테이블**: 복수형 명사 및 snake_case (예: members, orders)
- **데이터베이스 컬럼**: 단수형 명사 및 snake_case (예: member_id)

### 3. Git 커밋 메시지 규칙
커밋 메시지는 `type: 내용` 포맷으로 작성하며, 아래의 타입을 준수합니다.
- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 수정 (README, CONVENTION 등)
- **refactor**: 코드 리팩토링 (기능 변화 없음)
- **chore**: 빌드 구성 변경, 패키지 매니저 설정 등

### 4. 코드 리뷰 컨벤션 (Pn 규칙)
피드백 강도에 따라 리뷰 태그를 다르게 적용합니다.
- **P1 (필수 반영)**: 배포 전 반드시 수정해야 하는 치명적인 버그나 에러
- **P2 (가급적 반영)**: 프로젝트 핵심 컨벤션 위반 혹은 비효율성 개선 권장 사항
- **P3 (의견 및 토론)**: 아키텍처 대안 또는 기술적 토론이 필요한 사항
- **P4 (사소한 제안)**: 오타나 변수명 개선 등 반영 여부가 자율적인 사안
- **P5 (칭찬)**: 좋은 설계나 깔끔한 코드에 대한 긍정 피드백

### 5. 아키텍처 규칙 (Architecture Rules)

#### 컨텍스트 맵 (Context Map) 규칙
- **의존성 방향 제약**: `order`(주문) 컨텍스트만 `member`(회원), `menu`(메뉴), `point`(포인트) 컨텍스트를 단방향으로 참조할 수 있으며, 이외의 역방향 및 순환 참조는 금지됩니다.
- **결합도 최소화**: 컨텍스트 간의 직접적인 클래스/엔티티 참조를 피하고 가급적 인터페이스나 이벤트를 통해 느슨하게 통합합니다.

#### 컨텍스트 라우팅 (Context Routing) 규칙
- **URI 경로 분리**: API 경로 세그먼트의 첫 단어로 진입할 바운디드 컨텍스트 이름을 명시합니다. (예: `/menus/**`, `/orders/**`, `/points/**`)
- **컨트롤러 위임 한계**: 컨트롤러는 요청 검증과 응답 변환만 담당하며 핵심 비즈니스 로직은 도메인 컨텍스트 내의 서비스 레이어로 즉시 라우팅해야 합니다.

---

## 로컬 테스트 및 구동 방법

### 1. 단일 백엔드 서버 구동 (H2 인메모리 DB)
로컬에 Java 17이 설치되어 있어야 합니다. 터미널에서 다음 명령어를 실행합니다.
```bash
./gradlew bootRun
```
서버가 성공적으로 구동되면 웹 브라우저에서 http://localhost:8080/index.html 주소로 접속하여 프론트엔드 페이지를 테스트할 수 있습니다.

### 2. 멀티 컨테이너 환경 구동 (Docker Compose)
Docker 및 Docker Compose가 설치되어 있어야 합니다. 터미널에서 다음 명령어를 실행합니다.
```bash
docker compose up --build
```
모든 컨테이너가 정상 기동되면 웹 브라우저에서 http://localhost/index.html (포트 80) 주소로 접속하여 로드밸런싱이 적용된 환경에서 프론트엔드 페이지를 테스트할 수 있습니다.

---
##### 어드민 계정
ID) admin
PW) admin123
---



