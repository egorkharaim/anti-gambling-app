# Командные контракты

Этот документ фиксирует рабочие договорённости между `bankroll-web` и слоями
`bankroll-application` / `bankroll-domain` / `bankroll-infrastructure`.

Цель простая: чтобы два разработчика могли идти параллельно и не расходились в:

- названиях use case;
- названиях DTO и полей;
- форматах денег, дат и ошибок;
- границах ответственности между слоями.

Если правило из этого файла меняется, сначала обновляется документ, потом код.

## 1. Границы ответственности

### Разработчик 1 — domain/application/data

Отвечает за:

- доменные модели и финансовые правила;
- application use case;
- интерфейсы репозиториев;
- MySQL и Liquibase;
- unit и integration tests.

### Разработчик 2 — web/ux

Отвечает за:

- Spring MVC и REST-контроллеры;
- request/response DTO web-слоя;
- HTML, CSS, JavaScript и Thymeleaf;
- валидацию пользовательского ввода на уровне формы;
- отображение результатов use case.

### Общее правило

Финансовые вычисления в JavaScript запрещены.

Web-слой:

- принимает ввод;
- передаёт команды в application;
- показывает результат;
- не пересчитывает лимиты, деньги, статистику и warning-логику.

## 2. Package и naming contracts

- Базовый package root проекта: `com.bankrolldiscipline`.
- `BankrollManagerApplication` находится в `com.bankrolldiscipline`.
- Доменные классы лежат внутри `com.bankrolldiscipline.domain`.
- Application-контракты лежат внутри `com.bankrolldiscipline.application`.
- Infrastructure-адаптеры лежат внутри `com.bankrolldiscipline.infrastructure`.
- Web-контроллеры, формы и view-model лежат внутри `com.bankrolldiscipline.web`.

Это нужно, чтобы Spring корректно сканировал проект и чтобы структура была
предсказуемой для обоих разработчиков.

## 3. Контракт именования

- Доменные сущности и value object называются существительными:
  `Money`, `FinancialProfile`, `BankrollPlan`, `GameSession`.
- Use case называются действием:
  `CreateFinancialProfileUseCase`, `UpdateFinancialProfileUseCase`,
  `CreateBankrollPlanUseCase`, `StartGameSessionUseCase`.
- Команды use case называются с суффиксом `Command`.
- Результаты use case называются с суффиксом `Result`.
- Web DTO называются с суффиксами `Request` и `Response`.
- Mapper не содержит бизнес-логики.

## 4. Первый обязательный контракт по данным

### Деньги

- Тип денег в Java: только `Money`.
- Внутри `Money` используется `BigDecimal`.
- Создание денег из `double` запрещено.
- Валюта передаётся отдельным кодом, например `PLN`, `EUR`, `USD`.
- На границе web/API денежные значения передаются строкой, а не `double`.

Пример:

```json
{
  "monthlyIncome": "3500.00",
  "currency": "PLN"
}
```

### Даты и время

- `LocalDate` используется для периода плана.
- `Instant` используется для событий во времени.
- Время хранится в UTC.
- Часовой пояс пользователя хранится отдельно.

### Идентификаторы

- Пока используем `Long` для идентификаторов агрегатов.
- Поле идентификатора в DTO называется `id`.

## 5. Первый набор application-контрактов

Это минимальный набор, который нужен для параллельной работы.

### 5.1 Financial profile

Use case:

- `CreateFinancialProfileUseCase`
- `GetFinancialProfileUseCase`
- `UpdateFinancialProfileUseCase`

Команда создания/обновления профиля должна содержать:

- `currency`
- `monthlyIncome`
- `mandatoryExpenses`
- `savingsGoal`
- `emergencyContribution`
- `allocationPercentage`
- `timeZone`

Результат профиля должен содержать:

- `id`
- `currency`
- `monthlyIncome`
- `mandatoryExpenses`
- `savingsGoal`
- `emergencyContribution`
- `allocationPercentage`
- `timeZone`
- `disposableIncome`
- `recommendedLimit`

### 5.2 Bankroll plan

Use case:

- `CreateBankrollPlanUseCase`
- `GetBankrollPlansUseCase`
- `GetBankrollPlanDetailsUseCase`
- `DecreaseActiveLimitUseCase`
- `ActivateBankrollPlanUseCase`

Команда создания плана должна содержать:

- `type`
- `periodStart`
- `periodEnd`
- `initialLimit`
- `sessionLossLimit`
- `maxSessionsPerDay`
- `maxSessionDurationMinutes`

Результат плана должен содержать:

- `id`
- `type`
- `periodStart`
- `periodEnd`
- `recommendedLimit`
- `initialLimit`
- `currentLimit`
- `usedLossLimit`
- `remainingLossLimit`
- `status`

### 5.3 Sessions

Use case:

- `StartGameSessionUseCase`
- `FinishGameSessionUseCase`
- `GetActiveGameSessionUseCase`
- `GetGameSessionHistoryUseCase`

Команда старта сессии должна содержать:

- `startedAt`
- `openingBalance`

Команда завершения сессии должна содержать:

- `sessionId`
- `endedAt`
- `deposited`
- `withdrawn`
- `closingBalance`
- `turnover`
- `notes`

Результат сессии должен содержать:

- `id`
- `startedAt`
- `endedAt`
- `openingBalance`
- `deposited`
- `withdrawn`
- `closingBalance`
- `turnover`
- `result`
- `lossLimitUsed`
- `status`

## 6. Web routing contracts

До появления полной бизнес-логики web-слой может безопасно строить страницы:

- `/`
- `/profile`
- `/plans`
- `/sessions`
- `/calendar`
- `/statistics`

Пока у use case нет стабильной реализации, страницы могут использовать:

- пустые шаблоны;
- mock view model;
- статические заглушки;
- form layout без сохранения.

Но web-слой не должен придумывать собственные финансовые формулы.

## 7. Error contract

Если application отклоняет команду по бизнес-правилу, web-слой должен получить
стабильную ошибку с:

- `code`
- `message`

Примеры кодов:

- `CURRENCY_ALREADY_LOCKED`
- `ACTIVE_LIMIT_INCREASE_NOT_ALLOWED`
- `PLAN_PERIOD_OVERLAP`
- `ACTIVE_SESSION_ALREADY_EXISTS`
- `SESSION_END_BEFORE_START`

Тексты ошибок для пользователя могут отличаться от внутренних `code`, но сами
`code` должны быть стабильными.

## 8. Что web может делать без ожидания Ивана

Разработчик 2 может делать сразу:

- layout приложения;
- навигацию;
- шаблоны страниц;
- form markup;
- client-side UX-валидацию обязательности полей;
- отображение статусов и warning-блоков как UI-компонентов.

Разработчик 2 не должен без согласования фиксировать:

- состав полей application command;
- формулу расчёта сумм;
- финальные enum-значения бизнес-статусов;
- структуру persistence-модели.

## 9. Что нужно согласовать перед следующими PR

Перед PR с реальной интеграцией нужно совместно подтвердить:

1. Финальные имена первых use case и DTO.
2. Формат передачи `Money` между web и application.
3. Список бизнес-ошибок для profile и plan.
4. Enum-значения `PlanType`, `PlanStatus`, `SessionStatus`.
5. Какие поля обязательны в profile, plan и session form.

## 10. Правило изменения контрактов

Если один разработчик хочет изменить:

- имя поля;
- имя use case;
- enum;
- формат даты, денег или ошибки;

то он сначала обновляет этот документ в отдельном PR или в начале своего PR, и
только потом меняет код.
