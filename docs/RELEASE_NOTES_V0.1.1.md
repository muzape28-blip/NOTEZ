# NOTEZ v0.1.1

**Status:** Release candidate notes — publish setelah signed release APK lulus UAT.
**Tanggal target:** 2026-09-26
**Basis:** Markdown Preview v2 + trash glyph polish setelah v0.1.0.

---

## Highlights

### Markdown Preview v2

Mode view catatan kini memakai local WebView Reading View dengan bundled `markdown-it` agar Markdown gaya README/GitHub/Obsidian lebih rapi.

Yang membaik:

- table Markdown render sebagai table nyata;
- table lebar bisa horizontal scroll;
- bare URL otomatis menjadi link;
- code block dan inline code lebih nyaman dibaca;
- task list tampil sebagai checkbox view-only;
- callout GitHub/Obsidian-style:
  - `NOTE`
  - `TIP`
  - `IMPORTANT`
  - `WARNING`
  - `CAUTION`
- raw HTML tetap aman sebagai teks/escaped, bukan dieksekusi;
- remote image Markdown tampil sebagai placeholder/link, bukan auto-load.

### Home card trash glyph

Icon delete di list note diganti menjadi custom trash glyph agar aksi hapus lebih jelas daripada icon `X`/delete bawaan.

Behavior delete tidak berubah:

- tap trash untuk hapus;
- undo tetap tersedia;
- tidak ada fitur real Trash/Recently Deleted pada rilis ini.

---

## Privacy/offline stance

NOTEZ tetap local-first dan offline-friendly:

- tidak menambah `android.permission.INTERNET`;
- Markdown renderer memakai asset lokal dari APK, bukan CDN;
- remote image tidak di-fetch otomatis;
- link eksternal dibuka lewat browser/handler luar saat user tap;
- tidak ada native JavaScript bridge di WebView preview.

---

## UAT evidence

Debug APK sudah lulus UAT device oleh user:

- CI debug green;
- debug APK installed on real Android phone;
- Markdown table/link/code/task/callout/raw HTML/image placeholder behavior PASS;
- existing features reported safe.

Detail UAT:

```text
docs/UAT_MARKDOWN_PREVIEW_V2_RESULT_2026_09_26.md
```

Release APK tetap perlu compact UAT sebelum GitHub Release dipublish.

---

## Known notes

- Table alignment mengikuti Markdown/GFM syntax:
  - `:---` = left;
  - `:---:` = center;
  - `---:` = right.
- Raw HTML allowlist sengaja ditunda ke future RFC/phase agar MVP tetap aman dan UAT tidak membengkak.
- Remote image rendering langsung tidak tersedia karena NOTEZ tetap tanpa `INTERNET` permission.

---

## Suggested release UAT checklist

```text
App opens                                  : PASS/FAIL
Open UAT note                             : PASS/FAIL
Table + horizontal scroll                 : PASS/FAIL
Bare URL opens external browser           : PASS/FAIL
Raw HTML does not execute                 : PASS/FAIL
Remote image placeholder appears          : PASS/FAIL
Trash glyph visible                       : PASS/FAIL
Music drawer still works                  : PASS/FAIL
No INTERNET permission in release APK     : PASS/FAIL
```
