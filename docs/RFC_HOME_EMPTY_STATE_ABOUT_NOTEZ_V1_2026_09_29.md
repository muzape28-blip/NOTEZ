# RFC — NOTEZ Home Empty State + Tentang NOTEZ v1

**Tanggal:** 2026-09-29  
**Status:** CI VERIFIED — debug build passed; waiting device UAT
**Repo:** NOTEZ  
**Jenis perubahan:** UX polish kecil + halaman bantuan lokal/offline  
**Prinsip utama:** home kosong terasa branded, hangat, dan helpful tanpa onboarding popup atau UI ramai.

---

## 1. Ringkasan keputusan

Tambahkan empty state baru saat NOTEZ benar-benar belum punya catatan:

```text
[Icon kuda NOTEZ]

NOTEZ

Catat ide, rencana, dan lainnya
sebelum ditelan waktu.

Ketuk + untuk mulai.
Panduan NOTEZ
```

Karakter desain:

```text
center organized
minimalis
elegan
user friendly
tidak ramai
theme-aware
```

Tambahkan juga halaman lokal/offline:

```text
Tentang NOTEZ / Panduan NOTEZ
```

Halaman ini menjelaskan apa itu NOTEZ, cara mulai, mode edit/mode baca, Markdown, backup/export, privacy/offline stance, dan batasan fitur saat ini. Konten bisa ditulis ulang dari `README.md`, bukan copy mentah.

---

## 2. Current source inspection

Source yang sudah dicek:

```text
AGENTS.md
README.md
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/res/layout/activity_main.xml
app/src/main/java/com/zaba/notez/MarkdownGuideActivity.kt
app/src/main/java/com/zaba/notez/SettingsActivity.kt
app/src/main/res/layout/activity_settings.xml
app/src/main/AndroidManifest.xml
app/src/main/res/drawable-nodpi/ic_notez_horse_launcher_fg.png
```

### 2.1 Home empty state saat ini

`activity_main.xml` punya `TextView`:

```text
id: @+id/empty
text: "Belum ada catatan.\nKetuk + untuk mulai."
gravity: center
visibility: gone
```

`MainActivity.observe()` sekarang hanya membedakan:

```text
hasil list kosong  → list GONE, empty VISIBLE
hasil ada          → list VISIBLE, empty GONE
```

Belum ada perbedaan antara:

```text
app benar-benar kosong
vs search tidak menemukan hasil
```

### 2.2 Panduan Markdown saat ini

Sudah ada `MarkdownGuideActivity` yang:

- memakai theme sesuai `ThemePref`;
- memakai `MarkdownPreviewRenderer`;
- membaca asset lokal `help/markdown_guide.md`;
- tidak membutuhkan internet.

Ini bisa dijadikan pola untuk halaman `Tentang NOTEZ`.

### 2.3 Settings saat ini

`SettingsActivity` sudah punya section `Aplikasi` dengan row:

```text
Privacy / Offline
Versi
```

Halaman `Tentang NOTEZ` bisa ditaruh di section ini sebagai row baru:

```text
Tentang NOTEZ
```

### 2.4 Asset icon

Icon kuda NOTEZ tersedia di:

```text
app/src/main/res/drawable-nodpi/ic_notez_horse_launcher_fg.png
```

Untuk empty state, asset ini bisa dipakai sebagai visual utama. Jika ukuran/kontras kurang cocok, bisa dibuat drawable/vector turunan khusus empty state, tapi MVP sebaiknya reuse asset yang sudah user approve.

---

## 3. Problem statement

Saat NOTEZ kosong, pesan sekarang fungsional tapi dingin:

```text
Belum ada catatan.
Ketuk + untuk mulai.
```

User mengusulkan empty state yang lebih branded:

```text
Icon kuda NOTEZ
NOTEZ
catat ide, rencana, dan lainnya sebelum ditelan waktu
Ketuk + untuk mulai.
Panduan NOTEZ
```

Ini bagus karena:

- memberi identitas NOTEZ sejak layar pertama;
- menjelaskan value app secara singkat;
- tetap tidak mengganggu user lama karena hanya muncul saat kosong;
- bisa mengarahkan user baru ke panduan lengkap tanpa popup.

---

## 4. Goals

- Mengganti empty state kosong menjadi branded, minimal, dan jelas.
- Memakai icon kuda NOTEZ di empty state.
- Menampilkan copy final:

```text
NOTEZ

Catat ide, rencana, dan lainnya
sebelum ditelan waktu.

Ketuk + untuk mulai.
Panduan NOTEZ
```

- `Panduan NOTEZ` bisa diklik dan membuka halaman lokal/offline.
- Tambah halaman `Tentang NOTEZ` / `Panduan NOTEZ` yang bisa dibuka dari:
  - empty state;
  - Settings → Aplikasi.
- Membedakan empty state app kosong dengan search no-result.
- Tidak menambah permission internet.
- Tidak menambah onboarding popup, drawer hint, atau UI ramai.
- Tetap theme-aware untuk semua curated themes.

---

## 5. Non-goals

Tidak termasuk scope ini:

- NOTEZ Explorer / folder tree;
- database migration;
- folder/tag/pin/archive/trash;
- Markdown renderer behavior baru;
- release/tag APK baru;
- animasi besar;
- remote documentation;
- analytics/telemetry;
- automatic tutorial modal;
- perubahan drawer besar;
- redesign total home list/card.

---

## 6. UX design

### 6.1 Empty state alignment

Rekomendasi final:

```text
center organized
```

Aturan:

- icon center;
- title `NOTEZ` center;
- tagline center, maksimal 2 baris;
- hint `Ketuk + untuk mulai.` center;
- link `Panduan NOTEZ` center;
- tanpa card;
- tanpa animasi di MVP;
- vertical position center-ish, tidak terlalu bawah.

### 6.2 Empty state copy final

```text
NOTEZ

Catat ide, rencana, dan lainnya
sebelum ditelan waktu.

Ketuk + untuk mulai.
Panduan NOTEZ
```

Catatan bahasa:

- pakai `lainnya`, bukan `lainya`;
- gunakan sentence-case untuk kesan lebih premium;
- tagline dibuat dua baris supaya wrap di HP tetap cantik.

### 6.3 Visual detail draft

Approximation:

```text
Icon size        : 72dp–88dp
Title            : 24sp, bold, colorOnSurface
Tagline          : 15sp, colorOnSurfaceVariant, center, max width sekitar 280dp
Hint             : 14sp, colorOnSurfaceVariant
Panduan NOTEZ    : 14sp, colorPrimary/accent, clickable text button
Spacing          : generous but compact
```

No card untuk empty state agar tetap clean.

### 6.4 State rules

Rules yang harus diimplementasikan:

```text
No notes + no search query:
  show welcome empty state

No notes + search query:
  show search/no-result empty state, bukan welcome

Has notes + search no result:
  show "Tidak ada hasil" state, bukan welcome

Has notes:
  show note list
```

Search no-result copy draft:

```text
Tidak ada hasil.
Coba kata kunci lain.
```

Kalau database benar-benar kosong tapi search terbuka, copy bisa tetap search-context:

```text
Belum ada catatan untuk dicari.
Ketuk + untuk mulai.
```

Namun MVP bisa memakai satu search-empty text yang aman:

```text
Tidak ada hasil.
```

### 6.5 Click behavior

- Tap FAB `+` tetap membuat catatan baru seperti sekarang.
- Tap `Panduan NOTEZ` membuka `AboutNotezActivity` / `NotezGuideActivity`.
- Back dari halaman panduan kembali ke home.
- Empty state hilang otomatis saat note list tidak kosong.
- Empty state muncul lagi jika semua notes terhapus dan tidak ada search query.

---

## 7. Tentang NOTEZ / Panduan NOTEZ page

### 7.1 Placement

Akses dari:

```text
Home empty state → Panduan NOTEZ
Settings → Aplikasi → Tentang NOTEZ
```

Tidak disarankan menambah top-level drawer item baru agar drawer tetap clean.

### 7.2 Layout

Gunakan pola mirip `MarkdownGuideActivity`:

```text
Top bar: back + title "Tentang NOTEZ"
Body: local WebView Reading View via MarkdownPreviewRenderer
Asset: app/src/main/assets/help/about_notez.md
```

Halaman lengkap bersifat article-style dan **left-aligned** karena konten lebih panjang.

### 7.3 Draft struktur konten

```md
# Tentang NOTEZ

NOTEZ adalah aplikasi Android lokal untuk menyimpan ide, catatan, todo, draft, snippet, dan hal penting sebelum lupa.

## Fokus NOTEZ
- cepat dicatat
- nyaman dibaca
- offline-first
- privasi aman
- Markdown-ready

## Mulai cepat
1. Ketuk + untuk membuat catatan.
2. Tulis judul dan isi.
3. Ketuk centang untuk masuk mode baca.
4. Ketuk pensil untuk edit lagi.

## Mode edit dan mode baca
Edit mode memakai teks biasa.
Mode baca memakai Markdown Preview v4.

## Markdown
NOTEZ mendukung heading, list, table, callout, code block, footnote, definition list, dan safe raw HTML kecil.
Untuk syntax lengkap, buka Panduan Markdown.

## Backup dan ekspor
NOTEZ mendukung backup/import JSON dan export TXT.

## Privasi dan offline
NOTEZ tidak memakai permission INTERNET, tidak memakai CDN, dan tidak memuat remote resource diam-diam.

## Batasan saat ini
Remote image tidak auto-load, belum ada sync/cloud, dan belum ada real Trash.
```

Content harus ditulis ulang dari README agar lebih user-facing.

---

## 8. Technical design draft

### 8.1 Files kandidat

```text
app/src/main/java/com/zaba/notez/AboutNotezActivity.kt
app/src/main/res/layout/activity_about_notez.xml
app/src/main/assets/help/about_notez.md
app/src/main/res/layout/activity_main.xml
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/res/layout/activity_settings.xml
app/src/main/java/com/zaba/notez/SettingsActivity.kt
app/src/main/AndroidManifest.xml
app/src/main/res/values/strings.xml
```

### 8.2 Home layout change

Replace plain `TextView @id/empty` with a vertical container, e.g.:

```text
LinearLayout @id/empty_welcome
  ImageView horse icon
  TextView NOTEZ
  TextView tagline
  TextView hint
  TextView Panduan NOTEZ clickable

TextView @id/empty_search
```

Alternative: keep one container and swap copy by state, but separate views are cleaner.

### 8.3 MainActivity state logic

Need distinguish:

```kotlin
val emptyResult = it.isEmpty()
val searching = query.isNotBlank()
```

Recommended:

```text
list visible         : !emptyResult
welcome visible      : emptyResult && !searching
search empty visible : emptyResult && searching
```

Caveat: `it.isEmpty()` while searching cannot tell whether database has notes. For MVP, if search query is not blank and result empty, show search-empty copy. This is acceptable and avoids extra DB flow.

### 8.4 About activity

Use same renderer as Markdown guide:

```kotlin
markdownPreview = MarkdownPreviewRenderer(this, webView)
val markdown = assets.open("help/about_notez.md").bufferedReader().use { it.readText() }
markdownPreview.render(markdown)
```

### 8.5 Settings row

Add row in `Aplikasi` section:

```text
Tentang NOTEZ
```

Tap opens `AboutNotezActivity`.

### 8.6 Manifest

Add activity:

```xml
<activity android:name=".AboutNotezActivity" android:exported="false" />
```

Do not add `INTERNET` permission.

---

## 9. Accessibility

- Horse icon decorative in empty state:

```text
importantForAccessibility="no"
contentDescription="@null"
```

- Text copy readable by TalkBack.
- `Panduan NOTEZ` clickable text should be focusable/clickable with clear text.
- FAB already needs meaningful content description; current copy is `Catatan baru`.
- About page back button uses existing `@string/cd_back`.

---

## 10. Acceptance criteria

### Product

- [ ] Saat tidak ada catatan dan search tidak aktif, home menampilkan centered welcome empty state.
- [ ] Empty state menampilkan icon kuda NOTEZ.
- [ ] Empty state menampilkan copy final:

```text
NOTEZ
Catat ide, rencana, dan lainnya
sebelum ditelan waktu.
Ketuk + untuk mulai.
Panduan NOTEZ
```

- [ ] Tap `Panduan NOTEZ` membuka halaman `Tentang NOTEZ` lokal/offline.
- [ ] Settings → Aplikasi punya row `Tentang NOTEZ`.
- [ ] Tap Settings row membuka halaman yang sama.
- [ ] Tap FAB `+` tetap membuat catatan baru seperti sebelumnya.
- [ ] Welcome empty state hilang saat ada catatan.
- [ ] Search no-result tidak menampilkan welcome NOTEZ.

### Privacy/security

- [ ] Tidak ada `android.permission.INTERNET`.
- [ ] Tidak ada CDN/external script/style/font.
- [ ] Tidak ada `addJavascriptInterface`.
- [ ] About content berasal dari asset lokal.
- [ ] Link eksternal, jika ada, tetap mengikuti policy renderer.

### Regression

- [ ] Note list existing tetap tampil normal.
- [ ] Search open/close tetap normal.
- [ ] Drawer/Settings/Panduan Markdown/Music drawer tidak berubah behavior.
- [ ] Theme switching tetap konsisten.
- [ ] Markdown Preview v4 tidak diubah.

---

## 11. Test plan

Static/source checks:

```bash
# XML parse
python3 - <<'PY'
import xml.etree.ElementTree as ET
from pathlib import Path
for p in Path('app/src/main/res').rglob('*.xml'):
    ET.parse(p)
print('xml ok')
PY

# privacy guards
grep -R "android.permission.INTERNET" app/src/main || true
grep -R "addJavascriptInterface" app/src/main || true
grep -R "https://cdn\|unpkg\|jsdelivr\|cdnjs" app/src/main || true

git diff --check
```

Manual UAT:

```text
Fresh/no-note state shows welcome                 : PASS/FAIL
Tap Panduan NOTEZ from empty state opens About    : PASS/FAIL
Back returns home                                 : PASS/FAIL
Tap + creates note                                : PASS/FAIL
After note exists, welcome hidden                 : PASS/FAIL
Search with no result shows search empty          : PASS/FAIL
Settings → Tentang NOTEZ opens About              : PASS/FAIL
Theme change keeps empty/About readable           : PASS/FAIL
No INTERNET permission in built artifact          : PASS/FAIL
```

---

## 12. Local verification

Static/source checks run on 2026-09-29:

```text
XML parse app/src/main/res/**/*.xml                    : PASS
No android.permission.INTERNET                         : PASS
No addJavascriptInterface                              : PASS
No CDN marker in app/src/main                           : PASS
git diff --check                                       : PASS
Local Android build                                    : NOT RUN — no Gradle wrapper/global gradle in sandbox
```

CI verification for implementation commit `0a7ea6b`:

```text
GitHub Actions run : 36522374451
URL                : https://github.com/muzape28-blip/NOTEZ/actions/runs/36522374451
Conclusion         : success
Artifact           : notez-debug
Artifact id        : 11013099085
Artifact size      : 8,096,186 bytes
```

---

## 13. Risks and mitigations

### Risk 1 — Empty state becomes too promotional

Mitigation:

- keep copy short;
- no card;
- no animation MVP;
- one help link only.

### Risk 2 — Search no-result confused with app empty

Mitigation:

- explicit state split for `query.isBlank()` vs search query.

### Risk 3 — About page duplicates Markdown Guide

Mitigation:

- About explains app usage and philosophy;
- Markdown Guide remains syntax-specific;
- About can link/mention Panduan Markdown without copying the whole guide.

### Risk 4 — Drawer/settings clutter

Mitigation:

- About goes in Settings under `Aplikasi`, not drawer top-level.
- Empty state link only appears when home is empty.

---

## 14. Rollback

If feature is disliked:

- revert `AboutNotezActivity`, layout, asset, manifest entry;
- restore old `TextView @id/empty` copy;
- remove Settings row click handler.

No database migration or data changes are involved, so rollback is low-risk.

---

## 15. Recommendation

Implementation follows the approved scope:

```text
Home Empty State + Tentang NOTEZ v1
- center organized welcome on truly empty home
- icon kuda NOTEZ
- copy approved by user
- local About/Panduan NOTEZ page
- Settings row
- no new permissions
- no onboarding popup
- no Explorer/database change yet
```

Next gate: GitHub Actions CI, then device UAT.
