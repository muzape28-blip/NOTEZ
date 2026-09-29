# UAT — Raw HTML Compatibility P1 + Optional Image Cache (2026-09-29)

Status: CI VERIFIED — PENDING DEVICE UAT

## Build Under Test

- Latest commit: `f451d80 fix: support README tables and placeholder tap`.
- Feature commits: `60f53e9`, `b8618e5`.
- Compile fix commits: `2d63c48`, `034dc26`.
- GitHub Actions run: `36532925848` — `success`.
- Artifact: `notez-debug`, artifact id `11017995661`, size `8,118,093` bytes.

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
- Raw README screenshot `<table>`, `<tr>`, and `<td align="center">` blocks render as a table, not escaped text.
- The main `<div align="center">` content is centered.
- `<h1>ZCODE</h1>` renders as a heading.
- `<strong>` renders bold.
- `<em>` renders italic.
- Raw `<img>` becomes NOTEZ image placeholder/chip before loading.
- `alt` text such as `ZCODE logo`, `CI Build`, and `GNU GPLv3` is visible inside placeholders.
- `width="112"` makes the logo placeholder compact/sized, not full-screen.
- Placeholder shows `Load & cache` for remote HTTP(S) image sources.
- Link wrapping the badge does not make WebView load remote content internally.


## Expected — Optional Load & Cache

Use a small PNG/JPG/WebP/GIF remote image URL for this section.

1. Tap the body of a remote image placeholder, not only the small button.
2. Expected: it behaves like `Load & cache` and shows the confirmation dialog.
3. Cancel, then tap the explicit `Load & cache` button.
4. Expected: same confirmation dialog.
5. Confirm the dialog shows the source domain and explains one-time internet use.
6. Tap `Load & cache` in the dialog.
7. Expected: image downloads, saves locally, and the Reading View rerenders with the cached image.
8. Turn off network / airplane mode and reopen the note.
9. Expected: cached image still appears from local cache.
10. Open Settings → Aplikasi → `Hapus cache gambar online`.
11. Confirm cache size is shown and clearing cache works.
12. Reopen note.
13. Expected: image returns to placeholder state.

Notes:

- Remote images must not auto-load just by opening the note.
- Relative GitHub README image paths may show `Load & cache` when NOTEZ can infer the GitHub repo from links in the same note.
- SVG badge URLs may remain placeholder if unsupported by current image-cache policy.

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
- no remote image auto-load happens;
- `INTERNET` is used only after user taps and confirms `Load & cache`;
- remote script/style/font/iframe still does not load.

## Result

Pending.
