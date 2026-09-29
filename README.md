# NOTEZ

**NOTEZ** adalah aplikasi Android lokal untuk menyimpan ide, catatan, todo, draft, snippet, dan hal penting lain sebelum lupa.

Fokus NOTEZ:

```text
cepat dicatat
nyaman dibaca
offline-first
privasi aman
Markdown-ready
```

NOTEZ tidak mengejar jadi aplikasi cloud besar. Semua fitur inti dirancang tetap jalan tanpa internet.

---

## Highlight saat ini

- Catatan tanpa batas karakter buatan app.
- Edit mode native Android `EditText`.
- View mode dengan Markdown Preview v4 berbasis local WebView Reading View.
- Welcome empty state dengan icon NOTEZ dan link Panduan NOTEZ saat belum ada catatan.
- Panduan NOTEZ / Tentang NOTEZ lokal/offline.
- Panduan Markdown lokal/offline dari drawer.
- Search catatan.
- Halaman Pengaturan dari drawer.
- Tema bawaan curated dengan picker preview:
  - GitHub Dark;
  - NOTEZ You Dark;
  - NOTEZ You Warm;
  - Fade Choco Matcha;
  - Blue Moon Cheese;
  - Raspberry Night;
  - Gloomy Sakura Night;
  - Gloomy Lavender;
  - Gloome Dark Sunset;
  - Dark Forest;
  - Tokyo Night;
  - Kawaii Catpucinn;
  - OLED Black;
  - Cobalt2.
- Ekspor/import JSON.
- Ekspor TXT.
- Folder backup otomatis.
- Music drawer/player lokal.
- Delete note dengan custom trash glyph + undo snackbar.
- Offline-first dengan optional user-triggered online image cache (`INTERNET` hanya dipakai saat user tap `Load & cache`).

---

## Markdown Preview

NOTEZ mendukung Markdown umum untuk catatan rapi:

- heading `#`, `##`, `###`;
- bold `**teks**`;
- italic `*teks*`;
- bold + italic `***teks***`;
- strikethrough `~~teks~~`;
- highlight `==teks==` / `<mark>teks</mark>`;
- superscript/subscript ringan `x^2^` dan `H~2~O`;
- bullet list dan numbered list;
- checklist `- [ ]` / `- [x]`;
- quote `>`;
- callout:
  - `NOTE`;
  - `TIP`;
  - `IMPORTANT`;
  - `WARNING`;
  - `CAUTION`;
- Markdown link dan bare URL;
- inline code;
- fenced code block dengan local syntax highlighting ringan;
- table dengan alignment `:---`, `:---:`, `---:`;
- footnote `[^1]` + `[^1]: catatan`;
- definition list `Istilah` lalu `: definisi`;
- image placeholder untuk remote image, dengan tombol `Load & cache` manual untuk menyimpan gambar online ke cache lokal;
- safe raw HTML allowlist: tag kecil seperti `<br>`, `<kbd>`, `<mark>`, `<details>`, definition list, plus subset README-style seperti `<div align="center">`, `<h1>`-`<h6>`, `<p>`, `<strong>/<b>`, `<em>/<i>`, `<a href="...">`, dan `<img>` sebagai placeholder;
- raw HTML di luar allowlist tetap tampil sebagai teks/escaped, bukan dijalankan.

Panduan lengkap tersedia langsung di aplikasi:

```text
Drawer → Panduan Markdown
```

Panduan ini offline, read-only, dan punya daftar isi clickable untuk lompat ke bagian tertentu.

---

## Privacy / offline stance

NOTEZ tetap offline-first dan menjaga permukaan jaringan tetap kecil:

- `android.permission.INTERNET` hanya dipakai untuk gambar online saat user tap `Load & cache`;
- tidak memakai CDN;
- tidak memuat remote script/style/font/iframe;
- remote image Markdown/raw HTML tidak otomatis diunduh;
- gambar yang berhasil di-load disimpan lokal agar bisa dibaca offline lagi;
- Markdown renderer memakai asset lokal dari APK;
- WebView preview tidak memakai `addJavascriptInterface`;
- link eksternal dibuka lewat aplikasi/browser luar saat user tap.

Prinsipnya:

```text
Catatan user tetap lokal.
Online hanya saat user meminta.
Konten asing tidak dieksekusi diam-diam.
```

---

## Apa yang belum bisa dilakukan NOTEZ

Beberapa hal di bawah **belum** didukung. Sebagian sudah dicatat sebagai target update, upgrade, atau perbaikan ke depan, tapi tetap akan dipilih satu per satu agar NOTEZ tidak jadi berat dan rawan bug.

### Markdown / preview

- Raw HTML bebas/tanpa batas tidak didukung.
  - Hanya allowlist aman yang aktif.
  - README-style `<img>` didukung sebagai placeholder, bukan gambar remote aktif.
  - Tag seperti `<script>`, `<style>`, `<iframe>`, `<form>`, event handler `onclick=`, `style=`, `class=`, dan `javascript:` tetap diblok/escaped.
- Remote image tidak auto-render.
  - User bisa tap `Load & cache` untuk mengambil gambar online sekali lalu menyimpannya lokal.
  - SVG remote tetap hati-hati; tipe utama yang didukung cache: PNG, JPG/JPEG, WebP, GIF.
  - Future candidate: local image/file attachment.
- Checkbox di preview belum interaktif.
  - Untuk mengubah checklist, edit teks Markdown `- [ ]` / `- [x]` langsung.
- Fitur Markdown lanjutan belum didukung:
  - LaTeX/math penuh;
  - Mermaid/diagram;
  - emoji shortcode `:smile:`;
  - auto table-of-contents seperti `[[TOC]]`.
- Syntax Obsidian-style seperti wikilinks `[[Note]]`, backlinks, graph, dan embeds belum tersedia.

### Notes / organization

- Belum ada tag/label bawaan.
- Belum ada folder/notebook bawaan.
- Belum ada pin/favorite/archive.
- Belum ada real Trash / Recently Deleted.
  - Delete sekarang memakai undo snackbar.
- Belum ada sync/cloud account.
- Belum ada kolaborasi multi-device.

### UI / appearance

- Tema curated sudah tersedia sebagai bawaan APK dan dipilih lewat preview picker.
- Belum ada custom theme editor / import theme sendiri.
- Belum ada theme marketplace/plugin ecosystem.
- Belum ada arbitrary CSS snippet seperti Obsidian.
- Belum ada card style/density setting.
- Future candidate:
  - Material/Obsidian-inspired appearance polish lanjutan;
  - tambahan tema curated seperti NOTEZ Paper;
  - opsi card style/density jika benar-benar dibutuhkan.

### Export / sharing

- Export saat ini fokus JSON/TXT.
- Belum ada export PDF/HTML/Markdown bundle.
- Belum ada share sheet khusus untuk rendered preview.

---

## Roadmap direction

Arah pengembangan NOTEZ tetap bertahap:

```text
1. Stabil dulu.
2. Jaga offline/privacy-first.
3. Tambah fitur kecil yang benar-benar kepakai.
4. Hindari scope besar tanpa RFC/UAT.
```

Candidate ke depan:

- Local attachments / local images yang tetap offline-first.
- Wikilinks/internal note links ala Obsidian-lite.
- Tags, folder/notebook, pin/favorite, atau archive.
- Real Trash / Recently Deleted.
- Export PDF/HTML/Markdown bundle.
- Share sheet khusus untuk rendered preview.
- Markdown advanced opsional: LaTeX/math penuh, Mermaid/diagram, emoji shortcode, auto TOC.
- UI polish lanjutan: card style/density, custom theme editor, tambahan tema curated.

---

## Release

Rilis APK tersedia di GitHub Releases:

```text
https://github.com/muzape28-blip/NOTEZ/releases
```

---

## Status teknis

NOTEZ adalah aplikasi Android native Kotlin.

Status source `main` saat ini:

```text
Settings Page v1       : CI + device UAT PASS
Theme System v2        : CI + device UAT PASS
Markdown Preview v4    : CI + device UAT PASS
Release publik terbaru : tetap sesuai GitHub Releases, belum otomatis berubah hanya karena main sudah update
```

Kontrak penting yang dijaga:

```text
Offline-first with user-triggered INTERNET only
No CDN
No remote scripts/styles/fonts/iframes
No remote image auto-load
No addJavascriptInterface
Edit mode native EditText
View mode local WebView Reading View
```
