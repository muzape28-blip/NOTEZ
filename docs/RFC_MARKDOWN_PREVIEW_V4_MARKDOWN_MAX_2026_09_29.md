# RFC — NOTEZ Markdown Preview v4 / Markdown Max Tahap 1

**Tanggal:** 2026-09-29  
**Status:** DEVICE VERIFIED / PASS — CI green and user UAT pass
**Basis:** local commit `8114065 feat: add markdown preview v3`  
**Tujuan:** menyelesaikan satu batch Markdown yang lebih lengkap dan rapi agar user cukup UAT satu APK final.

---

## 1. Ringkasan

Markdown Preview v4 melanjutkan v3 dengan fitur renderer-level yang masih aman untuk NOTEZ:

```text
Safe HTML allowlist v3
+ syntax highlighting lokal ringan
+ Markdown highlight ==teks==
+ superscript/subscript ringan
+ footnotes
+ definition lists
+ polish table/code/callout/footnote reading view
```

Kontrak yang tetap dijaga:

```text
No INTERNET permission
No CDN
No remote script/style/font/image
No addJavascriptInterface
Edit mode tetap native EditText
View mode tetap local WebView Reading View
Raw HTML bebas tetap tidak didukung
```

---

## 2. Fitur masuk scope

### 2.1 Markdown syntax baru

```md
Ini ==penting==.
Air = H~2~O.
Rumus kecil: x^2^.

Kalimat dengan footnote.[^1]

[^1]: Isi catatan kaki.

API
: Application Programming Interface
```

### 2.2 Safe raw HTML tetap kecil

Allowlist aktif:

```text
br, sub, sup, kbd, mark, u, s, small, details, summary, abbr, cite, dl, dt, dd
```

Atribut publik yang boleh:

```text
abbr[title]
details[open]
```

Yang tetap escaped/tidak aktif:

```text
script, style, iframe, object, embed, form, input, button, textarea,
select, option, video, audio, canvas, svg, math, link, meta, title, img,
onclick/onload/event handler, style/class/id bebas, javascript:, data:, file:, content:
```

### 2.3 Visual polish

- Code block card lebih premium dengan badge bahasa.
- Local syntax highlighting ringan untuk bahasa umum.
- Table memakai rounded scroll wrapper dan border lebih halus.
- Callout memakai background/border yang lebih classy.
- Footnotes dirender sebagai section bawah dengan backlink.
- Definition list punya gaya glosarium ringan.

---

## 3. Non-goals

Tidak masuk batch ini:

- Mermaid;
- LaTeX/math engine penuh;
- wikilinks/backlinks;
- tags/properties database behavior;
- local image attachments;
- remote image auto-load;
- CSS snippets bebas;
- plugin system;
- copy-code button;
- release/tag baru.

---

## 4. Implementation notes

File utama:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/assets/help/markdown_guide.md
README.md
```

Teknik:

- `markdown-it` tetap bundled local asset dari APK.
- Raw HTML tetap masuk sanitizer allowlist sebelum DOM aktif.
- DOM hasil render tetap disanitasi ulang via inert `<template>`.
- `==`, `^`, `~`, dan footnote ref memakai small local markdown-it inline rules.
- Footnote definitions diekstrak sebelum render, dengan guard agar fenced code block tidak ikut diproses.
- Definition list dipreprocess menjadi safe `<dl>/<dt>/<dd>`, juga dengan guard fenced code block.
- Generated footnotes dan syntax spans dibuat setelah sanitizer agar class/id raw user tetap tidak dibuka bebas.

---

## 5. Acceptance criteria

### Product

- [x] `==penting==` tampil sebagai highlight.
- [x] `x^2^` tampil superscript ringan.
- [x] `H~2~O` tampil subscript ringan.
- [x] Footnote `[^1]` lompat ke catatan kaki bawah.
- [x] Definition list tampil sebagai glosarium rapi.
- [x] Code block punya badge/highlight lokal.
- [x] Table/callout tetap lebih rapi.
- [x] Panduan Markdown menjelaskan fitur baru.

### Security/privacy

- [x] Tidak ada `android.permission.INTERNET`.
- [x] Tidak ada CDN marker.
- [x] Tidak ada `addJavascriptInterface`.
- [x] `<script>` tidak execute.
- [x] `<iframe>` tidak embed.
- [x] Raw `<img>` tidak auto-load.
- [x] `onclick=`, `style=`, `class=`, `id=`, `javascript:` tidak aktif sebagai raw user capability.
- [x] Markdown remote image tetap placeholder/link.

### Regression

- [x] Heading anchor / guide TOC tetap jalan.
- [x] Task list tetap view-only.
- [x] GFM table alignment tetap jalan.
- [x] Callout NOTE/TIP/IMPORTANT/WARNING/CAUTION tetap jalan.
- [x] Edit mode tetap native EditText dan autosave tidak diubah.

---

## 6. Local verification

Static/source checks run on 2026-09-29:

```text
XML parse app/src/main/res/**/*.xml                    : PASS
No android.permission.INTERNET                         : PASS
No addJavascriptInterface                              : PASS
No CDN marker in app/src/main                           : PASS
Renderer JS extraction + node --check                  : PASS
UAT v3/v4 JSON parse                                   : PASS
git diff --check                                       : PASS
Local Android build                                    : NOT RUN — no Gradle wrapper/global gradle in sandbox
```

CI verification for implementation commit `1bee29f`:

```text
GitHub Actions run : 36515946866
URL                : https://github.com/muzape28-blip/NOTEZ/actions/runs/36515946866
Conclusion         : success
Artifact           : notez-debug
Artifact id        : 11010543404
Artifact size      : 8,094,466 bytes
```

CI verification for latest docs commit `7146fc9`:

```text
GitHub Actions run : 36516206526
URL                : https://github.com/muzape28-blip/NOTEZ/actions/runs/36516206526
Conclusion         : success
Artifact           : notez-debug
Artifact id        : 11011126427
Artifact size      : 8,094,464 bytes
```

Device UAT on 2026-09-29:

```text
Result        : PASS / memuaskan
Evidence      : user reported "Yuuppsss semuanya pass memuaskan" after testing debug artifact
Screenshots   : Screenshot_20260929-103059.png through Screenshot_20260929-103143.png
Observed pass : highlight, superscript/subscript, footnotes, definition list, safe HTML,
                code badges/highlighting, table alignment, callout card, remote image placeholder,
                and unsafe HTML/link non-execution
```

---

## 7. Rollback

Rollback commit v4 untuk kembali ke checkpoint v3:

```text
git revert <v4 commit>
```

Jika perlu rollback lebih jauh, revert juga commit `8114065 feat: add markdown preview v3`.
