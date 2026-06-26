# CONTEXT.md

## Meta
- Last updated: 2026-06-26
- Owner: Ivan Poretskyi
- Status: active

## 1. System Overview
- Product domain: Strict bankroll management for safer gambling behavior.
- High-level architecture: Modular Java Spring Boot web application with separated domain, application, infrastructure, and web modules.
- Core modules:
  - `bankroll-domain`: pure business rules and value objects.
  - `bankroll-application`: use cases and orchestration.
  - `bankroll-infrastructure`: persistence, database migrations, external adapters.
  - `bankroll-web`: Spring Boot app, controllers, templates, static assets.

## 2. Tech Stack
- Frontend: Server-rendered web UI with Thymeleaf, HTML, CSS, and JavaScript where useful.
- Backend: Java 21, Spring Boot, Maven multi-module project.
- Database: MySQL.
- Migrations: Liquibase.
- Infra/Hosting: Local development through Docker Compose is preferred.
- CI/CD: GitHub pull requests and CI checks.

## 3. Repository Map
- `/`: Maven parent project, project documentation, team contracts.
- `/bankroll-domain`: domain model, value objects, calculations, unit tests.
- `/bankroll-application`: use cases and command/result models.
- `/bankroll-infrastructure`: database-related implementation and Liquibase changelogs.
- `/bankroll-web`: Spring Boot entry point, controllers, templates, CSS, JavaScript.
- `/TEAM_CONTRACTS.md`: team rules and architectural agreements.
- `/DEVELOPMENT_PLAN.md`: high-level implementation plan.

## 4. Global Rules
- Coding standards:
  - Package root must be `com.bankrolldiscipline`.
  - Domain classes must stay under `com.bankrolldiscipline.domain`.
  - Application use cases must stay under `com.bankrolldiscipline.application`.
  - Infrastructure code must stay under `com.bankrolldiscipline.infrastructure`.
  - Web code must stay under `com.bankrolldiscipline.web`.
- Money rules:
  - Java money values must use `Money`.
  - `Money` is backed by `BigDecimal`.
  - `double` and `float` are not allowed for money.
  - Web/API money values must use strings plus currency.
- Branching strategy:
  - Work in feature branches.
  - Open pull requests ready for review; do not create draft PRs.
- Error handling policy:
  - Domain validation errors should fail fast with stable, understandable `IllegalArgumentException` messages.
  - Raw parsing exceptions should not leak from public factory methods.
- Logging/observability policy:
  - Keep MVP simple; introduce structured logging when application services and persistence appear.

## 5. Domain Model
- Current value objects:
  - `Money`
  - `CurrencyCode`
  - `Percentage`
- Current domain concepts:
  - `FinancialProfile`
  - `FinancialProfileCalculation`
  - `DisposableIncomeCalculator`
- Planned concepts:
  - Planning period: week or month.
  - Bankroll limit.
  - Bankroll activity.
  - Warning state.
- Invariants:
  - Money must have a currency.
  - Financial profile money fields must share one currency.
  - Negative financial profile amounts are invalid.
  - Allocation percentage must be from 0 to 100 inclusive.
  - Active limits can be decreased but not increased.

## 6. API Contracts
- Public endpoints: Not finalized yet.
- Internal endpoints/events: Not finalized yet.
- Request/response contracts:
  - Money values must be string decimals.
  - Currency is currently USD for MVP.
  - Domain results should expose explicit fields rather than ambiguous maps.
- Backward compatibility rules:
  - Avoid breaking command/result field names once web forms and tests depend on them.

## 7. Data & Storage
- Schemas: Not implemented yet.
- Migration policy:
  - All database changes must go through Liquibase changelogs.
  - Migrations must be reviewable and deterministic.
- Retention policy: Not defined for MVP.
- Backup/recovery: Not defined for MVP.

## 8. Security & Compliance
- AuthN/AuthZ: Out of scope for MVP.
- Secrets handling:
  - Do not commit real database passwords or personal secrets.
  - Use local environment variables or local-only configuration for sensitive values.
- PII/data classification:
  - MVP may store sensitive financial self-reported data.
  - Avoid storing unnecessary personal information.
- Compliance constraints:
  - The app must not present itself as medical, legal, or gambling advice.
  - UI language should encourage control and stopping, not gambling performance.

## 9. Performance Constraints
- Latency SLO: Not formally defined; MVP pages should feel instant in local development.
- Throughput target: Not relevant for MVP.
- Cost constraints: Keep local development lightweight; Dockerized MySQL is preferred.

## 10. Architecture Decision Log
1. Decision: Web-first application.
   - Context: The team wants to start with web and move toward apps later.
   - Choice: Build Spring Boot web first.
   - Consequences: Thymeleaf pages and web controllers come before mobile clients.
2. Decision: MySQL instead of SQLite.
   - Context: The team wants production-like relational storage and Docker-based local development.
   - Choice: Use MySQL with Liquibase.
   - Consequences: Developers need Docker Desktop or local MySQL.
3. Decision: USD-only MVP.
   - Context: Multi-currency support adds complexity.
   - Choice: Start with USD.
   - Consequences: `CurrencyCode` remains useful, but UI initially exposes only USD.
4. Decision: Money through `Money` and `BigDecimal`.
   - Context: Floating-point types are unsafe for financial calculations.
   - Choice: Use `Money` everywhere in Java domain code.
   - Consequences: Tests must guard against accidental `double`/`float` money logic.

## 11. Dependencies
- External services: None for MVP.
- Third-party SDKs: None planned for MVP.
- Known limitations:
  - No authentication in MVP.
  - No real payment or gambling-platform integration.
  - No mobile app yet.

## 12. Operational Runbook
- Start: Use Maven/Spring Boot for the web module; Dockerized MySQL will be added/configured as the persistence layer matures.
- Build: `./mvnw verify` on Unix-like shells or `.\mvnw.cmd verify` on Windows.
- Test: Run module-level tests during feature work and full Maven verify before PR.
- Deploy: Not defined yet.
- Rollback: Not defined yet.
