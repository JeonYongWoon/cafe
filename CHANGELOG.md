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
