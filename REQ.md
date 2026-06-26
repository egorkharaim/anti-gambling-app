# REQ.md

## Meta
- Last updated: 2026-06-26
- Owner: Ivan Poretskyi
- Status: active

## 1. Product Brief
- Product/Feature: Anti Gambling App.
- One-line value proposition: Help users define a strict bankroll limit in advance, track it, and receive warnings before they lose control.
- Why now: The project needs a clear MVP scope before feature-by-feature implementation with two developers and an AI coding agent.

## 2. Target Audience
- Primary segment: People who gamble or place bets and want better control over their bankroll.
- Secondary segment: Beginners and users at risk of impulsive gambling behavior.
- Context of use: A user plans the next week or month, sets a financial profile, chooses a limit, tracks play, and receives warnings.
- Pain points:
  - Impulsive decisions during play.
  - Losing track of available bankroll.
  - Increasing limits after emotions take over.
  - Confusing money calculations or inconsistent currency handling.

## 3. Product Goals
- Keep the MVP focused on accounting and warnings.
- Make money calculations correct, predictable, and test-covered.
- Prevent limit increases inside an active planning period.
- Keep the interface simple enough that users can make decisions before play, not during emotional pressure.

## 4. Jobs To Be Done
- When I plan my gambling budget for the next week or month, I want to define a strict limit so I can avoid spending more than I intended.
- When I record bankroll activity, I want to see how much remains so I can stop before crossing my own boundary.
- When my remaining bankroll becomes risky, I want clear warnings so I can pause before making impulsive decisions.

## 5. Scope

### In scope for MVP
- Financial profile.
- Recommended bankroll limit calculation.
- User-selected planning period: week or month.
- Bankroll accounting.
- Warnings based on limit usage.
- USD as the initial currency.
- Web application first.
- Domain and application logic covered by tests.

### Out of scope for MVP
- Authentication and user accounts.
- Real payments.
- Casino, bookmaker, or gambling-platform integrations.
- Advice on how to win or play.
- Blocking websites or apps.
- Mobile applications.
- Multi-currency support beyond USD.

## 6. Functional Requirements
1. FR-001: The system must allow a user to define a financial profile with monthly income, mandatory expenses, savings goal, emergency contribution, allocation percentage, currency, and time zone.
2. FR-002: The system must calculate disposable income and a recommended gambling limit from the financial profile.
3. FR-003: The system must allow the user to choose a planning period: week or month.
4. FR-004: The system must allow the user to set a bankroll limit for the selected period.
5. FR-005: The system must not allow increasing a limit inside the active planning period.
6. FR-006: The system may allow decreasing a limit inside the active planning period.
7. FR-007: The system must track bankroll activity and remaining available bankroll.
8. FR-008: The system must show warnings when spending approaches or reaches the active limit.
9. FR-009: All money values in Java domain code must use `Money` backed by `BigDecimal`; `double` and `float` are not allowed for money.
10. FR-010: Web/API money values must be represented as decimal strings with a separate currency field.

## 7. Non-Functional Requirements
- Correctness: Financial calculations must be deterministic and covered by tests.
- Reliability: Invalid money, currency, percentage, and period input must fail with clear validation errors.
- Security: MVP stores no payment credentials and performs no gambling-platform integration.
- Accessibility: Warnings must not rely on color alone; text must clearly explain the state.
- Localization: Initial MVP may be English or Russian in UI, but money/currency logic starts with USD only.

## 8. User Scenarios
1. Happy path: User creates a financial profile, receives a recommended limit, chooses week/month, sets a limit, records activity, and sees remaining bankroll.
2. Edge case: User tries to increase an active limit; the system rejects it and explains that only decreases are allowed during the period.
3. Warning path: User records activity close to the limit; the system shows a visible warning with remaining amount and risk level.

## 9. Acceptance Criteria
1. Given a valid financial profile, when calculations run, then disposable income and recommended limit are computed using `Money` and `BigDecimal`.
2. Given an active limit, when the user attempts to increase it, then the change is rejected.
3. Given an active limit, when the user decreases it, then the new lower limit is accepted.
4. Given bankroll usage near the configured warning threshold, when the page is shown, then the user sees a clear warning message.

## 10. Success Metrics
- North star metric: Users can complete a full plan-and-track flow without increasing an active limit.
- Leading indicators:
  - Financial profile completion rate.
  - Number of bankroll plans created.
  - Number of warning states displayed before a limit is crossed.
- Guardrail metrics:
  - No money calculations using floating-point types.
  - No MVP feature that encourages gambling or gives betting advice.

## 11. Risks and Assumptions
- Assumption: Users make the main limit decision before the next week or month starts.
- Assumption: USD-only MVP is acceptable for the first version.
- Risk: Overbuilding account/auth/payment features too early.
  - Mitigation: Keep MVP focused on local accounting and warnings.
- Risk: UI language could accidentally sound like gambling advice.
  - Mitigation: Warnings must focus on control, limits, and stopping behavior.

## 12. Open Questions
1. Which warning thresholds should be used first: 50%, 75%, 90%, 100%, or a simpler set?
2. Should MVP UI language be English, Russian, or mixed during development?
3. Should bankroll activity initially track only spending, or also wins/losses/deposits separately?
