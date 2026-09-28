# RFC — NOTEZ Settings Page v1

| Field | Value |
| --- | --- |
| Tanggal | 2026-09-29 |
| Status | CI VERIFIED — debug build success; device UAT pending |
| Repo | NOTEZ |
| Basis saat ditulis | `main` @ `795b504` (`docs: record splash system bar fix evidence`) |
| Jenis perubahan | Navigation/settings UX restructure + future export/appearance foundation |
| Prinsip utama | Drawer tetap clean; Settings pindah ke halaman sendiri; existing backup/export/theme flow tetap aman; tidak ada network/dependency besar tanpa approval. |

---

## 1. Ringkasan keputusan yang diusulkan

Current drawer NOTEZ masih memuat Settings sebagai submenu inline:

```text
Panduan Markdown
Settings
  Tema
  Ekspor JSON
  Ekspor TXT
  Impor JSON
  Folder backup otomatis
Music
```

User mengusulkan Settings tidak lagi memakai submenu/card di drawer. Saat user tap Settings/Pengaturan, NOTEZ pindah ke halaman Settings sendiri.

Proposal:

```text
Drawer utama:
- Panduan Markdown
- Pengaturan
- Music

Pengaturan page:
- Tampilan
- Data & Backup
- Export / Save
- Aplikasi / Tentang
```

Settings page **jangan pakai card besar-besar**. Gunakan list section ala Android native:

```text
Section title
Row setting
Row setting
Divider tipis
Section title
Row setting
```

Tujuan utama v1:

- drawer lebih clean;
- settings bisa berkembang tanpa menumpuk di drawer;
- existing actions (`Tema`, export/import JSON/TXT, folder backup otomatis) tetap tersedia;
- menjadi pondasi untuk:
  - font setting;
  - Theme System v2;
  - export PDF/HTML/Markdown;
  - backup/save flow yang lebih jelas.

---

## 2. Source/repo inspection sebelum RFC

File yang dicek:

```text
README.md
app/src/main/res/layout/activity_main.xml
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/java/com/zaba/notez/ThemePref.kt
app/src/main/java/com/zaba/notez/BackupHelper.kt
app/src/main/AndroidManifest.xml
app/src/main/res/values/strings.xml
```

Current facts:

- Drawer layout berada di `activity_main.xml`.
- Current drawer punya top-level `Panduan Markdown`, `Settings`, dan include `Music`.
- `Settings` sekarang expandable submenu di drawer, bukan Activity/page.
- `MainActivity.setupDrawer()` mengatur:
  - open `MarkdownGuideActivity`;
  - expand/collapse settings submenu;
  - show theme dialog;
  - export JSON;
  - export TXT;
  - import JSON;
  - select auto backup folder.
- Existing export/import implementation ada di `MainActivity`:
  - `CreateDocument("*/*")` untuk export;
  - `OpenDocument()` untuk import JSON;
  - `OpenDocumentTree()` untuk backup folder;
  - `BackupHelper.toJson`, `BackupHelper.toTxt`, `BackupHelper.fromJson`.
- `ThemePref` saat ini menyimpan pilihan theme di SharedPreferences dan dipakai saat Activity `setTheme()`.
- `MarkdownGuideActivity` sudah menjadi contoh halaman terpisah sederhana dengan top bar/back button dan local WebView renderer.
- App tetap tidak memakai `android.permission.INTERNET`.

Implikasi:

```text
Settings Page v1 bisa dibuat sebagai Activity baru tanpa mengubah DB/schema.
Existing action logic dapat dipindah/di-share dari MainActivity ke SettingsActivity.
Drawer utama bisa disederhanakan tanpa mengubah Music drawer behavior.
```

---

## 3. Goals

Settings Page v1 harus:

1. Membuat drawer NOTEZ lebih clean.
2. Mengganti drawer submenu `Settings` menjadi navigasi ke halaman `Pengaturan`.
3. Tidak memakai card besar untuk settings; gunakan section + row.
4. Mempertahankan semua action settings lama:
   - Tema;
   - Ekspor JSON;
   - Ekspor TXT;
   - Impor JSON;
   - Folder backup otomatis.
5. Menjadi pondasi untuk appearance/export yang lebih besar:
   - jenis font;
   - ukuran teks;
   - theme picker preview;
   - export PDF;
   - export Markdown/HTML.
6. Tetap menjaga offline/privacy-first contract:
   - no `INTERNET`;
   - no CDN;
   - no remote fonts/assets;
   - no `addJavascriptInterface`.
7. Tidak mengubah data note, delete behavior, Markdown renderer, atau music playback.

---

## 4. Non-goals

RFC ini tidak langsung bertujuan untuk:

- mengimplementasikan Markdown Preview v3;
- mengaktifkan raw HTML allowlist;
- menambah syntax highlighting code block;
- mengimplementasikan full Theme System v2;
- mengimplementasikan real Trash / Recently Deleted;
- mengubah Music drawer/player;
- menambah sync/cloud/account;
- menambah `INTERNET` permission;
- menambah remote font/theme marketplace;
- menambah arbitrary CSS snippets;
- membuat export PDF dengan library besar tanpa RFC/approval lanjutan;
- mengubah editor dari native `EditText`;
- mengubah view mode dari local WebView Reading View.

---

## 5. Proposed drawer after Settings v1

Drawer utama direkomendasikan menjadi:

```text
NOTEZ

Panduan Markdown
Pengaturan

Music
  ...current music drawer content...
```

Perubahan label:

```text
Settings → Pengaturan
```

Rationale:

- UI konsisten Bahasa Indonesia;
- drawer tidak menampung backup/export/theme detail;
- Settings page menjadi tempat fitur yang memang settings/export/backup.

Current rejected behavior tetap dijaga:

```text
No drawer hint/onboarding text.
Drawer tetap clean.
Swipe-only behavior tetap boleh dipertahankan.
```

---

## 6. Proposed Settings page structure

### 6.1 Layout style

Style yang direkomendasikan:

```text
Top bar:
← Pengaturan

Section title kecil/muted
Row 48–56dp
Row 48–56dp
Divider subtle
```

Bukan:

```text
Material card besar untuk tiap setting
Dashboard tile
Onboarding/hint text panjang
```

Rationale:

- sesuai request user: jangan card;
- ringan dan native;
- cocok dengan NOTEZ simple/offline identity;
- mudah berkembang.

### 6.2 Draft information architecture

```text
Pengaturan

Tampilan
  Tema                         GitHub Dark
  Jenis font                   Default
  Ukuran teks                  Normal

Data & Backup
  Ekspor semua catatan (.json)
  Impor backup (.json)
  Ekspor semua catatan (.txt)
  Folder backup otomatis

Export & Save
  Export PDF                   future
  Export Markdown bundle       future
  Export HTML                  future

Aplikasi
  Privacy / Offline
  Versi                        0.1.2+
```

Catatan penting:

- Untuk MVP, jangan tampilkan terlalu banyak row `future` yang belum aktif jika membuat UI terasa palsu.
- `Export PDF`, `Markdown bundle`, dan `HTML` bisa dicatat di RFC ini tapi implementasi boleh phase terpisah.

---

## 7. Settings v1 MVP scope

MVP paling aman:

```text
1. Tambah SettingsActivity / PengaturanActivity.
2. Drawer `Settings` diganti label `Pengaturan` dan membuka SettingsActivity.
3. Hapus drawer settings submenu dari drawer utama.
4. Pindahkan existing actions ke SettingsActivity:
   - Tema;
   - Ekspor JSON;
   - Ekspor TXT;
   - Impor JSON;
   - Folder backup otomatis.
5. Tambah section `Aplikasi` minimal:
   - Privacy / Offline info;
   - Versi app.
6. Jangan implement PDF/font/theme preview dulu kecuali disetujui sebagai phase berikutnya.
```

Kenapa MVP dibuat konservatif:

- memindahkan settings saja sudah mengubah navigasi UX;
- export/import perlu activity result handling;
- theme change dari SettingsActivity perlu memastikan MainActivity ikut update saat kembali;
- PDF/font/theme preview punya risiko dan UAT sendiri.

---

## 8. Future phase inside Settings track

### Phase 2 — Appearance settings

Candidate:

```text
Tampilan
- Tema dengan preview kecil
- Jenis font
- Ukuran teks editor
- Ukuran teks Markdown preview
- Gaya card / density
```

Font setting v1 yang aman:

```text
Default sistem
Serif
Sans-serif
Monospace
```

Rules:

- gunakan system font dulu;
- jangan download remote font;
- font custom hanya jika dibundel lokal dan lisensinya jelas;
- tentukan apakah font berlaku untuk editor, preview, atau keduanya.

Rekomendasi awal:

```text
Font setting global sederhana dulu:
Default / Serif / Monospace.
```

### Phase 3 — Save/export file improvements

`Save file` perlu dibedakan:

```text
Export all notes        → Settings
Export current note     → Editor/View note
Export rendered preview → Editor/View note or share/export screen
```

MVP existing sudah punya:

```text
Ekspor JSON
Ekspor TXT
```

Future candidates:

```text
Export semua catatan sebagai Markdown bundle
Export note saat ini sebagai .md
Export note saat ini sebagai .txt
Export rendered note sebagai .html
```

### Phase 4 — Export PDF

Recommended technical direction:

```text
WebView print / Android PrintManager / Save as PDF
```

Rationale:

- no dependency besar;
- tetap offline;
- memanfaatkan rendered Markdown HTML/CSS;
- hasil bisa rapi jika print CSS disiapkan.

Default PDF visual:

```text
light/paper print style
bukan dark full background
heading/table/code/callout tetap rapi
```

Non-goal PDF v1:

```text
silent direct PDF generation tanpa system print dialog
```

Karena direct PDF layout manual lebih berat dan rawan hasil jelek.

---

## 9. Technical design notes

### 9.1 Activity and layout

Candidate files:

```text
app/src/main/java/com/zaba/notez/SettingsActivity.kt
app/src/main/res/layout/activity_settings.xml
```

Manifest:

```text
SettingsActivity exported=false
```

Theme:

```text
setTheme(ThemePref.styleOf(ThemePref.get(this)))
```

### 9.2 Moving existing actions

Existing methods in `MainActivity` may move or be duplicated carefully:

```text
showThemeDialog()
exportJson()
exportTxt()
openDoc launcher
openTree launcher
```

Better implementation direction:

```text
SettingsActivity owns settings-related ActivityResult launchers.
MainActivity no longer owns export/import/backup settings actions.
```

This keeps MainActivity focused on home list/search/FAB/drawer/music.

### 9.3 Theme change behavior

Current theme is applied at Activity creation. If user changes theme inside SettingsActivity, need ensure:

```text
SettingsActivity updates/recreates itself.
MainActivity reflects theme after returning.
```

Options:

1. `SettingsActivity` sets result flag; `MainActivity` checks and recreates.
2. `MainActivity.onResume()` tracks last theme value and recreates if changed.
3. Finish SettingsActivity after theme selection and recreate MainActivity.

Recommended:

```text
MainActivity tracks last applied theme in onResume and recreates if ThemePref changed.
```

But implementation should avoid recreate loops.

### 9.4 Version display

Version can be read from:

```text
packageManager.getPackageInfo(packageName, 0).versionName
```

Display candidate:

```text
Versi 0.1.2
```

If build type/commit is not available, do not fake it.

### 9.5 Privacy/offline info

Settings page can show a simple dialog:

```text
NOTEZ tidak memakai INTERNET permission.
Markdown renderer memakai asset lokal.
Remote image tidak auto-load.
Link eksternal dibuka lewat aplikasi luar saat user tap.
```

Keep it short; no scary wall of text.

---

## 10. Interaction details

### 10.1 Opening Settings

Current drawer row:

```text
Settings
```

Becomes:

```text
Pengaturan
```

On tap:

```text
closeDrawerThen { startActivity(Intent(this, SettingsActivity::class.java)) }
```

### 10.2 Back behavior

SettingsActivity should have:

```text
back arrow
system back returns to previous screen
```

No drawer inside Settings page for MVP.

### 10.3 Rows

Row style:

```text
height 48–56dp
primary text 15–16sp
summary/value text optional, muted
selectableItemBackground
```

Rows that launch document picker should remain direct and discoverable.

---

## 11. Acceptance criteria

### 11.1 Product acceptance

- [ ] Drawer shows `Panduan Markdown`, `Pengaturan`, and Music section.
- [ ] Drawer no longer expands a settings submenu.
- [ ] Tap `Pengaturan` opens a dedicated settings page.
- [ ] Settings page uses section/list rows, not cards.
- [ ] Back arrow/system back returns to previous screen.
- [ ] Theme selection still works.
- [ ] Export JSON still works.
- [ ] Export TXT still works.
- [ ] Import JSON still works.
- [ ] Folder backup otomatis selection still works.
- [ ] MainActivity home/search/FAB/delete unchanged.
- [ ] Music drawer/player unchanged.
- [ ] Markdown guide unchanged.

### 11.2 Security/privacy acceptance

- [ ] No `android.permission.INTERNET`.
- [ ] No CDN.
- [ ] No remote script/style/font/image.
- [ ] No `addJavascriptInterface`.
- [ ] No new dependency unless separately approved.
- [ ] Export/import still use Android document picker, not hidden filesystem/network behavior.
- [ ] No secrets/tokens committed.

### 11.3 Technical acceptance

- [ ] XML parse valid.
- [ ] Manifest contains SettingsActivity with `exported=false`.
- [ ] IDs removed from drawer submenu no longer referenced by MainActivity.
- [ ] ActivityResult launchers live in valid lifecycle owner.
- [ ] Theme change does not create recreate loop.
- [ ] `git diff --check` passes.
- [ ] Debug CI build passes.

---

## 12. UAT plan

Manual UAT after implementation + CI green:

1. Install debug APK.
2. Open NOTEZ.
3. Swipe drawer.
4. Confirm drawer is clean:
   - Panduan Markdown;
   - Pengaturan;
   - Music.
5. Tap `Pengaturan`.
6. Confirm settings page opens with back arrow.
7. Confirm rows are list/section style, not cards.
8. Change theme and confirm it applies correctly.
9. Return to home and confirm theme is still consistent.
10. Export JSON and confirm document picker/export success.
11. Export TXT and confirm document picker/export success.
12. Import valid JSON backup and confirm notes restored.
13. Try invalid JSON and confirm safe error message.
14. Pick backup folder and confirm toast/behavior.
15. Open Markdown Guide from drawer; confirm unchanged.
16. Create/open/delete note; confirm home flows unchanged.
17. Open Music drawer/player; confirm unchanged.
18. Rotate device if possible while on Settings page.
19. Confirm no INTERNET permission introduced.

Device evidence only becomes `DEVICE VERIFIED` if user reports pass.

---

## 13. Risks and mitigations

### Risk 1 — Theme changes do not update MainActivity

Mitigation:

- track theme value in MainActivity and recreate on return if changed;
- avoid infinite recreate loops;
- UAT theme change from Settings.

### Risk 2 — Export/import breaks when moved from MainActivity

Mitigation:

- keep same `BackupHelper` implementation;
- move ActivityResult launchers carefully;
- UAT JSON/TXT export and JSON import.

### Risk 3 — Settings grows too crowded

Mitigation:

- implement MVP rows only;
- future rows should be added when implemented, not as fake disabled clutter;
- use section grouping.

### Risk 4 — PDF export scope becomes too large

Mitigation:

- keep PDF out of Settings v1 implementation;
- define separate implementation phase/RFC if needed;
- prefer Android PrintManager/WebView print before adding libraries.

### Risk 5 — Font setting introduces licensing/dependency risk

Mitigation:

- use system fonts first;
- only bundle custom fonts with clear license;
- no remote fonts.

---

## 14. Rollback plan

If Settings page causes issues:

```text
restore drawer settings submenu
remove SettingsActivity from manifest
restore MainActivity settings handlers
no database migration needed
no note content affected
```

If only one action breaks:

```text
temporarily hide or disable the broken row
keep other settings rows working
```

---

## 15. Relation to other active RFC tracks

This RFC is one of three planned tracks:

```text
1. Settings Page v1 / Menu Settings
2. Theme System v2 / Appearance Polish
3. Markdown Preview v3 / GitHub x Obsidian Classy Reading View
```

Recommended order:

```text
Settings Page v1 first
→ UI / Appearance Polish
→ Markdown Preview v3
```

Reason:

- Settings page becomes the home for theme/font/export controls.
- UI polish can then use Settings cleanly.
- Markdown v3 is the riskiest and should happen after navigation/settings foundation is stable.

---

## 16. Current decision recommendation

Recommended next implementation scope:

```text
Implement Settings Page v1 MVP:
- create SettingsActivity + list-section layout;
- drawer `Settings` → `Pengaturan` opens SettingsActivity;
- remove drawer settings submenu;
- move existing Tema/export/import/backup actions into SettingsActivity;
- add simple Privacy/Offline and Version rows;
- no PDF/font implementation yet;
- no new dependency;
- run static checks + CI;
- request device UAT.
```

Status of this RFC:

```text
CI VERIFIED for debug build.
Static XML/security/source checks passed.
Local Gradle build is not available in the sandbox because there is no Gradle wrapper/global gradle.
GitHub Actions debug build passed: run 36480199407.
Debug artifact: notez-debug, artifact ID 10996746709, size metadata 8,073,170 bytes.
Device UAT pending.
```
