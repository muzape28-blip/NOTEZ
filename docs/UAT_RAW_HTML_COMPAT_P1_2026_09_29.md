# UAT — Raw HTML Compatibility P1 (2026-09-29)

Status: PENDING DEVICE UAT

## Build Under Test

- Commit: pending.
- GitHub Actions run: pending.
- Artifact: pending.

## Test Note

Paste this into a NOTEZ note and open Reading View:

```html
<div align="center">
  <img src="https://example.com/zcode_logo.png" alt="ZCODE logo" width="112">
  <h1>ZCODE</h1>
  <p><strong>IDE Python Android yang gratis, offline-first, dan menjadikan ARMv7/HP terbatas sebagai target kelas satu.</strong></p>
  <p><em>Bukan tentang punya perangkat terbaik. Tentang tetap bisa berkarya dengan perangkat yang kita punya.</em></p>
</div>
<p>
  <a href="https://github.com/muzape28-blip/ZCODE/actions/workflows/build.yml"><img src="https://github.com/muzape28-blip/ZCODE/actions/workflows/build.yml/badge.svg" alt="CI Build"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPLv3-2ea44f.svg" alt="GNU GPLv3"></a>
  <img src="https://img.shields.io/badge/Python-3.11%20%7C%20Chaquopy-3776AB?logo=python&logoColor=white" alt="Python 3.11 Chaquopy">
</p>

---

## Apa itu ZCODE?

ZCODE adalah IDE Python untuk Android.
```

## Expected — Supported HTML

- Raw tags above are not shown as literal HTML text.
- The main `<div align="center">` content is centered.
- `<h1>ZCODE</h1>` renders as a heading.
- `<strong>` renders bold.
- `<em>` renders italic.
- Raw `<img>` becomes NOTEZ image placeholder/chip.
- `alt` text such as `ZCODE logo`, `CI Build`, and `GNU GPLv3` is visible inside placeholders.
- `width="112"` makes the logo placeholder compact/sized, not full-screen.
- Link wrapping the badge remains tappable only as a safe external link; WebView does not load it internally.

## Expected — Safety

Paste this too:

```html
<script>alert("x")</script>
<style>body { color: red }</style>
<iframe src="https://example.com"></iframe>
<div style="position:fixed; inset:0" onclick="alert('x')">unsafe</div>
<a href="javascript:alert(1)">bad link</a>
```

Expected:

- script/style/iframe do not execute;
- free `style=` does not affect page layout;
- `onclick` does nothing;
- `javascript:` link is inactive/unsafe;
- no remote image auto-load happens.

## Result

Pending.
