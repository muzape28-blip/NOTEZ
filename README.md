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
- View mode dengan Markdown Preview v2 berbasis local WebView Reading View.
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
- Tetap tanpa permission `INTERNET`.

---

## Markdown Preview

NOTEZ mendukung Markdown umum untuk catatan rapi:

- heading `#`, `##`, `###`;
- bold `**teks**`;
- italic `*teks*`;
- bold + italic `***teks***`;
- strikethrough `~~teks~~`;
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
- fenced code block;
- table dengan alignment `:---`, `:---:`, `---:`;
- image placeholder untuk remote image;
- raw HTML tampil sebagai teks/escaped, bukan dijalankan.

Panduan lengkap tersedia langsung di aplikasi:

```text
Drawer → Panduan Markdown
```

Panduan ini offline, read-only, dan punya daftar isi clickable untuk lompat ke bagian tertentu.

---

## Privacy / offline stance

NOTEZ sengaja menjaga permukaan jaringan tetap kecil:

- tidak memakai `android.permission.INTERNET`;
- tidak memakai CDN;
- tidak memuat remote script/style/font/image;
- Markdown renderer memakai asset lokal dari APK;
- WebView preview tidak memakai `addJavascriptInterface`;
- link eksternal dibuka lewat aplikasi/browser luar saat user tap;
- remote image Markdown tidak otomatis diunduh.

Prinsipnya:

```text
Catatan user tetap lokal.
Konten asing tidak dieksekusi diam-diam.
```

---

## Apa yang belum bisa dilakukan NOTEZ

Beberapa hal di bawah **belum** didukung. Sebagian sudah dicatat sebagai target update, upgrade, atau perbaikan ke depan, tapi tetap akan dipilih satu per satu agar NOTEZ tidak jadi berat dan rawan bug.

### Markdown / preview

- Safe raw HTML allowlist belum aktif.
  - Saat ini raw HTML tetap tampil sebagai teks/escaped.
  - Future candidate: allowlist terbatas seperti `<br>`, `<sub>`, `<sup>`, `<kbd>`, atau `<mark>` lewat RFC/UAT terpisah.
- Remote image belum auto-render.
  - NOTEZ tetap no `INTERNET`.
  - Future candidate: local image/file attachment yang tetap offline-first.
- Checkbox di preview belum interaktif.
  - Untuk mengubah checklist, edit teks Markdown `- [ ]` / `- [x]` langsung.
- Fitur Markdown lanjutan belum didukung:
  - footnote;
  - highlight `==teks==`;
  - superscript/subscript syntax non-HTML;
  - LaTeX/math;
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

- Theme system v2 baru tahap awal:
  - theme picker sudah punya preview kecil;
  - tema curated sudah bertambah, tapi masih lokal/bawaan APK.
- Belum ada theme marketplace/plugin ecosystem.
- Belum ada arbitrary CSS snippet seperti Obsidian.
- Future candidate:
  - Material/Obsidian-inspired appearance polish lanjutan;
  - card style/density setting;
  - tambahan tema curated seperti NOTEZ Paper.

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

- Theme System v2 / Appearance Polish.
- Local attachments.
- Wikilinks/internal note links.
- Real Trash / Recently Deleted.
- Safe HTML allowlist untuk Markdown Preview.
- Export format tambahan.

---

## Release

Rilis APK tersedia di GitHub Releases:

```text
https://github.com/muzape28-blip/NOTEZ/releases
```

---

## Status teknis

NOTEZ adalah aplikasi Android native Kotlin.

Kontrak penting yang dijaga:

```text
No INTERNET permission
No CDN
No remote scripts/styles/fonts/images
No addJavascriptInterface
Edit mode native EditText
View mode local WebView Reading View
```
