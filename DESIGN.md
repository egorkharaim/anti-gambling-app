# DESIGN.md

## Meta
- Last updated: 2026-06-26
- Owner: Ivan Poretskyi
- Status: active
- Inspiration: Resend-style minimal dark developer UI from getdesign.md.

## 1. Visual Theme & Atmosphere
Anti Gambling App should feel strict, calm, and precise. The interface is not a casino dashboard and must never borrow gambling aesthetics. It should feel closer to a financial control panel: dark canvas, clear numbers, quiet cards, strong warnings, and zero visual excitement around play.

The Resend-inspired direction is useful because it is minimal, dark, text-led, and technical. We adapt that language for a user-facing financial discipline tool:

- Dark-first canvas.
- Off-white text.
- Thin borders instead of heavy shadows.
- Small, precise buttons.
- Monospace accents for amounts, limits, and status labels.
- Semantic warnings that are clear but not sensational.

## 2. Color Palette & Roles
- Canvas: `#000000`
- Surface card: `#0a0a0c`
- Surface elevated: `#101012`
- Surface deep: `#06060a`
- Primary text: `#fcfdff`
- Body text: `rgba(252,253,255,0.86)`
- Secondary text: `rgba(252,253,255,0.7)`
- Muted text: `#a1a4a5`
- Hairline: `rgba(255,255,255,0.06)`
- Hairline strong: `rgba(255,255,255,0.14)`
- Primary button background: `#fcfdff`
- Primary button text: `#000000`
- Link/accent blue: `#3b9eff`
- Success: `#11ff99`
- Warning: `#ffc53d`
- Danger: `#ff2047`

### Product-specific semantic colors
- Safe state: green, used sparingly for “within limit”.
- Warning state: yellow, used for approaching the limit.
- Danger state: red, used for limit reached/exceeded or risky behavior warning.
- Neutral state: muted gray, used for inactive periods and secondary explanations.

## 3. Typography Rules
- UI font: Inter or system sans-serif.
- Numeric/technical accents: Geist Mono, JetBrains Mono, or another readable monospace.
- Display text: use large, calm sans-serif headings; do not use casino/gaming-style display fonts.

### Hierarchy
| Role | Size | Weight | Use |
|---|---:|---:|---|
| Page hero | 48-72px | 400-600 | Landing/dashboard opener |
| Section title | 24-32px | 500-600 | Main cards and page sections |
| Card title | 18-24px | 500 | Financial profile, bankroll, warnings |
| Body | 14-16px | 400 | Explanations |
| Caption | 12-13px | 400 | Metadata and helper text |
| Amount | 18-40px | 500-600 | Money values, preferably monospace |
| Status label | 11-13px | 500-600 | Uppercase or compact labels |

## 4. Component Styling

### Buttons
- Primary button:
  - White/off-white background.
  - Black text.
  - 8px border radius.
  - 36-40px height.
  - Used for the next safe action, not for encouraging play.
- Secondary button:
  - Elevated dark surface.
  - Off-white text.
  - 1px hairline border.
- Destructive/risk button:
  - Avoid bright red filled buttons unless the action is truly destructive.
  - Prefer outlined danger styling with explicit text.

### Cards
- Use dark cards on black canvas.
- 12px border radius.
- 1px translucent border.
- 24-32px internal padding.
- No heavy drop shadows.
- Cards should represent decisions and facts:
  - Financial profile.
  - Recommended limit.
  - Active bankroll.
  - Remaining amount.
  - Warning state.

### Inputs
- Dark background.
- 1px strong hairline border.
- 8px radius.
- Clear label above every input.
- Helper text for financial consequences.
- Money inputs must show currency separately, initially USD.

### Warning blocks
- Must include text, not just color.
- Warning block should explain:
  - current usage;
  - remaining amount;
  - suggested safe action, usually stop, pause, or reduce limit.
- Danger copy must be direct but not shame-based.

## 5. Layout Principles
- Base spacing: 4px/8px scale.
- Main content max width: around 1200px.
- Dashboard cards: 1 column on mobile, 2-3 columns on desktop.
- Keep generous vertical spacing between major sections.
- Important numbers should be visually isolated.
- Avoid dense trading-dashboard layouts; this app is about control, not action.

## 6. Depth & Elevation
- Level 0: black canvas.
- Level 1: surface card with subtle border.
- Level 2: elevated card for active decision or current period.
- Level 3: warning card with semantic border/accent.
- No traditional drop shadows.
- No glowing casino-style neon treatment.

## 7. Do's and Don'ts

### Do
- Do keep the interface serious and calm.
- Do make remaining bankroll and limit status obvious.
- Do use monospace for important money values.
- Do make warnings impossible to miss.
- Do explain why a limit cannot be increased.
- Do keep forms short and readable.

### Don't
- Don't use gambling imagery: dice, cards, slots, chips, jackpot language.
- Don't use “win”, “boost”, “play smarter”, or betting advice language.
- Don't make limit changes feel like a game.
- Don't hide danger states behind color-only indicators.
- Don't add auth/payments/integrations to MVP UI.

## 8. Responsive Behavior
- Mobile first:
  - single-column cards;
  - large touch targets;
  - sticky or easy-to-find current limit summary.
- Desktop:
  - dashboard can use 2-3 columns;
  - financial profile form and result summary can sit side by side.
- Warning states must remain visible on all screen sizes.

## 9. Agent Prompt Guide
- Build screens as a strict financial control interface, not a gambling app.
- Use dark Resend-inspired minimalism: black canvas, subtle borders, off-white text, monospace numeric accents.
- Always show money values clearly with USD.
- For warnings, include textual explanation and semantic color.
- If a UI choice could encourage gambling, choose the calmer control-oriented option.
