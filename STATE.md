# STATE.md

## Meta
- Last updated: 2026-06-26 14:45
- Owner: Ivan Poretskyi
- Current phase: build

## 1. Current Objective
- Sprint/iteration goal: Build a locally working web MVP slice with landing page, mock dashboard flow, strict DUI JSON validation, and safe fallback rendering.
- Deadline: Not fixed.
- Definition of done:
  - Domain behavior is covered by unit tests.
  - Web routes and mock API are covered by tests.
  - No financial calculation uses floating-point types.
  - Documentation is updated when scope or rules change.

## 2. Status Snapshot
- Overall: on-track
- Completion: 40%
- Main blocker: Persistence and real application use cases are not implemented yet.

## 3. Active Tasks
| ID | Task | Owner | Status | ETA | Notes |
|---|---|---|---|---|---|
| TASK-001 | Review and merge financial profile domain PR | Ivan | review | next | PR #8 is open with financial profile calculations. |
| TASK-002 | Define bankroll limit domain rules | Ivan | todo | next | Must support week/month planning and no active limit increases. |
| TASK-003 | Define warning thresholds | Ivan | done | now | Mock flow uses safe, warning at 75%+, danger at 100%+. |
| TASK-004 | Add application use cases for financial profile | Ivan | todo | later | Create/get/update use cases after domain PR is accepted. |
| TASK-005 | Local web MVP shell with DUI mock-flow | Ivan | done | now | Landing, dashboard, forms, validation, fallback, and tests are in place. |

## 4. Backlog
| Priority | Item | Impact | Effort | Status |
|---|---|---|---|---|
| P0 | Financial profile application use cases | high | medium | todo |
| P0 | Bankroll limit domain | high | medium | todo |
| P0 | Bankroll activity tracking domain | high | medium | todo |
| P0 | Warning calculation domain | high | medium | todo |
| P1 | MySQL persistence for profiles and limits | high | high | todo |
| P1 | Web forms for financial profile and limit setup | high | high | todo |
| P1 | Dashboard showing remaining bankroll and warnings | high | high | todo |
| P2 | Docker Compose for local MySQL | medium | low | todo |
| P2 | Integration tests with database | medium | medium | todo |

## 5. Recently Completed
- 2026-06-26: Project setup with Maven, Spring Boot, MySQL, and Liquibase was merged.
- 2026-06-26: `Money` and `CurrencyCode` domain primitives were merged.
- 2026-06-26: Web application shell was merged.
- 2026-06-26: Financial profile domain branch was created and pushed as PR #8.
- 2026-06-26: Local web MVP slice was added with landing page, dashboard mock-flow, strict DUI whitelist validation, fallback rendering, and web/API tests.

## 6. Risks
| Risk | Probability | Impact | Mitigation | Owner |
|---|---|---|---|---|
| Scope creep into auth/payments/integrations | med | high | Keep MVP scope in `REQ.md` and reject unrelated tasks. | Ivan |
| Incorrect financial calculations | med | high | Use `Money`, `BigDecimal`, and domain unit tests. | Ivan |
| UI accidentally encourages gambling | med | high | Keep copy focused on limits, warnings, and stopping. | Ivan |
| Persistence design appears before domain is stable | med | med | Finish domain invariants before database schema. | Ivan |

## 7. Decisions Since Last Update
- Decision: Start with USD only.
  - Why: Reduces MVP complexity.
  - Tradeoff: Multi-currency UX is postponed.
- Decision: User chooses week or month planning period.
  - Why: Different users plan behavior on different time horizons.
  - Tradeoff: Domain must model period explicitly.
- Decision: Active limits can only be decreased.
  - Why: Prevents emotional limit escalation.
  - Tradeoff: User must wait for the next period to set a higher limit.
- Decision: Current work will be done in Ivan's environment.
  - Why: Simplifies the first implementation phase.
  - Tradeoff: Team handoff still needs docs and clear PRs.
- Decision: Use a local DUI mock-flow before persistence.
  - Why: Gives a working product slice while domain/application contracts mature.
  - Tradeoff: Data is local/mock and must be replaced by real use cases later.

## 8. Next 3 Actions
1. Finish review cycle for PR #8.
2. Implement bankroll limit domain with week/month period and no-increase invariant.
3. Replace local DUI mock calculations with application use cases after domain rules are merged.

## 9. Handoff Notes
- What the next contributor should do first: Read `REQ.md`, `CONTEXT.md`, `TEAM_CONTRACTS.md`, and current PRs before coding.
- What to avoid:
  - Do not add auth, payments, or gambling integrations to MVP.
  - Do not use `double` or `float` for money.
  - Do not create draft pull requests.
