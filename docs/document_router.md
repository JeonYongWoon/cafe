# 문서 조회 라우터 (Document Router)

본 문서는 에이전트들이 작업을 수행할 때 불필요한 마크다운 문서를 전체 스캔하여 토큰을 낭비하는 상황을 방지하기 위한 라우팅 맵입니다. 각 에이전트 세션은 자신의 역할 및 수행 태스크에 지정된 문서만 최소한으로 로드해야 합니다.

## 1. 에이전트 역할별 전용 조회 문서 맵

| 에이전트 역할 | 필수 조회 문서 | 선택 조회 문서 (필요 시에만) |
| :--- | :--- | :--- |
| **메인 오케스트라** | [orchestrator_rules.md](file:///Users/t2026-m0045/Downloads/cafe/docs/orchestrator_rules.md)<br>[document_router.md](file:///Users/t2026-m0045/Downloads/cafe/docs/document_router.md) | [REQUIREMENTS.md](file:///Users/t2026-m0045/Downloads/cafe/docs/REQUIREMENTS.md)<br>[safety_stop_template.md](file:///Users/t2026-m0045/Downloads/cafe/docs/safety_stop_template.md) |
| **구현 에이전트** | [subagent_rules.md](file:///Users/t2026-m0045/Downloads/cafe/docs/subagent_rules.md)<br>[CONVENTION.md](file:///Users/t2026-m0045/Downloads/cafe/docs/CONVENTION.md)<br>[adr.md](file:///Users/t2026-m0045/Downloads/cafe/docs/adr.md)<br>[ai_common_mistakes.md](file:///Users/t2026-m0045/Downloads/cafe/docs/ai_common_mistakes.md) | [API.md](file:///Users/t2026-m0045/Downloads/cafe/docs/API.md)<br>[ERD.md](file:///Users/t2026-m0045/Downloads/cafe/docs/ERD.md) |
| **검증 에이전트** | [subagent_rules.md](file:///Users/t2026-m0045/Downloads/cafe/docs/subagent_rules.md)<br>[CONVENTION.md](file:///Users/t2026-m0045/Downloads/cafe/docs/CONVENTION.md) | [adr.md](file:///Users/t2026-m0045/Downloads/cafe/docs/adr.md) |
| **QA 에이전트** | [subagent_rules.md](file:///Users/t2026-m0045/Downloads/cafe/docs/subagent_rules.md)<br>[API.md](file:///Users/t2026-m0045/Downloads/cafe/docs/API.md) | [REQUIREMENTS.md](file:///Users/t2026-m0045/Downloads/cafe/docs/REQUIREMENTS.md)<br>[WIREFRAME.md](file:///Users/t2026-m0045/Downloads/cafe/docs/WIREFRAME.md) |
| **리뷰 에이전트** | [subagent_rules.md](file:///Users/t2026-m0045/Downloads/cafe/docs/subagent_rules.md)<br>[review_log_template.md](file:///Users/t2026-m0045/Downloads/cafe/docs/review_log_template.md)<br>[ai_common_mistakes.md](file:///Users/t2026-m0045/Downloads/cafe/docs/ai_common_mistakes.md) | [CONVENTION.md](file:///Users/t2026-m0045/Downloads/cafe/docs/CONVENTION.md)<br>[adr.md](file:///Users/t2026-m0045/Downloads/cafe/docs/adr.md) |

## 2. 규격 템플릿 문서 맵

- **이슈 생성 시 템플릿**:
  - 신규 기능 구현: [feature_request.md](file:///Users/t2026-m0045/Downloads/cafe/.github/ISSUE_TEMPLATE/feature_request.md)
  - 버그 리포트: [bug_report.md](file:///Users/t2026-m0045/Downloads/cafe/.github/ISSUE_TEMPLATE/bug_report.md)
- **PR 생성 시 템플릿**: [pull_request_template.md](file:///Users/t2026-m0045/Downloads/cafe/.github/pull_request_template.md)
