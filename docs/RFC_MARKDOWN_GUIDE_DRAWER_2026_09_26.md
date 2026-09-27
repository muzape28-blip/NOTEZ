# RFC — NOTEZ Panduan Markdown Lokal di Drawer

**Tanggal:** 2026-09-26  
**Status:** IMPLEMENTED LOCALLY — static checks passed; menunggu CI/device UAT  
**Repo:** NOTEZ  
**Basis saat ditulis:** `main` @ `0285b3f` (`docs: finalize v0.1.1 release notes`)  
**Jenis perubahan:** UX/help content + drawer navigation proposal  
**Prinsip utama:** bantu user memahami Markdown secara ringkas, lengkap, offline, dan spesifik untuk fitur NOTEZ.

---

## 1. Ringkasan keputusan yang diusulkan

Tambahkan satu menu bantuan di drawer/sidebar:

```text
Panduan Markdown
```

Menu ini membuka halaman lokal/offline berisi panduan Markdown yang:

- ringkas di awal lewat cheat sheet;
- punya **daftar isi yang bisa ditap** agar user bisa lompat langsung ke section yang dibutuhkan;
- tetap lengkap dan spesifik lewat contoh + penjelasan per syntax;
- ditulis ulang khusus untuk NOTEZ, bukan copy-paste artikel mentah;
- berbasis sumber resmi seperti CommonMark, GitHub Flavored Markdown, GitHub Docs, dan Obsidian Help;
- menjelaskan behavior NOTEZ sendiri:
  - raw HTML tidak dirender aktif;
  - remote image tidak auto-load;
  - checkbox di preview bersifat tampilan baca, bukan kontrol edit;
  - callout, table, code block, link/autolink, dan task list didukung oleh Markdown Preview v2.

Perubahan ini cocok sebagai kandidat kecil setelah `v0.1.1` karena langsung mendukung fitur Markdown Preview v2 tanpa mengubah data model, permission, parser security, atau home card behavior.

---

## 2. Keputusan scope terbaru

Berdasarkan diskusi setelah release `v0.1.1`:

```text
Card layout setting List/Grid: keep/catat dulu, bukan scope RFC ini.
Raw HTML allowlist: defer jauh, candidate versi besar seperti v2.0.0.
Panduan Markdown lokal/offline: kandidat fokus berikutnya.
```

Implikasi RFC ini:

- tidak mengubah home card/list/grid;
- tidak menambah setting tampilan card;
- tidak menambah raw HTML allowlist;
- tidak mengubah semantics Markdown utama;
- boleh menambah enhancement kecil untuk **safe internal heading anchors** agar daftar isi lokal bisa scroll ke section;
- tidak menambah `INTERNET` permission;
- hanya mendesain menu + halaman panduan lokal.

---

## 3. Problem statement

NOTEZ `v0.1.1` sudah punya Markdown Preview v2 yang jauh lebih rapi untuk:

- heading;
- bold/italic/strikethrough;
- list;
- checklist;
- table;
- link/autolink;
- inline code;
- fenced code block;
- callout;
- image placeholder;
- raw HTML safe-as-text.

Masalah berikutnya bukan renderer dulu, tapi pemahaman user:

```text
User bisa melihat hasil Markdown yang bagus,
tapi belum tentu tahu cara menulis syntax Markdown yang benar.
```

Contoh pertanyaan yang mungkin muncul:

- Apa itu Markdown?
- Kenapa `**teks**` bisa jadi tebal?
- Bagaimana cara menggabungkan `** **` dengan kata/kalimat biasa?
- Kenapa table butuh baris `---`?
- Kenapa `---:` bikin kolom rata kanan?
- Kenapa raw HTML tidak aktif di NOTEZ?
- Kenapa remote image tidak langsung tampil?
- Bagaimana bikin catatan rapi tanpa format ribet?

Panduan lokal menyelesaikan gap ini tanpa membuat home screen penuh hint/onboarding. Karena draft panduan bisa cukup panjang, daftar isi yang bisa ditap menjadi bagian penting dari UX, bukan sekadar bonus.

---

## 4. Current source inspection

Files yang sudah dicek untuk konteks desain:

```text
AGENTS.md
README.md
app/src/main/java/com/zaba/notez/MainActivity.kt
app/src/main/res/layout/activity_main.xml
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/AndroidManifest.xml
```

### 4.1 Drawer/settings saat ini

`activity_main.xml` memakai `DrawerLayout` dengan panel drawer berisi:

```text
NOTEZ header
Settings row
  Tema
  Ekspor JSON
  Ekspor TXT
  Impor JSON
  Folder backup otomatis
Music drawer include
```

`MainActivity.setupDrawer()` menghubungkan menu settings ke action/dialog/export/import.

Implikasi:

- drawer sudah punya pola click handler `closeDrawerThen { ... }`;
- menu bantuan bisa ditambahkan tanpa mengubah home screen;
- perlu keputusan placement: top-level drawer row atau submenu Settings.

### 4.2 Markdown renderer saat ini

`MarkdownPreviewRenderer` sudah menjadi renderer lokal untuk Reading View:

- markdown-it dibundel dari APK assets;
- tidak ada CDN/external script/style/font;
- raw HTML disabled (`html: false`);
- tidak memakai `addJavascriptInterface`;
- remote WebView navigation diblokir dan link eksternal dibuka via intent;
- remote image dirender sebagai placeholder/link;
- WebView `blockNetworkLoads = true`;
- tidak perlu `INTERNET` permission.

Implikasi:

```text
Panduan Markdown bisa dirender dengan renderer yang sama agar tampil konsisten.
```

### 4.3 Permission/network

Kontrak `v0.1.1` tetap:

```text
Tidak ada android.permission.INTERNET
```

RFC ini tidak mengubah kontrak tersebut.

---

## 5. Goals

Panduan Markdown harus:

1. **Mudah ditemukan** dari drawer/sidebar.
2. **Offline/local**, tersedia tanpa internet.
3. **Ringkas di awal**, agar user cepat menemukan syntax dasar.
4. **Navigable**, dengan daftar isi yang bisa ditap untuk lompat ke heading/section terkait.
5. **Lengkap dan spesifik di bawahnya**, dengan contoh syntax + penjelasan fungsi.
6. **NOTEZ-specific**, bukan tutorial Markdown generik yang menjanjikan fitur yang belum ada.
7. **Jujur soal batasan**:
   - raw HTML tidak aktif;
   - remote images tidak auto-load;
   - preview checkbox tidak bisa diklik untuk mengubah note;
   - external links dibuka di luar NOTEZ jika user tap.
8. **Konsisten secara visual** dengan Markdown Preview v2.
9. **Tidak mengganggu UX utama**:
   - no home hint;
   - no onboarding popup;
   - no drawer hint text;
   - no forced tutorial.

---

## 6. Non-goals

RFC ini tidak bertujuan untuk:

- membuat Markdown editor baru;
- membuat live preview/edit inline;
- menambah Markdown toolbar;
- menambah copy-code button;
- menambah search dalam panduan;
- menambah floating/auto-generated table of contents;
- menambah `[[TOC]]` otomatis untuk semua catatan;
- fetch artikel dari internet saat app berjalan;
- menambah CDN/script/font/image remote;
- menambah `INTERNET` permission;
- mengaktifkan raw HTML;
- membuat safe HTML allowlist;
- mengubah table/callout/parser behavior;
- mengubah data model notes;
- mengubah backup/import/export;
- mengubah music drawer;
- mengubah home card menjadi grid/list setting.

---

## 7. Sumber referensi dan kebijakan konten

### 7.1 Sumber utama

Panduan ditulis ulang berdasarkan sumber resmi/primer berikut:

- CommonMark Help:  
  https://commonmark.org/help/
- GitHub Flavored Markdown Spec:  
  https://github.github.com/gfm/
- GitHub Docs — Basic writing and formatting syntax:  
  https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax
- GitHub Docs — Organizing information with tables:  
  https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/organizing-information-with-tables
- Obsidian Help — Basic formatting syntax:  
  https://obsidian.md/help/syntax
- Obsidian Help — Advanced formatting syntax:  
  https://obsidian.md/help/advanced-syntax
- Obsidian Help — Callouts:  
  https://obsidian.md/help/callouts
- Obsidian Help — HTML content:  
  https://obsidian.md/help/html

### 7.2 Konten tidak boleh copy-paste mentah

Walau user meminta “ambil artikel aja”, strategi yang lebih aman dan rapi:

```text
ambil konsep dari sumber resmi
→ tulis ulang dengan bahasa NOTEZ
→ hanya masukkan fitur yang NOTEZ support
→ jelaskan batasan NOTEZ secara eksplisit
```

Alasan:

- menghindari masalah copyright/licensing artikel;
- menghindari panduan terlalu panjang atau tidak relevan;
- menghindari janji fitur yang NOTEZ belum punya;
- membuat panduan terasa menyatu dengan app.

---

## 8. Rekomendasi UX placement

Ada dua opsi placement.

### Option A — Top-level drawer item: `Panduan Markdown` recommended

Struktur:

```text
NOTEZ
Panduan Markdown
Settings
  Tema
  Ekspor JSON
  Ekspor TXT
  Impor JSON
  Folder backup otomatis
Music drawer
```

Pros:

- paling mudah ditemukan;
- bantuan bukan setting, jadi tidak perlu dibury;
- hanya tambah satu row, tidak bikin home clutter;
- cocok karena Markdown Preview v2 adalah fitur inti catatan.

Cons:

- drawer punya satu item tambahan di top-level.

### Option B — Di dalam Settings submenu

Struktur:

```text
Settings
  Tema
  Panduan Markdown
  Ekspor JSON
  Ekspor TXT
  Impor JSON
  Folder backup otomatis
```

Pros:

- drawer top-level tetap minimal;
- pola submenu sudah ada.

Cons:

- agak tersembunyi;
- “Panduan” bukan benar-benar setting.

### Recommendation

Pilih **Option A** kecuali user ingin drawer top-level super minimal.

Rationale:

```text
Panduan Markdown adalah help/documentation, bukan konfigurasi.
Satu item top-level di drawer masih clean dan jauh lebih discoverable.
```

---

## 9. Proposed implementation design

### 9.1 Files kandidat

```text
app/src/main/java/com/zaba/notez/MarkdownGuideActivity.kt
app/src/main/res/layout/activity_markdown_guide.xml
app/src/main/assets/help/markdown_guide.md
app/src/main/res/values/strings.xml
app/src/main/res/layout/activity_main.xml
app/src/main/AndroidManifest.xml
```

Jika butuh icon optional:

```text
app/src/main/res/drawable/ic_markdown_guide.xml
```

MVP bisa tanpa icon agar minimal.

### 9.2 Navigation flow

```text
User swipe drawer
→ tap Panduan Markdown
→ drawer closes
→ MarkdownGuideActivity opens
→ guide markdown local asset rendered with MarkdownPreviewRenderer
→ back returns to MainActivity
```

### 9.3 Activity design

`MarkdownGuideActivity` MVP:

- set theme sesuai current `ThemePref`;
- layout simple:
  - top row with back button + title `Panduan Markdown`;
  - WebView content area;
- read asset `help/markdown_guide.md`;
- render via `MarkdownPreviewRenderer`;
- call renderer `destroy()` in `onDestroy()`.

No editor fields, no save, no note creation.

### 9.4 Reuse renderer

Renderer reuse gives:

- visual consistency;
- same table/code/callout behavior;
- same raw HTML disabled behavior;
- same remote image placeholder behavior;
- same link hardening.

Important:

```text
Guide content itself should avoid raw HTML dependence.
```

If the guide needs to show raw HTML syntax, put it inside fenced code blocks or inline code so it displays as literal text.

### 9.5 Manifest

Add activity declaration only:

```xml
<activity android:name=".MarkdownGuideActivity" />
```

Do not add:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 9.6 Clickable daftar isi / internal heading anchors

Draft panduan panjang butuh navigasi cepat. Daftar isi di bagian atas harus berupa link Markdown internal, bukan teks polos:

```md
1. [Cheat sheet cepat](#1-cheat-sheet-cepat)
2. [Teks tebal](#2-teks-tebal)
3. [Teks miring](#3-teks-miring)
```

Untuk subbagian:

```md
15. [Code block](#15-code-block)
    - [Code block untuk berbagai bahasa pemrograman](#15a-code-block-untuk-berbagai-bahasa-pemrograman)
    - [Tips menulis catatan yang enak dibaca di layar kecil](#15b-tips-menulis-catatan-yang-enak-dibaca-di-layar-kecil)
```

Renderer perlu heading anchor support yang aman:

```text
h1-h6 text → deterministic slug → id on heading
href="#slug" → scroll inside same WebView document
http/https/mailto/tel → still open external app
unsafe schemes → still blocked
```

Slug rule draft:

```text
- lowercase;
- trim;
- remove punctuation except letters, numbers, hyphen, underscore, and spaces;
- spaces become hyphens;
- repeated spaces/hyphens collapse to one hyphen;
- duplicate slug gets suffix: -2, -3, ...
```

Example:

```text
## 16. Table
→ id="16-table"

## 15a. Code block untuk berbagai bahasa pemrograman
→ id="15a-code-block-untuk-berbagai-bahasa-pemrograman"
```

Security contract:

```text
Allow only local fragment links that match safe anchor shape, e.g. #16-table.
Do not allow javascript:, file:, content:, data:, or arbitrary WebView navigation.
Do not use raw HTML anchors like <a id="..."></a> because raw HTML remains disabled.
```

Implementation can be either:

1. general additive support in `MarkdownPreviewRenderer`; or
2. a renderer option used by `MarkdownGuideActivity` only.

Recommendation:

```text
Add it to MarkdownPreviewRenderer as a small additive feature, but keep semantics conservative:
only heading ids + safe same-document #fragment links.
```

---

## 10. Guide content contract

Panduan harus punya tiga lapis:

1. **Daftar isi clickable** — untuk lompat cepat ke section panjang.
2. **Cheat sheet cepat** — untuk user yang hanya ingin syntax.
3. **Penjelasan detail** — untuk user yang ingin memahami fungsi dan cara gabung dengan kalimat.

Format per section:

```text
Judul fitur
- Fungsi
- Tulis
- Contoh dalam kalimat
- Catatan/jebakan umum
```

---

## 11. Draft struktur isi panduan

### 11.1 Pembuka

Judul:

```md
# Panduan Markdown NOTEZ
```

Isi konsep:

```text
Markdown adalah cara menulis teks biasa dengan tanda sederhana supaya catatan terlihat rapi saat dibaca.
Di NOTEZ, tulis Markdown di mode edit, lalu lihat hasil rapinya di mode view.
```

### 11.2 Daftar isi clickable

Daftar isi di awal harus berupa link internal ke heading, bukan teks polos. Contoh:

```md
## Daftar isi

1. [Cheat sheet cepat](#1-cheat-sheet-cepat)
2. [Teks tebal](#2-teks-tebal)
3. [Teks miring](#3-teks-miring)
4. [Tebal + miring](#4-tebal-miring)
5. [Coret (strikethrough)](#5-coret-strikethrough)
16. [Table](#16-table)
18. [Raw HTML](#18-raw-html)
```

Catatan:

- daftar isi manual tetap ditulis di asset guide;
- renderer hanya memberi heading `id` dan mengizinkan link `#anchor`;
- ini bukan `[[TOC]]` otomatis dan bukan floating outline.

### 11.3 Cheat sheet cepat

Contoh tabel awal:

```md
| Mau bikin | Tulis |
| --- | --- |
| Tebal | `**teks**` |
| Miring | `*teks*` |
| Tebal + miring | `***teks***` |
| Coret | `~~teks~~` |
| Judul | `# Judul` |
| Bullet list | `- item` |
| Checklist | `- [ ] tugas` |
| Link | `[label](https://contoh.com)` |
| Inline code | `` `kode` `` |
| Quote | `> kutipan` |
| Callout | `> [!NOTE]` |
```

Catatan: tabel ini harus ditulis dengan escaping yang benar agar backtick nested tetap tampil.

### 11.4 Bold / tebal

Harus detail karena user secara eksplisit minta penjelasan fungsi `** **`.

Konten wajib:

```md
## Teks tebal

Gunakan dua bintang sebelum dan sesudah teks.

Tulis:

`Ini **penting** banget.`

Artinya:

- kata `penting` berada di dalam pasangan `** **`;
- hanya kata itu yang menjadi tebal;
- teks di luar pasangan `** **` tetap normal.

Contoh kalimat penuh:

`**Jangan lupa backup catatan sebelum update.**`

Contoh beberapa bagian tebal:

`Hari ini fokus ke **Markdown Preview v2** dan **UI polish**.`

Hindari:

`** tebal **`

Lebih aman:

`**tebal**`
```

### 11.5 Italic / miring

```md
Gunakan satu bintang:

`Ini *miring*.`

Pakai miring untuk penekanan ringan, istilah asing, atau catatan kecil.
```

### 11.6 Bold + italic

```md
`Ini ***sangat penting***.`
```

Jelaskan bahwa `***teks***` berarti tebal sekaligus miring.

### 11.7 Strikethrough / coret

```md
`~~rencana lama~~ rencana baru`
```

Cocok untuk revisi dan perubahan keputusan.

### 11.8 Heading / judul

```md
# Judul utama
## Bagian besar
### Bagian kecil
```

Wajib jelaskan:

- pakai spasi setelah `#`;
- `#Judul` kurang ideal;
- heading membantu note panjang lebih mudah discan.

### 11.9 Paragraf dan line break

Jelaskan:

```md
Ini paragraf pertama.

Ini paragraf kedua.
```

Satu baris kosong membuat paragraf baru.

### 11.10 Bullet list dan numbered list

```md
- Ide pertama
- Ide kedua
- Ide ketiga

1. Buka NOTEZ
2. Tulis catatan
3. Masuk view mode
```

### 11.11 Checklist / task list

```md
- [ ] Tulis draft
- [x] Review
- [ ] Release
```

Wajib jelaskan behavior NOTEZ:

```text
Di preview NOTEZ, checkbox tampil sebagai status baca.
Untuk mengubahnya, edit teks Markdown-nya.
```

### 11.12 Quote

```md
> Ini kutipan atau catatan penting.
```

### 11.13 Callout

Wajib karena NOTEZ support callouts.

```md
> [!NOTE]
> Informasi biasa.

> [!TIP]
> Saran yang membantu.

> [!IMPORTANT]
> Hal penting yang perlu diingat.

> [!WARNING]
> Peringatan sebelum melakukan sesuatu.

> [!CAUTION]
> Risiko atau tindakan yang perlu ekstra hati-hati.
```

Jelaskan:

- callout dibuat dari quote `>`;
- baris pertama menentukan jenis callout;
- isi callout juga diawali `>`.

### 11.14 Link dan bare URL

```md
[GitHub](https://github.com)
```

Jelaskan:

- teks di `[]` adalah label;
- alamat di `()` adalah tujuan;
- bare URL seperti `https://github.com` juga bisa menjadi link di preview NOTEZ.

### 11.15 Inline code

```md
File `README.md` berisi dokumentasi project.
```

Gunakan untuk:

- nama file;
- nama setting;
- command kecil;
- potongan kode pendek.

### 11.16 Code block

Gunakan empat backtick di guide agar contoh tiga backtick bisa ditampilkan.

````md
```kotlin
val appName = "NOTEZ"
println(appName)
```
````

Jelaskan:

- tiga backtick membuka dan menutup blok kode;
- nama bahasa setelah backtick pembuka bersifat label/hint;
- cocok untuk snippet panjang.

### 11.17 Table

Wajib jelas karena sebelumnya ada kasus alignment kanan.

```md
| Nama | Status | Catatan |
| --- | --- | --- |
| Markdown | Done | Preview v2 |
| UI polish | Plan | Future |
```

Jelaskan:

- baris pertama = header;
- baris kedua = pemisah;
- baris berikutnya = isi;
- `|` memisahkan kolom.

Alignment:

```md
| Kiri | Tengah | Kanan |
| :--- | :---: | ---: |
| A | B | C |
```

Makna:

```text
:---  = rata kiri
:---: = rata tengah
---:  = rata kanan
```

### 11.18 Image

Markdown umum:

```md
![Alt text](https://example.com/image.png)
```

Behavior NOTEZ:

```text
Remote image tidak dimuat otomatis karena NOTEZ offline/privacy-first.
Preview menampilkan placeholder/link, bukan mendownload gambar.
```

### 11.19 Raw HTML

Bagian ini harus memakai penjelasan lengkap, bukan sekadar “tidak didukung”, karena user bisa mengira raw HTML yang tampil sebagai teks adalah bug.

Konten wajib menjelaskan:

- beberapa platform Markdown seperti GitHub/Obsidian bisa menerima sebagian HTML mentah, tetapi tetap melakukan sanitization/limitasi;
- NOTEZ saat ini sengaja membuat raw HTML tampil sebagai teks/escaped, bukan elemen aktif;
- ini bukan bug, melainkan keputusan keamanan, privacy-first, offline-first, dan predictability;
- contoh tag aman/umum seperti `<br>`, `<sub>`, `<sup>`, `<kbd>` tetap belum aktif di versi saat ini;
- tag berbahaya seperti `<script>`, `<iframe>`, remote script/style, `onclick=`, dan `javascript:` tidak boleh aktif;
- jika user ingin menulis contoh HTML, gunakan inline code atau code block;
- future wording harus berupa “bisa dipertimbangkan sebagai safe HTML allowlist di versi besar/RFC terpisah”, bukan janji bahwa NOTEZ pasti akan mengaktifkan raw HTML.

Draft wording untuk guide:

````md
## Raw HTML

Markdown di beberapa tempat, seperti GitHub atau Obsidian, bisa menerima sebagian tag HTML mentah. Contohnya:

```md
<br>
<sub>2</sub>
<kbd>Ctrl</kbd>
<script>alert("hi")</script>
```

Di NOTEZ, raw HTML sengaja tidak dirender sebagai elemen aktif. Artinya, tag seperti `<br>`, `<div>`, `<sub>`, `<kbd>`, atau `<script>` akan tampil sebagai teks biasa/escaped, bukan dijalankan.

Ini bukan bug. Ini adalah keputusan keamanan dan privasi:

- catatan hasil copy-paste dari web tidak bisa menjalankan script diam-diam;
- NOTEZ tetap offline-first dan tidak memuat resource asing;
- tampilan catatan lebih mudah diprediksi;
- tidak ada risiko tag seperti `<script>`, `<iframe>`, event handler seperti `onclick=`, atau link `javascript:` aktif.

Kalau kamu ingin menulis contoh HTML sebagai dokumentasi, bungkus dengan inline code atau code block:

```md
Contoh tag: `<br>` dipakai untuk baris baru di HTML.
```

atau:

````md
```html
<br>
<p>Contoh paragraf HTML</p>
```
````

Untuk saat ini, gunakan syntax Markdown bawaan NOTEZ seperti heading, list, table, callout, link, dan code block.

Di masa depan, NOTEZ bisa saja mempertimbangkan safe HTML allowlist, misalnya hanya tag kecil yang aman seperti `<br>`, `<sub>`, `<sup>`, `<kbd>`, atau `<mark>`. Kalau itu dilakukan, fiturnya harus lewat RFC/UAT terpisah dan tetap tidak akan mengizinkan HTML berbahaya seperti `<script>`, `<iframe>`, remote script/style, `onclick=`, atau `javascript:`.
````

Kalimat yang harus dihindari:

```text
NOTEZ akan mengaktifkan raw HTML di versi berikutnya.
```

Alasan:

```text
Terlalu menjanjikan roadmap. Safe HTML allowlist masih future candidate, bukan komitmen release.
```

### 11.20 Escape karakter Markdown

```md
\*\*ini tidak jadi tebal\*\*
```

Jelaskan:

- backslash `\` membuat karakter Markdown tampil sebagai teks biasa.

### 11.21 Contoh catatan rapi

Akhiri dengan contoh note real:

```md
# Rencana v0.1.2

> [!IMPORTANT]
> Fokus ke polish kecil, jangan ubah behavior besar.

## Todo

- [x] Publish v0.1.1
- [ ] Bikin Panduan Markdown
- [ ] Review card layout setting

## Catatan

Gunakan **List mode** sebagai default.
Grid mode bisa jadi opsi tambahan di Settings nanti.
```


### 11.22 Draft guide import cleanup notes

User sudah menyediakan draft awal di workspace upload:

```text
/home/user/uploads/markdown_guide.md
```

Draft tersebut **belum otomatis menjadi source repo** sampai implementation. Sebelum dipindahkan ke `app/src/main/assets/help/markdown_guide.md`, lakukan cleanup berikut:

- ubah daftar isi plain text menjadi link internal clickable;
- pastikan semua link daftar isi cocok dengan slug heading yang dihasilkan renderer;
- cek contoh bold di bagian `Teks tebal` agar benar-benar menunjukkan `**...**`;
- audit nested code fences: jika contoh Markdown berisi triple backtick di dalamnya, wrapper luar harus memakai empat backtick atau lebih;
- hapus separator `---` ganda yang tidak perlu;
- nuance bagian `Table of contents otomatis`: `[[TOC]]` otomatis tetap tidak didukung, tapi daftar isi manual dengan link internal didukung untuk panduan;
- pastikan guide tidak memakai raw HTML aktif untuk anchor atau layout;
- pastikan klaim fitur hanya menyebut behavior yang benar-benar ada di NOTEZ.


---

## 12. Tone bahasa

Panduan harus pakai bahasa Indonesia yang jelas dan natural:

```text
ramah
singkat
nggak kaku
nggak terlalu akademis
spesifik ke cara pakai NOTEZ
```

Hindari:

- paragraf terlalu panjang;
- istilah teknis tanpa contoh;
- klaim “semua Markdown didukung”;
- menyebut fitur future seolah sudah ada.

---

## 13. Acceptance criteria

### 13.1 Product acceptance

- [ ] Ada menu `Panduan Markdown` di drawer/sidebar.
- [ ] Tap menu membuka halaman panduan lokal.
- [ ] Panduan bisa dibaca offline tanpa permission internet.
- [ ] Panduan punya daftar isi clickable di bagian atas.
- [ ] Tap item daftar isi scroll ke heading/section terkait tanpa membuka browser eksternal.
- [ ] Panduan punya cheat sheet cepat.
- [ ] Panduan menjelaskan `**teks**` secara detail, termasuk cara menggabungkannya dengan kata/kalimat.
- [ ] Panduan menjelaskan heading, paragraph, list, checklist, quote, callout, link, inline code, code block, table, image behavior, raw HTML behavior, dan escape character.
- [ ] Panduan menyatakan batasan NOTEZ secara jujur.
- [ ] Back button kembali ke home.
- [ ] Theme app tetap konsisten.
- [ ] Drawer/music/settings/export/import tidak berubah behavior.

### 13.2 Security/privacy acceptance

- [ ] `AndroidManifest.xml` tetap tanpa `INTERNET`.
- [ ] Tidak ada CDN/external script/style/font.
- [ ] Tidak ada `addJavascriptInterface`.
- [ ] Link internal yang diizinkan hanya same-document fragment `#anchor` aman.
- [ ] Guide content berasal dari asset lokal.
- [ ] Raw HTML tetap disabled/escaped.
- [ ] Remote image tetap placeholder/link, tidak auto-load.

### 13.3 Technical acceptance

- [ ] XML layout parse valid.
- [ ] Asset guide terbaca dari APK assets.
- [ ] Markdown guide render via existing renderer atau wrapper yang sama aman.
- [ ] Heading anchors generated deterministically for guide TOC.
- [ ] External links still open via external intent; `#anchor` links stay inside guide.
- [ ] No generated cache/dependency files committed.
- [ ] Static guards no `INTERNET`, no `addJavascriptInterface`, no CDN URL in guide renderer path.
- [ ] Debug CI green sebelum minta device UAT.

---

## 14. UAT plan

Manual UAT setelah implementation:

1. Install debug APK dari CI green.
2. Buka NOTEZ.
3. Swipe drawer.
4. Tap `Panduan Markdown`.
5. Verifikasi halaman terbuka.
6. Scroll dari atas sampai bawah.
7. Tap beberapa item daftar isi, misalnya `Teks tebal`, `Table`, dan `Raw HTML`; pastikan halaman scroll ke section yang benar.
8. Verifikasi table cheat sheet rapi dan bisa horizontal scroll jika perlu.
9. Verifikasi contoh `**teks**`, `*teks*`, checklist, callout, code block tampil jelas.
10. Tap link sumber jika ada:
   - harus membuka external browser/handler;
   - WebView NOTEZ tidak navigasi bebas.
11. Back ke home.
12. Verifikasi note list/search/FAB/delete undo masih normal.
13. Verifikasi music drawer tidak terganggu.
14. Verifikasi tema tetap konsisten setelah ganti theme.

Device UAT evidence harus dilabeli sebagai user/device verified hanya jika user benar-benar melaporkan pass.

---

## 15. Risks and mitigations

### Risk 1 — Drawer makin ramai

Mitigasi:

- hanya tambah satu item;
- tidak menambah home hint/onboarding;
- jika dirasa ramai, pindahkan ke Settings submenu.

### Risk 2 — Panduan terlalu panjang

Mitigasi:

- cheat sheet di awal;
- section pendek;
- contoh spesifik;
- hindari sejarah Markdown panjang.

### Risk 3 — Guide menjanjikan fitur yang belum ada

Mitigasi:

- tulis NOTEZ-specific;
- tandai batasan raw HTML/image/checklist;
- review guide sebelum implementation.

### Risk 3a — Daftar isi clickable salah target

Mitigasi:

- slug rule deterministic dan didokumentasikan;
- daftar isi ditulis mengikuti slug hasil renderer;
- UAT tap beberapa anchor penting: awal, tengah, akhir;
- jika slug mismatch, fix guide link atau slug generator sebelum release.

### Risk 4 — Reuse WebView renderer membawa behavior link/image

Mitigasi:

- reuse renderer yang sudah punya hardening;
- tidak memakai bridge;
- block network loads;
- no INTERNET manifest guard.

### Risk 5 — Copy-paste artikel kena licensing/noise

Mitigasi:

- jangan copy mentah;
- tulis ulang dengan sumber resmi dicatat di RFC/release notes bila perlu.

---

## 16. Rollback plan

Jika fitur bermasalah:

- hapus drawer menu row dan click handler;
- hapus `MarkdownGuideActivity` manifest entry;
- hapus activity layout dan asset guide;
- renderer Markdown Preview v2 untuk notes tidak perlu disentuh jika tidak diubah.

Rollback harus kecil karena fitur bersifat additive.

---

## 17. Open questions sebelum implementasi

1. Placement final:

```text
A. Top-level drawer item `Panduan Markdown` recommended
B. Di dalam Settings submenu setelah Tema
```

2. Apakah daftar isi clickable memakai numbering panjang lengkap atau hanya section inti?

Recommendation:

```text
Pakai daftar lengkap, tapi tetap rapikan subbagian panjang dengan indentasi.
Karena guide panjang, navigasi lengkap lebih berguna daripada terlalu minimal.
```

3. Apakah guide boleh menampilkan section `Sumber resmi` di bawah?

Recommendation:

```text
Boleh, tapi singkat. Link eksternal hanya sebagai referensi, bukan dependency app.
```

4. Apakah style bahasa guide lebih formal atau santai?

Recommendation:

```text
Bahasa Indonesia jelas, natural, tidak terlalu formal. Hindari slang berlebihan di dokumen app.
```

---

## 18. Recommendation final

Lanjutkan dengan scope kecil:

```text
v0.1.2 candidate:
Panduan Markdown lokal/offline di drawer
```

Pilih:

```text
Top-level drawer item `Panduan Markdown`
MarkdownGuideActivity
Local asset `help/markdown_guide.md`
Clickable daftar isi via safe internal heading anchors
Render with existing MarkdownPreviewRenderer
No INTERNET
No raw HTML allowlist
No home card layout change
```

Status RFC ini: **IMPLEMENTED LOCALLY**. Static checks sudah PASS, tetapi belum CI/device verified.
