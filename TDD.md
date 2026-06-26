# TDD.md

## Meta
- Last updated: 2026-06-26
- Owner: Ivan Poretskyi
- Status: active

## 1. Testing Strategy
- Primary approach: TDD for domain and application logic.
- Workflow: red -> green -> refactor.
- Test pyramid ratio:
  - Mostly unit tests for domain and application.
  - Some integration tests for persistence once MySQL repositories exist.
  - Light web/controller tests for page routing and validation.
- Coverage target: No numeric target yet; every business rule must have at least one direct test.

## 2. Test Environments
- Local: Windows development environment with Java 21, Maven Wrapper, Git, and Docker Desktop.
- CI: GitHub checks should run full Maven verification.
- Staging: Not defined for MVP.

## 3. Test Types
- Unit:
  - `Money`
  - `CurrencyCode`
  - `Percentage`
  - `FinancialProfile`
  - Bankroll limits
  - Warning calculations
- Integration:
  - MySQL persistence through repositories.
  - Liquibase migrations.
- Contract:
  - Application command/result models once use cases exist.
- E2E:
  - Not required in the first MVP slice.
- Visual regression:
  - Not required yet.
- Performance smoke:
  - Not required yet.

## 4. Red-Green-Refactor Workflow
1. Write a failing test that captures the behavior.
2. Implement minimal code to pass.
3. Refactor with tests green.
4. Run module tests.
5. Run full `mvnw verify` before commit/PR.
6. Update docs and `STATE.md` if the behavior changes project scope or status.

## 5. Feature Test Template

### Feature Name
- Requirement link: `REQ.md#6-functional-requirements`
- Context link: `CONTEXT.md#5-domain-model`

### Cases
1. Happy path: valid input produces expected domain result.
2. Validation: invalid input fails with a stable, understandable exception.
3. Error handling: malformed parsing input does not leak raw low-level exceptions.
4. Edge conditions: zero, exact limit boundary, max/min percentage, period boundary.

### Test Data
- Fixtures: Build explicit test values in each test unless reuse improves clarity.
- Factories: Add test factories only when setup becomes repetitive.
- Mocks/stubs: Avoid mocks in pure domain tests.

### Exit Criteria
- All mandatory cases are green.
- No flaky tests.
- Full Maven verification passes before PR.
- Code follows team contracts.

## 6. Regression Checklist
- Existing core flows unaffected.
- `Money` remains the only Java representation for money.
- Currency mismatches are rejected.
- Active limit increase remains impossible.
- Warnings remain text-visible, not color-only.
- Critical bug fixes are covered by tests.

## 7. Quality Gates in CI
- Checkstyle/lint: Must pass.
- Unit tests: Must pass.
- Integration tests: Must pass once they exist.
- E2E smoke: Not required yet.
- Coverage threshold: Not configured yet.

## 8. Defect Log Template
| Bug ID | Found in | Test added? | Root cause | Preventive action |
|---|---|---|---|---|
| BUG-001 |  | yes/no |  |  |

## 9. Flakiness Protocol
- How to quarantine: Do not ignore flaky tests silently; mark the issue in PR notes and fix before merge when it touches core financial logic.
- Max quarantine period: No quarantine for financial domain tests.
- Owner to fix: Feature owner.
