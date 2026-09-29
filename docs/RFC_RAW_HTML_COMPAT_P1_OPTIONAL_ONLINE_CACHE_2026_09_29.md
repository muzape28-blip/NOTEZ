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

P1 keeps NOTEZ without `android.permission.INTERNET`.

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

## P2 Direction — Not Implemented In This Patch

Optional remote image cache is intentionally not implemented in P1.

Reason: Android `INTERNET` is not a runtime/temporary permission. Adding it to the manifest changes NOTEZ from `no INTERNET permission` to `offline-first with user-triggered online access`.

Future P2 candidate:

- add `android.permission.INTERNET`;
- keep remote image placeholders by default;
- add `Load & cache` action on placeholders;
- show a consent dialog with domain/source;
- download only after user taps;
- store private local cache;
- render cached image offline;
- provide `Clear remote image cache` setting;
- no background fetch;
- no remote scripts/styles/fonts/iframes;
- treat SVG badges carefully, probably placeholder/chip first unless a separate SVG sanitizer/render policy is approved.

## Implementation Notes

- Main renderer: `app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt`.
- User guide: `app/src/main/assets/help/markdown_guide.md`.
- About page: `app/src/main/assets/help/about_notez.md`.
- README updated to describe the expanded safe raw HTML subset.

## Local Checks

- XML parse: PASS.
- No `android.permission.INTERNET`: PASS.
- No `addJavascriptInterface`: PASS.
- No CDN marker: PASS.
- Embedded preview JavaScript syntax smoke (`new Function(...)` in Node): PASS.
- `git diff --check`: PASS.
- Local Android build: NOT RUN — no Gradle wrapper/global Gradle available in sandbox.

## CI Evidence

Pending push and GitHub Actions run.

## Device UAT

Pending user/device verification.
