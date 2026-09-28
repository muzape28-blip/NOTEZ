# RFC — NOTEZ Theme System v2 / Appearance Polish

| Field | Value |
| --- | --- |
| Tanggal | 2026-09-28 |
| Status | CI VERIFIED — debug build success; device UAT pending |
| Repo | NOTEZ |
| Basis saat ditulis | `main` @ `19e946e` (`docs: finalize v0.1.2 release notes`) |
| Jenis perubahan | UI/UX appearance + theme system proposal |
| Prinsip utama | NOTEZ tetap cepat, offline-first, Markdown-focused, Android-native, dan clean; appearance polish harus curated, bukan plugin/theme marketplace bebas. |

---

## 1. Ringkasan keputusan yang diusulkan

Setelah rilis `v0.1.2`, arah UI/UX berikutnya yang paling sehat adalah:

```text
Theme System v2 / Appearance Polish
```

Bukan rewrite besar. Bukan Obsidian clone. Bukan Notion clone.

Proposal inti:

- pertahankan identitas NOTEZ:
  - local/offline-first;
  - native Android;
  - Markdown-ready;
  - simple note capture;
  - no `INTERNET` permission;
- rapikan theme system yang sekarang masih sederhana;
- tambahkan theme picker dengan preview kecil;
- tambah tema curated, bukan marketplace;
- polish home cards, drawer/settings, dan Markdown reading feel;
- optional appearance settings yang kecil dan aman:
  - card style;
  - card density;
  - preview text size;
- jangan menyentuh data model, renderer security contract, sync, plugin, atau navigation besar dulu.

Direction singkatnya:

```text
Google Keep cepat
+ Standard Notes tenang/private
+ Markor/Joplin Markdown lokal
+ Material Design 3 polish
+ sedikit Obsidian/Logseq/Anytype sebagai inspirasi future linking, bukan scope sekarang.
```

---

## 2. Source/repo inspection sebelum RFC

File yang dicek untuk konteks current implementation:

```text
AGENTS.md
README.md
app/src/main/java/com/zaba/notez/ThemePref.kt
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/java/com/zaba/notez/EditorActivity.kt
app/src/main/java/com/zaba/notez/NoteAdapter.kt
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/res/values/colors.xml
app/src/main/res/values/themes.xml
app/src/main/res/layout/activity_main.xml
app/src/main/res/layout/activity_editor.xml
app/src/main/res/layout/activity_markdown_guide.xml
app/src/main/res/layout/item_note.xml
```

Current facts:

- Theme disimpan di `SharedPreferences` lewat `ThemePref`:
  - key: `notez/theme`;
  - current constants:
    - `OLED = 0`;
    - `GITHUB_DARK = 1`;
    - `COBALT2 = 2`;
  - default saat ini: `GITHUB_DARK`.
- Theme diterapkan dengan `setTheme(ThemePref.styleOf(...))` di:
  - `MainActivity`;
  - `EditorActivity`.
- Markdown Reading View memakai `MarkdownPreviewRenderer`, dan renderer membaca `ThemePref` sendiri untuk warna preview.
- Current palette token masih minimal:
  - bg;
  - surface;
  - text;
  - secondary/muted;
  - accent;
  - danger.
- Home card saat ini sudah memakai `MaterialCardView`:
  - background `?attr/colorSurface`;
  - radius `12dp`;
  - elevation `0dp`;
  - stroke `1dp` `?attr/colorOutline`;
  - title/preview/meta + trash button.
- FAB sudah floating overlay setelah v0.1.2.
- Drawer sudah punya:
  - `Panduan Markdown` top-level;
  - `Settings` expandable;
  - `Tema`, export/import/backup;
  - music drawer include.
- Ada mixed language kecil: top-level label `Settings`, sedangkan child menu sudah Bahasa Indonesia.

Implikasi penting:

```text
Theme System v2 sebaiknya evolve dari ThemePref dan resource theme yang ada,
bukan rewrite total.
```

---

## 3. Research basis dan sampling limit

Research dilakukan sebagai design reference, bukan hands-on full audit semua aplikasi. Sebagian sumber adalah official docs; sebagian adalah artikel/review UI sebagai secondary source.

### 3.1 Reference apps/materials

| Reference | Takeaway untuk NOTEZ | Yang dihindari |
| --- | --- | --- |
| Google Keep | Home cepat, card-based, search, FAB/quick capture, list/grid sebagai future option | Sticky-note board terlalu ramai, quick-create menu berlebihan |
| Material Design 3 | FAB untuk primary action, nav bar hanya jika ada 3-5 top-level destination, komponen native konsisten | Bottom nav/bottom app bar palsu tanpa kebutuhan |
| Standard Notes | Calm/private UI, tags/search, protected/archived/trashed sebagai future organization | Account/subscription/editor ecosystem sebagai scope sekarang |
| Joplin | Offline-first Markdown, notebooks/tags/search, mobile editor/read mode polish | Plugin/sync/notebook tree terlalu berat untuk NOTEZ sekarang |
| Markor | Android local Markdown/text utility, offline, no unnecessary permissions, local preview workflow | Jadi file manager penuh atau multi-format editor berat |
| Simplenote | Minimal, search/tag-first, pins, Markdown sederhana | Terlalu polos sampai Markdown Preview NOTEZ kehilangan identitas |
| Notion | Visual hierarchy, spacing, page structure, empty state | Block editor/database/collaboration model |
| Anytype | Local-first/private positioning, modern cards, ownership story | Object/type/relation/graph workspace complexity |
| Logseq | Local-first linked knowledge, backlinks/graph sebagai future idea | Block-first outliner/query/graph sebagai scope sekarang |
| GitHub Markdown/README style | Markdown readability, code/table/callout feel, predictable formatting | Raw HTML behavior GitHub tidak harus dicopy karena NOTEZ lebih strict |

### 3.2 External references used

Primary/official-ish references:

- Material Design 3 — Floating action button guidelines:
  https://m3.material.io/components/floating-action-button/guidelines
- Material Design 3 — Navigation bar guidelines:
  https://m3.material.io/components/navigation-bar/guidelines
- Material Design 3 — Toolbars guidelines:
  https://m3.material.io/components/toolbars/guidelines
- Standard Notes — Plans/features:
  https://standardnotes.com/plans
- Standard Notes — Security explanation:
  https://standardnotes.com/help/3/how-does-standard-notes-secure-my-notes
- Standard Notes — Search improvements:
  https://standardnotes.com/blog/search-improvements
- Joplin Help:
  https://joplinapp.org/help/
- Joplin 3.6 release notes:
  https://joplinapp.org/news/20260505-release-3-6/
- Joplin plugins help:
  https://joplinapp.org/plugins/
- Markor repository:
  https://github.com/gsantner/markor
- Notion offline docs:
  https://www.notion.com/help/use-pages-offline
- Notion API quick start / block model reference:
  https://developers.notion.com/guides/get-started/quick-start
- Anytype sync/backup docs:
  https://doc.anytype.io/anytype/data/sync-and-backup
- Anytype local-only docs:
  https://doc.anytype.io/anytype/data/sync-and-backup/local-only
- Anytype collaboration docs:
  https://doc.anytype.io/anytype/collaborate/collaboration
- Logseq toolkit overview:
  https://bellingcat.gitbook.io/toolkit/more/all-tools/logseq
- Logseq DB update discussion:
  https://discuss.logseq.com/t/whats-new-with-logseq-db-may-16th-2026/35020

Secondary UI/design references:

- Google Design — Keep/Material design adaptation:
  https://medium.com/google-design/how-google-designers-adapt-material-e2818ad09d7d
- XDA — Material You Google Keep:
  https://www.xda-developers.com/material-you-google-keep/
- 9to5Google — Keep Material You widget/UI direction:
  https://9to5google.com/2021/09/16/google-keep-material-you-widget-2/
- 9to5Google — Google Keep FAB redesign:
  https://9to5google.com/2024/11/05/google-keep-fab-redesign/
- Android Police — Google Keep Material 3 Expressive look:
  https://www.androidpolice.com/google-keep-on-android-now-fully-supports-the-material-3-expressive-look/
- Simplenote blog:
  https://simplenote.com/blog/
- Simplenote App Store listing:
  https://apps.apple.com/us/app/simplenote/id289429962
- Simplenote Play Store listing:
  https://play.google.com/store/apps/details?id=com.automattic.simplenote

---

## 4. Product constraints yang wajib dijaga

Theme System v2 tidak boleh merusak kontrak NOTEZ saat ini:

```text
No android.permission.INTERNET
No CDN
No remote scripts/styles/fonts/images
No addJavascriptInterface
Edit mode tetap native EditText
View mode tetap local WebView Reading View
Raw HTML tetap escaped/disabled by default
Delete tetap delete-with-undo, bukan real Trash
No drawer hint/onboarding text
No plugin ecosystem
No arbitrary CSS snippets
No remote theme marketplace
```

Batasan penting:

- Theme boleh mempercantik app, tapi tidak boleh membuka remote content.
- Markdown preview boleh ikut theme, tapi renderer security contract tidak berubah.
- Appearance settings boleh membantu user, tapi jangan membuat Settings jadi berat.
- Card polish boleh dilakukan, tapi jangan ubah navigation atau data behavior.

---

## 5. Goals

Theme System v2 / Appearance Polish harus:

1. Membuat NOTEZ terasa lebih mature dan konsisten secara visual.
2. Tetap menjaga capture flow cepat:

   ```text
   home → + → tulis
   home → search → buka note
   ```

3. Mempertahankan semua theme lama agar user tidak kehilangan pilihan.
4. Menambah theme curated yang jelas personality-nya.
5. Membuat picker tema lebih visual lewat preview kecil.
6. Menyamakan feel native UI dan Markdown Reading View.
7. Memberi sedikit kontrol appearance tanpa menjadikan NOTEZ aplikasi kustomisasi berat.
8. Meningkatkan readability catatan, card, code block, table, dan callout.
9. Mempertahankan accessibility dasar:
   - target tap minimal mendekati 48dp untuk action penting;
   - contrast primary text aman;
   - content description tidak hilang;
   - delete action tetap discoverable.
10. Bisa diverifikasi dengan static checks + CI + device UAT.

---

## 6. Non-goals

RFC ini tidak bertujuan untuk:

- membuat plugin system;
- membuat theme marketplace;
- mengizinkan arbitrary CSS snippet;
- fetch theme dari internet;
- menambah permission network;
- mengubah raw HTML policy;
- menambah sync/account/cloud;
- membuat Notion-like block editor;
- membuat Logseq-like outliner;
- membuat Obsidian-like graph;
- membuat real Trash / Recently Deleted;
- mengubah delete flow;
- mengubah editor dari native `EditText`;
- mengganti Markdown renderer utama;
- menambah dependency besar;
- mengubah database schema catatan;
- menambah bottom navigation;
- menambah drawer hint/onboarding text.

---

## 7. Design principles untuk NOTEZ

### 7.1 Quick capture first

Setiap polish harus tunduk ke flow utama:

```text
buka app
lihat catatan
cari / tambah
selesai
```

Kalau setting/theme bikin capture lebih lambat, itu bukan polish.

### 7.2 Calm privacy, not loud security theater

Ambil rasa Standard Notes/Anytype:

```text
tenang
rapi
private
percaya diri
```

Tapi jangan memenuhi UI dengan badge/alert privacy. Privacy stance cukup muncul di README, guide, dan wording seperlunya.

### 7.3 Markdown readability is product identity

NOTEZ bukan sekadar text box. Markdown Preview v2 adalah nilai utama.

Theme harus memperhatikan:

- heading;
- body text;
- muted text;
- links;
- code block;
- inline code;
- table borders;
- callout accent;
- image placeholder;
- task checkbox.

### 7.4 Curated personalization

User boleh pilih tampilan, tapi pilihan harus terbatas dan bagus.

```text
curated themes > unlimited CSS
preview picker > raw config
small settings > settings maze
```

### 7.5 Android-native, not web app

Pakai Material/native components yang sudah ada. Jangan mengubah app jadi UI web full-screen.

---

## 8. Proposed Theme System v2 scope

### 8.1 Theme list

Recommended curated themes untuk v2:

| Theme | Status | Intent |
| --- | --- | --- |
| GitHub Dark | existing, pertahankan default | Markdown/dev/readme vibe, balanced dark |
| OLED Black / OLED Hijau | existing, polish naming optional | AMOLED/focus, NOTEZ identity lama |
| Cobalt2 | existing | playful coder theme |
| NOTEZ You Dark | new candidate | Material You-ish dark, Android-native calm |
| NOTEZ Paper | new candidate, light/warm | reading/writing nyaman siang hari |

Catatan compatibility:

```text
Jangan reorder integer constants ThemePref lama.
```

Recommended future constant append-only:

```kotlin
const val OLED = 0
const val GITHUB_DARK = 1
const val COBALT2 = 2
const val NOTEZ_YOU_DARK = 3
const val NOTEZ_PAPER = 4
```

Alasan:

- user lama yang menyimpan value `0`, `1`, atau `2` tidak berubah tema secara tidak sengaja;
- migration preference tidak perlu;
- fallback bisa tetap aman ke GitHub Dark.

### 8.2 Token model

Current token sudah cukup untuk MVP:

```text
background
surface
text
secondary/muted
accent
onAccent
danger
```

Untuk polish lebih rapi, candidate token tambahan:

```text
outline / border
surfaceSoft
codeBackground
codeBorder
cardBackground
cardBorder
calloutNote
calloutTip
calloutImportant
calloutWarning
calloutCaution
```

MVP boleh tetap memakai derived values dulu, tapi jangan hardcode terlalu banyak warna di renderer jika nanti theme bertambah.

### 8.3 Theme picker dengan preview kecil

Current picker adalah `AlertDialog` single-choice text list.

Proposed v2 picker:

```text
Tema
────────────────
[mini preview] GitHub Dark
[mini preview] OLED Black
[mini preview] Cobalt2
[mini preview] NOTEZ You Dark
[mini preview] NOTEZ Paper
```

Mini preview bisa sederhana:

```text
┌───────────────┐
│ Title         │
│ preview text  │
│ code  link    │
└───────────────┘
```

Acceptance preview:

- preview memakai palette theme target, bukan current theme saja;
- selected theme terlihat jelas;
- tap item langsung apply dan `recreate()` seperti sekarang;
- tidak perlu network/image/font.

### 8.4 Card appearance

Proposed settings kecil:

```text
Tampilan → Gaya card
- Outline      default/current-ish
- Soft filled  Material-ish
- Compact      more notes visible
```

Atau kalau mau lebih kecil untuk MVP:

```text
Gaya card: Outline / Soft
Kepadatan: Nyaman / Ringkas
```

Recommended MVP:

```text
Phase 1: polish default card only
Phase 2: add card style/density setting kalau default sudah enak
```

Card polish candidate:

- radius konsisten `12dp` atau `16dp`;
- stroke lebih halus per theme;
- preview text lebih muted;
- meta/date lebih rapi;
- delete trash tetap ada, muted, tidak jadi primary action;
- FAB tidak tertutup card terakhir.

### 8.5 Markdown Reading View alignment

`MarkdownPreviewRenderer` perlu ikut theme baru.

Current `PreviewColors.from(activity)` harus diupdate kalau theme baru masuk. Jangan sampai native UI berubah tema, tapi WebView preview fallback ke GitHub Dark.

Candidate polish:

- body text line-height tetap nyaman;
- code block background sesuai theme;
- table border sesuai theme;
- callout tetap readable di semua theme;
- NOTEZ Paper membutuhkan warna CSS light yang jelas;
- raw HTML tetap escaped;
- remote image tetap placeholder.

### 8.6 Drawer/settings language polish

Current drawer top-level memakai `Settings`; submenu memakai Bahasa Indonesia.

Proposal kecil:

```text
Settings → Pengaturan
Tema → Tema
Ekspor JSON → Ekspor JSON
Ekspor TXT → Ekspor TXT
Impor JSON → Impor JSON
Folder backup otomatis → Folder backup otomatis
```

No hint text. No onboarding. Tetap clean.

### 8.7 Empty state polish

Current empty state:

```text
Belum ada catatan.
Ketuk + untuk mulai.
```

Ini masih acceptable karena muncul hanya saat kosong dan bukan drawer hint. Kalau dipoles, tetap singkat:

```text
Belum ada catatan.
Ketuk + untuk mulai nulis.
```

Tapi ini bukan prioritas v2.

---

## 9. Recommended implementation phases

### Phase 0 — Design audit / token audit

Status: proposal only.

Scope:

- catat semua resource warna/theme yang dipakai;
- cari hardcoded color di layout/Kotlin/renderer;
- pastikan theme lama masih kompatibel;
- tentukan token minimal.

Deliverable:

```text
diff kecil atau catatan audit sebelum implementation besar
```

### Phase 1 — Theme System v2 MVP

Recommended first implementation batch:

- append theme constants baru;
- tambah palette resources untuk `NOTEZ You Dark`;
- update `themes.xml`;
- update `ThemePref.NAMES`;
- update `MarkdownPreviewRenderer.PreviewColors`;
- ganti picker dari text-only ke preview item sederhana;
- keep existing themes.

Kenapa mulai dari dark theme dulu?

```text
Semua current theme parent-nya Material3.Dark.NoActionBar.
Light theme butuh cek lebih banyak: dialog, status/nav bar, contrast, WebView CSS, card stroke, icon tint.
```

### Phase 2 — NOTEZ Paper / light theme

Masukkan setelah Phase 1 stabil.

Scope:

- tambah `Theme.Material3.Light.NoActionBar` variant jika perlu;
- audit dialog text/input/icon tint;
- audit Markdown guide + editor + home cards;
- UAT siang/light mode.

### Phase 3 — Card appearance setting

Scope candidate:

- `AppearancePref` kecil untuk:
  - card style;
  - density;
- update `item_note.xml`/adapter binding sesuai style;
- no grid/list setting dulu kecuali disetujui terpisah.

### Phase 4 — Preview typography setting

Candidate later:

```text
Ukuran teks preview:
- Kecil
- Normal
- Besar
```

Ini mempengaruhi Markdown WebView CSS dan mungkin editor body. Perlu UAT readability.

---

## 10. MVP recommendation

Kalau harus dipilih satu patch paling aman, gw rekomendasikan:

```text
Theme System v2 MVP:
1. Tambah NOTEZ You Dark.
2. Buat theme picker preview sederhana.
3. Update Markdown preview colors untuk semua theme.
4. Rename/polish label Settings → Pengaturan.
5. Tidak tambah card style setting dulu.
```

Kenapa:

- impact visual terasa;
- scope masih kecil;
- tidak menyentuh DB;
- tidak menyentuh note behavior;
- tidak membuka permission/network;
- lebih mudah di-UAT.

`NOTEZ Paper` dan card style/density bagus, tapi lebih aman setelah picker + dark theme stabil.

---

## 11. Acceptance criteria

### 11.1 Product acceptance

- [ ] Existing theme `OLED`, `GitHub Dark`, dan `Cobalt2` tetap tersedia.
- [ ] Existing stored theme preference tidak berubah meaning-nya.
- [ ] Theme baru muncul di picker.
- [ ] Theme picker menampilkan preview visual kecil, bukan hanya list teks.
- [ ] Tap theme menerapkan theme dan refresh UI.
- [ ] Home, editor edit mode, editor view mode, drawer, guide, snackbar, dan dialogs tetap readable.
- [ ] Markdown Reading View mengikuti theme yang dipilih.
- [ ] FAB tetap primary create action di kanan bawah.
- [ ] Delete trash glyph tetap di card dan tetap memakai undo snackbar.
- [ ] Search tetap bisa dibuka/tutup.
- [ ] Drawer tetap clean; tidak ada onboarding/hint baru.
- [ ] App tetap terasa cepat untuk buka home dan buka editor.

### 11.2 Security/privacy acceptance

- [ ] Tidak ada `android.permission.INTERNET`.
- [ ] Tidak ada CDN/remote script/style/font/image.
- [ ] Tidak ada `addJavascriptInterface`.
- [ ] Raw HTML tetap disabled/escaped.
- [ ] Remote Markdown image tetap placeholder/link, bukan auto-fetch.
- [ ] Theme tidak diunduh dari network.
- [ ] Tidak ada token/secret/config sensitif yang ter-commit.

### 11.3 Technical acceptance

- [ ] XML parse valid.
- [ ] Kotlin compile via CI debug build.
- [ ] `ThemePref` append-only untuk constants lama.
- [ ] Theme fallback aman untuk unknown value.
- [ ] `MarkdownPreviewRenderer` punya mapping untuk theme baru.
- [ ] Layout tap targets action utama tetap minimal sekitar 40-48dp; destructive action tidak dibuat lebih dominan dari create.
- [ ] No dependency baru kecuali dibahas dan disetujui dulu.
- [ ] No generated cache/build artifact committed.

---

## 12. UAT plan

Manual UAT setelah implementation + debug CI green:

1. Install debug APK dari CI.
2. Buka NOTEZ home.
3. Buka drawer → Pengaturan/Settings → Tema.
4. Pastikan picker theme dengan preview muncul.
5. Pilih setiap theme satu per satu:
   - GitHub Dark;
   - OLED/OLED Black;
   - Cobalt2;
   - NOTEZ You Dark;
   - NOTEZ Paper jika sudah masuk phase tersebut.
6. Untuk setiap theme:
   - home cards readable;
   - title/preview/meta readable;
   - trash glyph terlihat tapi tidak terlalu dominan;
   - FAB terlihat jelas;
   - snackbar delete/undo readable;
   - drawer readable;
   - dialog theme readable;
   - search input readable.
7. Buka note Markdown sample:
   - heading;
   - bold/italic;
   - table;
   - code block;
   - callout;
   - checklist;
   - link;
   - remote image placeholder;
   - raw HTML escaped.
8. Toggle edit/view mode.
9. Tutup app, buka lagi, pastikan theme pilihan tersimpan.
10. Rotate device jika memungkinkan.
11. Cek low brightness untuk dark theme dan bright environment untuk light theme.

Evidence labels:

- `DESIGNED`: RFC ini.
- `IMPLEMENTED`: code sudah ditulis.
- `LOCALLY VERIFIED`: static checks/lokal tersedia dan pass.
- `CI VERIFIED`: GitHub Actions debug build pass.
- `DEVICE VERIFIED`: user install di device dan lapor pass.
- `RELEASED`: APK published di GitHub Release.

---

## 13. Risks and mitigations

### Risk 1 — Scope creep jadi theme/plugin ecosystem

Mitigasi:

- theme list curated;
- no arbitrary CSS;
- no marketplace;
- no remote theme import;
- no plugin architecture.

### Risk 2 — Light theme merusak dialog/icon contrast

Mitigasi:

- implement dark theme MVP dulu;
- light theme jadi phase terpisah;
- UAT semua screen/dialog sebelum claim pass.

### Risk 3 — WebView preview beda warna dari native UI

Mitigasi:

- update mapping `PreviewColors` bersamaan dengan resource theme;
- buat static guard sederhana agar theme constants dan preview mapping tidak tertinggal;
- UAT note Markdown sample per theme.

### Risk 4 — Stored preference lama berubah arti

Mitigasi:

- jangan reorder constants `0`, `1`, `2`;
- append constants baru;
- fallback unknown ke GitHub Dark.

### Risk 5 — Card style setting bikin UI terlalu kompleks

Mitigasi:

- jangan masuk MVP pertama;
- polish default card dulu;
- kalau setting masuk, batasi ke 2-3 opsi.

### Risk 6 — Visual polish mengganggu fast capture

Mitigasi:

- no modal/tutorial on launch;
- no extra step sebelum create note;
- no bottom nav;
- no hidden primary action.

---

## 14. Rollback plan

Kalau implementation theme v2 bermasalah:

- revert perubahan `ThemePref`, `colors.xml`, `themes.xml`, theme picker layout/adapter, dan renderer color mapping;
- preference lama tetap aman jika constants lama tidak diubah;
- tidak ada migrasi DB;
- tidak ada perubahan note content;
- tidak perlu rollback backup/import/export.

Jika theme baru saja yang bermasalah:

```text
hide/remove theme baru dari NAMES/styleOf mapping
fallback ke GitHub Dark
```

---

## 15. Open decisions sebelum coding

Sebelum implement, perlu approve detail kecil ini:

1. Nama final theme lama:
   - tetap `OLED Hijau`; atau
   - rename display ke `OLED Black` / `OLED Green` tanpa mengubah stored constant.
2. MVP theme baru:
   - hanya `NOTEZ You Dark`; atau
   - langsung tambah `NOTEZ You Dark` + `NOTEZ Paper`.
3. Theme picker style:
   - simple custom dialog list dengan preview; atau
   - screen/settings page khusus appearance.
4. Card appearance setting:
   - defer setelah theme picker stabil; atau
   - masuk sekalian sebagai v2 scope.

Rekomendasi gw:

```text
1. Display rename: OLED Black.
2. MVP theme baru: NOTEZ You Dark dulu.
3. Picker: custom dialog list dengan mini preview.
4. Card appearance setting: defer phase berikutnya.
```

---

## 16. Current decision recommendation

Untuk next implementation task, scope paling tajam:

```text
Implement Theme System v2 MVP:
- append NOTEZ_YOU_DARK theme;
- create preview-based theme picker;
- keep old themes compatible;
- update Markdown preview color mapping;
- polish Settings → Pengaturan label;
- run static checks + CI;
- ask user device UAT.
```

Status sampai update ini:

```text
IMPLEMENTED LOCALLY untuk Theme System v2 MVP.
NOTEZ You Dark dan curated dark theme batch ditambahkan secara append-only.
Theme picker preview kecil ditambahkan di Settings page.
Markdown Preview renderer memakai token ThemePref sehingga ikut semua theme curated.
Static XML/source/security checks passed locally.
Local Gradle build tidak tersedia di sandbox karena tidak ada Gradle wrapper/global gradle.
GitHub Actions debug build passed: run 36482324634.
Debug artifact: notez-debug, artifact ID 10997580254, size metadata 8,077,165 bytes.
Device UAT pending.
```
