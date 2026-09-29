# NOTEZ v0.1.3

**Status:** Production release notes for GitHub Release `v0.1.3`.
**Version:** `versionName 0.1.3`, `versionCode 4`.

---

## Highlights

### Home welcome polish

- Welcome empty state appears when NOTEZ has zero notes.
- Horse crest branding is centered, larger, and no longer wrapped in a separate circle background.
- Status bar follows theme/background for a cleaner first screen.
- `Panduan NOTEZ` opens local/offline About NOTEZ content.

### Search and theme picker polish

- Search eye is hidden while total notes = 0.
- Search eye appears once at least one note exists.
- Theme picker is grouped into Signature, Gloomy, Cozy Earth, and Coder Night sections.
- Theme preview mini-cards are clearer.

### Raw HTML README compatibility

Safe raw HTML support was expanded for common README-style content:

- `<div align="center">`
- `<h1>`–`<h6>`
- `<p>`
- `<strong>` / `<b>`
- `<em>` / `<i>`
- `<a href="...">`
- `<table>`, `<tr>`, `<td align="center">`, and related simple table tags
- `<img>` as a safe placeholder

Dangerous/free HTML remains blocked or sanitized:

- `<script>`
- `<style>`
- `<iframe>`
- event handlers such as `onclick=`
- `style=` free CSS
- `javascript:` links

### Optional user-triggered online image cache

NOTEZ is now:

```text
offline-first, with optional user-triggered online image loading and local caching
```

Behavior:

- remote images do not auto-load;
- placeholders show `Load & cache`;
- tapping placeholder body also triggers the same load flow;
- NOTEZ shows a native confirmation dialog with the source domain;
- download happens only after user confirms;
- successfully downloaded images are cached locally and can be viewed offline;
- Settings includes `Hapus cache gambar online`.

This release intentionally adds `android.permission.INTERNET` for the user-triggered cache feature only.

---

## Device UAT evidence

User reported PASS for:

- Home Empty State + Tentang NOTEZ;
- Home/Search/Theme polish;
- Raw HTML README rendering;
- ZCODE README screenshot-table compatibility;
- placeholder body tap and explicit `Load & cache` flow;
- welcome horse no-circle hotfix.

---

## Known limits

- SVG badge rendering remains conservative; raw SVG/badge policy is future work.
- Local image/attachment support is not available yet.
- Real Trash / Recently Deleted is not available yet.
- Card appearance setting is not available yet.
- NOTEZ Explorer internal/virtual is not available yet.

See:

```text
docs/PENDING_FEATURES_AFTER_V0.1.3.md
```

---

## Release verification summary

- Local text/resource checks: PASS (`git diff --check`, XML parse, no `addJavascriptInterface`, no CDN marker).
- Local Gradle build: unavailable in the sandbox because no Gradle wrapper/global Gradle is present.
- Main CI and production workflow evidence are recorded in GitHub Actions and the GitHub Release body.
- Final signed APK asset SHA-256 is recorded in GitHub Release `v0.1.3`.
