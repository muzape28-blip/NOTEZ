# RFC — Raw HTML Compatibility P1 + Optional Online Cache Direction (2026-09-29)

Status: IMPLEMENTED LOCALLY — CI PENDING — DEVICE UAT PENDING

## Context

User tested a GitHub README-style note and saw raw HTML rendered as visible text, for example:

- `<div align="center">`
- `<img src="..." alt="..." width="112">`
- `<h1>...</h1>`
- `<p><strong>...</strong></p>`
- `<p><em>...</em></p>`
- `<a href="..."><img src="..." alt="..."></a>`

The existing Markdown Preview v4 intentionally allowed only a very small safe raw-HTML subset, so this README-style HTML was escaped.

User approved the direction:

- support a little more README-style raw HTML;
- keep dangerous HTML/CSS/script blocked;
- render remote images as placeholders first;
- product direction becomes: **NOTEZ offline-first, with optional user-triggered online loading and local caching**;
- optional online cache is a separate future phase because it changes the network/permission contract.

## P1 Scope Implemented Locally

P1 initially kept NOTEZ without `android.permission.INTERNET`; the same local batch was extended with the approved optional online cache direction before push/UAT, so current head intentionally includes `android.permission.INTERNET` for user-triggered image loading only.

### Newly supported raw HTML subset

- Text/structure:
  - `<div>`
  - `<p>`
  - `<h1>` through `<h6>`
  - `<strong>` / `<b>`
  - `<em>` / `<i>`
- Links:
  - `<a href="...">` for safe `https:`, `http:`, `mailto:`, `tel:`, and same-document `#anchor` links.
  - Unsafe href values such as `javascript:` remain inactive.
- Alignment:
  - `align="left|center|right|justify"` on `<div>`, `<p>`, and headings.
  - This is mapped to internal NOTEZ alignment classes, not free CSS.
- Images:
  - raw `<img>` becomes a NOTEZ placeholder/chip;
  - `alt` becomes the visible label;
  - `width` / `height` are used for placeholder sizing with safe clamping;
  - remote images are not fetched and not embedded as active `<img>`.

### Still blocked / stripped

- `<script>`
- `<style>`
- `<iframe>`
- unrestricted `<form>`/embed-like HTML
- `style=` free CSS
- `class=` / `id=` free user classes
- event handlers such as `onclick=` / `onload=`
- `javascript:` links
- remote script/style/font/image auto-loading

## Offline-first Direction

Accepted product wording:

> NOTEZ is offline-first, with optional user-triggered online loading and local caching.

Meaning:

- core notes/edit/search/preview/settings remain usable offline;
- NOTEZ does not make network requests just because a note contains a remote URL;
- any future online fetch must be explicitly user-triggered;
- source/domain must be visible before fetching;
- fetched content should be cached locally for later offline reading;
- user must be able to clear cached remote content;
- no remote script/style/font/iframe execution.

## Optional Online Image Cache — Implemented In Same Local Batch

Android `INTERNET` is not a runtime/temporary permission. Adding it to the manifest changes NOTEZ from `no INTERNET permission` to:

> offline-first with user-triggered online access.

Approved behavior implemented locally:

- added `android.permission.INTERNET`;
- remote image placeholders remain the default;
- placeholders include a `Load & cache` action when source is remote HTTP(S);
- tapping `Load & cache` shows a native confirmation dialog with the source domain;
- download happens only after user confirms;
- downloaded images are stored in private local cache under app files;
- cached images are served back to the WebView through `https://notez.local/cache/image/...` only;
- WebView network loads remain blocked for arbitrary remote resources;
- Settings includes `Hapus cache gambar online`;
- no background fetch;
- no remote scripts/styles/fonts/iframes;
- supported cached image MIME types: PNG, JPG/JPEG, WebP, GIF;
- SVG badge rendering remains cautious and may stay placeholder unless a separate SVG policy is approved.

## Implementation Notes

- Main renderer: `app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt`.
- Remote image cache helper: `app/src/main/java/com/zaba/notez/markdown/RemoteImageCache.kt`.
- Manifest: `app/src/main/AndroidManifest.xml` intentionally includes `android.permission.INTERNET` for user-triggered image cache.
- Settings cache clear row: `SettingsActivity.kt` and `activity_settings.xml`.
- User guide: `app/src/main/assets/help/markdown_guide.md`.
- About page: `app/src/main/assets/help/about_notez.md`.
- README updated to describe the expanded safe raw HTML subset and offline-first/user-triggered-online posture.

## Local Checks

- XML parse: PASS.
- `android.permission.INTERNET` present intentionally for user-triggered image cache: PASS.
- No `addJavascriptInterface`: PASS.
- No CDN marker: PASS.
- Embedded preview JavaScript syntax smoke (`new Function(...)` in Node): PASS.
- `git diff --check`: PASS.
- Local Android build: NOT RUN — no Gradle wrapper/global Gradle available in sandbox.

## CI Evidence

Pending push and GitHub Actions run.

## Device UAT

Pending user/device verification.
