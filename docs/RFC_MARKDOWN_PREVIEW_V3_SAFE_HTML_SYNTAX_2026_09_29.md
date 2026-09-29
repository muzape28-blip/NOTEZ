# RFC — NOTEZ Markdown Preview v3: Safe HTML Allowlist + Local Syntax Highlighting

**Tanggal:** 2026-09-29  
**Status:** IMPLEMENTED LOCALLY — static/source checks passed; Android build waits for CI  
**Repo:** NOTEZ  
**Basis keputusan:** user setuju setelah riset perbandingan GitHub vs Obsidian raw HTML.  
**Jenis perubahan:** Markdown reading view security/rendering upgrade.  

---

## 1. Ringkasan

Markdown Preview v3 meng-upgrade renderer lokal NOTEZ dengan dua hal yang sebelumnya ditunda dari Preview v2:

1. **Safe raw HTML allowlist kecil** untuk tag penulisan yang berguna.
2. **Syntax highlighting lokal ringan** untuk fenced code block.

Kontrak lama tetap dijaga:

```text
No INTERNET permission
No CDN
No remote script/style/font/image
No addJavascriptInterface
Edit mode tetap native EditText
View mode tetap local WebView Reading View
```

---

## 2. Scope yang disetujui

### 2.1 Safe raw HTML allowlist

Tag raw HTML yang boleh aktif:

```text
br, sub, sup, kbd, mark, u, s, small, details, summary, abbr, cite
```

Atribut yang boleh:

```text
abbr[title]
details[open]
```

Atribut lain, termasuk `style`, `class`, `id`, dan semua `on*` event handler, tidak menjadi kontrak publik dan harus dibuang/tidak aktif.

### 2.2 Yang tetap diblok / escaped

```text
script, style, iframe, object, embed, form, input, button, textarea,
select, option, video, audio, canvas, svg, math, link, meta, title, img
```

Skema/fitur berbahaya tetap tidak aktif:

```text
javascript:
file:
content:
data:
srcdoc
remote auto-load resource
onclick= / onload= / event handler lain
arbitrary style/class/id
```

### 2.3 Syntax highlighting lokal ringan

Supported label utama:

```text
kotlin, java, javascript/js, typescript/ts, python/py, bash/sh/shell,
json, xml, html, css, markdown/md
```

Tidak memakai Prism/Shiki/highlight.js dari CDN. Implementasi sekarang memakai highlighter kecil lokal di renderer shell supaya tidak menambah dependency/supply-chain baru.

---

## 3. Rationale

Hasil riset GitHub vs Obsidian:

- GitHub mendukung sebagian raw HTML, tetapi memakai sanitizer kuat dan GFM tagfilter.
- Obsidian mendukung sanitized HTML untuk catatan lokal, tapi tetap membatasi parser dan memperingatkan risiko.
- NOTEZ harus lebih ketat karena local WebView, offline-first, dan no-INTERNET contract.

Arah NOTEZ:

```text
GitHub-style sanitizer discipline
+ Obsidian-style writing convenience
+ NOTEZ privacy/offline boundary
```

---

## 4. Implementasi

File utama:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/assets/help/markdown_guide.md
README.md
```

Perubahan teknis:

- `markdown-it` tetap asset lokal dari APK.
- Parser sekarang `html: true`, tetapi raw HTML token dirender lewat sanitizer allowlist sebelum masuk DOM aktif.
- HTML hasil render dimasukkan ke inert `<template>`, disanitasi lagi sebagai defense-in-depth, baru ditempel ke preview.
- Markdown image tetap diganti placeholder/link, bukan `<img>` aktif.
- External link tetap dibuka di aplikasi/browser luar saat user tap.
- Same-document `#anchor` tetap dipakai untuk daftar isi lokal.
- Code block diberi badge bahasa dan syntax span lokal setelah DOM aman.

---

## 5. Acceptance criteria

### Product

- [ ] `<br>` aktif sebagai line break.
- [ ] `<sub>`, `<sup>`, `<kbd>`, `<mark>`, `<u>`, `<s>`, `<small>` tampil dengan style aman.
- [ ] `<details><summary>...</summary>...</details>` bisa dibuka/tutup di Reading View.
- [ ] `<abbr title="...">...</abbr>` mempertahankan title aman.
- [ ] Fenced code block berlabel bahasa menampilkan badge dan highlight lokal ringan.
- [ ] Panduan Markdown menjelaskan safe raw HTML allowlist dan limitasinya.

### Security/privacy

- [ ] `AndroidManifest.xml` tetap tanpa `android.permission.INTERNET`.
- [ ] Tidak ada CDN/external script/style/font.
- [ ] Tidak ada `addJavascriptInterface`.
- [ ] `<script>` tampil sebagai teks/escaped dan tidak execute.
- [ ] `<iframe>`, `<style>`, raw `<img>`, `onclick=`, `style=`, `class=`, dan `javascript:` tidak aktif.
- [ ] Remote Markdown image tetap placeholder/link.
- [ ] WebView tetap `blockNetworkLoads = true`.

### Regression

- [ ] Heading anchors / guide TOC tetap lompat ke section.
- [ ] Table alignment tetap jalan.
- [ ] Task list tetap view-only.
- [ ] Callout NOTE/TIP/IMPORTANT/WARNING/CAUTION tetap tampil.
- [ ] Edit mode tetap native `EditText` dan autosave tidak diubah.

---

## 6. UAT sample

````md
# Markdown Preview v3 Smoke

Aman<br>Baris baru
H<sub>2</sub>O dan x<sup>2</sup>
Tekan <kbd>Ctrl</kbd> + <kbd>S</kbd>
<mark>Highlight aman</mark>

<details open>
<summary>Detail</summary>
Isi detail aman.
</details>

```kotlin
fun helloNotez() {
    val safe = true
    println("NOTEZ v3: $safe")
}
```

<script>alert('NO')</script>
<iframe src="https://example.com"></iframe>
<div onclick="alert('NO')">raw div unsafe</div>
<img src="https://example.com/a.png">
[unsafe](javascript:alert(1))
![remote](https://example.com/a.png)
````

Expected:

- safe tags aktif;
- code block punya badge/highlight;
- unsafe raw HTML terlihat sebagai teks atau tidak aktif;
- remote image tetap placeholder;
- tidak ada prompt/alert/navigation internal.

---

## 7. Local verification

Static/source checks run on 2026-09-29:

```text
XML parse app/src/main/res/**/*.xml                    : PASS
No android.permission.INTERNET                         : PASS
No addJavascriptInterface                              : PASS
No CDN marker in app/src/main                           : PASS
Renderer JS extraction + node --check                  : PASS
UAT JSON parse                                         : PASS
git diff --check                                       : PASS
Local Android build                                    : NOT RUN — no Gradle wrapper/global gradle in sandbox
```

Canonical Android compile/build evidence must come from GitHub Actions CI.

---

## 8. Non-goals

Tidak termasuk scope ini:

- full raw HTML bebas;
- CSS snippets ala Obsidian;
- remote images;
- local attachments;
- live preview editor;
- Mermaid/math/LaTeX;
- Markdown `==highlight==` syntax;
- copy-code button;
- release/tag baru.

---

## 9. Rollback

Rollback paling kecil:

```text
git revert commit Markdown Preview v3
```

Atau secara manual:

- kembalikan `MarkdownPreviewRenderer` ke raw HTML escaped (`html: false`);
- hapus syntax highlighting enhancer;
- kembalikan wording guide/README ke Preview v2 behavior.
