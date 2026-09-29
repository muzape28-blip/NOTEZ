# NOTEZ

**NOTEZ** adalah aplikasi Android lokal untuk menyimpan ide, catatan, todo, draft, snippet, dan hal penting lain sebelum lupa.

**Latest production release:** `v0.1.4` — hotfix tampilan versi aplikasi di Pengaturan. Feature batch utama tetap `v0.1.3`.

```text
cepat dicatat
nyaman dibaca
offline-first
online hanya saat user meminta
Markdown-ready
```

NOTEZ tidak mengejar jadi aplikasi cloud besar. Fitur inti tetap dirancang jalan tanpa internet: buat catatan, edit, baca ulang, search, theme, backup/import/export, dan panduan lokal.

---

## Highlight v0.1.3

- Home kosong sekarang punya welcome state NOTEZ yang clean, branded, dan theme-aware.
- `Panduan NOTEZ` / `Tentang NOTEZ` tersedia lokal/offline dari home kosong dan Settings.
- Search eye disembunyikan saat total catatan `0`, lalu muncul setelah ada catatan.
- Theme picker dikelompokkan:
  - `NOTEZ SIGNATURE`
  - `GLOOMY SERIES`
  - `COZY EARTH`
  - `CODER NIGHT`
- Markdown Reading View makin kuat:
  - heading, list, checklist, table, callout, code block, footnote, definition list;
  - syntax highlighting lokal ringan;
  - safe raw HTML subset untuk README-style content;
  - raw `<table>` screenshot README sederhana;
  - raw `<img>` sebagai placeholder aman.
- Remote image tetap **tidak auto-load**.
- User bisa tap `Load & cache` untuk mengambil gambar online secara manual, lalu NOTEZ menyimpannya lokal agar bisa dibaca offline lagi.
- Settings punya aksi `Hapus cache gambar online`.
- Splash/launcher/empty-state branding memakai horse crest NOTEZ.

---

## Cara pakai cepat

1. Tap tombol `+` untuk membuat catatan.
2. Tulis judul dan isi catatan di mode edit native Android `EditText`.
3. Tap centang untuk masuk Reading View.
4. Tap pensil untuk edit ulang.
5. Buka drawer untuk Settings, Panduan Markdown, dan Music drawer lokal.

---

## Markdown support

NOTEZ mendukung format umum untuk catatan rapi:

- heading `#`, `##`, `###`;
- bold, italic, bold+italic, strikethrough;
- highlight `==teks==` / `<mark>teks</mark>`;
- superscript/subscript ringan `x^2^` dan `H~2~O`;
- bullet list, numbered list, checklist;
- quote dan callout `NOTE`, `TIP`, `IMPORTANT`, `WARNING`, `CAUTION`;
- link Markdown dan bare URL;
- inline code dan fenced code block;
- table dengan alignment;
- footnote;
- definition list;
- raw HTML aman seperti `<kbd>`, `<mark>`, `<details>`, `<abbr>`, `<cite>`;
- README-style subset seperti `<div align="center">`, `<h1>`-`<h6>`, `<p>`, `<strong>`, `<em>`, `<a>`, `<table>`, `<tr>`, `<td align="center">`, dan `<img>` placeholder.

Panduan lengkap ada di aplikasi:

```text
Drawer → Panduan Markdown
```

---

## Offline-first / privacy stance

NOTEZ sekarang memakai prinsip:

```text
Local by default.
Online only when you ask.
Offline again after cache.
```

Detailnya:

- `android.permission.INTERNET` ada hanya untuk **user-triggered image loading/cache**.
- Remote image tidak dimuat otomatis saat catatan dibuka.
- Tap `Load & cache` menampilkan konfirmasi domain sebelum NOTEZ mengambil gambar.
- Gambar yang berhasil diambil disimpan di private local cache aplikasi.
- Cached image bisa tampil lagi saat offline.
- Cache gambar online bisa dihapus dari Settings.
- Tidak memakai CDN.
- Tidak memuat remote script/style/font/iframe.
- WebView Reading View tidak memakai `addJavascriptInterface`.
- Link eksternal dibuka lewat aplikasi/browser luar saat user tap.

---

## Tema bawaan

Theme picker berisi curated themes:

### NOTEZ SIGNATURE

- OLED Black
- NOTEZ You Dark
- Cobalt2
- NOTEZ You Warm

### GLOOMY SERIES

- Gloomy Sakura Night
- Gloomy Lavender
- Gloome Dark Sunset
- Raspberry Night

### COZY EARTH

- Dark Forest
- Fade Choco Matcha
- Kawaii Catpucinn

### CODER NIGHT

- GitHub Dark
- Tokyo Night
- Blue Moon Cheese

---

## Data, backup, dan export

- Backup/import semua catatan via JSON.
- Export semua catatan ke TXT.
- Folder backup otomatis via Android Storage Access Framework.
- Data catatan tetap lokal di perangkat.
- Tidak ada sync/cloud/collaboration bawaan.

---

## Batasan saat ini

- Raw HTML tetap allowlist, bukan browser bebas.
- `style=`, `class=`, `id=`, event handler seperti `onclick=`, `script`, `style`, `iframe`, dan `javascript:` tetap diblok/di-nonaktifkan.
- Remote SVG badge belum dirender bebas; policy SVG perlu RFC terpisah.
- Local image/attachment belum ada.
- Checkbox di preview belum interaktif; ubah `[ ]` / `[x]` dari mode edit.
- Belum ada tag/folder/notebook/pin/archive.
- Delete masih undo snackbar, belum real Trash / Recently Deleted.
- Belum ada PDF/HTML/Markdown bundle export.
- Belum ada wikilinks/backlinks/graph/embeds.
- Belum ada Mermaid/LaTeX/emoji shortcode/auto TOC.

---

## Target planning setelah v0.1.3

Lihat dokumen:

```text
docs/PENDING_FEATURES_AFTER_V0.1.3.md
```

Ringkasannya:

- Local image / attachment support.
- Real Trash / Recently Deleted.
- Card appearance setting.
- Raw SVG badge policy.
- NOTEZ Explorer internal / virtual.

---

## Release

APK rilis tersedia di GitHub Releases:

```text
https://github.com/muzape28-blip/NOTEZ/releases
```

---

## Status teknis

NOTEZ adalah aplikasi Android native Kotlin.

```text
Edit mode       : native Android EditText
Reading View    : local WebView + bundled markdown-it asset
Network stance  : offline-first, user-triggered online image cache only
No CDN          : yes
No WebView bridge: no addJavascriptInterface
```

Evidence utama untuk batch v0.1.3:

- Home Empty State + Tentang NOTEZ: CI + device PASS.
- Home/Search/Theme polish: CI + device PASS.
- Raw HTML + optional image cache: CI + device PASS.
- Welcome horse no-circle hotfix: CI PASS, device PASS by user report.
