# agent.md

## Mission
Help the team build Anti Gambling App predictably through the documentation loop:

`REQ -> CONTEXT -> STATE -> TDD -> DESIGN -> CODE`

The product goal is correctness and user protection before speed or visual flash.

## Source of Truth
1. `REQ.md` - why the product exists, MVP scope, acceptance criteria.
2. `CONTEXT.md` - architecture, stack, global rules, domain invariants.
3. `STATE.md` - current status, active tasks, backlog, handoff notes.
4. `TDD.md` - how quality is verified.
5. `DESIGN.md` - how the UI should look and feel.
6. `TEAM_CONTRACTS.md` - team agreements and code ownership rules.

## Routing Rules
- If the request is about business value, users, scope, or acceptance criteria: update `REQ.md`.
- If the request is about architecture, modules, stack, or invariants: update `CONTEXT.md`.
- If the request is about current progress, next tasks, or ownership: update `STATE.md`.
- If the request is about tests or quality gates: update `TDD.md`.
- If the request is about UI, layout, colors, components, or copy tone: update `DESIGN.md`.
- If the request changes team workflow: update `TEAM_CONTRACTS.md` when appropriate.

## Working Loop
1. Read `REQ.md`, `CONTEXT.md`, and `TEAM_CONTRACTS.md`.
2. Check `STATE.md` for current objective and next actions.
3. Define or update tests using `TDD.md`.
4. Implement the smallest correct slice.
5. Run relevant tests.
6. Update `STATE.md` when progress, risks, or next actions change.
7. If UI changed, check against `DESIGN.md`.

## Quality Gates
- No feature without acceptance criteria.
- No domain rule without tests.
- No money calculation using `double` or `float`.
- No financial value in Java domain code outside `Money`.
- No active limit increase inside the current planning period.
- No UI copy that encourages gambling or gives betting advice.
- No draft pull requests.

## Product Guardrails
- This is not a gambling assistant.
- This is not a betting strategy tool.
- This is not a casino integration product.
- The app helps users set limits, track bankroll, receive warnings, and reduce impulsive decisions.

## Document Hygiene
- Keep documents short, factual, and current.
- Do not duplicate the same decision in many places unless each document needs it from its own angle.
- Prefer clear decisions over vague plans.
- Update dates when content materially changes.
