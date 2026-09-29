# RFC — Home/Search/Theme Polish v1 (2026-09-29)

Status: IMPLEMENTED LOCALLY — CI PENDING — DEVICE UAT PENDING

## Scope

Small visual/behavior polish batch after Home Empty State + Tentang NOTEZ v1.

### Home Empty State Polish P1

- Status bar follows the active theme home background for a more immersive first screen.
- Welcome horse icon enlarged to the 110–120dp range.
- Added subtle static glow behind the horse icon.
- Strengthened title hierarchy: NOTEZ at 28sp, bold, with light letter spacing.
- Tagline/hint use muted opacity to stay elegant and less noisy.
- Spacing tightened into clearer hierarchy: icon/title, title/tagline, tagline/hint.
- FAB keeps the same action but has stronger 8dp elevation/pressed shadow.

### Search Eye P2

- Keep the eye icon as NOTEZ identity.
- Hide the eye while total notes = 0.
- Show the eye once at least one note exists.
- Search no-result state remains separate and does not hide the eye merely because active search results are empty.

### Theme Picker Grouping P2

- No theme was removed.
- Grouped order:
  - NOTEZ SIGNATURE: OLED Black, NOTEZ You Dark, Cobalt2, NOTEZ You Warm.
  - GLOOMY SERIES: Gloomy Sakura Night, Gloomy Lavender, Gloome Dark Sunset, Raspberry Night.
  - COZY EARTH: Dark Forest, Fade Choco Matcha, Kawaii Catpucinn.
  - CODER NIGHT: GitHub Dark, Tokyo Night, Blue Moon Cheese.
- Active theme stays in its own group; it is not promoted to the top/bottom.
- Preview chip becomes a clearer mini-card: outer background = theme background, inner card = theme surface, Aa = theme text, accent bar = theme accent.
- No Compose/LazyColumn/stickyHeader rewrite in this pass.

## Implementation Notes

- Main home layout polish lives in `app/src/main/res/layout/activity_main.xml`.
- Static glow drawable: `app/src/main/res/drawable/bg_notez_empty_horse_glow.xml`.
- Search eye total-note presence logic lives in `MainActivity.kt` using a separate total-note observer.
- Theme grouping and mini-card previews live in `SettingsActivity.kt`.

## Local Checks

- XML parse: PASS.
- No `android.permission.INTERNET`: PASS.
- No `addJavascriptInterface`: PASS.
- No CDN marker: PASS.
- `git diff --check`: PASS.
- Local Android build: NOT RUN — no Gradle wrapper/global Gradle available in sandbox.

## CI Evidence

Pending push and GitHub Actions run.

## Device UAT

Pending user/device verification.
