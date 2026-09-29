# Tentang NOTEZ

NOTEZ adalah aplikasi Android lokal untuk menyimpan ide, catatan, todo, draft, snippet, dan hal penting sebelum lupa.

Fokusnya sederhana:

- cepat dicatat;
- nyaman dibaca;
- offline-first;
- privasi aman;
- Markdown-ready.

NOTEZ tidak mengejar jadi aplikasi cloud besar. Semua fitur inti dirancang tetap jalan tanpa internet.

---

## Mulai cepat

1. Ketuk tombol `+` untuk membuat catatan baru.
2. Tulis judul dan isi catatan.
3. Ketuk tombol centang untuk masuk mode baca.
4. Ketuk ikon pensil kalau ingin mengedit lagi.

Mode baca akan menampilkan catatan dengan Markdown Preview v4.

---

## Mode edit dan mode baca

**Mode edit** memakai teks biasa agar input tetap ringan dan familiar.

**Mode baca** memakai Reading View lokal berbasis WebView untuk menampilkan Markdown yang rapi: heading, list, table, callout, code block, footnote, definition list, dan safe raw HTML kecil.

---

## Markdown di NOTEZ

NOTEZ mendukung banyak format Markdown yang sering dipakai untuk catatan harian, project notes, draft, dan dokumentasi ringan:

- heading;
- tebal, miring, coret;
- highlight `==teks==`;
- superscript dan subscript ringan;
- list dan checklist;
- table;
- callout;
- code block dengan badge dan highlight lokal;
- footnote;
- definition list;
- safe raw HTML kecil seperti `<kbd>`, `<mark>`, `<sub>`, `<sup>`, dan `<details>`.

Untuk syntax lengkap, buka **Panduan Markdown** dari drawer.

---

## Backup, impor, dan ekspor

NOTEZ menyediakan:

- backup/import JSON untuk menyimpan semua catatan;
- ekspor TXT untuk salinan teks sederhana;
- folder backup otomatis jika kamu memilih folder tujuan.

Gunakan backup JSON sebelum mencoba perubahan besar atau sebelum pindah perangkat.

---

## Privasi dan offline

NOTEZ menjaga catatan tetap lokal.

```text
No INTERNET permission
No CDN
No remote scripts/styles/fonts/images
No native WebView bridge
```

Remote image di Markdown tidak dimuat otomatis. Link eksternal hanya dibuka lewat aplikasi/browser luar saat kamu tap.

---

## Tema dan tampilan

NOTEZ punya beberapa tema curated bawaan dan theme picker dengan preview kecil. Tema memengaruhi home, settings, dan Reading View agar catatan tetap nyaman dibaca.

---

## Batasan saat ini

Beberapa fitur belum ada dan sengaja dipilih bertahap:

- remote image belum auto-render;
- checkbox di preview belum interaktif;
- belum ada LaTeX/math penuh;
- belum ada Mermaid/diagram;
- belum ada wikilinks/backlinks/graph;
- belum ada tag/folder/pin/archive;
- belum ada real Trash / Recently Deleted;
- belum ada sync/cloud.

Prinsip NOTEZ tetap sama: stabil dulu, jaga offline/privacy-first, lalu tambah fitur kecil yang benar-benar kepakai.
