# RFC — NOTEZ v0.1.6 UX, Rendering, and Space Workspace Foundation

| | |
|---|---|
| **Status** | Approved for planning, not implemented yet |
| **Target release candidate** | `v0.1.6` |
| **Tanggal** | 2026-09-30 |
| **Owner** | NOTEZ maintainer |
| **Tipe scope** | Product direction + Android native implementation plan |
| **Baseline stabil saat ini** | `v0.1.5` production release |

> [!NOTE]
> **Cara membaca versi rapi ini**
> Isi dokumen asli **tidak diubah**. Yang ditambahkan hanya:
> - **Saran** — kotak `[!TIP]` di bawah bagian terkait, berisi usulan tambahan/alternatif.
> - **Footnote** — penanda `[^n]` yang penjelasannya ada di bagian paling bawah.
> - **Daftar isi** — ringkasan nomor bab di bawah ini.

## Daftar Isi

1. Executive Summary
2. Keputusan Produk
3. Goals
4. Non-Goals
5. Istilah
6. Baseline NOTEZ Saat Ini
7. Arsitektur Target v0.1.6
8. Prinsip Storage
9. UX Overview
10. Drawer Space UX
11. Create File / Save As Decision
12. New File Dialog
13. Editor Mode
14. Save Semantics
15. File Type Behavior
16. Proposed Package Structure
17. Proposed Classes
18. Intent Contract
19. MainActivity Changes
20. activity_main.xml Changes
21. EditorActivity Changes
22. Dirty State Details
23. Local Notes Preservation
24. Error Handling
25. Security & Privacy
26. Accessibility / UX Polish
27. Implementation Phases
28. Acceptance Criteria v0.1.6
29. Test Matrix
30. Risks and Mitigations
31. Open Questions
32. File Path Checklist for ACODE
33. Branch Strategy
34. Release Plan for v0.1.6 Later
35. Final Decision
36. Scope Update Setelah Evaluasi Fork
37. Accepted UX Fix 1 — Smooth Editor Scroll
38. Accepted UX Fix 2 — Expandable Settings Sections
39. Rejected Fork Patch — Markdown Renderer Optimization
40. Updated v0.1.6 Scope Setelah Fork Test
41. Updated Implementation Order
42. Updated Release Note Draft for v0.1.6
43. Updated Final Decision

---

## 1. Executive Summary

`v0.1.6` diarahkan menjadi pondasi fitur **Space Workspace**.

Konsep produknya:

```text
NOTEZ = ibu / pendamping / pengasuh catatan
SPACE = rumah / root folder tempat semua file NOTEZ tinggal
File = anak-anak catatan yang dirawat di dalam rumah itu
```

Dengan arah ini, NOTEZ tidak lagi sekadar aplikasi catatan yang menyimpan semua data di database internal. NOTEZ mulai bergerak ke model:

```text
local-first
file-owned-by-user
offline-first
privacy-first
workspace-based notes
```

Fokus `v0.1.6` bukan Card Grid, bukan PDF export, dan bukan full file manager. Fokusnya adalah fondasi aman:

```text
Buat/Pilih SPACE
→ tampilkan tree folder/file di drawer
→ buat file .md/.txt di SPACE
→ buka file dari SPACE
→ edit file
→ save manual ke file tersebut
→ lindungi perubahan dengan dirty state
```

> [!TIP]
> **Saran:** Executive Summary di atas hanya menyebut Space. Sejak bab 36–40, scope `v0.1.6` bertambah (scroll editor, Settings expandable, renderer R1). Supaya pembaca yang berhenti di bab 1 tidak salah paham, tambahkan satu paragraf penutup:
>
> ```text
> Catatan scope: selain Space Foundation, v0.1.6 juga membawa UX polish
> (scroll editor lebih mulus, Settings expandable) dan groundwork renderer
> yang aman (cache markdown-it). Detail ada di bab 36–43.
> ```

---

## 2. Keputusan Produk

### 2.1 Keputusan utama

Untuk `v0.1.6`, NOTEZ akan mulai menjadi **Space-first app**.

Artinya, pengalaman baru yang ingin diarahkan adalah:

```text
Bukan lagi: “Ketuk + untuk mulai.”
Tapi:     “Buat SPACE untuk rumah catatanmu.”
```

### 2.2 Kenapa Space lebih penting daripada Card Grid

Card Grid hanya mengubah cara list catatan internal ditampilkan:

```text
Local notes list → local notes grid
```

Itu berguna, tapi sifatnya kosmetik/UX layer.

Space mengubah identitas NOTEZ:

```text
catatan bukan cuma tersimpan di app
catatan hidup sebagai file milik user
folder user menjadi rumah utama catatan
```

Jadi untuk roadmap sekarang:

```text
Card Grid: ditunda / drop dulu
Space Foundation: lanjut sebagai arah v0.1.6
```

### 2.3 Keputusan release discipline

`v0.1.6` tidak boleh langsung menjadi perubahan raksasa tanpa tahap.

Aturan:

```text
spec dulu
implement fase kecil
CI/debug build
device test
PR/merge main kalau PASS
release stable hanya setelah signed APK verified
```

---

## 3. Goals

Tujuan `v0.1.6`:

1. Membuat konsep **SPACE** sebagai root folder user untuk catatan/file NOTEZ.
2. Menggunakan Android Storage Access Framework agar user memilih folder secara manual dan transparan.
3. Menampilkan file tree di drawer sebagai akses cepat ke file di SPACE.
4. Mendukung file editable dasar:
   - `.md`
   - `.markdown`
   - `.txt`
5. Membuat file baru langsung di dalam SPACE.
6. Membuka file dari SPACE ke editor.
7. Menyimpan perubahan file secara manual.
8. Menambahkan dirty state agar perubahan tidak hilang diam-diam.
9. Menjaga Local Notes lama tetap aman.
10. Menjaga prinsip NOTEZ:
    - offline-first,
    - privacy-first,
    - tanpa cloud wajib,
    - tanpa background indexing agresif,
    - tanpa permission storage brutal.

> [!TIP]
> **Saran:** Goals belum mencakup item dari bab 37–39. Usulan lanjutan daftar:
>
> ```text
> 11. Editor scroll lebih mulus untuk catatan panjang (bab 37).
> 12. Settings dikelompokkan jadi section expandable (bab 38).
> 13. Groundwork performa renderer yang aman, hanya R1 (bab 39).
> ```

---

## 4. Non-Goals

Hal yang **tidak masuk** `v0.1.6`:

```text
Card Grid
PDF export
edit PDF
rename file
move file
delete file dari Space
create nested folder dari NOTEZ
global search semua file Space
background indexing seluruh folder
cloud sync
remote fetch otomatis
unrestricted storage permission
MANAGE_EXTERNAL_STORAGE
raw filesystem path hardcoded
workflow_dispatch untuk debug build
full Home redesign final
hapus Room database
hapus Local Notes lama
```

Catatan penting:

> `v0.1.6` adalah pondasi Space, bukan file manager lengkap.

---

## 5. Istilah

| Istilah | Makna |
|---|---|
| NOTEZ | Aplikasi Android native Kotlin untuk catatan lokal/offline |
| SPACE | Folder root pilihan user yang menjadi rumah file NOTEZ |
| Local Notes | Catatan lama yang tersimpan di Room database internal |
| Space File | File `.md`, `.markdown`, atau `.txt` yang berada di SPACE |
| SAF | Storage Access Framework Android |
| DocumentFile | Wrapper Android untuk file/folder dari SAF |
| Dirty state | Kondisi file sudah berubah di editor tapi belum disimpan |
| Root folder URI | URI SAF folder SPACE yang disimpan NOTEZ |
| File tree | Tampilan folder/file bertingkat di drawer |

---

## 6. Baseline NOTEZ Saat Ini

Baseline sebelum `v0.1.6`:

```text
main / v0.1.5
```

Arsitektur sekarang:

```text
Room Database
    ↓
NoteDao
    ↓
MainActivity Home list
    ↓
NoteAdapter + item_note.xml
    ↓
EditorActivity
    ↓
EditText edit mode / WebView Reading View
```

File penting saat ini:

| Path | Peran sekarang |
|---|---|
| `app/src/main/java/com/zaba/notez/MainActivity.kt` | Home, drawer, search, FAB, local notes list |
| `app/src/main/java/com/zaba/notez/EditorActivity.kt` | Editor local notes, autosave Room, Reading View |
| `app/src/main/java/com/zaba/notez/Note.kt` | Entity Room untuk local note |
| `app/src/main/java/com/zaba/notez/NoteDao.kt` | Query Room local notes |
| `app/src/main/java/com/zaba/notez/AppDatabase.kt` | Database Room |
| `app/src/main/java/com/zaba/notez/NoteAdapter.kt` | Adapter Home local notes |
| `app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt` | Renderer Markdown Reading View |
| `app/src/main/res/layout/activity_main.xml` | Layout Home + drawer + FAB + empty state |
| `app/src/main/res/layout/activity_editor.xml` | Layout editor + WebView + toolbar |
| `app/src/main/res/layout/include_music_drawer.xml` | Bagian Music di drawer |

---

## 7. Arsitektur Target v0.1.6

Setelah Space Foundation, NOTEZ punya dua sumber data:

```text
1. Local Notes
   Source: Room database
   Status: legacy/masih didukung

2. Space Files
   Source: folder pilihan user via SAF
   Status: arah utama baru
```

Diagram target:

```text
MainActivity
├─ Local Notes area
│  └─ Room → NoteDao → NoteAdapter
│
└─ Drawer Space
   └─ SpaceRepository
      ├─ SpacePref root URI
      ├─ DocumentFile folder tree
      ├─ .md/.markdown/.txt filter
      └─ open/create file

EditorActivity
├─ Local Note Mode
│  └─ Room autosave behavior lama
│
└─ Space File Mode
   ├─ read file from ContentResolver
   ├─ manual save to file URI
   ├─ dirty state
   └─ back prompt when unsaved
```

---

## 8. Prinsip Storage

### 8.1 Wajib pakai SAF

Space harus menggunakan:

```text
ACTION_OPEN_DOCUMENT_TREE
DocumentFile
takePersistableUriPermission
ContentResolver
```

Jangan pakai:

```text
MANAGE_EXTERNAL_STORAGE
hardcoded /storage/emulated/0/...
scan seluruh storage tanpa consent
```

### 8.2 Kenapa SAF

SAF cocok untuk NOTEZ karena:

- user memilih folder manual,
- permission jelas dan transparan,
- app tidak butuh akses brutal ke semua storage,
- selaras dengan privacy-first,
- aman untuk Android modern.

### 8.3 Batasan SAF yang harus diterima

SAF tidak selalu terasa seperti file path biasa. Yang disimpan NOTEZ bukan path mentah, tapi URI.

```text
Yang disimpan:
content://...

Bukan:
/storage/emulated/0/NOTEZ_SPACE
```

UI boleh menyebut “folder SPACE”, tapi implementasi tetap URI SAF.

> [!TIP]
> **Saran:** Tambahkan dua batasan SAF lain yang akan langsung terasa oleh user:
>
> - Android 11+ menolak root storage dan folder Download sebagai SPACE.[^1]
>   Beri petunjuk di UI: "Pilih atau buat subfolder, mis. Documents/NOTEZ".
> - Jumlah izin folder persisten per app dibatasi.[^2]
>   Saat user ganti SPACE, lepas izin lama dengan releasePersistableUriPermission.

---

## 9. UX Overview

### 9.1 Kondisi belum ada SPACE dan belum ada Local Notes

Home menampilkan onboarding Space.

Konsep tampilan:

```text
[Logo NOTEZ]
NOTEZ
Catatan butuh rumah.
Buat SPACE untuk menyimpan file .md dan .txt milikmu.

[Buat / Pilih SPACE]
[Panduan NOTEZ]
```

Behavior:

```text
FAB hidden atau diarahkan ke pilih SPACE
search eye hidden
empty local note list tidak jadi fokus utama
```

### 9.2 Kondisi belum ada SPACE tapi ada Local Notes lama

Jangan sembunyikan data lama.

Konsep:

```text
Local Notes tetap tampil
Ada callout/CTA kecil:
“Buat SPACE untuk menyimpan file NOTEZ sebagai .md/.txt.”
```

Behavior:

```text
Local notes tetap bisa dibuka
FAB behavior perlu diputuskan: local note lama atau arahkan ke Space
rekomendasi: kalau belum ada Space, FAB menampilkan dialog ajakan buat Space terlebih dulu
```

### 9.3 Kondisi SPACE sudah aktif

Home mulai Space-first.

Konsep:

```text
SPACE aktif: [nama folder]
Gunakan drawer untuk membuka file atau tekan + untuk membuat file baru.
```

Behavior:

```text
FAB = buat file baru di SPACE
Drawer Space aktif
Local Notes tetap bisa tersedia lewat area legacy / menu
```

---

## 10. Drawer Space UX

User request:

```text
Space di drawer
letaknya di bawah Music
expandable
menampilkan tree file
```

Keputusan:

```text
Space diletakkan setelah include_music_drawer di drawer MainActivity.
```

Konsep drawer:

```text
NOTEZ

Panduan Markdown
Pengaturan

Music
  ...

Space
  [Buat/Pilih SPACE]
  [File Baru]
  [Refresh]
  ├─ README.md
  ├─ todo.txt
  ├─ project
  │  ├─ ide.md
  │  └─ draft.txt
  └─ archive
     └─ old.md
```

Folder behavior:

```text
Tap folder collapsed → expand
Tap folder expanded  → collapse
Tap file             → open editor
```

Tree loading:

```text
lazy expand
scan folder hanya saat dibuka
jangan scan semua nested folder otomatis
```

---

## 11. Create File / Save As Decision

User preference:

```text
Lebih prefer Save As karena file NOTEZ harus tinggal di SPACE.
```

Keputusan teknis untuk `v0.1.6`:

```text
Implementasi memakai create-before-edit.
```

Artinya:

```text
User memilih nama file + tipe file dulu
NOTEZ membuat file kosong di SPACE
Editor membuka file tersebut
Save berikutnya menulis ke file itu
```

### 11.1 Kenapa bukan tulis dulu lalu Save As belakangan

Flow “tulis dulu, Save As belakangan” terlihat natural, tapi berisiko:

```text
app crash sebelum Save As → draft hilang
butuh draft autosave internal
state editor lebih rumit
konsep Space-first jadi setengah matang
```

### 11.2 Flow v0.1.6 yang dipilih

```text
Tap + / File Baru
↓
Dialog File Baru
↓
Input nama file
↓
Pilih tipe: Markdown (.md) / Text (.txt)
↓
NOTEZ membuat file di SPACE
↓
EditorActivity buka file itu
↓
User edit
↓
Save manual
```

Ini tetap memenuhi rasa “Save As” karena user menentukan:

```text
nama file
extension
tempat file hidup
```

Bedanya, file dibuat sebelum editor agar tidak ada draft liar.

---

## 12. New File Dialog

Dialog minimal:

```text
Judul: File baru di SPACE

Nama file:
[________________]

Tipe:
( ) Markdown (.md)
( ) Text (.txt)

[Batal] [Buat]
```

Validasi nama file:

```text
tidak kosong
tidak hanya spasi
tidak mengandung /
tidak mengandung karakter kontrol
extension otomatis ditambahkan kalau user belum menulisnya
kalau file sudah ada, minta konfirmasi atau tolak dulu
```

Rekomendasi v0.1.6:

```text
Kalau file sudah ada → tolak dengan pesan jelas.
Overwrite belum masuk v0.1.6.
```

Contoh copy error:

```text
Nama file kosong.
Nama file tidak boleh mengandung garis miring.
File sudah ada di SPACE.
SPACE belum dipilih.
Gagal membuat file. Pilih ulang SPACE atau coba nama lain.
```

> [!TIP]
> **Saran:** Perluas validasi. Beberapa provider (mis. kartu SD atau FAT) menolak karakter selain `/`, dan `createFile` bisa mengubah nama diam-diam.[^3]
>
> ```kotlin
> private val illegalChars = Regex("""[\\/:*?"<>|\p{Cntrl}]""")
>
> fun validateName(raw: String): String? = when {
>     raw.isBlank() -> "Nama file kosong."
>     illegalChars.containsMatchIn(raw) -> "Nama file mengandung karakter yang tidak diizinkan."
>     raw.trim().length > 100 -> "Nama file terlalu panjang."
>     else -> null // valid
> }
> ```
>
> Setelah `createFile`, cek `created.name` sama dengan nama yang diminta. Kalau beda, tampilkan nama akhir ke user, jangan diam-diam.

---

## 13. Editor Mode

`EditorActivity` harus punya dua mode.

### 13.1 Local Note Mode

Mode lama.

Trigger:

```text
Intent extra: note_id
```

Behavior:

```text
source = Room database
title editable
body editable
autosave behavior lama tetap
Reading View = MarkdownPreviewRenderer
```

### 13.2 Space File Mode

Mode baru.

Trigger:

```text
Intent extra: space_file_uri
Intent extra: space_file_name
Intent extra: space_file_mime_or_kind
```

Behavior:

```text
source = file URI via SAF
title = nama file
body = isi file
save = manual
no autosave setiap ketik
dirty state aktif
back dirty prompt aktif
```

> [!TIP]
> **Saran:** Nama extra ketiga di sini (`space_file_mime_or_kind`) berbeda dengan bab 18.2 (`space_file_kind`). Samakan supaya tidak terjadi bug key salah ketik:
>
> ```text
> space_file_uri: String
> space_file_name: String
> space_file_kind: String   // "markdown" | "text"
> ```
>
> Lebih aman lagi kalau key dijadikan konstanta di satu tempat:
>
> ```kotlin
> object EditorExtras {
>     const val SPACE_FILE_URI = "space_file_uri"
>     const val SPACE_FILE_NAME = "space_file_name"
>     const val SPACE_FILE_KIND = "space_file_kind"
> }
> ```

### 13.3 Title di Space File Mode

Untuk `v0.1.6`, title file tidak diedit dari title field.

Alasan:

```text
edit title = rename file
rename file belum masuk scope v0.1.6
```

Keputusan:

```text
Space File Mode:
- tampilkan nama file sebagai title
- title edit hidden/readonly
- body tetap editable
```

Rename masuk fase berikutnya.

---

## 14. Save Semantics

### 14.1 Local Note Mode

Tetap seperti sekarang:

```text
autosave ke Room
onPause save blocking
counter tetap bekerja
```

### 14.2 Space File Mode

Manual save:

```text
User edit body
↓
dirty = true
↓
User tap Save
↓
ContentResolver.openOutputStream(uri, "wt")
↓
write text
↓
dirty = false
↓
toast: Tersimpan
```

Kalau save gagal:

```text
dirty tetap true
muncul error
jangan keluar editor
```

> [!TIP]
> **Saran:** Tiga hal yang layak dicatat untuk Space File Mode:
>
> 1. Mode `"wt"` memotong file lebih dulu, jadi kalau proses tulis gagal di tengah, isi file bisa terpotong.[^4] Karena teks masih ada di editor dan `dirty` tetap `true`, user bisa mencoba save ulang.
> 2. Baca/tulis di `Dispatchers.IO` dan paksa UTF-8.
> 3. File bisa berubah dari luar NOTEZ (Syncthing, file manager). Simpan `lastModified` saat load, bandingkan saat save.
>
> ```kotlin
> suspend fun writeText(uri: Uri, text: String) = withContext(Dispatchers.IO) {
>     val out = context.contentResolver.openOutputStream(uri, "wt")
>         ?: throw IOException("openOutputStream null")
>     out.bufferedWriter(Charsets.UTF_8).use { it.write(text) }
> }
> ```
>
> ```kotlin
> // saat load
> loadedModified = DocumentFile.fromSingleUri(this, uri)?.lastModified() ?: 0L
> // saat save: kalau lastModified sekarang != loadedModified → tanya user (Timpa / Batal)
> ```
>
> Opsional: batasi ukuran file yang dibuka (mis. 1 MB di v0.1.6) dan tampilkan pesan jelas kalau lebih besar, agar `EditText` tidak membuat app lambat atau OOM.

### 14.3 Back saat dirty

Dialog:

```text
Perubahan belum disimpan.

[Simpan] [Buang] [Batal]
```

Behavior:

```text
Simpan → attempt save → kalau sukses keluar → kalau gagal tetap di editor
Buang  → keluar tanpa save
Batal  → tutup dialog, tetap di editor
```

> [!TIP]
> **Saran:** `onBackPressed()` sudah deprecated.[^5] Gunakan `OnBackPressedCallback`:
>
> ```kotlin
> onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
>     override fun handleOnBackPressed() {
>         if (editorMode == EditorMode.SPACE_FILE && dirty) {
>             showDirtyDialog()
>         } else {
>             isEnabled = false
>             onBackPressedDispatcher.onBackPressed()
>         }
>     }
> })
> ```
>
> Ini juga berlaku untuk tombol back di toolbar/up dan gesture back. Search term `onBackPressed` di bab 32 tetap berguna untuk menemukan kode lama yang perlu dimigrasi.

---

## 15. File Type Behavior

### 15.1 Markdown files

Extensions:

```text
.md
.markdown
```

Behavior:

```text
Edit mode = EditText
Reading View = MarkdownPreviewRenderer
Save = text markdown ke file
```

Code block fix dari `v0.1.5` harus tetap berlaku untuk `.md` dari SPACE.

### 15.2 Text files

Extension:

```text
.txt
```

Behavior ideal:

```text
Edit mode = EditText
Reading View = plain text preview
Save = plain text ke file
```

Penting:

```text
.txt tidak boleh dianggap Markdown penuh.
```

Kalau file `.txt` berisi:

```text
# ini cuma teks
```

Maka Reading View plain text harus tetap menampilkan `#`, bukan heading Markdown.

### 15.3 Plain text rendering option

Opsi implementasi:

```text
MarkdownPreviewRenderer.renderPlainText(text)
```

Atau:

```text
EditorActivity menampilkan plain text langsung di bodyEmpty/bodyWebView khusus
```

Rekomendasi:

```text
Tambahkan path renderPlainText yang aman dan sederhana.
```

Namun jangan refactor besar renderer di fase awal.

> [!TIP]
> **Saran:** Bentuk `renderPlainText` paling sederhana adalah `<pre>` dengan teks yang di-escape. Tidak perlu markdown-it dan tidak perlu JS.
>
> ```kotlin
> fun renderPlainText(text: String) {
>     val escaped = TextUtils.htmlEncode(text)
>     val html = "<pre style=\"white-space:pre-wrap;word-wrap:break-word\">$escaped</pre>"
>     webView.loadDataWithBaseURL(NOTEZ_BASE_URL, html, "text/html", "UTF-8", null)
> }
> ```
>
> Pastikan warna teks/latar mengikuti tema aktif, sama seperti Reading View Markdown.

---

## 16. Proposed Package Structure

Tambahkan package:

```text
app/src/main/java/com/zaba/notez/space/
```

Kandidat file:

```text
app/src/main/java/com/zaba/notez/space/SpacePref.kt
app/src/main/java/com/zaba/notez/space/SpaceNode.kt
app/src/main/java/com/zaba/notez/space/SpaceRepository.kt
app/src/main/java/com/zaba/notez/space/SpaceTreeAdapter.kt
```

Layout baru:

```text
app/src/main/res/layout/item_space_node.xml
```

Drawable/icon baru:

```text
app/src/main/res/drawable/ic_folder.xml
app/src/main/res/drawable/ic_file_text.xml
app/src/main/res/drawable/ic_save.xml
```

Strings:

```text
app/src/main/res/values/strings.xml
```

Layouts yang akan kena:

```text
app/src/main/res/layout/activity_main.xml
app/src/main/res/layout/activity_editor.xml
```

Activities yang akan kena:

```text
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/java/com/zaba/notez/EditorActivity.kt
```

Renderer kemungkinan kena:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
```

---

## 17. Proposed Classes

### 17.1 SpacePref.kt

Tugas:

```text
simpan root URI SPACE
ambil root URI SPACE
hapus root URI SPACE
cek apakah SPACE sudah dipilih
```

Konsep API:

```kotlin
object SpacePref {
    fun getRootUri(context: Context): Uri?
    fun setRootUri(context: Context, uri: Uri)
    fun clear(context: Context)
    fun hasSpace(context: Context): Boolean
}
```

### 17.2 SpaceNode.kt

Tugas:

```text
model item tree
```

Konsep data:

```kotlin
data class SpaceNode(
    val name: String,
    val uri: Uri,
    val isDirectory: Boolean,
    val depth: Int,
    val isExpanded: Boolean = false,
    val childrenLoaded: Boolean = false
)
```

### 17.3 SpaceRepository.kt

Tugas:

```text
akses DocumentFile
filter file
create file
read file
write file
```

Konsep API:

```kotlin
class SpaceRepository(private val context: Context) {
    fun root(): DocumentFile?
    fun listChildren(folderUri: Uri): List<SpaceNode>
    fun createFile(parentUri: Uri, displayName: String, kind: SpaceFileKind): Uri
    suspend fun readText(uri: Uri): String
    suspend fun writeText(uri: Uri, text: String)
    fun isSupportedFile(name: String): Boolean
}
```

> [!TIP]
> **Saran:** `listChildren` sebaiknya `suspend` dan jalan di `Dispatchers.IO`. `DocumentFile.listFiles()` melakukan query terpisah per anak sehingga lambat di folder besar. Untuk versi cepat, query `DocumentsContract` sekali per folder:
>
> ```kotlin
> suspend fun listChildren(treeUri: Uri, folderDocId: String): List<SpaceNode> =
>     withContext(Dispatchers.IO) {
>         val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, folderDocId)
>         val projection = arrayOf(
>             DocumentsContract.Document.COLUMN_DOCUMENT_ID,
>             DocumentsContract.Document.COLUMN_DISPLAY_NAME,
>             DocumentsContract.Document.COLUMN_MIME_TYPE
>         )
>         context.contentResolver.query(childrenUri, projection, null, null, null)?.use { c ->
>             buildList { while (c.moveToNext()) { /* map ke SpaceNode, filter .md/.markdown/.txt */ } }
>         } ?: emptyList()
>     }
> ```
>
> Urutan tampil juga perlu diputuskan: folder dulu lalu file, urut abjad tanpa peduli huruf besar/kecil.

### 17.4 SpaceTreeAdapter.kt

Tugas:

```text
render folder/file tree di drawer
handle indent depth
handle click folder/file
```

Konsep callback:

```kotlin
class SpaceTreeAdapter(
    private val onFolderToggle: (SpaceNode) -> Unit,
    private val onFileOpen: (SpaceNode) -> Unit
)
```

---

## 18. Intent Contract

### 18.1 Local Note Mode existing

Saat buka local note:

```text
note_id: Long
is_new: Boolean
```

Tetap dipertahankan.

### 18.2 Space File Mode new

Saat buka Space file:

```text
space_file_uri: String
space_file_name: String
space_file_kind: String
```

Contoh value `space_file_kind`:

```text
markdown
text
```

Decision:

```text
EditorActivity menentukan mode dari extra.
Jika ada space_file_uri → Space File Mode.
Jika tidak ada → Local Note Mode.
```

---

## 19. MainActivity Changes

Target file:

```text
app/src/main/java/com/zaba/notez/MainActivity.kt
```

Area yang akan disentuh:

```text
onCreate()
setupDrawer()
FAB click listener
openEditor()
observe()/empty state area
new ActivityResult launcher for ACTION_OPEN_DOCUMENT_TREE
```

### 19.1 Add folder picker launcher

Konsep:

```kotlin
private val openSpace = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
    if (uri != null) {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        SpacePref.setRootUri(this, uri)
        refreshSpaceTree()
    }
}
```

Catatan:

- Ini contoh kandidat, belum patch final.
- Pastikan import dan flag sesuai Android API.

> [!TIP]
> **Saran:** Bungkus `takePersistableUriPermission` dengan `try/catch (SecurityException)` dan lepas SPACE lama sebelum menyimpan yang baru:
>
> ```kotlin
> private val openSpace = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
>     if (uri == null) return@registerForActivityResult
>     val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
>     try {
>         SpacePref.getRootUri(this)?.let { old ->
>             if (old != uri) contentResolver.releasePersistableUriPermission(old, flags)
>         }
>         contentResolver.takePersistableUriPermission(uri, flags)
>         SpacePref.setRootUri(this, uri)
>         refreshSpaceTree()
>     } catch (e: SecurityException) {
>         Toast.makeText(this, "Folder ini tidak bisa dipakai sebagai SPACE. Pilih folder lain.", Toast.LENGTH_LONG).show()
>     }
> }
> ```

### 19.2 FAB behavior

Current behavior:

```text
FAB creates Room Note
```

Target behavior:

```text
if Space exists:
    FAB opens New Space File dialog
else:
    FAB prompts Create/Pilih SPACE
```

Local Note creation lama jangan dihapus dulu; bisa dipindah ke legacy action nanti.

### 19.3 Drawer Space setup

`setupDrawer()` sekarang menangani:

```text
Panduan Markdown
Pengaturan
```

Target:

```text
setupDrawer()
setupSpaceDrawer()
```

Jangan bikin `MainActivity.kt` terlalu kusut. Kalau terlalu panjang, pisah helper/controller:

```text
app/src/main/java/com/zaba/notez/space/SpaceDrawerController.kt
```

Opsional jika adapter/controller lebih rapi.

---

## 20. activity_main.xml Changes

Target file:

```text
app/src/main/res/layout/activity_main.xml
```

Area drawer sekarang ada setelah:

```text
<include layout="@layout/include_music_drawer" />
```

Tambahkan section Space setelah Music.

Konsep struktur XML:

```xml
<TextView
    android:id="@+id/drawer_space_header"
    ...
    android:text="Space" />

<LinearLayout
    android:id="@+id/drawer_space_content"
    ...>

    <TextView
        android:id="@+id/space_pick_root"
        ...
        android:text="Buat / Pilih SPACE" />

    <TextView
        android:id="@+id/space_new_file"
        ...
        android:text="File baru" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/space_tree"
        ... />
</LinearLayout>
```

Catatan:

- Ini contoh kandidat.
- Bisa juga pakai LinearLayout manual untuk fase pertama, tapi RecyclerView lebih cocok untuk tree.

> [!TIP]
> **Saran:** Drawer sudah berisi Music dan menu lain dalam satu kolom. Kalau `RecyclerView` `space_tree` diberi tinggi `wrap_content` di dalam `ScrollView`, semua item dirender sekaligus dan scroll bisa saling rebutan. Dua opsi:
>
> ```text
> A. Beri space_tree tinggi tetap/maksimum (mis. 320dp) + nestedScrollingEnabled.
> B. Pindahkan seluruh isi drawer ke satu RecyclerView multi-viewType.
> ```
>
> Untuk fase pertama, opsi A paling kecil risikonya.

---

## 21. EditorActivity Changes

Target file:

```text
app/src/main/java/com/zaba/notez/EditorActivity.kt
```

Perubahan besar:

```text
introduce EditorSource / mode
separate Local Note save vs Space File save
manual save button for Space File Mode
dirty state
back prompt
```

### 21.1 Proposed state

```kotlin
private enum class EditorMode {
    LOCAL_NOTE,
    SPACE_FILE
}

private var editorMode = EditorMode.LOCAL_NOTE
private var spaceFileUri: Uri? = null
private var spaceFileName: String = ""
private var spaceFileKind: String = "markdown"
private var dirty = false
```

### 21.2 Save button

Existing editor toolbar has edit/done toggle. Space mode likely needs save affordance.

Options:

```text
Option A: add save icon next to edit/done
Option B: use done icon as save in Space mode
Option C: add overflow menu
```

Recommendation v0.1.6:

```text
Add explicit save icon for Space File Mode.
```

Target layout:

```text
app/src/main/res/layout/activity_editor.xml
```

Drawable:

```text
app/src/main/res/drawable/ic_save.xml
```

Content description:

```text
@string/cd_save_file
```

---

## 22. Dirty State Details

Dirty state updates when:

```text
body text changes in Space File Mode
```

For `.md` files, if title is readonly, title changes do not apply.

Dirty state resets when:

```text
file loaded successfully
file saved successfully
user chooses Buang changes
```

UI hints:

```text
save icon enabled only when dirty
optional title suffix: *
optional counter suffix: “ • belum disimpan”
```

Recommendation:

```text
Keep first implementation simple:
- enable/disable save button
- back prompt
- toast on save
```

> [!TIP]
> **Saran:** Ada jebakan di rotasi layar/recreate. Kalau `onCreate` selalu membaca ulang file dari URI, teks yang belum disimpan akan tertimpa isi file lama. `EditText` sendiri memulihkan teksnya otomatis (selama punya `id`), jadi:
>
> ```kotlin
> override fun onCreate(savedInstanceState: Bundle?) {
>     super.onCreate(savedInstanceState)
>     // ... setContentView, findViewById ...
>     if (editorMode == EditorMode.SPACE_FILE) {
>         if (savedInstanceState == null) loadSpaceFile()      // pertama kali saja
>         else dirty = savedInstanceState.getBoolean("dirty")   // pulihkan flag
>     }
> }
>
> override fun onSaveInstanceState(outState: Bundle) {
>     super.onSaveInstanceState(outState)
>     outState.putBoolean("dirty", dirty)
> }
> ```
>
> Pastikan juga listener `TextWatcher` dipasang **setelah** teks dimuat, supaya proses load tidak langsung menandai `dirty = true`.

---

## 23. Local Notes Preservation

Room files should remain:

```text
app/src/main/java/com/zaba/notez/Note.kt
app/src/main/java/com/zaba/notez/NoteDao.kt
app/src/main/java/com/zaba/notez/AppDatabase.kt
```

Do not remove:

```text
Local Notes list
NoteAdapter
delete undo
autosave local notes
backup/export existing settings
```

For `v0.1.6`, Local Notes become legacy-compatible, not deleted.

Future feature:

```text
Export Local Notes to SPACE
```

Not v0.1.6.

---

## 24. Error Handling

### 24.1 SPACE not selected

Trigger:

```text
user taps File Baru / FAB but no Space root exists
```

Expected:

```text
show dialog / prompt to create or pick Space
```

Copy:

```text
SPACE belum dipilih.
Buat atau pilih folder SPACE dulu untuk menyimpan file NOTEZ.
```

### 24.2 Permission revoked

Trigger:

```text
saved URI exists but cannot access DocumentFile
```

Expected:

```text
show error
clear or keep stale URI with “Pilih ulang SPACE” action
```

Copy:

```text
NOTEZ tidak bisa mengakses SPACE ini. Pilih ulang folder SPACE.
```

> [!TIP]
> **Saran:** Ada dua cara deteksi izin hilang, dan sebaiknya dua-duanya dipakai. Cek daftar izin persisten (murah, tanpa I/O) lalu cek `canRead()`/`canWrite()` (menangkap folder yang dihapus atau dipindah):
>
> ```kotlin
> fun hasSpaceAccess(context: Context): Boolean {
>     val root = SpacePref.getRootUri(context) ?: return false
>     val granted = context.contentResolver.persistedUriPermissions.any {
>         it.uri == root && it.isReadPermission && it.isWritePermission
>     }
>     if (!granted) return false
>     val doc = DocumentFile.fromTreeUri(context, root)
>     return doc != null && doc.exists() && doc.canRead() && doc.canWrite()
> }
> ```
>
> Untuk pertanyaan “clear or keep stale URI”, rekomendasi: **simpan URI lama** sampai user memilih SPACE baru, supaya kalau folder hanya sementara tidak tersedia (mis. SD card dicabut) tidak perlu setup ulang dari nol.

### 24.3 File deleted externally

Trigger:

```text
user opens recent/tree node but file no longer exists
```

Expected:

```text
show toast/dialog
refresh tree
stay on Home/drawer
```

Copy:

```text
File tidak ditemukan. Mungkin sudah dipindah atau dihapus dari luar NOTEZ.
```

### 24.4 Save failed

Trigger:

```text
ContentResolver output stream null/throws
```

Expected:

```text
dirty remains true
show error
editor remains open
```

Copy:

```text
Gagal menyimpan file. Cek izin SPACE atau ruang penyimpanan.
```

---

## 25. Security & Privacy

Rules:

```text
No cloud sync
No automatic upload
No background network
No CDN
No remote script/style/font
No addJavascriptInterface
No MANAGE_EXTERNAL_STORAGE
No unrestricted file scanning
No hidden background indexing
```

Space scanning must be:

```text
manual/user-triggered
limited to selected root folder
lazy per expanded folder
transparent
```

Remote image cache rules from previous NOTEZ policy remain unchanged:

```text
manual/user-triggered only
clearable
no silent background fetch
```

---

## 26. Accessibility / UX Polish

Minimum:

```text
folder/file rows have readable text
icons have content description if clickable
save button has contentDescription
new file dialog fields are labeled
error messages clear
```

Suggested strings:

```text
cd_pick_space = Pilih folder SPACE
cd_new_space_file = Buat file baru di SPACE
cd_save_file = Simpan file
cd_expand_space_folder = Buka folder
cd_collapse_space_folder = Tutup folder
```

---

## 27. Implementation Phases

### Phase 0 — branch hygiene

Use dev branch:

```text
ZAQIxNOTEZ
```

Sync from main first:

```bash
git checkout ZAQIxNOTEZ
git fetch origin
git merge --no-edit origin/main
git push origin ZAQIxNOTEZ
```

No release version bump during feature work.

### Phase 1 — Space root selection

Implement:

```text
SpacePref
folder picker ActivityResult
persistable URI permission
basic UI CTA in Home/drawer
```

Acceptance:

```text
user can select folder
app remembers it after restart
app can detect missing/revoked permission gracefully
```

### Phase 2 — Space drawer tree read-only

Implement:

```text
SpaceNode
SpaceRepository list/filter
Space drawer section
expand/collapse folders
show .md/.markdown/.txt
```

Acceptance:

```text
folder tree appears
supported files visible
unsupported files ignored or visually skipped
folder expand/collapse works
```

### Phase 3 — Create file in SPACE

Implement:

```text
New File dialog
create .md/.txt in selected Space root
open new file in EditorActivity Space File Mode
```

Acceptance:

```text
can create new .md
can create new .txt
duplicate filename handled safely
created file appears in tree after refresh
```

### Phase 4 — Editor Space File Mode

Implement:

```text
load text from URI
show file name title
body editable
manual save
save icon
```

Acceptance:

```text
open .md from tree
open .txt from tree
edit and save
reopen shows saved changes
```

### Phase 5 — Dirty state and back prompt

Implement:

```text
dirty detection
back dialog
save/discard/cancel branches
```

Acceptance:

```text
unsaved edit cannot be lost silently
save branch works
discard branch works
cancel branch works
```

### Phase 6 — Plain text preview

Implement minimal `.txt` preview behavior.

Acceptance:

```text
.txt content displayed as plain text
# remains #, not Markdown heading
special HTML chars escaped safely
```

### Phase 7 — regression pass

Test:

```text
Local Notes
v0.1.5 code block fix
Settings version
search eye behavior
backup/export settings not broken
```

> [!TIP]
> **Saran:** Urutan di bab ini (Phase 0–7, hanya Space) dan bab 41 (Step 1–6, UX dulu baru Space) bisa membingungkan saat eksekusi. Usulan satu urutan kanonik:
>
> ```text
> Step 1  = Phase 0        sync branch
> Step 2  = bab 37         editor scroll
> Step 3  = bab 38         Settings expandable
> Step 4  = bab 39 (R1)    cache markdown-it (opsional)
> Step 5  = Phase 1–6      Space Foundation
> Step 6  = Phase 7        regression pass
> ```
>
> Dengan begitu bab 27 menjadi detail dari Step 5 saja.

---

## 28. Acceptance Criteria v0.1.6

### Space root

```text
PASS: user can choose folder SPACE
PASS: root URI persists after restart
PASS: user can pick a different SPACE
PASS: permission revoked does not crash app
```

### Drawer tree

```text
PASS: Space appears below Music
PASS: Space section expandable
PASS: folder tree renders
PASS: folder expand/collapse works
PASS: .md files visible
PASS: .markdown files visible
PASS: .txt files visible
PASS: unsupported files do not break tree
```

### File creation

```text
PASS: user can create .md file in SPACE
PASS: user can create .txt file in SPACE
PASS: empty filename rejected
PASS: filename with / rejected
PASS: duplicate filename handled safely
PASS: created file opens in editor
```

### Editor file mode

```text
PASS: Space file opens with correct content
PASS: file name shown as title
PASS: body editable
PASS: save writes file
PASS: reopen shows saved content
PASS: failed save keeps dirty state
```

### Dirty state

```text
PASS: edit marks file dirty
PASS: save clears dirty
PASS: back while dirty shows prompt
PASS: Simpan saves and exits
PASS: Buang exits without saving
PASS: Batal stays in editor
```

### Regression

```text
PASS: Local Notes still visible/openable
PASS: local note autosave still works
PASS: delete undo local note still works
PASS: Home search eye hidden at zero notes
PASS: Home search eye visible after notes exist
PASS: v0.1.5 code block fixed label still works
PASS: Settings version remains correct after future release bump
```

> [!TIP]
> **Saran:** Kata `PASS:` di depan setiap baris adalah *kriteria yang harus tercapai*, bukan hasil test. Supaya bisa dipakai langsung sebagai checklist hasil device test, ubah jadi task list (tampil sebagai checkbox di Reading View):
>
> ```md
> ### Space root
>
> - [ ] User bisa memilih folder SPACE
> - [ ] Root URI bertahan setelah restart
> - [ ] User bisa memilih SPACE lain
> - [ ] Izin dicabut tidak membuat app crash
> ```
>
> Tambahkan tiga kriteria yang belum ada:
>
> ```md
> - [ ] Rotasi layar saat file dirty tidak menghilangkan teks
> - [ ] SPACE di Android 11+ ditolak dengan pesan jelas bila root/Download dipilih
> - [ ] File UTF-8 dengan emoji/aksara non-Latin tersimpan dan terbaca utuh
> ```

---

## 29. Test Matrix

### Fresh install

```text
No local notes
No Space
Open app
Expected: Space onboarding visible
```

### Pick Space

```text
Tap Buat/Pilih SPACE
Select folder
Restart app
Expected: SPACE remembered
```

### Empty Space

```text
SPACE folder empty
Open drawer
Expected: Space section shows empty/help state
```

### Create Markdown

```text
Tap +
Name: test
Type: Markdown
Expected: test.md created and opened
Edit content
Save
Reopen
Expected: content persists
```

### Create Text

```text
Tap +
Name: todo
Type: Text
Expected: todo.txt created and opened
Edit content with # symbols
Save
Preview/read
Expected: plain text, not markdown heading
```

### Nested folders

```text
SPACE/project/ide.md
Expand Space
Expand project
Tap ide.md
Expected: file opens
```

### Unsupported files

```text
SPACE/photo.jpg
SPACE/file.pdf
SPACE/archive.zip
Expected: tree does not crash; unsupported files hidden or non-openable
```

### Dirty prompt

```text
Open file
Edit
Back
Choose Batal
Expected: stay editor
Back again
Choose Buang
Expected: exits, file unchanged
Open file
Edit
Back
Choose Simpan
Expected: exits, file changed
```

### Permission revoked

```text
Pick Space
Revoke permission externally / simulate missing access
Open app
Expected: clear error + choose Space again
```

### Regression code block

```text
Open .md with long fenced code block
Reading View
Horizontally scroll code
Expected: language label stays fixed
```

---

## 30. Risks and Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| SAF permission revoked | Space inaccessible | Detect failure, prompt pilih ulang Space |
| File write fails | data not saved | Keep dirty state, show error, do not exit |
| App crash before save | edits lost | v0.1.6 uses create-before-edit and dirty prompt; future draft autosave optional |
| Large folder tree slow | UI lag | Lazy expand, no full background indexing |
| Duplicate filename | accidental overwrite | Reject duplicates in v0.1.6 |
| Title edit implies rename | confusing/data risk | Title readonly in Space File Mode |
| `.txt` rendered as Markdown | wrong display | Plain text preview path |
| MainActivity becomes too huge | maintainability risk | Extract SpaceRepository/Adapter/optional controller |
| Local Notes hidden | user panic | Keep Local Notes accessible |
| Scope creep to PDF/file manager | delayed release | Explicit non-goals |

> [!TIP]
> **Saran:** Baris risiko tambahan yang muncul dari saran-saran di atas:
>
> | Risk | Impact | Mitigation |
> |---|---|---|
> | Rotasi/recreate menimpa teks dirty | Edit hilang diam-diam | Load file hanya saat `savedInstanceState == null`, simpan flag `dirty` |
> | File diubah dari luar NOTEZ | Save menimpa versi lebih baru | Bandingkan `lastModified`, tanya user |
> | Provider mengubah nama file saat create | File `.md` jadi nama lain | Cek `created.name` setelah `createFile` |
> | File sangat besar dibuka di `EditText` | Lambat/OOM | Batas ukuran awal, pesan jelas |
> | Root/Download ditolak di Android 11+ | User bingung memilih SPACE | Petunjuk di UI: pilih subfolder |

---

## 31. Open Questions

These can be resolved before implementation or during first prototype.

### 31.1 Where exactly should Local Notes live after Space exists?

Options:

```text
A. keep current Home list
B. add drawer item “Catatan Lokal”
C. make Home show Space, with Local Notes legacy link
```

Recommendation for v0.1.6:

```text
Do not remove current Local Notes yet.
Add Space without destroying old Home behavior.
```

### 31.2 Should unsupported files be hidden or shown disabled?

Options:

```text
A. hide unsupported files
B. show disabled rows
```

Recommendation:

```text
Hide unsupported files for v0.1.6 to keep UI clean.
```

### 31.3 Should New File support subfolder target?

Options:

```text
A. create only in Space root
B. create in currently selected/expanded folder
```

Recommendation:

```text
v0.1.6 create in Space root only, unless implementation is already stable.
Subfolder creation/target can wait.
```

### 31.4 Should `.markdown` new file type be offered?

Recommendation:

```text
For create dialog, offer Markdown (.md) only.
For open/filter, support both .md and .markdown.
```

---

## 32. File Path Checklist for ACODE

### Main/Home/Drawer

Open:

```text
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/res/layout/activity_main.xml
app/src/main/res/layout/include_music_drawer.xml
```

Search terms:

```text
setupDrawer
fab
empty_welcome
search_toggle
include_music_drawer
```

### Editor

Open:

```text
app/src/main/java/com/zaba/notez/EditorActivity.kt
app/src/main/res/layout/activity_editor.xml
```

Search terms:

```text
note_id
applyMode
saveBlocking
saveSnapshot
onBackPressed
editToggle
```

### Renderer

Open:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
```

Search terms:

```text
fun render
buildHtml
escapeHtml
enhanceCodeBlocks
notez-code-card
```

### New package

Create:

```text
app/src/main/java/com/zaba/notez/space/SpacePref.kt
app/src/main/java/com/zaba/notez/space/SpaceNode.kt
app/src/main/java/com/zaba/notez/space/SpaceRepository.kt
app/src/main/java/com/zaba/notez/space/SpaceTreeAdapter.kt
```

### Strings/icons/layout

Open/create:

```text
app/src/main/res/values/strings.xml
app/src/main/res/layout/item_space_node.xml
app/src/main/res/drawable/ic_folder.xml
app/src/main/res/drawable/ic_file_text.xml
app/src/main/res/drawable/ic_save.xml
```

---

## 33. Branch Strategy

Development branch:

```text
ZAQIxNOTEZ
```

Stable/release branch:

```text
main
```

Flow:

```text
sync ZAQIxNOTEZ from main
implement Space phases
push ZAQIxNOTEZ
open PR to main
CI build
manual device test
merge only after PASS
```

Do not:

```text
push unfinished Space directly to main
release from ZAQIxNOTEZ
tag before signed production build verified
```

---

## 34. Release Plan for v0.1.6 Later

When implementation is done and tested:

```text
1. Merge Space PR to main after CI + device PASS
2. Prepare release bump:
   versionName 0.1.6
   versionCode 7
3. Update release workflow name/confirm/artifact to v0.1.6
4. Add RELEASE_NOTES_V0.1.6.md
5. Main CI PASS
6. Run production workflow
7. Verify APK badging:
   versionCode='7'
   versionName='0.1.6'
8. Verify signature
9. Record SHA-256
10. Tag v0.1.6
11. Publish GitHub Release
```

---

## 35. Final Decision

Approved direction:

```text
v0.1.6 = Space Workspace Foundation
```

Core behavior:

```text
Create/Pilih SPACE
→ SPACE becomes root folder / rumah catatan
→ Drawer shows expandable file tree
→ + creates new .md/.txt file inside SPACE
→ Tap file opens editor
→ Editor saves manually to file
→ Dirty prompt protects unsaved changes
→ Local Notes remain safe
```

Deferred:

```text
Card Grid
PDF export
rename/delete/move files
create subfolders
global Space search
full Home redesign
```

This keeps NOTEZ moving toward a stronger product identity without turning `v0.1.6` into an uncontrolled file manager rewrite.

> [!TIP]
> **Saran:** Bab 35 masih menyebut `v0.1.6 = Space Workspace Foundation` saja, padahal keputusan final terbaru ada di bab 43. Tambahkan satu baris penunjuk agar tidak ada dua “Final Decision” yang tampak bertentangan:
>
> ```text
> Catatan: keputusan ini diperluas oleh bab 36–43 (UX + Rendering groundwork).
> Jika ada beda, bab 43 yang berlaku.
> ```

---

## 36. Scope Update Setelah Evaluasi Fork

Bagian ini ditambahkan setelah test artifact fork `zabaniyah221-stack/NOTEZ`.

Hasil test device terhadap fork:

```text
Editor scroll speed: PASS / terasa lebih mulus
Settings expandable: PASS / terasa rapi dan nyaman
Markdown renderer optimization: FAIL / Reading View blank, Markdown tidak tampil
```

Keputusan:

```text
Jangan merge fork utuh.
Ambil dua ide yang terbukti bagus.
Tolak patch renderer fork.
Implement ulang dengan gaya NOTEZ sendiri.
```

Dampak ke RFC `v0.1.6`:

```text
v0.1.6 tidak hanya Space Foundation.
v0.1.6 juga membawa UX polish yang mendukung Space:
- editor scroll lebih mulus;
- Settings dibuat expandable;
- renderer Markdown diberi rencana perbaikan aman, bukan patch rusak dari fork.
```

Nama RFC diubah dari:

```text
RFC — NOTEZ v0.1.6 Space Workspace Foundation
```

Menjadi:

```text
RFC — NOTEZ v0.1.6 UX, Rendering, and Space Workspace Foundation
```

Alasan perubahan nama:

```text
Space tetap inti besar v0.1.6,
tapi UX editor, Settings, dan renderer Markdown ikut jadi bagian rencana v0.1.6.
```

Catatan penting:

> Penjelasan Space di section sebelumnya tetap berlaku. Bagian ini hanya menambahkan scope UX/performance yang terbukti relevan dari evaluasi fork.

---

## 37. Accepted UX Fix 1 — Smooth Editor Scroll

### 37.1 Status evaluasi

Status dari test artifact fork:

```text
PASS secara device feel.
Editor mode terasa lebih mulus.
```

Keputusan:

```text
Terima idenya.
Jangan cherry-pick commit fork mentah-mentah.
Implement ulang / apply secara kecil dan terukur di branch ZAQIxNOTEZ.
```

### 37.2 File yang diperbaiki

Path yang terkait:

```text
app/src/main/res/layout/activity_editor.xml
app/src/main/java/com/zaba/notez/EditorActivity.kt
```

Tidak perlu menyentuh:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/java/com/zaba/notez/SettingsActivity.kt
```

Untuk fix ini, scope harus kecil:

```text
Editor scroll only.
```

### 37.3 Keadaan sebelum diperbaiki di NOTEZ v0.1.5 / baseline v0.1.6

Di baseline sekarang, body editor adalah `EditText` langsung di dalam `FrameLayout`.

Path:

```text
app/src/main/res/layout/activity_editor.xml
```

Bentuk baseline konseptual:

```xml
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:layout_weight="1">

    <EditText
        android:id="@+id/edit_body"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:hint="Tulis ide sebelum lupa..."
        android:textColor="?attr/colorOnSurface"
        android:textColorHint="?attr/colorOnSurfaceVariant"
        android:gravity="top"
        android:inputType="textMultiLine|textNoSuggestions"
        android:scrollbars="vertical"
        android:background="@null" />

    <android.webkit.WebView
        android:id="@+id/view_body_web"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone" />
</FrameLayout>
```

Di Kotlin baseline, visibility edit mode langsung mengontrol `bodyEdit`.

Path:

```text
app/src/main/java/com/zaba/notez/EditorActivity.kt
```

Bentuk baseline konseptual:

```kotlin
private lateinit var bodyEdit: EditText
```

```kotlin
bodyEdit = findViewById(R.id.edit_body)
```

```kotlin
bodyEdit.visibility = if (editing) View.VISIBLE else View.GONE
```

### 37.4 Problem baseline

Saat catatan panjang, `EditText` langsung sebagai scroll container bisa terasa kurang smooth karena satu view memegang banyak tanggung jawab:

```text
render teks panjang
handle scroll
handle cursor
handle selection
handle keyboard
handle input multi-line
```

Efek yang terasa di device:

```text
scroll edit mode kurang ringan
scroll saat teks panjang terasa berat
keyboard + cursor bisa membuat experience tidak senyaman Reading View
```

### 37.5 Konsep perbaikan

Pisahkan tanggung jawab scroll dari `EditText`.

Struktur baru:

```text
ScrollView @id/edit_body_scroll
└── EditText @id/edit_body
```

`ScrollView` menangani scroll vertikal, sedangkan `EditText` fokus sebagai area input.

Target XML konseptual:

```xml
<ScrollView
    android:id="@+id/edit_body_scroll"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true"
    android:scrollbars="vertical"
    android:overScrollMode="ifContentScrolls">

    <EditText
        android:id="@+id/edit_body"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Tulis ide sebelum lupa..."
        android:textColor="?attr/colorOnSurface"
        android:textColorHint="?attr/colorOnSurfaceVariant"
        android:gravity="top"
        android:inputType="textMultiLine|textNoSuggestions"
        android:scrollbars="none"
        android:background="@null" />
</ScrollView>
```

Target Kotlin konseptual:

```kotlin
private lateinit var bodyEditContainer: View
private lateinit var bodyEdit: EditText
```

```kotlin
bodyEditContainer = findViewById(R.id.edit_body_scroll)
bodyEdit = findViewById(R.id.edit_body)
```

Di `applyMode()`:

```kotlin
bodyEditContainer.visibility = if (editing) View.VISIBLE else View.GONE
```

Bukan:

```kotlin
bodyEdit.visibility = if (editing) View.VISIBLE else View.GONE
```

> [!TIP]
> **Saran:** Dua tambahan kecil supaya kursor tidak tertutup keyboard, dan supaya area tap di bawah teks tetap fokus ke `EditText`:
>
> ```xml
> <!-- AndroidManifest.xml -->
> <activity
>     android:name=".EditorActivity"
>     android:windowSoftInputMode="adjustResize" />
> ```
>
> ```kotlin
> // Tap di area kosong bawah teks → fokus ke akhir teks
> bodyEditContainer.setOnClickListener {
>     bodyEdit.requestFocus()
>     bodyEdit.setSelection(bodyEdit.text.length)
>     (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
>         .showSoftInput(bodyEdit, InputMethodManager.SHOW_IMPLICIT)
> }
> ```
>
> Karena `ScrollView` + `EditText wrap_content` merender seluruh teks sebagai satu layout, tambahkan **uji ekstrem 1.000+ baris** ke matrix bab 37.8. Kalau ternyata lebih berat dari baseline pada teks sangat panjang, opsi mundurnya: pertahankan `EditText` sebagai scroller sendiri (baseline) dan hanya aktifkan `ScrollView` di bawah ambang tertentu.

### 37.6 Risiko yang harus diuji

Walaupun device test fork terasa bagus, implementasi ini tetap punya risiko:

```text
cursor tidak otomatis ikut terlihat saat keyboard muncul;
select/copy/paste bisa berubah rasa;
EditText wrap_content dengan teks sangat panjang tetap bisa berat;
ScrollView + EditText nested bisa punya edge-case touch handling.
```

Karena itu fix ini wajib diuji sendiri di NOTEZ branch, bukan dianggap aman hanya karena fork artifact terasa mulus.

### 37.7 Acceptance criteria

Fix ini dianggap PASS jika:

```text
catatan pendek tetap normal;
catatan panjang 100-300 baris scroll lebih mulus atau minimal tidak lebih buruk;
keyboard muncul tanpa menutup cursor secara fatal;
cursor di akhir teks tetap bisa dicapai;
copy/paste/select tetap bekerja;
autosave tetap berjalan;
counter tetap update;
switch Edit ↔ Reading tetap normal;
Reading View Markdown tetap tampil;
code block v0.1.5 tetap fixed label;
tidak ada crash saat back/onPause.
```

### 37.8 Test matrix khusus editor scroll

```text
1. Buka note pendek, masuk edit mode.
2. Ketik beberapa kata, pastikan autosave tersimpan.
3. Buka note panjang 100+ baris.
4. Scroll cepat dari atas ke bawah.
5. Tap tengah teks, ketik.
6. Tap akhir teks, ketik.
7. Select/copy/paste beberapa baris.
8. Buka keyboard, scroll saat keyboard aktif.
9. Switch ke Reading View.
10. Switch balik ke Edit Mode.
11. Keluar editor, buka lagi, pastikan content tersimpan.
```

---

## 38. Accepted UX Fix 2 — Expandable Settings Sections

### 38.1 Status evaluasi

Status dari test artifact fork:

```text
PASS secara device feel.
Settings terasa lebih rapi, ringkas, dan nyaman.
```

Keputusan:

```text
Terima idenya.
Implement ulang/rapikan di branch NOTEZ sendiri.
Jangan dicampur dengan renderer optimization.
```

### 38.2 File yang diperbaiki

Path yang terkait:

```text
app/src/main/res/layout/activity_settings.xml
app/src/main/java/com/zaba/notez/SettingsActivity.kt
```

Tidak perlu menyentuh:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/java/com/zaba/notez/EditorActivity.kt
```

Untuk fix ini, scope harus jelas:

```text
Settings UI grouping only.
```

### 38.3 Keadaan sebelum diperbaiki di NOTEZ v0.1.5 / baseline v0.1.6

Di baseline sekarang, Settings menampilkan semua section terbuka sekaligus.

Struktur UI saat ini:

```text
Pengaturan

Tampilan
  Tema

Data & Backup
  Ekspor semua catatan (.json)
  Impor backup (.json)
  Ekspor semua catatan (.txt)
  Folder backup otomatis

Aplikasi
  Tentang NOTEZ
  Privacy / Offline
  Hapus cache gambar online
  Versi
```

Path:

```text
app/src/main/res/layout/activity_settings.xml
```

Bentuk baseline konseptual section title:

```xml
<TextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Tampilan"
    android:textColor="?attr/colorPrimary"
    android:textSize="13sp"
    android:textStyle="bold" />
```

Lalu langsung diikuti row:

```xml
<LinearLayout
    android:id="@+id/settings_row_theme"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    ...>
    ...
</LinearLayout>
```

Untuk section lain, pola baseline juga mirip:

```text
judul section statis
row-row langsung terlihat
separator
judul section berikutnya
row-row langsung terlihat
```

Di Kotlin baseline, tidak ada toggle section.

Path:

```text
app/src/main/java/com/zaba/notez/SettingsActivity.kt
```

Baseline `onCreate()` hanya memasang click listener row:

```kotlin
findViewById<View>(R.id.settings_row_theme).setOnClickListener { showThemeDialog() }
findViewById<TextView>(R.id.settings_row_export_json).setOnClickListener { exportJson() }
findViewById<TextView>(R.id.settings_row_import_json).setOnClickListener { openDoc.launch(arrayOf("application/json")) }
findViewById<TextView>(R.id.settings_row_export_txt).setOnClickListener { exportTxt() }
findViewById<TextView>(R.id.settings_row_auto_backup_folder).setOnClickListener { openTree.launch(null) }
```

Tidak ada:

```text
section_header_appearance
section_content_appearance
section_indicator_appearance
setupExpandableSections()
toggleSection(...)
```

### 38.4 Problem baseline

Saat semua row terbuka, Settings terasa panjang dan padat.

Masalah UX:

```text
semua kategori terlihat sekaligus;
user harus scroll lebih banyak;
Data & Backup dan Aplikasi memenuhi layar;
fitur penting tersebar tanpa affordance collapse;
ketika nanti Space settings ditambahkan, halaman akan makin panjang.
```

Karena `v0.1.6` akan menambah konsep Space, Settings perlu lebih modular.

### 38.5 Konsep perbaikan

Ubah setiap kategori menjadi section expandable.

Target kategori:

```text
Tampilan
Data & Backup
Aplikasi
```

Struktur baru:

```text
section_header_appearance
section_content_appearance

section_header_data
section_content_data

section_header_app
section_content_app
```

Setiap header punya indicator:

```text
▲ = expanded
▼ = collapsed
```

### 38.6 Target XML konseptual

Contoh section Tampilan:

```xml
<LinearLayout
    android:id="@+id/section_header_appearance"
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:background="?attr/selectableItemBackground"
    android:clickable="true"
    android:focusable="true"
    android:gravity="center_vertical"
    android:orientation="horizontal">

    <TextView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Tampilan"
        android:textColor="?attr/colorPrimary"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/section_indicator_appearance"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="▲" />
</LinearLayout>

<LinearLayout
    android:id="@+id/section_content_appearance"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <!-- settings_row_theme stays here -->
</LinearLayout>
```

Contoh collapsed default:

```xml
<LinearLayout
    android:id="@+id/section_content_data"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:visibility="gone">

    <!-- Data & Backup rows -->
</LinearLayout>
```

### 38.7 Target Kotlin konseptual

Tambah function:

```kotlin
private fun setupExpandableSections() {
    val headerAppearance = findViewById<View>(R.id.section_header_appearance)
    val contentAppearance = findViewById<View>(R.id.section_content_appearance)
    val indicatorAppearance = findViewById<TextView>(R.id.section_indicator_appearance)

    val headerData = findViewById<View>(R.id.section_header_data)
    val contentData = findViewById<View>(R.id.section_content_data)
    val indicatorData = findViewById<TextView>(R.id.section_indicator_data)

    val headerApp = findViewById<View>(R.id.section_header_app)
    val contentApp = findViewById<View>(R.id.section_content_app)
    val indicatorApp = findViewById<TextView>(R.id.section_indicator_app)

    headerAppearance.setOnClickListener { toggleSection(contentAppearance, indicatorAppearance) }
    headerData.setOnClickListener { toggleSection(contentData, indicatorData) }
    headerApp.setOnClickListener { toggleSection(contentApp, indicatorApp) }
}
```

Tambah helper:

```kotlin
private fun toggleSection(contentView: View, indicatorView: TextView) {
    val isVisible = contentView.visibility == View.VISIBLE
    contentView.visibility = if (isVisible) View.GONE else View.VISIBLE
    indicatorView.text = if (isVisible) "▼" else "▲"
}
```

Panggil di `onCreate()`:

```kotlin
setupExpandableSections()
updateSummaries()
```

> [!TIP]
> **Saran:** Ganti tema memanggil `recreate()` (bab 38.10 langkah 4), dan itu membuat semua section kembali ke default. Supaya posisi buka/tutup user tidak reset, simpan state-nya, dan sekalian tambahkan deskripsi untuk pembaca layar:
>
> ```kotlin
> private fun toggleSection(header: View, content: View, indicator: TextView) {
>     val expand = content.visibility != View.VISIBLE
>     content.visibility = if (expand) View.VISIBLE else View.GONE
>     indicator.text = if (expand) "▲" else "▼"
>     header.contentDescription = getString(
>         if (expand) R.string.cd_collapse_section else R.string.cd_expand_section
>     )
> }
>
> override fun onSaveInstanceState(outState: Bundle) {
>     super.onSaveInstanceState(outState)
>     outState.putBoolean("sec_appearance", findViewById<View>(R.id.section_content_appearance).isVisible)
>     outState.putBoolean("sec_data", findViewById<View>(R.id.section_content_data).isVisible)
>     outState.putBoolean("sec_app", findViewById<View>(R.id.section_content_app).isVisible)
> }
> ```
>
> Pulihkan nilainya di `onCreate` sebelum memanggil `setupExpandableSections()`. `recreate()` otomatis memanggil `onSaveInstanceState`, jadi tidak perlu kode tambahan untuk kasus ganti tema.

### 38.8 Default expanded/collapsed yang direkomendasikan

Mengikuti hasil fork yang terasa enak:

```text
Tampilan: expanded
Data & Backup: collapsed
Aplikasi: collapsed
```

Catatan:

```text
Versi akan berada di dalam Aplikasi.
User perlu expand Aplikasi untuk melihat versi.
```

Jika nanti user merasa versi harus selalu terlihat, alternatif:

```text
Tampilan: expanded
Data & Backup: collapsed
Aplikasi: expanded
```

Keputusan final default bisa ditentukan saat device test.

> [!TIP]
> **Saran:** Alternatif ketiga yang menjaga Settings tetap ringkas tetapi versi tetap terlihat: pindahkan baris **Versi** keluar dari section Aplikasi, jadikan teks kecil permanen di dasar halaman.
>
> ```text
> Tampilan: expanded
> Data & Backup: collapsed
> Aplikasi: collapsed
> ─────────────
> NOTEZ v0.1.6   ← selalu terlihat
> ```
>
> Ini juga memudahkan verifikasi versi saat device test rilis (bab 34 langkah 7) tanpa harus membuka section apa pun.

### 38.9 Acceptance criteria

Fix ini dianggap PASS jika:

```text
Tampilan bisa collapse/expand;
Data & Backup bisa collapse/expand;
Aplikasi bisa collapse/expand;
indicator ▲/▼ sesuai state;
Tema tetap bisa dibuka dan dipilih;
Export JSON tetap jalan;
Import JSON tetap jalan;
Export TXT tetap jalan;
Folder backup otomatis tetap jalan;
Tentang NOTEZ tetap terbuka;
Privacy dialog tetap terbuka;
Hapus cache gambar online tetap jalan;
Versi tetap menampilkan versionName yang benar;
rotasi/recreate tidak crash;
back button tetap finish activity.
```

### 38.10 Test matrix khusus Settings expandable

```text
1. Buka Settings.
2. Collapse Tampilan.
3. Expand Tampilan, buka Tema.
4. Ganti tema, pastikan Settings recreate aman.
5. Expand Data & Backup.
6. Test export JSON.
7. Test import JSON cancel.
8. Test export TXT.
9. Test folder backup otomatis cancel/pilih folder.
10. Expand Aplikasi.
11. Buka Tentang NOTEZ.
12. Buka Privacy / Offline.
13. Tap Hapus cache gambar online.
14. Cek Versi.
```

---

## 39. Rejected Fork Patch — Markdown Renderer Optimization

### 39.1 Status evaluasi

Status dari test artifact fork:

```text
FAIL.
Reading View blank.
Markdown tidak tampil.
RFC Space tidak render.
```

Keputusan:

```text
Reject patch renderer fork.
Do not merge.
Do not cherry-pick.
Do not use as-is.
```

### 39.2 File yang bermasalah

Path:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
```

### 39.3 Keadaan baseline NOTEZ v0.1.5 / v0.1.6 sebelum optimasi

Baseline sekarang aman, walau ada delay.

Alur baseline:

```text
EditorActivity.applyMode(false)
↓
markdownPreview.render(currentContent)
↓
MarkdownPreviewRenderer.render(markdown)
↓
buildHtml(markdown)
↓
webView.loadDataWithBaseURL(...)
↓
inline markdown-it
↓
JS render + sanitize + enhance
↓
Reading View tampil
```

Bentuk baseline konseptual:

```kotlin
fun render(markdown: String) {
    currentMarkdown = markdown
    webView.loadDataWithBaseURL(
        NOTEZ_BASE_URL,
        buildHtml(markdown),
        "text/html",
        "UTF-8",
        null
    )
}
```

Kelemahan baseline:

```text
setiap render reload full WebView document;
markdown-it JS di-inline ulang;
HTML shell dibangun ulang;
render Markdown panjang terasa delay.
```

Kelebihan baseline:

```text
Reading View tampil;
renderer path sederhana;
code block fix v0.1.5 stabil;
remote image placeholder/cache flow tetap jalan;
sanitizer tetap jalan;
table/callout/footnote/code block tetap jalan.
```

### 39.4 Apa yang dicoba fork

Fork mencoba dua ide:

```text
1. cache markdown-it asset secara static;
2. setelah WebView loaded, render ulang via evaluateJavascript instead of loadDataWithBaseURL.
```

Konsep fork di Kotlin:

```kotlin
if (isLoaded) {
    val js = "if (window.renderNotez) { window.renderNotez(...) }"
    webView.evaluateJavascript(js, null)
} else {
    webView.loadDataWithBaseURL(...)
}
```

Konsep ini secara teori bagus, tapi implementasi fork gagal.

### 39.5 Kenapa fork gagal

Masalah yang ditemukan dari static review + device test:

```text
window.renderNotez tidak didefinisikan dengan benar;
doRender dibuat tapi tidak dipanggil dengan benar;
parameter render dynamic berisiko ditimpa oleh value lama;
initial render path menjadi tidak jelas;
Reading View blank saat diuji dengan RFC Space.
```

Akibat:

```text
Markdown tidak tampil.
Mode View gagal menjalankan fungsi utama NOTEZ.
```

Karena Reading View adalah fitur inti, patch ini tidak boleh masuk.

### 39.6 Rencana perbaikan renderer yang aman

Walaupun patch fork ditolak, problem delay tetap valid.

Rencana aman dibagi fase.

#### Renderer Phase R0 — jangan rusak baseline

Aturan:

```text
selama belum ada test kuat, pertahankan loadDataWithBaseURL baseline;
jangan ubah lifecycle renderer besar-besaran;
jangan sentuh sanitizer tanpa alasan;
jangan mengubah code block DOM fix v0.1.5.
```

#### Renderer Phase R1 — static markdown-it cache

Ide yang boleh dipertimbangkan:

```text
cache isi markdown-it.umd.min.js di companion object
agar asset tidak dibaca ulang per renderer instance
```

Target konseptual:

```kotlin
private val markdownItJs: String
    get() = cachedMarkdownItJs ?: synchronized(MarkdownPreviewRenderer::class.java) {
        cachedMarkdownItJs ?: activity.assets
            .open("markdown/markdown-it.umd.min.js")
            .bufferedReader()
            .use { it.readText() }
            .also { cachedMarkdownItJs = it }
    }
```

```kotlin
private companion object {
    @Volatile private var cachedMarkdownItJs: String? = null
}
```

Kenapa ini relatif aman:

```text
tidak mengubah JS render flow;
tidak mengubah DOM enhancer;
tidak mengubah sanitizer;
hanya mengurangi read asset berulang.
```

Risiko:

```text
impact performa mungkin kecil;
harus pastikan tidak leak Activity;
cache hanya String asset, bukan Context/Activity, jadi aman jika ditulis benar.
```

> [!TIP]
> **Saran:** Karena risiko yang disebut sendiri adalah “impact performa mungkin kecil”, ukur dulu sebelum dan sesudah agar R1 tidak masuk hanya berdasarkan perasaan. `render()` hanya *memulai* load, jadi waktu yang bermakna adalah sampai halaman selesai:
>
> ```kotlin
> private var renderStart = 0L
>
> fun render(markdown: String) {
>     renderStart = SystemClock.elapsedRealtime()
>     // ... loadDataWithBaseURL seperti baseline ...
> }
>
> // di WebViewClient renderer
> override fun onPageFinished(view: WebView, url: String) {
>     Log.d("NOTEZ", "reading view ready in ${SystemClock.elapsedRealtime() - renderStart} ms")
> }
> ```
>
> Bandingkan rata-rata beberapa kali render pada catatan yang sama (mis. RFC ini) antara baseline dan R1. Jika selisihnya kecil, tunda R1 dan biarkan bab 39.6 R0 tetap berlaku. Catatan tambahan: pada `render()` pertama, pembacaan asset terjadi di main thread; kalau terukur signifikan, muat asset di background saat `EditorActivity` dibuat.

#### Renderer Phase R2 — skip duplicate render dengan hati-hati

Ide opsional:

```text
jangan render ulang jika markdown sama persis dan tidak ada alasan refresh.
```

Tapi ini harus hati-hati karena ada kasus yang tetap butuh refresh:

```text
theme berubah;
remote image selesai dicache;
renderer baru dibuat;
WebView reload;
activity recreate.
```

Karena itu R2 tidak boleh asal:

```kotlin
if (markdown == lastRenderedMarkdown) return
```

Perlu state tambahan:

```text
lastRenderedMarkdown
lastRenderedTheme
forceRefresh reason
```

R2 bisa ditunda.

#### Renderer Phase R3 — WebView shell reuse, desain ulang benar

Ini optimasi besar dan tidak wajib untuk `v0.1.6`.

Desain yang benar harus seperti ini:

```javascript
(function () {
  'use strict';

  var preview = document.getElementById('preview');
  var md = window.markdownit(...);

  function renderNotez(source, githubRawBase, cachedImages) {
    // preprocess source
    // render markdown
    // sanitize DOM
    // enhance headings/tables/tasks/code/callouts/footnotes/images
  }

  window.renderNotez = renderNotez;

  renderNotez(INITIAL_MARKDOWN, INITIAL_BASE, INITIAL_CACHED_IMAGES);
})();
```

Kotlin baru boleh memanggil:

```kotlin
webView.evaluateJavascript("window.renderNotez(...)", null)
```

Jika dan hanya jika:

```text
window.renderNotez sudah pasti ada;
initial render juga memakai function yang sama;
semua enhancer tetap dipanggil;
sanitizer tetap dipakai;
image placeholder/cache flow tetap bekerja;
code block label fixed tetap bekerja.
```

R3 harus punya RFC/test sendiri sebelum masuk main.

> [!TIP]
> **Saran:** Bila nanti R3 dikerjakan, dua hal ini mencegah kegagalan seperti fork:
>
> ```text
> 1. Jangan menyusun argumen JS dengan string concatenation dari teks Markdown.
>    Kirim sebagai string JSON yang di-escape (JSONObject.quote) supaya tanda kutip,
>    backslash, dan </script> di dalam catatan tidak merusak skrip.
> 2. Tambahkan flag "ready" di JS (mis. window.__notezReady = true setelah render awal),
>    dan hanya panggil evaluateJavascript kalau flag itu sudah true.
> ```
>
> ```kotlin
> val arg = JSONObject.quote(markdown)   // hasil sudah berupa string JS yang aman
> webView.evaluateJavascript("window.__notezReady && window.renderNotez($arg, base, cached)", null)
> ```
>
> Dengan begitu kalau `renderNotez` belum ada, hasilnya tidak diam-diam kosong: jatuhkan ke `loadDataWithBaseURL` baseline sebagai fallback.

### 39.7 Acceptance criteria renderer improvement

Renderer improvement dianggap PASS jika:

```text
RFC Space markdown panjang tampil;
catatan kosong tetap tampil empty state;
heading/table/list/task list tampil;
code block label fixed tetap bekerja;
callout tetap bekerja;
footnote tetap bekerja;
raw HTML allowlist tetap aman;
remote image placeholder tetap muncul;
manual image load tetap bisa memicu native flow;
cached image tetap tampil;
link external tetap diblok/dibuka sesuai policy;
mode Edit ↔ View berkali-kali tidak blank;
render setelah edit menampilkan content terbaru;
theme change tidak menyebabkan stale color;
WebView tidak crash.
```

### 39.8 Keputusan renderer untuk v0.1.6

Keputusan scope yang direkomendasikan:

```text
v0.1.6 boleh mengambil Renderer Phase R1 jika static review + device test PASS.
v0.1.6 tidak mengambil Renderer Phase R3 shell reuse.
```

Dengan kata lain:

```text
Fix yang aman: cache markdown-it String.
Fix yang ditunda: evaluateJavascript shell reuse.
```

---

## 40. Updated v0.1.6 Scope Setelah Fork Test

Scope utama tetap Space Foundation.

Tambahan UX yang masuk rencana `v0.1.6`:

```text
1. Smooth Editor Scroll
   - activity_editor.xml
   - EditorActivity.kt
   - status: accepted idea, implement cleanly

2. Expandable Settings Sections
   - activity_settings.xml
   - SettingsActivity.kt
   - status: accepted idea, implement cleanly

3. Safe Markdown Renderer Improvement Plan
   - MarkdownPreviewRenderer.kt
   - status: renderer fork rejected;
   - allowed: safe static markdown-it cache if tested;
   - deferred: WebView shell reuse/evaluateJavascript render.

4. Space Workspace Foundation
   - package space/*
   - MainActivity drawer integration
   - EditorActivity Space File Mode
   - SAF root folder
   - .md/.txt create/open/save
```

Yang tetap tidak masuk:

```text
full renderer rewrite;
merge fork renderer patch;
PDF export;
rename/delete/move file;
Card Grid;
full Home redesign final.
```

---

## 41. Updated Implementation Order

Urutan yang direkomendasikan agar aman:

### Step 1 — sync dev branch

```bash
git checkout ZAQIxNOTEZ
git fetch origin
git merge --no-edit origin/main
git push origin ZAQIxNOTEZ
```

### Step 2 — patch editor scroll only

Files:

```text
app/src/main/res/layout/activity_editor.xml
app/src/main/java/com/zaba/notez/EditorActivity.kt
```

Test:

```text
editor long note scroll
keyboard
autosave
switch view/edit
Reading View regression
```

### Step 3 — patch Settings expandable only

Files:

```text
app/src/main/res/layout/activity_settings.xml
app/src/main/java/com/zaba/notez/SettingsActivity.kt
```

Test:

```text
all settings rows still work
version visible after expand
theme recreate safe
```

### Step 4 — optional renderer R1 only

Files:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
```

Allowed change:

```text
cache markdown-it asset String only
```

Forbidden in this step:

```text
evaluateJavascript shell reuse
window.renderNotez lifecycle rewrite
large JS restructuring
```

### Step 5 — Space Foundation

Follow sections 8-35 of this RFC.

### Step 6 — full regression

Test:

```text
v0.1.5 code block fix
Reading View markdown
Editor scroll
Settings expandable
Local Notes
Space create/open/save
```

> [!TIP]
> **Saran:** Satu commit/PR per step membuat rollback mudah kalau device test gagal. Contoh pola nama commit:
>
> ```bash
> git commit -m "feat(editor): separate scroll from EditText (v0.1.6 step 2)"
> git commit -m "feat(settings): expandable sections (v0.1.6 step 3)"
> git commit -m "perf(renderer): cache markdown-it asset (v0.1.6 step 4)"
> ```
>
> Kalau satu step gagal test, cukup `git revert <hash>` untuk step itu tanpa menyentuh step lain.

---

## 42. Updated Release Note Draft for v0.1.6

Draft only, not final:

```md
# NOTEZ v0.1.6

## Added

- Introduced Space Foundation: choose a local SPACE folder and manage .md/.txt files from NOTEZ.
- Added initial Space file flow for creating, opening, editing, and saving local Markdown/Text files.

## Improved

- Improved Edit Mode scrolling for long notes.
- Added expandable Settings sections for cleaner navigation.
- Prepared safer Markdown renderer performance groundwork without changing the stable Reading View contract.

## Kept stable

- Local Notes remain supported.
- Reading View code block labels remain fixed while code scrolls horizontally.
```

> [!TIP]
> **Saran:** Tambahkan bagian singkat untuk hal yang perlu diketahui user, karena Space berbeda dari Local Notes:
>
> ```md
> ## Notes
>
> - SPACE memakai izin folder Android (SAF). NOTEZ hanya bisa mengakses folder yang kamu pilih.
> - Android 11+ tidak mengizinkan memilih root penyimpanan atau folder Download. Pilih atau buat subfolder.
> - Local Notes lama tidak diubah dan tetap tersimpan di dalam app.
> ```

---

## 43. Updated Final Decision

Final v0.1.6 direction after fork evaluation:

```text
v0.1.6 = UX + Rendering groundwork + Space Workspace Foundation
```

Accepted from fork evaluation:

```text
Smooth editor scroll idea
Expandable Settings idea
```

Rejected from fork evaluation:

```text
Markdown renderer evaluateJavascript/shell-reuse patch
```

Implementation rule:

```text
ambil ide yang terbukti bagus;
jangan merge patch mentah;
implement ulang kecil-kecil;
test device per scope;
jaga renderer v0.1.5 tetap stabil.
```

---

## Footnote

[^1]: Sejak Android 11 (API 30), ACTION_OPEN_DOCUMENT_TREE tidak lagi bisa memberi akses ke root penyimpanan internal/SD card maupun folder Download. User harus memilih (atau membuat) subfolder di dalamnya.
[^2]: Sistem membatasi jumlah izin URI persisten per aplikasi (128 sebelum Android 11, 512 sejak Android 11). Untuk satu SPACE tidak masalah, tapi tetap lepas izin lama saat user mengganti SPACE.
[^3]: DocumentFile.createFile(mimeType, displayName) bisa menambahkan atau mengubah ekstensi berdasarkan MIME type, tergantung provider. Perilaku text/markdown untuk .md khususnya perlu diuji di device; kalau hasilnya nama yang salah, alternatifnya membuat dengan MIME application/octet-stream lalu memverifikasi nama akhirnya.
[^4]: Mode "w" di Android 10+ tidak menjamin file dipotong (truncate); "wt" memang dipakai supaya isi lama terhapus penuh. Konsekuensinya, kegagalan di tengah penulisan bisa meninggalkan file terpotong, sehingga status dirty yang tetap true adalah pengaman utamanya.
[^5]: Activity.onBackPressed() deprecated sejak Android 13 (API 33). Pengganti resminya adalah OnBackPressedDispatcher / OnBackPressedCallback dari AndroidX Activity.
