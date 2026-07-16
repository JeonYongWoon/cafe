# 변경 이력 (CHANGELOG)

---

## 2026-07-16

### 18:13 | Redis 세션 직렬화 구현 및 에러 엔드포인트 예외 처리 개정
* **[FEAT]** Redis 세션 클러스터링을 위한 직렬화 적용
  - Redis 세션 저장소 내에 로그인 회원 정보를 안전하게 보관할 수 있도록 RedisSessionConfig 설정을 활성화하고 SessionCreateResponse DTO에 Serializable 인터페이스를 확장 구현했습니다.
* **[BUG]** 인터셉터 내 /error 엔드포인트 필터링 제외
  - Spring Boot 내부 에러 디스패칭 시 호출되는 /error 경로를 WebConfig 내 인터셉터 검증 대상에서 제외함으로써, 권한이 끊긴 상태의 오류 코드 응답 차단 마스킹 버그를 해결했습니다.
* **[FEAT]** 도커 컴포즈 캐릭터셋 튜닝
  - docker-compose.yml 파일 내 mysql 컨테이너 커맨드 인자에 utf8mb4 및 utf8mb4_unicode_ci 인코딩 튜닝을 명문화하여 적용했습니다.

### 17:57 | README.md 내 k6 부하 테스트 가이드 바로가기 링크 추가
* **[DOCS]** README.md 관련 문서 목록 업데이트
  - docs/k6/LOAD_TEST.md 경로의 부하 테스트 성능 가이드 문서 바로가기 링크를 README.md 관련 문서 목록에 연동하여 문서 검색 접근성을 확보했습니다.

### 17:49 | README.md 내 로컬 실행 가이드 추가 및 프론트엔드 실측 이미지 연동
* **[DOCS]** README.md 문서 보강
  - 다른 사용자들이 프로젝트를 로컬 개발 환경에 띄워 테스트할 수 있도록 Gradle 기반 단일 서버 구동법 및 Docker Compose 기반 분산 멀티 컨테이너 구동 가이드를 명확하게 추가했습니다.
* **[FEAT]** 프론트엔드 실제 화면 이미지 연동
  - 사용자가 직접 캡처하여 제공한 실제 커피숍 주문 시스템 웹 인터페이스 화면 이미지(cafe_web_ui.png)를 docs/images/ 경로에 배치하고, README.md 파일 내에서 시각적 참고 자료로 렌더링되도록 연동을 완료했습니다.


### 17:41 | k6 부하 테스트 결과의 분 단위 로그 형식 기록 보강
* **[FEAT]** k6 성능 지표 누적 로그 설계
  - docs/k6/LOAD_TEST.md 가이드 문서 내에 부하 테스트 결과를 날짜, 시간, 분 단위의 로그 형식으로 누적 기록할 수 있도록 결과 포맷을 전면 개편했습니다.
  - 기존 로컬 단일 인스턴스 테스트 결과([2026-07-16 17:27])와 도커 다중 인스턴스/로드 밸런싱 환경 테스트 결과([2026-07-16 17:40])를 각각 분 단위 로그로 분류하여 이력을 누적했습니다.

### 17:34 | 다중 인스턴스 배포 및 로드 밸런싱을 위한 로컬 Docker 환경 구축
* **[FEAT]** Docker Compose 기반 멀티 컨테이너 아키텍처 구성
  - Nginx(로드 밸런서), Spring Boot WAS 인스턴스 2대, MySQL(공용 DB), Redis(공용 세션 저장소)로 구성된 로컬 분산 배포 환경을 정의했습니다.
* **[FEAT]** Spring Session Redis 의존성 및 Docker 환경 설정 주입
  - build.gradle에 MySQL 커넥터 및 Spring Session Redis 의존성을 추가했습니다.
  - 컨테이너 내부 환경에 맞게 MySQL 및 Redis 접속 호스트명을 바인딩하는 application-docker.properties를 생성했습니다.
* **[FEAT]** Nginx 리버스 프록시 및 로드 밸런서 설정
  - nginx.conf를 작성하여 포트 80으로 들어오는 모든 사용자 요청을 2대의 백엔드 Spring Boot 컨테이너(app-1, app-2)로 라운드 로빈 방식으로 분산 전달하도록 로드 밸런싱을 적용했습니다.

### 17:27 | k6 부하 테스트 스크립트 및 GitHub Actions 워크플로 구축
* **[FEAT]** k6 부하 테스트 스크립트 신규 구축
  - k6/load-test.js 스크립트를 작성하여 전체 메뉴 조회 API(GET /menus) 및 인기 메뉴 조회 API(GET /menus/popular)에 대한 트래픽 부하 테스트 시나리오를 구성했습니다.
  - 가상 사용자 10명, 30초 지속 및 에러율 1% 미만, 응답속도 p(95) 200ms 이내 등의 성능 임계 기준을 적용했습니다.
* **[FEAT]** GitHub Actions 워크플로 파일 추가
  - .github/workflows/k6-load-test.yml 파일을 생성하여 수동 트리거 및 main 브랜치 push 이벤트 발생 시 작동하는 CI 기반의 k6 부하 테스트 워크플로를 구축했습니다.
  - 워크플로 내에서 JDK 17 설정, Gradle 빌드, Spring Boot 백그라운드 구동, 헬스체크 대기 및 k6 부하 테스트 구동 단계를 정의하고 최종 애플리케이션 로그 아티팩트를 보존하도록 설정했습니다.
* **[FEAT]** k6 부하 테스트 가이드 문서 생성
  - docs/k6/LOAD_TEST.md 경로에 k6 설치 방법, 실행 방법(로컬 및 CI), 최근 테스트 결과 등을 종합한 성능 가이드 문서를 추가했습니다.

### 17:10 | 장바구니/포인트 충전 비로그인 접근 차단 및 관리자 권한 제어 고도화
* **[FEAT]** 세션 기반 로그인 인증 및 관리자 인가 차단 인터셉터 구현
  - 회원 도메인에 MemberRole(USER, ADMIN)을 추가하고 엔티티 및 SessionCreateResponse 응답 DTO에 반영했습니다.
  - SecurityInterceptor 및 WebConfig를 도입하여 로그인 없이 회원/관리자 API 접근 시 401(MEMBER_UNAUTHORIZED) 응답을 반환하고, 일반 사용자가 /admin/** 경로 접근 시 403(ORDER_UNAUTHORIZED_ACCESS) 예외를 발생시키도록 백엔드 보안 장치를 완성했습니다.
  - SessionController에서 로그인 시 HTTP 세션(`LOGIN_MEMBER`)에 로그인 정보를 바인딩하여 세션 상태를 관리하도록 하였습니다.
* **[FEAT]** 기본 관리자 계정 자동 생성
  - DataInitializer 컴포넌트를 추가해 서버 기동 시 관리자 전용 계정(admin/admin123)이 자동으로 암호화 저장되도록 구현했습니다.
* **[FEAT]** 프론트엔드 비로그인 차단 및 관리자 버튼 동적 은폐
  - app.js 내 showPage에서 로그인 여부와 ADMIN 권한을 각각 체크해 유효하지 않은 페이지 강제 접근을 원천 봉쇄했습니다.
  - updateUserUI 함수에서 로그인한 사용자가 ADMIN일 때만 관리자 네비게이션 버튼이 렌더링되고 그 외에는 미노출되도록 동적 노출 제어 구조를 이식했습니다.


### 17:06 | 아메리카노 및 카페라떼 이미지 URL 재교정
* **[BUG]** 카페라떼 이미지 깨짐 현상 및 아메리카노 매핑 오류 해결
  - 카페라떼 이미지의 만료/서빙 오류를 해결하기 위해 브라우저에서 안정적으로 노출이 확인된 라떼아트 컵 사진 URL로 교체했습니다.
  - 아메리카노의 대표적 비주얼과 맞지 않던 사진을 정통 크레마 블랙커피 잔 사진 URL로 교정했습니다.
  - 돌체라떼 이미지 또한 겹치지 않게 아이스 연유 라떼 레이어 샷 사진으로 순환 교체했습니다.

### 17:04 | 상단 로마자 로고 개편 및 8종 전 메뉴 이미지 매핑 고도화
* **[FEAT]** 헤더 브랜드 로고 개편
  - 상단의 기존 국문 텍스트 '커피숍 주문 시스템'을 로마자 대문자 표기의 'JEONS CAFE'로 변경했습니다.
  - 글자 자간 간격(letter-spacing: 3px)과 굵은 가중치(800) 스타일 속성을 적용하여 현대적이고 고급스러운 무드를 구성했습니다.
* **[FEAT]** 8종 커피 및 에이드 전 메뉴 1:1 이미지 매핑
  - 아메리카노, 카페라떼, 돌체라떼, 카라멜마키아토, 바닐라라떼, 카푸치노, 에스프레소, 자몽에이드의 총 8종 메뉴 이름과 매칭되는 고유 고해상도 Unsplash 사진 주소를 확보하여 연동했습니다.
  - 이로써 사진 누락으로 인해 잘못 나타나던 시각 오류를 완전히 해결했습니다.

### 17:00 | 인스타 감성 테마 UI 리뉴얼 및 커스텀 토스트/모달 알림 도입
* **[FEAT]** 인스타 감성 스타일의 비주얼 리뉴얼
  - 폰트 디자인을 Outfit 및 Noto Sans KR 웹 폰트로 전면 전환하고, 오프화이트 크림 베이지(#FAF8F5) 바탕 톤을 전면 적용했습니다.
  - 상단 헤더에 투명 흐림 처리(Backdrop Blur)의 글래스모피즘 효과를 가미하고, 메뉴 카드와 각종 버튼의 둥글기와 크기를 인스타 감성 톤으로 미려하게 리스타일링했습니다.
* **[FEAT]** 커스텀 토스트 알림 및 컨펌 다이얼로그 모달 컴포넌트 구현
  - 기존 브라우저 기본 경고(alert) 및 확인(confirm) 창을 완전히 대체하기 위해, 화면 우측 하단에서 스택 형태로 부드럽게 등장했다가 3초 후 슬라이딩-아웃되며 소멸하는 '커스텀 토스트(Toast)' 모듈을 탑재했습니다.
  - 장바구니 이동 등을 재확인할 때 전체 화면을 부드럽게 흐리며(Blur) 예쁜 카드로 의사를 묻는 '커스텀 모달(Confirm Modal)'을 이식했습니다.

### 16:56 | 프론트엔드 고화질 이미지 연동 및 히어로 배너 추가
* **[FEAT]** Unsplash 고해상도 무료 이미지 연동
  - 아메리카노, 카페라떼, 돌체라떼 메뉴명에 맞춰 감각적이고 선명한 커피 음료 실사 이미지를 동적으로 적용했습니다.
  - 기타 매칭되지 않는 신규 메뉴 추가 시에도 예비용 실사 커피 사진이 기본 적용되도록 대체 로직을 구현했습니다.
* **[FEAT]** 메인 상단 브랜드 히어로 배너(Hero Banner) 구역 신설
  - index.html 및 style.css를 확장하여 아늑한 분위기의 카페 인테리어 배경 이미지를 적용하고, 그라데이션 오버레이 및 텍스트 레이아웃을 통해 브랜드 아이덴티티를 노출했습니다.

### 16:46 | 백엔드 API 연동을 위한 정적 프론트엔드 웹 애플리케이션 구축
* **[FEAT]** Vanilla HTML/CSS/JS 기반의 단일 페이지 애플리케이션(SPA) 구축
  - 백엔드 스프링 부트 서버의 내장 정적 리소스 디렉토리(src/main/resources/static)에 index.html, style.css, app.js 신규 파일들을 생성했습니다.
  - 로그인, 메뉴 조회, 실시간 인기 메뉴 통계, 포인트 충전, 장바구니 주문 및 결제, 그리고 어드민용 주문 현황판 및 상태 변경 제어 기능을 모두 구현하고 백엔드 API와 완전히 연동되도록 바인딩했습니다.
  - 별도 노드 개발 서버 없이 스프링 부트 서버 하나만 기동하면 바로 웹 브라우저(http://localhost:8080/index.html)에서 프론트엔드 전체 동작을 CORS 에러 없이 테스트할 수 있는 기반을 마련했습니다.

### 16:41 | 통합 테스트 데이터 격리 및 재시도 AOP 로깅 보강 (피드백 반영)
* **[BUG]** ConcurrencyIntegrationTest 내 H2 DB 데이터 누수 차단
  - ConcurrencyIntegrationTest 실행 시 생성된 가상 주문 및 결제 데이터가 DB 테이블에 잔존해 타 테스트의 집계 검증을 방해하던 문제를 해결했습니다.
  - setUp 단계에서 pointHistoryRepository와 orderRepository의 deleteAll() 처리를 적용하여 테스트 간 완벽한 격리를 보장했습니다 (ADR-004 준수).
* **[FEAT]** OptimisticLockRetryAspect 내 Slf4j 로깅 보강
  - 낙관적 락 실패 시도 횟수, 대상 메서드 시그니처 및 임의의 지터 백오프 시간 정보를 Slf4j warn 로그로 기록하고, 재시도 횟수 초과 시 error 로그를 찍도록 고도화했습니다.

### 16:32 | AOP 기반 선언적 재시도 메커니즘 리팩토링 (피드백 반영)
* **[REFACTOR]** 컨트롤러 청결화 및 선언적 애너테이션(@RetryOnCollision) 전환
  - 컨트롤러 내 수동 람다 실행기(OptimisticLockRetryExecutor) 및 주입 의존성을 완전히 제거하여, 컨트롤러의 본연 역할에만 충실하도록 개선했습니다 (컨벤션 6.2 준수).
  - 컨트롤러의 chargePoint 및 createOrder 메서드 상단에 @RetryOnCollision 애너테이션을 부착하여 동시성 실패 시의 재시도를 선언적으로 명시했습니다.
* **[FEAT]** AOP 기반 OptimisticLockRetryAspect 구현
  - build.gradle에 spring-boot-starter-aop 의존성을 수용하고, @RetryOnCollision 애너테이션을 가로채어 백오프 지터(30~70ms 무작위 대기)와 함께 최대 15회 자동 재시도하는 공통 Aspect 컴포넌트를 구축했습니다.

### 16:30 | 동시성 이슈 대응 및 검증 통합 테스트 구현
* **[FEAT]** 낙관적 락 재시도 헬퍼 클래스(OptimisticLockRetryExecutor) 구현
  - JPA 낙관적 락 충돌 시 백오프 대기 및 지터(Jitter, 30~70ms 무작위 대기)를 가미하여 최대 15회 재시도하는 공통 실행기 유틸리티를 작성했습니다.
* **[FEAT]** 포인트 충전 및 주문 결제 API 재시도 로직 도입
  - PointController와 OrderController의 진입점(Controller 계층)에 OptimisticLockRetryExecutor를 적용하여, 트랜잭션 경계 밖에서 안정적으로 재시도가 일어나도록 조치했습니다.
* **[FEAT]** 에러 코드 표준화 및 글로벌 예외 핸들링 보강
  - ErrorCode enum에 SYSTEM_CONCURRENCY_ERROR(409 Conflict) 에러 코드를 새로 할당했습니다.
  - GlobalExceptionHandler에 ObjectOptimisticLockingFailureException 및 OptimisticLockingFailureException 예외 핸들러를 추가하여, 재시도 횟수 초과 시 최종적으로 409 Conflict 응답을 클라이언트에게 정상 노출하도록 구현했습니다.
* **[TEST]** 멀티스레드 기반 동시성 검증 통합 테스트(ConcurrencyIntegrationTest) 신규 구축
  - H2 인메모리 실제 DB 환경 하에 CountDownLatch와 ExecutorService를 활용하여 동시에 발생하는 10개의 포인트 충전 요청과 5개의 주문 결제 요청이 데이터 유실 및 정합성 훼손 없이 모두 성공함을 증명하는 통합 테스트를 통과시켰습니다.


### 16:22 | 관리자용 주문 상태 변경 API 구현
* **[FEAT]** 주문 상태 순차 전이 검증 비즈니스 로직 구현
  - Order 엔티티 내에 setter를 원천 금지하고, 주문 상태 전이 규칙(RECEIVED -> PREPARING -> READY_FOR_PICKUP -> COMPLETED)을 검증하는 updateStatus 메서드를 구현했습니다.
  - 전이 룰 위반 시 CustomException(ErrorCode.ORDER_INVALID_STATUS) 예외를 발생시키도록 안전장치를 설계했습니다.
* **[FEAT]** 에러 코드 표준화 및 글로벌 예외 핸들링 견고화
  - 주문 관련 에러 접두사 규칙(ORDER_)을 준수하여 ORDER_INVALID_STATUS 에러 코드를 ErrorCode enum에 정의했습니다.
  - GlobalExceptionHandler에 HttpMessageNotReadableException 처리 핸들러를 보강하여 잘못된 문자열의 Enum 변환 오류(InvalidFormatException)가 발생했을 때도 리플렉션을 통해 유연하게 ORDER_INVALID_STATUS 에러 코드로 변환해 반환하도록 구현했습니다.
* **[API]** PATCH /orders/{orderId}/status 주문 상태 변경 API 구현
  - 관리자가 특정 주문의 진행 상태를 전환하고, 변경된 주문 ID(orderId), 상태(status), 갱신시각(updatedAt)을 ApiResponse 공통 성공 포맷으로 반환하는 API 핸들러를 구현했습니다.
* **[TEST]** MockMvc 슬라이스 테스트 확충 및 전체 성공 확인
  - OrderControllerTest 내에 주문 상태 변경 성공 케이스, 미존재 주문 대상 호출 실패 케이스, 잘못된 문자열 바인딩 실패 케이스, 규칙 위반 전이 실패 케이스를 모두 구현하여 빌드 테스트를 완벽하게 통과(BUILD SUCCESSFUL)시켰습니다.

### 16:08 | 관리자용 전체 주문 조회 API 구현
* **[FEAT]** 식별자 직접 참조 아키텍처 규칙 및 바운디드 컨텍스트 격리 준수 설계
  - Order 엔티티에서 Member 객체 직접 참조(@ManyToOne)를 완전히 배제하여 식별자 직접 참조(memberId)를 기본 원칙으로 유지함으로써 아키텍처 결합도를 최소화했습니다.
  - 어드민 전체 주문 조회 시 OrderService가 타 도메인에 접근하지 않도록 격리하고, 조율 계층인 OrderFacade가 OrderService(최신순 주문 목록 조회)와 MemberService(IN 쿼리를 통한 회원 목록 조회)를 연계해 데이터를 최종 DTO로 변환하도록 구현했습니다.
* **[FEAT]** MemberService 다수 ID 일괄 조회 기능 추가
  - N+1 문제를 근본적으로 방지하기 위해 MemberRepository의 findAllById(memberIds)를 활용하여 대량의 회원 이름 데이터를 IN 쿼리로 한 번에 획득하는 getMembers(memberIds) 비즈니스 로직을 구축했습니다.
* **[API]** GET /admin/orders 관리자 주문 목록 조회 API 구현
  - 관리자가 전체 주문 목록을 최신순(createdAt DESC)으로 조회할 수 있는 전용 엔드포인트를 구현했습니다.
  - Primary Key 컬럼명 'id'를 식별자 매핑 규칙에 따라 'orderId'로 치환하여 반환하며, 회원의 'username' 필드를 포함시켜 ApiResponse 공통 포맷으로 200 OK 응답 처리합니다.
* **[TEST]** AdminOrderController 및 API 슬라이스 Mocking 테스트 검증 완료
  - AdminOrderControllerTest 단독 슬라이스 테스트를 작성하여 MockMvc 환경에서 GET /admin/orders 요청에 대한 성공 코드, DTO 및 ApiResponse 포맷 정상 여부를 완벽히 검증했습니다.
  - OrderFacadeTest에 getAllOrdersForAdminSuccess 테스트를 추가하여, 퍼사드 레이어에서의 주문 정보와 회원 데이터의 융합 및 매핑 정합성을 단위 검증 완료했습니다.

### 15:37 | 인기 메뉴 조회 API 구현
* **[FEAT]** Bounded Context 단방향 의존성 제약 준수 및 설계
  - menu 패키지가 order 패키지를 참조하지 못하는 의존성 제약을 지키기 위해 PopularMenuProvider 인터페이스를 활용한 의존성 역전(DIP)을 구현했습니다.
* **[FEAT]** JPQL 기반 인기 메뉴 집계 쿼리 및 리포지토리 구현
  - OrderItemRepository를 신설하고 지정 범위 일수 내 주문 항목을 집계(SUM)하여 인기 메뉴 순으로 정렬하는 통계 JPQL 쿼리를 구현했습니다.
* **[API]** GET /menus/popular 인기 메뉴 목록 조회 API 구현
  - 최근 days(기본값 7) 일 동안의 누적 주문 수가 많은 상위 3가지 인기 메뉴를 조회하고 DTO 형식(menuId, name, price, orderCount)으로 매핑하여 반환하도록 구현했습니다.
* **[REFACTOR]** days 파라미터 양수 검증 가드 보완
  - MenuController에 @Validated 및 @Positive(message = "조회 기간은 양수여야 합니다.") 애너테이션을 적용하여 0 이하 정수 또는 음수 기간 인입을 차단했습니다.
* **[TEST]** Repository, Service, Controller 전 계층 슬라이스 및 단위 검증 완료
  - OrderItemRepositoryTest, MenuServiceTest, MenuControllerTest를 추가 및 갱신하여 전체 테스트를 정상 통과시켰습니다.

### 12:40 | 주문 상세 조회 API 구현 (피드백 반영 개정)
* **[FEAT]** Bounded Context 격리를 반영한 주문 상세 조회 구현 (GET /orders/{orderId})
  - OrderService가 타 도메인 Repository(MenuRepository)를 직접 의존하지 않도록 분리하고, OrderFacade가 OrderService(인가 검증 완료된 주문 획득)와 MenuService(메뉴 일괄 조회)를 조율하여 최종 DTO를 빌드 및 반환하도록 책임을 격리했습니다.
  - 헤더 X-Member-Id 또는 쿼리 파라미터 memberId를 통해 현재 로그인한 사용자 식별값을 제공받아 본인의 주문 정보와 대조하도록 구현했습니다.
* **[FEAT]** 에러 코드 접두사 규칙 준수 및 예외 추가
  - ErrorCode에 ORDER_NOT_FOUND (404 Not Found) 및 ORDER_UNAUTHORIZED_ACCESS (403 Forbidden)를 추가 정의하여 도메인 접두사 규칙(ORDER_)을 일치시켰습니다.
* **[PERF]** Fetch Join 및 IN 쿼리를 통한 N+1 쿼리 최적화
  - OrderRepository에 findByIdWithOrderItems 메서드를 추가하여 Order와 OrderItem을 Fetch Join으로 한 번에 로드했습니다.
  - MenuService에 getMenus 메서드를 추가해 연계된 menuId들을 IN 쿼리로 한 번에 일괄 조회하여 N+1 성능 저하 문제를 예방했습니다.
* **[SEC]** 식별자 파라미터 양수 방어 검증 도입
  - OrderController에 @Validated 및 경로 변수 orderId에 @Positive 애너테이션을 달아 유효하지 않은 식별자(0 이하, 음수) 인입에 대한 DB 조회를 원천 차단했습니다.
* **[TEST]** 주문 상세 조회 기능 테스트 검증 완료
  - OrderServiceTest: getOrderAndValidate 메서드의 성공 및 실패 예외 상황들을 검증하는 단위 테스트를 보완했습니다.
  - OrderFacadeTest: getOrderDetailSuccess 등 퍼사드 레이어의 DTO 맵핑 조합 비즈니스 로직을 검증하는 테스트를 추가했습니다.
  - OrderControllerTest: GET /orders/{orderId} 성공 및 실패 케이스를 OrderFacade 목킹으로 원복하여 검증하는 테스트를 갱신했습니다.

---

## 2026-07-15

### 17:00 | 프로젝트 문서 구조 개편 및 에이전트 오케스트레이션 규칙 정립
* **[DOCS]** 프로젝트 루트 마크다운 파일들의 docs/ 디렉토리 통합 이동
  - API.md, CONVENTION.md, ERD.md, REQUIREMENTS.md, WIREFRAME.md 파일들을 docs/ 디렉토리 하위로 이동하여 루트 구조를 정리했습니다.
* **[DOCS]** 에이전트 설정 파일 AGENTS.md 경량화
  - .agents/AGENTS.md 내에 공통 행동 규칙(변경 전 승인 필수, 이모지 금지, 한국어 사용, 개발 로그 작성 등)만 남겨두고 불필요한 컨벤션 세부 본문을 제거하여 가독성을 높였습니다.
* **[DOCS]** 에이전트 오케스트레이션 및 위험도 제어 규칙 문서 신설
  - docs/document_router.md: 토큰 절약을 위한 역할별 전용 조회 문서 맵 정의.
  - docs/orchestrator_rules.md: 메인 오케스트라의 태스크 위험도 분류(고/중/저) 및 2회 거절 시 안전 정지(Safety Stop) 로직 기술.
  - docs/subagent_rules.md: 구현, 검증, QA, 리뷰 에이전트의 역할 규정 및 테스팅 수준(통합, 슬라이스, 단위 테스트) 차등화 지침.
  - docs/adr.md: JPA 엔티티 롬복 애너테이션 제한, Bounded Context 간 의존성 제거, 트랜잭션 전파 및 위험 등급별 다층적 검증 전략 도입에 관한 아키텍처 의사결정 기록(ADR) 문서 작성.
* **[DOCS]** 에이전트 산출물 일관성을 위한 템플릿 신설
  - docs/safety_stop_template.md & docs/review_log_template.md: 에이전트 중단 리포트 및 상세 리뷰 로그용 마크다운 템플릿 기획.
  - .github/pull_request_template.md: PR(피얼) 제출 표준 템플릿 생성.
  - .github/ISSUE_TEMPLATE/feature_request.md & bug_report.md: 깃허브 신규 기능 추가 및 버그 리포트 이슈 템플릿 생성.
* **[DOCS]** README.md 관련 문서 바로가기 링크 갱신
  - 루트에서 docs/ 디렉토리로 이동한 마크다운 파일들의 참조 경로를 수정하고, 신규 설계된 7개의 규칙 및 템플릿 마크다운 문서 링크들을 새로 등재하였습니다.

---

### 16:15 | 2차 코드 리뷰 피드백 반영 (캡슐화 보완 및 에러 핸들링 유연화)
* **[REFACTOR]** OrderItem 연관관계 캡슐화 강화
  - OrderItem 엔티티의 setOrder() 편의 메서드를 제거하고, 빌더 생성자에 부모 Order 객체를 필수로 주입받도록 생명주기를 강하게 결합했습니다. (P1-1)
  - Order.addOrderItem()에서 양방향 연관관계를 직접 수립하는 책임을 빌더 시점으로 전가하여 영속 전 객체의 불완전 상태 가능성을 차단했습니다.
* **[REFACTOR]** 에러 코드 접두사 규격화 (P2-1)
  - DUPLICATE_USERNAME -> MEMBER_DUPLICATE_USERNAME
  - INVALID_CREDENTIALS -> MEMBER_INVALID_CREDENTIALS
  - INVALID_CHARGE_AMOUNT -> POINT_INVALID_CHARGE_AMOUNT
  - INSUFFICIENT_POINT -> POINT_INSUFFICIENT
  - 위 에러 코드 변경 사항을 모든 서비스, 엔티티, 컨트롤러 및 테스트 소스 코드에 전수 갱신했습니다.
* **[REFACTOR]** GlobalExceptionHandler 유효성 검증 예외 동적 매핑 (P2-2)
  - 특정 필드(amount)의 하드코딩 분기 제거를 위해, 검증 어노테이션 메시지에 [에러코드]:[메시지] 포맷을 인코딩하고 이를 파싱해내는 동적 맵핑 구조를 구현했습니다.
  - SYSTEM_INVALID_INPUT_VALUE 공통 에러 코드를 추가 정의하여 디폴트 예외 상황에 대비했습니다.
* **[ARCH]** 비동기 스레드간 MDC(Trace ID) 복사 구현 (P3-1)
  - ThreadPoolTaskExecutor의 TaskDecorator로 작동할 MdcTaskDecorator를 신규 개발 및 등록하여, 비동기 스레드 실행 시에도 원래 요청 스레드의 MDC(Trace ID) 정보가 유실되지 않도록 연계 전파 구조를 완성했습니다.
* **[DESIGN]** 메뉴 검증 책임의 Menu 도메인 위임 (P3-2)
  - OrderFacade의 상태 if 검사 로직을 Menu.validateAvailable() 도메인 메서드로 위임하여 캡슐화 수준을 향상시켰습니다.
* **[REFACTOR]** MenuService import 경로 정리 (P4-1)
  - Menu 및 CustomException 등의 풀 클래스 패키지 선언을 제거하고 최상단 import 영역으로 모아 가독성을 개선했습니다.

---

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
- [x] **[API]** 인기 메뉴 목록 조회 API 구현 (`GET /menus/popular`)
- [x] **[QUERY]** 최근 7일 동안의 주문 데이터를 group by 통계로 산출하는 DB 쿼리 구현
- [x] **[TEST]** 일주일 주문 통계가 정상적으로 필터링되어 출력되는지 검증 테스트 작성

### 5. [관리자] 전체 주문 조회 API 구현
- [ ] **[API]** 전체 주문 목록 조회 API 구현 (`GET /admin/orders`)
- [ ] **[TEST]** 전체 데이터가 최신순으로 잘 출력되는지 검증 테스트 작성

### 6. [관리자] 주문 상태 변경 API 구현
- [ ] **[API]** 주문 상태 변경 API 구현 (`PATCH /orders/{orderId}/status`)
- [ ] **[TEST]** 허용되지 않는 상태 전이 차단 및 올바른 상태 업데이트 비즈니스 정합성 테스트 작성
