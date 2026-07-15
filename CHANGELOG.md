# 변경 이력 (CHANGELOG)

---

## 2026-07-15

### 16:01 | 와이어프레임 및 필수 요구사항 문서화 (WIREFRAME.md)
* **[DOCS]** 와이어프레임 이미지 저장 및 마크다운 문서 작성
  - 사용자가 제공한 와이어프레임 이미지를 프로젝트 내부 리소스 폴더인 `docs/images/wireframe.png` 경로에 저장하였습니다.
  - 프로젝트 루트에 `WIREFRAME.md` 문서를 새로 추가하여 와이어프레임 이미지와 요구사항 사양을 함께 기재하였습니다.
  - 마크다운 문서 내에 각 화면(메인페이지, 로그인, 메뉴 상세, 장바구니, 포인트 충전, 결제 완료, 관리자 페이지)의 세부 구성 항목들을 텍스트로 표기하여 가독성을 높였습니다.
  - `README.md` 파일의 관련 문서 바로가기 섹션에 `WIREFRAME.md`로 이동할 수 있는 바로가기 링크를 추가하여 문서 접근성을 개선하였습니다.

---

## 2026-07-14

### 16:09 | 코드 리뷰 P1/P2 피드백 반영 (POST /orders 아키텍처 정렬)
* **[P1-1][REFACTOR]** Order 엔티티 Member 직접 참조 제거
  - Order 엔티티에서 `@ManyToOne Member member` JPA 매핑을 제거하고, `@Column Long memberId` 식별자 직접 참조 방식으로 전환했습니다. (컨벤션 6.1)
  - DB 컬럼명(`member_id`)은 변경 없이 유지됩니다.
  - 연쇄 영향: OrderFacade 빌더 호출부 `.member(member)` → `.memberId(member.getId())`로 수정, OrderFacadeTest 수정.
* **[P1-2][REFACTOR]** OrderItem 엔티티 Menu 직접 참조 제거
  - OrderItem 엔티티에서 `@ManyToOne Menu menu` JPA 매핑을 제거하고, `@Column Long menuId` 식별자 직접 참조 방식으로 전환했습니다. (컨벤션 6.1)
  - Order 참조는 동일 컨텍스트(order.domain) 내부 관계이므로 유지합니다.
  - 연쇄 영향: OrderFacade 빌더 호출부 `.menu(menu)` → `.menuId(menu.getId())`, OrderResponse.from() 내 `getMenu().getId()` → `getMenuId()` 수정, OrderFacadeTest 수정.
* **[P1-3][REFACTOR]** OrderItem.setOrder() 캡슐화 의도 명시
  - `setOrder()` 메서드의 package-private 접근 수준이 동일 패키지 외부에서는 이미 접근 불가임을 확인하고, 코드 주석으로 `Order.addOrderItem()`이 유일한 진입점임을 명시했습니다.
* **[P1-4][DESIGN]** 트랜잭션 바운더리 현행 유지 결정
  - 포인트 차감과 주문 저장의 원자성 보장을 위해 단일 `@Transactional` 구조를 유지합니다. 컨벤션 6.3의 결제/재고 예외 경계에 해당함을 확인했습니다.
* **[P2-1][DOCS]** AGENTS.md 에러 코드 접두사 `MENU_` 공식 추가
  - 컨벤션 4.3 에러 코드 접두사 목록에 `MENU_` 항목을 추가했습니다.
* **[P2-2][DOCS]** AGENTS.md 퍼사드 예외 경계 규칙 공식 추가
  - 컨벤션 6.1 서비스 레이어 의존성 차단 항목에 퍼사드 계층이 타 도메인 서비스 직접 주입의 유일한 허용 예외 경계임을 명문화했습니다.

---

### 15:54 | 커피 주문 및 결제 API 구현 (POST /orders)
* **[API]** 주문 접수 및 포인트 결제 API 구현
  - 회원이 장바구니에 담긴 커피 상품들을 보유 포인트로 결제하고 주문을 접수하는 POST /orders API를 구현했습니다.
  - 퍼사드(OrderFacade) 계층을 신규 도입하여 OrderService 내부에 타 도메인 서비스(MemberService, MenuService, PointService)를 직접 주입하는 아키텍처 오염을 차단했습니다.
  - 응답 DTO(OrderResponse)에서 DB의 id PK 필드명을 식별자 매핑 규칙에 따라 orderId, menuId로 변환하여 반환합니다.
* **[ARCH]** 퍼사드(Facade) 패턴 및 비동기 이벤트 구조 도입
  - OrderFacade에서 회원 검증, 메뉴 유효성 및 판매 가능 여부 검증, 포인트 차감, 이력 저장, 주문 영속화, 이벤트 발행의 단계별 오케스트레이션을 수행합니다.
  - 결제 완료 후 외부 플랫폼 전송 시뮬레이션은 OrderCompletedEvent를 발행하고 @Async @EventListener 기반의 OrderEventListener가 비동기로 처리합니다.
  - @EnableAsync 어노테이션이 적용된 AsyncConfig 설정 클래스를 추가했습니다.
* **[DOMAIN]** Member 엔티티 usePoint 예외 처리 일원화
  - Member.usePoint() 메서드의 잔액 부족 예외를 기존 IllegalStateException에서 CustomException(ErrorCode.INSUFFICIENT_POINT)으로 변경하여 글로벌 예외 핸들러를 통한 일관된 API 실패 응답 처리 체계를 갖추었습니다.
* **[ERROR]** 에러 코드 2종 추가
  - MENU_NOT_AVAILABLE: 존재하지 않거나 SOLD_OUT / DISCONTINUED 상태의 메뉴 주문 시 발생 (400 Bad Request)
  - INSUFFICIENT_POINT: 보유 포인트 잔액이 주문 총액보다 부족할 시 발생 (400 Bad Request)
* **[SERVICE]** 각 도메인 서비스 조회/이력 메서드 확장
  - MemberService.getMember(Long memberId): 회원 엔티티 직접 조회 메서드 추가
  - MenuService.getMenu(Long menuId): 메뉴 엔티티 직접 조회 메서드 추가
  - PointService.recordPointUse(Long memberId, Long orderId, Long amount): 포인트 USE 이력 저장 메서드 추가
* **[TEST]** 주문 기능 테스트 작성
  - OrderFacadeTest: 주문 결제 성공, 포인트 부족 실패, 품절 메뉴 실패 케이스 단위 테스트 작성
  - OrderControllerTest: POST /orders 성공 응답 구조, MEMBER_NOT_FOUND / MENU_NOT_AVAILABLE / INSUFFICIENT_POINT 에러 케이스 MockMvc 테스트 작성
* **[BUILD]** 테스트 의존성 추가
  - LocalDateTime 직렬화 지원을 위해 jackson-datatype-jsr310을 testImplementation 스코프에 명시적으로 추가했습니다.

---

### 15:44 | 포인트 충전 API 리팩토링 및 DTO 유효성 검증 추가 (P1, P2 피드백 반영)
* **[REFACTOR]** PointHistory 식별자 직접 참조 방식으로 전환 (P1)
  - PointHistory 엔티티에서 타 도메인 객체(Member, Order)에 대한 JPA @ManyToOne 직접 매핑을 제거하고, Long 타입의 memberId 및 orderId 식별자를 저장하도록 구조를 개선했습니다.
  - 이로써 point 컨텍스트와 member, order 컨텍스트 간의 강한 결합도를 제거하고 도메인 자율성을 보장했습니다.
  - PointService 및 PointServiceTest 등 연관 구조의 빌더 생성 호출 부도 식별자 직접 매핑에 맞추어 연쇄 갱신했습니다.
* **[API]** DTO 유효성 검증(@Valid) 및 GlobalExceptionHandler 예외 가공 추가 (P2)
  - PointChargeRequest DTO 필드에 @NotNull 및 @Min(1000) 검증 어노테이션을 부착하고 PointController 진입 시 @Valid를 통해 1차 유효성 차단을 수행하도록 변경했습니다.
  - @Valid 바인딩 실패 시 발생하는 MethodArgumentNotValidException을 처리하여 클라이언트에게 ApiResponse 표준 실패 규격으로 INVALID_CHARGE_AMOUNT 등의 에러 정보를 리턴하도록 GlobalExceptionHandler에 핸들러를 보강했습니다.
* **[TEST]** DTO 검증 실패 케이스 테스트 보완
  - PointControllerTest에 1,000원 미만 비정상 금액 요청 시 MockMvc 환경에서 GlobalExceptionHandler를 거쳐 400 Bad Request 에러 형식으로 응답되는지 검증하는 단위 테스트 케이스를 확충했습니다.

---

### 15:41 | 포인트 충전 API 구현 (POST /points/charge)
* **[API]** 포인트 충전 API 구현
  - 특정 회원의 포인트를 충전하고, 충전 후의 잔여 포인트 정보와 충전 이력 식별키를 반환하는 POST /points/charge API를 구현했습니다.
  - @Setter 및 @Data 사용 금지 컨벤션을 준수하여, Member 엔티티 내부의 chargePoint 비즈니스 메서드를 호출해 포인트를 업데이트했습니다.
  - JPA 식별자 매핑 규칙에 따라 DB의 Primary Key id 컬럼명을 API 응답 내에서 memberId, pointHistoryId로 변환하여 제공하도록 DTO를 설계했습니다.
* **[ERROR]** 예외 처리 및 에러 코드 추가
  - 1,000원 미만의 비정상 충전 금액 검증을 위해 ErrorCode.INVALID_CHARGE_AMOUNT 에러 코드를 추가했습니다.
  - 회원이 존재하지 않을 경우 ErrorCode.MEMBER_NOT_FOUND 예외가 발생하도록 서비스를 구성했습니다.
* **[TEST]** 포인트 충전 기능 단위 테스트 작성
  - PointServiceTest: 포인트 충전 성공 및 최소 금액 미만 예외 상황, 회원 미존재 예외 상황에 대한 단위 테스트를 작성했습니다.
  - PointControllerTest: POST 호출을 모킹하여 성공 응답 구조 및 예외 발생 시 에러 응답 포맷(ApiResponse) 검증 테스트를 작성했습니다.

---

### 15:34 | Trace ID 기반 로깅 규칙 정의 (AGENTS.md, CONVENTION.md)
* **[RULE]** 공통 추적 식별자(Trace ID) 로깅 가이드 추가
  - 컨텍스트 간의 비동기 호출 전파 및 다중 컨텍스트 트랜잭션 진행 상황을 로그상에서 역추적할 수 있도록, 공통 추적 식별자(Trace ID)를 로그 메시지에 반드시 매핑하도록 규정했습니다.
  - 이를 보장하기 위해 멀티 스레드 및 비동기 구간 전파 시 MDC(Mapped Diagnostic Context)를 활용하여 추적 식별자가 유지되도록 명문화했습니다.

---

### 15:32 | API 버저닝 접두사 관련 라우팅 예외 반영 (AGENTS.md, CONVENTION.md)
* **[RULE]** 버전 접두사 예외 필터링 규칙 도입
  - API 경로 선두에 `/api/v1` 등의 공통 버전 접두사가 지정될 경우, 이를 건너뛰고(필터링) 그 뒤에 따라오는 비즈니스 도메인 명(예: `/api/v1/menus` -> `menus`)을 기준으로 첫 번째 세그먼트(컨텍스트명)를 인식하도록 라우팅 예외 조항을 추가했습니다.

---

### 15:29 | 규칙 개정 및 4차 피드백 반영 (AGENTS.md, CONVENTION.md)
* **[RULE]** JPA 엔티티 동등성(Equality) 비교 규칙 수립
  - 식별자(ID) 필드만을 동등성 비교의 기준으로 삼기 위해 `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` 및 PK 식별자 필드 `@EqualsAndHashCode.Include` 지정을 의무화했습니다.
* **[RULE]** 엔티티 기본 생성자 캡슐화 제약 구체화
  - JPA 지연 로딩 프록시를 보장하면서 외부 인스턴스 남발을 막기 위해 `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 설정을 룰에 명시했습니다.
* **[RULE]** 에러 응답 포맷 DTO 검증 실패(details) 정보 확장
  - 유효성 검증 예외 발생 시 구체적인 오류 필드와 원인을 배열 형식으로 전달하는 `details` 구조를 실패 JSON 응답 포맷에 추가했습니다.
* **[RULE]** 타 컨텍스트 서비스 직접 참조 차단
  - Bounded Context 간 서비스 구현체의 직접 의존 주입을 금지하고, 파사드(Facade) 계층 또는 전용 이벤트를 활용하도록 설계적 의존 제약을 추가했습니다.
* **[RULE]** 동사형 URI 설계에 대한 예외 허용 기준 수립
  - 프로세스 처리를 동사 리소스로 표현할 수 있는 예외적 경계(충전, 취소 등)를 명시하고 그 외에는 RESTful 원칙을 준수하도록 명문화했습니다.
* **[RULE]** Git 커밋 타입 세분화
  - 테스트 코드 추가/수정의 명확한 기록 추적을 위해 `test:` 커밋 타입을 신설했습니다.

---

### 15:26 | 아키텍처 규칙 보완 및 3차 피드백 반영 (AGENTS.md)
* **[RULE]** 식별자 직접 참조 가이드 추가
  - Bounded Context 간의 직접 객체 참조 매핑을 방지하고 식별자 직접 참조(`Long memberId` 등) 방식을 기본 설계 원칙으로 명시했습니다.
* **[RULE]** 트랜잭션 분리 표준화 조항 구체화
  - Spring Application Event 기반의 비동기 리스너 구성을 기본 표준으로 지정하고, 특수 정합성 보장 영역에 한해 `Propagation.REQUIRES_NEW` 방식을 사용하도록 규칙을 보강했습니다.
* **[RULE]** 가독성 미세 개선
  - `AGENTS.md` 내부에 불필요하게 존재하던 줄바꿈 공백들을 정돈했습니다.

---

### 15:24 | 아키텍처 및 예외 처리 규칙 강화
* **[RULE]** 예외 처리 에러 코드 규칙 신설 (AGENTS.md)
  - 회원(`MEMBER_`), 포인트(`POINT_`), 주문(`ORDER_`), 시스템(`SYSTEM_`) 등 도메인별 일관된 에러 코드 접두사를 할당하는 규칙을 명문화했습니다.
* **[RULE]** 트랜잭션 바운더리 규칙 신설 (AGENTS.md)
  - 컨텍스트 간 직접 결합을 방지하고 트랜잭션 분할을 보장하기 위해 `REQUIRES_NEW` 전파 속성 지정 또는 비동기(`Async`) 처리를 적용하도록 규정했습니다.
* **[RULE]** 규칙 문서 시인성 대폭 개선
  - `AGENTS.md` 전체 레이아웃에 구분선과 정렬 형식을 추가하여 규정 가독성을 대폭 향상시켰습니다.

---

### 15:15 | 회원 상세 조회 기능 및 2차 피드백 반영
* **[API]** 회원 상세 및 포인트 조회 구현
  - GET `/members/{memberId}` 엔드포인트를 제공하는 MemberController 구현
  - memberId 미존재 시 MEMBER_NOT_FOUND 예외 반환
  - 회원 정보 및 포인트 잔액을 반환하는 MemberService 구현
  - 데이터베이스 PK 식별자를 API 명세에 맞추어 memberId로 응답하는 MemberResponse DTO 구현
  - 에러 핸들링을 위한 ErrorCode.MEMBER_NOT_FOUND 상수 추가
* **[TEST]** 회원 상세 조회 단위 테스트 작성
  - MemberServiceTest: 회원 상세 조회 성공 시 DTO 반환 여부와 실패 시 예외 발생 검증
  - MemberControllerTest: GET 호출 성공 응답 구조 및 예외 발생 시 에러 응답 포맷 검증
* **[REFRACTOR]** 2차 리뷰 피드백 보완
  - MemberController 파라미터에 @Validated 및 @Positive 어노테이션을 적용하여 입력값 유효성 검증 적용
  - 유효성 검증 적용을 위해 spring-boot-starter-validation 라이브러리 의존성 추가 (build.gradle)
  - MemberResponse DTO 내 Null NPE 예방을 위한 방어 코드 추가
  - MemberControllerTest mockMvc 경로를 파라미터 바인딩 형식으로 수정

---

### 15:06 | 커피 메뉴 목록 조회 기능 구현
* **[API]** 커피 메뉴 목록 조회 구현
  - GET `/menus` 엔드포인트를 제공하는 MenuController 구현
  - AVAILABLE 및 SOLD_OUT 상태의 메뉴 데이터를 조회하여 DTO로 매핑하는 MenuService 구현
  - 데이터베이스 PK 식별자를 menuId로 매핑하는 MenuResponse DTO 구현
  - 특정 메뉴 상태 목록으로 필터링 조회가 가능한 findAllByStatusIn 쿼리 메서드를 정의한 MenuRepository 구현
* **[TEST]** 커피 메뉴 목록 조회 단위 테스트 작성
  - MenuServiceTest: AVAILABLE 및 SOLD_OUT 상태 메뉴 데이터 조회 정합성 검증
  - MenuControllerTest: GET /menus 호출 시 성공 응답 JSON 구조 및 데이터 형식 정합성 검증

---

### 14:54 | 도메인 아키텍처 규칙 설정
* **[RULE]** Bounded Context 아키텍처 가이드 반영
  - `order` 패키지만 `member`, `menu`, `point` 패키지를 참조할 수 있도록 의존성 방향 단방향 제한 (순환 참조 방지)
  - API 경로의 첫 세그먼트로 도메인 컨텍스트명을 명시하도록 강제하는 '컨텍스트 라우팅 규칙' 수립
  - 컨트롤러 내 비즈니스 로직 포함을 금지하고 서비스로 위임하도록 컨트롤러의 책임 제한

---

## 2026-07-13

### 17:59 | 에이전트 소통 및 계획서 제출 룰 강화
* **[RULE]** 작업 방식 및 승인 룰 제정
  - 에이전트 답변의 영어 출력을 전면 차단하는 '한국어 사용 절대 원칙' 추가
  - 대화방 가독성을 확보하기 위해 변경 소스 코드를 본문에 직접 기재하는 행위 금지
  - 계획서 제출 시 사용자 승인 버튼("Proceed")이 나타나도록 도구 옵션의 RequestFeedback 지정을 의무화

---

### 15:43 | 협업을 위한 코드 리뷰 컨벤션 반영
* **[RULE]** 피드백 강도 표기법(Pn Rule) 제정
  - 피드백의 긴급성과 중요도를 명확히 하여 의사소통 비효율성을 해소하는 P1 ~ P5 수준의 태그 규정
  - 작업 요청자의 자가 검토(Self-Review) 및 변경 맥락(Why/What/How) 작성 요령 수립

---

## 향후 작업 계획 (TODO)

### 1. 포인트 충전 API 구현
- [x] **[API]** 포인트 충전 API 구현 (POST /points/charge)
- [x] **[LOCK]** 낙관적 락(Optimistic Lock)을 활용한 포인트 동시 충전/차감 정합성 검증
- [x] **[TEST]** 포인트 충전 성공 단위 테스트 및 다중 스레드 기반 동시 충전 통합 테스트 작성

### 2. 주문 및 결제 API 구현
- [ ] **[API]** 커피 주문 및 결제 API 구현 (`POST /orders`)
- [ ] **[SERVICE]** 주문 건 생성 및 포인트 차감 정책 연계
- [ ] **[SYSTEM]** 결제 완료 후 외부 플랫폼으로 실시간 데이터 전송 모듈 구현
- [ ] **[TEST]** 주문 결제 성공, 잔액 부족 실패 케이스 및 다중 결제 동시성 검증 테스트 작성

### 3. 주문 상세 조회 API 구현
- [ ] **[API]** 주문 상세 조회 API 구현 (`GET /orders/{orderId}`)
- [ ] **[TEST]** 영수증 단건 반환 및 개별 매핑 정합성 테스트 작성

### 4. 인기 메뉴 조회 API 구현
- [ ] **[API]** 인기 메뉴 목록 조회 API 구현 (`GET /menus/popular`)
- [ ] **[QUERY]** 최근 7일 동안의 주문 데이터를 group by 통계로 산출하는 DB 쿼리 구현
- [ ] **[TEST]** 일주일 주문 통계가 정상적으로 필터링되어 출력되는지 검증 테스트 작성

### 5. [관리자] 전체 주문 조회 API 구현
- [ ] **[API]** 전체 주문 목록 조회 API 구현 (`GET /admin/orders`)
- [ ] **[TEST]** 전체 데이터가 최신순으로 잘 출력되는지 검증 테스트 작성

### 6. [관리자] 주문 상태 변경 API 구현
- [ ] **[API]** 주문 상태 변경 API 구현 (`PATCH /orders/{orderId}/status`)
- [ ] **[TEST]** 허용되지 않는 상태 전이 차단 및 올바른 상태 업데이트 비즈니스 정합성 테스트 작성
