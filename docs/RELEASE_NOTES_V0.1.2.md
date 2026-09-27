# NOTEZ v0.1.2

**Status:** Prepared — debug CI + device UAT PASS; signed release pending  
**Tanggal target:** 2026-09-27  
**Release URL:** Pending  
**Release commit:** Pending  
**APK asset:** Pending  
**APK SHA-256:** Pending  
**Production workflow:** Pending

---

## Highlights

### Panduan Markdown lokal/offline

NOTEZ sekarang punya halaman bantuan bawaan:

```text
Drawer → Panduan Markdown
```

Panduan ini:

- read-only dan lokal di APK;
- tidak muncul sebagai note biasa di home list;
- tidak masuk database catatan user;
- tidak perlu internet;
- dirender dengan Markdown Preview v2 yang sama dengan note reading view.

Isi panduan mencakup:

- apa itu Markdown;
- cheat sheet cepat;
- bold `**teks**`, italic `*teks*`, bold+italic, strikethrough;
- heading, paragraph, horizontal rule;
- bullet list, numbered list, checklist;
- quote dan callout;
- link, bare URL, dan internal heading link;
- inline code dan code block;
- table + alignment;
- image behavior;
- raw HTML behavior;
- escape karakter;
- FAQ, glosarium, contoh catatan lengkap, dan batasan NOTEZ.

### Clickable daftar isi

Panduan Markdown punya daftar isi yang bisa ditap.

Contoh:

```md
[16. Table](#16-table)
[18. Raw HTML](#18-raw-html)
```

Renderer sekarang memberi heading anchor lokal otomatis dan mengizinkan link `#anchor` yang aman untuk lompat di dokumen yang sama.

Security behavior tetap:

- `#anchor` lokal tetap di WebView guide/preview;
- `http`, `https`, `mailto`, `tel` tetap dibuka via external app/browser;
- unsafe scheme seperti `javascript:`, `file:`, `content:`, dan `data:` tetap diblokir.

### Raw HTML explanation lebih jelas

Panduan Markdown sekarang menjelaskan kenapa raw HTML tampil sebagai teks/escaped di NOTEZ:

- ini bukan bug;
- ini keputusan keamanan/privacy-first/offline-first;
- catatan hasil copy-paste dari web tidak bisa menjalankan script diam-diam;
- future safe HTML allowlist hanya candidate versi besar/RFC terpisah, bukan janji rilis berikutnya.

Raw HTML tetap disabled/escaped di `v0.1.2`.

### Home FAB floating cleanup

Home screen diperbaiki agar tombol `+` benar-benar floating dan tidak lagi membuat block/bottom bar kosong di bawah list.

Tidak ada perubahan pada flow delete:

- delete tetap via trash glyph di card;
- undo snackbar tetap ada;
- tidak ada long-press delete;
- tidak ada confirm dialog baru.

### README polish

README diperbarui agar lebih jelas menjelaskan:

- apa itu NOTEZ;
- fitur utama;
- privacy/offline stance;
- Markdown support;
- hal yang belum bisa dilakukan NOTEZ;
- target update/upgrade/perbaikan ke depan.

---

## Privacy/offline stance

NOTEZ tetap local-first:

- tidak menambah `android.permission.INTERNET`;
- tidak memakai CDN;
- tidak memuat remote script/style/font/image;
- Markdown renderer memakai asset lokal dari APK;
- tidak ada `addJavascriptInterface`;
- remote image tetap placeholder/link, bukan auto-load.

---

## UAT evidence

Debug APK sudah lulus CI dan device UAT user.

```text
Debug workflow : 36296562618
Status         : success
Artifact       : notez-debug
Device UAT     : PASS by user report
```

Detail UAT:

```text
docs/UAT_MARKDOWN_GUIDE_FAB_V0.1.2_RESULT_2026_09_27.md
```

Signed release APK UAT masih pending sampai production build selesai dan user menguji artifact release.

---

## Known notes

- Raw HTML allowlist sengaja belum diaktifkan.
- Remote image rendering langsung belum tersedia karena NOTEZ tetap tanpa `INTERNET` permission.
- Theme/plugin/material UI polish sudah disepakati sebagai diskusi setelah release ini, bukan bagian dari `v0.1.2`.
- Card List/Grid setting tetap dicatat sebagai future idea, bukan scope release ini.

---

## Release verification summary

```text
Feature commit                            : f84bc7e
Debug CI run 36296562618                  : PASS
Debug APK device UAT                      : PASS by user report
Production workflow                       : PENDING
Signed release APK device UAT             : PENDING
GitHub Release v0.1.2                     : PENDING
No INTERNET permission added              : YES
```
