# UAT — Home/Search/Theme Polish v1 (2026-09-29)

Status: PENDING DEVICE UAT

## Build Under Test

- Commit: `f8be97a feat: polish home search and theme picker`.
- GitHub Actions run: `36526954000` — `success` on rerun attempt 2.
- Artifact: `notez-debug`, artifact id `11015401107`, size `8,099,421` bytes.

## Checklist

### A. Home Empty State Polish

1. Install/open fresh build with zero notes.
2. Confirm status bar visually follows current theme/home background.
3. Confirm horse icon is larger than previous build and still centered/elegant.
4. Confirm subtle glow is visible but not splash-like or ramai.
5. Confirm NOTEZ title is stronger, larger, bold, and lightly spaced.
6. Confirm tagline is muted and readable.
7. Confirm spacing feels cleaner: icon/title, title/tagline, tagline/hint.
8. Confirm FAB `+` remains at bottom-right and shadow/elevation is more visible.

Expected: empty home feels immersive, minimal, and polished.

### B. Search Eye Visibility

1. With zero notes, confirm eye/search icon is hidden.
2. Add one note.
3. Return to home and confirm eye/search icon is visible.
4. Search a keyword with no matching result.
5. Confirm no-result state appears but eye/search icon remains available because total notes > 0.
6. Delete the last note.
7. Confirm eye/search icon hides again and welcome empty state returns.

Expected: eye visibility follows total note count, not filtered search result count.

### C. Theme Picker Grouping

1. Open Settings → Tema.
2. Confirm group headers are visible in this order:
   - NOTEZ SIGNATURE
   - GLOOMY SERIES
   - COZY EARTH
   - CODER NIGHT
3. Confirm all existing themes are still present.
4. Confirm current active theme remains in its group.
5. Confirm preview mini-cards show background/surface/text/accent clearly.
6. Select several themes across different groups.
7. Confirm selected theme applies and remains stable after returning to Settings.

Expected: theme picker is clearer without removing themes or changing navigation.

## Result

Pending.
