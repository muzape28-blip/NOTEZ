# NOTEZ v0.1.1

**Status:** Published — signed release APK lulus CI production + device UAT.
**Tanggal publish:** 2026-09-26
**Release URL:** https://github.com/muzape28-blip/NOTEZ/releases/tag/v0.1.1
**Release commit:** `ef31d50ce94d355706d266e2c63a5998330e750a`
**APK asset:** `NOTEZv0.1.1-release.apk` (2,906,652 bytes)
**APK SHA-256:** `365dab81c540422aa17bb7b8801aa8d16d1ef40165d53d849fbff6026c88420e`
**Production workflow:** `36250304432` — https://github.com/muzape28-blip/NOTEZ/actions/runs/36250304432

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

Debug APK dan signed release APK sudah lulus UAT device oleh user:

- CI debug green;
- debug APK installed on real Android phone;
- signed release APK installed on real Android phone;
- Markdown table/link/code/task/callout/raw HTML/image placeholder behavior PASS;
- existing features reported safe.

Detail UAT:

```text
docs/UAT_MARKDOWN_PREVIEW_V2_RESULT_2026_09_26.md
```

---

## Known notes

- Table alignment mengikuti Markdown/GFM syntax:
  - `:---` = left;
  - `:---:` = center;
  - `---:` = right.
- Raw HTML allowlist sengaja ditunda ke future RFC/phase agar MVP tetap aman dan UAT tidak membengkak.
- Remote image rendering langsung tidak tersedia karena NOTEZ tetap tanpa `INTERNET` permission.

---

## Release verification summary

```text
Debug CI run                              : PASS
Production workflow run 36250304432       : PASS
Signed release APK device UAT             : PASS
GitHub Release v0.1.1                     : PUBLISHED
Asset count                               : 1
APK asset                                 : NOTEZv0.1.1-release.apk
APK size                                  : 2,906,652 bytes
APK SHA-256                               : 365dab81c540422aa17bb7b8801aa8d16d1ef40165d53d849fbff6026c88420e
No INTERNET permission added              : YES
```
