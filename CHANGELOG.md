# 변경 이력 (CHANGELOG)

## 2026-07-14

### 추가
- 커피 메뉴 목록 조회 API 구현
  - GET /menus 엔드포인트를 제공하는 MenuController 구현
  - AVAILABLE 및 SOLD_OUT 상태의 메뉴 데이터를 조회하여 DTO로 매핑하는 MenuService 구현
  - 데이터베이스 PK 식별자(id)를 API 스펙 명세에 맞춘 menuId로 매핑하는 MenuResponse DTO 구현
  - 특정 메뉴 상태 목록으로 필터링 조회가 가능한 findAllByStatusIn 쿼리 메서드를 정의한 MenuRepository 구현
- 커피 메뉴 목록 조회 API 단위 테스트 구현
  - MenuServiceTest 구현: AVAILABLE 및 SOLD_OUT 상태 메뉴 데이터 조회 정합성 검증
  - MenuControllerTest 구현: GET /menus 호출 시 성공 응답 JSON 구조 및 데이터 형식 정합성 검증
- 회원 상세 및 잔여 포인트 조회 API 구현
  - GET /members/{memberId} 엔드포인트를 제공하는 MemberController 구현
  - memberId가 존재하지 않을 시 MEMBER_NOT_FOUND 예외를 던지며, 조회 시 회원 정보 및 실시간 포인트 잔액을 반환하는 MemberService 구현
  - 데이터베이스 PK 식별자(id)를 API 스펙에 맞춘 memberId로 매핑하는 MemberResponse DTO 구현
  - 존재하지 않는 회원 조회 시 에러 처리를 위한 ErrorCode.MEMBER_NOT_FOUND 상수 추가
- 회원 상세 및 잔여 포인트 조회 API 단위 테스트 구현
  - MemberServiceTest 구현: 회원 상세 및 잔여 포인트 조회 성공 시 DTO 반환 여부와 실패 시 예외 발생 여부 검증
  - MemberControllerTest 구현: GET /members/{memberId} 성공 시 응답 포맷 검증 및 예외 발생 시 에러 응답 포맷 검증
- 회원 상세 및 잔여 포인트 조회 API 2차 리뷰 피드백 반영
  - MemberController 및 memberId 파라미터에 @Validated 및 @Positive 유효성 검증 어노테이션 적용
  - 유효성 검증 적용을 위한 spring-boot-starter-validation 라이브러리 추가 (build.gradle)
  - MemberResponse DTO 내 Null 가드 절 구현을 통한 NullPointerException 예방
  - MemberControllerTest 내 mockMvc 경로 표현식을 파라미터화(get("/members/{memberId}", 1L))하도록 개선
