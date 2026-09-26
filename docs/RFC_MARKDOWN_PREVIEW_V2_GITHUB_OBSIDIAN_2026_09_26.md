# RFC — NOTEZ Markdown Preview v2: GitHub × Obsidian Hybrid + Home Card Delete Glyph

**Tanggal:** 2026-09-26
**Status:** DESIGNED + IMPLEMENTED LOCALLY — menunggu build/CI/device verification
**Repo:** NOTEZ
**Basis saat ditulis:** `main` @ `e3271a7` (`docs: polish v0.1.0 release notes`)
**Jenis perubahan:** UX/rendering design + small home-card polish proposal
**Prinsip utama:** rapi seperti README GitHub, nyaman seperti Reading View Obsidian, tetap lokal/offline seperti NOTEZ.

---

## 1. Ringkasan keputusan yang diusulkan

RFC ini mendokumentasikan dua perubahan utama. Setelah user berkata “gas”, **Phase 1 home trash glyph** dan **Markdown Preview v2 WebView lokal** sudah diimplementasikan di local workspace. Keduanya masih butuh build/CI/device verification sebelum disebut selesai/released.

1. **Markdown Preview v2** untuk mode view di `EditorActivity`:
   - tetap pakai **EditText native** untuk edit mode;
   - ganti/upgrade mode view menjadi **local WebView Reading View**;
   - render Markdown dengan engine lokal yang GitHub-like, kandidat utama: **bundled `markdown-it`**;
   - styling pakai CSS lokal agar table/code/link/callout rapi;
   - tetap **tanpa `INTERNET` permission**;
   - remote image tidak auto-load pada MVP;
   - link tap dibuka di browser eksternal, bukan navigasi di WebView NOTEZ;
   - raw HTML tidak dieksekusi.

2. **Home card delete glyph polish**:
   - icon delete note di list card diganti dari icon bawaan/visual yang terasa seperti `X` menjadi **custom trash/recycle-bin-like glyph** milik NOTEZ;
   - tidak meniru persis Recycle Bin Windows, hanya mengambil metafora “tempat sampah” agar aksi delete lebih jelas;
   - tidak mengubah flow delete/undo saat ini;
   - tidak menambah fitur real Trash/Recently Deleted pada patch kecil ini.

Strategi besarnya:

```text
GitHub gives NOTEZ:
- GFM compatibility
- table discipline
- autolink behavior
- code/document layout
- security/sanitization mindset

Obsidian gives NOTEZ:
- local-first reading experience
- clean view/source separation
- callout/note-taking feel
- future path to wikilinks/live preview

NOTEZ keeps:
- offline/private identity
- simple native editing
- small scope per phase
- no release/tag/APK history rewrite
```

---

## 1.1 Decision update setelah review raw HTML

Setelah diskusi tambahan pada 2026-09-26, user dan agent sepakat:

```text
Raw HTML allowlist tidak masuk MVP Markdown Preview v2.
MVP fokus ke Markdown/GFM rendering dulu: tables, URLs, task list, code, callouts, dan image placeholders.
Safe HTML allowlist menjadi future RFC/phase terpisah agar UAT dan maintenance tidak numpuk.
```

Implikasi:

- raw HTML tetap disabled/escaped pada MVP;
- tidak ada sanitizer allowlist di implementasi awal;
- tag seperti `<br>`, `<sub>`, `<sup>`, `<kbd>`, `<details>`, `<summary>`, `<picture>`, dan `<img>` akan dibahas nanti jika benar-benar dibutuhkan;
- fokus implementasi tetap pada preview Markdown yang rapi, offline, dan aman.

---

## 2. Current source inspection

### 2.1 Markdown pipeline saat ini

Source evidence:

- `app/src/main/java/com/zaba/notez/EditorActivity.kt`
- `app/src/main/res/layout/activity_editor.xml`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`

Saat RFC ditulis, mode view editor masih:

```text
Markwon → TextView inside ScrollView
```

`EditorActivity.kt` membuat Markwon dengan plugin:

```text
core
ext-strikethrough
ext-tasklist
```

`app/build.gradle.kts` berisi dependency Markwon:

```kotlin
implementation("io.noties.markwon:core:4.6.2")
implementation("io.noties.markwon:ext-strikethrough:4.6.2")
implementation("io.noties.markwon:ext-tasklist:4.6.2")
```

Belum ada dependency/plugin untuk:

```text
Markwon ext-tables
Markwon linkify
Markwon image
Markwon html
Markwon recycler / recycler-table
```

`activity_editor.xml` memakai satu `TextView`:

```text
ScrollView
  TextView view_body
```

Implikasi:

- table/multiple-column Markdown tidak punya layout engine ideal;
- bare URL belum otomatis menjadi link jika tidak ditulis `[text](url)`;
- image Markdown belum ditangani sebagai rich image;
- rendering GitHub-style README/SKILLS sulit rapi jika tetap di single TextView.

### 2.2 Permission/network saat ini

`AndroidManifest.xml` secara eksplisit menjaga NOTEZ tetap tanpa internet:

```text
Tidak ada android.permission.INTERNET
```

Manifest saat ini hanya memakai permission untuk fitur musik lokal:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Keputusan RFC ini mempertahankan prinsip tersebut:

```text
Markdown Preview v2 tidak boleh menambah INTERNET permission pada MVP.
```

### 2.3 Home list card saat ini

Source evidence:

- `app/src/main/res/layout/item_note.xml`
- `app/src/main/java/com/zaba/notez/NoteAdapter.kt`
- `app/src/main/java/com/zaba/notez/MainActivity.kt`

`item_note.xml` sudah berupa Material card dengan title, preview, meta, dan tombol delete. Tombol delete:

```xml
<ImageButton
    android:id="@+id/item_delete"
    android:layout_width="48dp"
    android:layout_height="48dp"
    android:contentDescription="Hapus catatan"
    android:src="@android:drawable/ic_delete"
    app:tint="?attr/colorOnSurfaceVariant" />
```

`NoteAdapter.kt` menghubungkan tombol itu ke callback:

```text
h.delete.setOnClickListener { onDelete(n) }
```

`MainActivity.kt` mengarah ke flow `deleteWithUndo(note)`.

Implikasi:

- action delete sudah punya tap target 48dp dan undo flow;
- polish yang diperlukan hanya mengganti icon menjadi glyph app-owned yang lebih jelas;
- tidak perlu ubah persistence/delete logic untuk patch icon.

---

## 3. Problem statement

User sering paste Markdown gaya README/SKILLS ke NOTEZ. Di GitHub/Obsidian, dokumen tersebut terlihat rapi. Di NOTEZ, mode view kadang terasa messy, terutama untuk:

1. **Tables / multiple columns**
   - TextView single column sulit memberi table layout yang nyaman.
   - Tabel lebar butuh horizontal scrolling atau CSS layout.

2. **Bare URLs**
   - URL mentah seperti `https://github.com/...` diharapkan otomatis clickable seperti GitHub.

3. **Image URLs**
   - `![alt](https://...)` saat ini belum punya behavior jelas.
   - NOTEZ tanpa `INTERNET`, jadi remote image auto-load akan mengubah privacy/offline contract.

4. **Code blocks**
   - Technical notes/README butuh monospace block, spacing, dan horizontal scroll.

5. **Callouts / alerts**
   - GitHub dan Obsidian sama-sama punya syntax blockquote-callout yang useful untuk catatan.

6. **Delete icon di home**
   - `X`/delete bawaan terasa kurang semantic. Untuk destructive action, trash glyph lebih jelas.

---

## 4. Goals

### 4.1 Markdown Preview v2 goals

- Membuat mode view Markdown lebih rapi untuk pasted README/SKILLS-style Markdown.
- Support baseline **GitHub Flavored Markdown** yang paling relevan untuk NOTEZ:
  - headings;
  - paragraphs;
  - bold/italic/strikethrough;
  - ordered/unordered/nested lists;
  - task lists;
  - blockquotes;
  - fenced code blocks;
  - inline code;
  - tables with alignment;
  - bare URL autolinks;
  - Markdown links;
  - Markdown images as safe placeholders for remote sources;
  - footnotes/callouts if dependency complexity remains reasonable.
- Memberi table layout yang jelas:
  - border halus;
  - readable cell padding;
  - horizontal scroll untuk table lebar;
  - alignment kiri/tengah/kanan sesuai GFM.
- Membuat code block nyaman:
  - monospace;
  - background berbeda;
  - horizontal scroll;
  - optional language label later.
- Menjaga NOTEZ tetap local/offline:
  - tidak menambah `INTERNET` permission;
  - tidak load CDN;
  - tidak load remote script/CSS/font/image.
- Menjaga edit mode tetap stabil:
  - `EditText` native tetap dipakai;
  - keyboard/input/autosave existing behavior tidak dirombak.

### 4.2 Home card goals

- Ganti delete icon menjadi custom trash glyph app-owned.
- Pertahankan tap target minimal 48dp.
- Pertahankan `deleteWithUndo` behavior.
- Gunakan tint calm/muted secara default, bukan merah permanen.
- Gunakan content description dari resource string.

---

## 5. Non-goals

### 5.1 Markdown non-goals untuk MVP

MVP tidak bertujuan untuk:

- full Obsidian clone;
- mengganti edit mode dengan CodeMirror 6;
- live preview inline ala Obsidian;
- plugin ecosystem;
- graph view;
- Mermaid diagram rendering;
- MathJax/LaTeX rendering;
- syntax highlighting berat berbasis Shiki/Prism pada MVP;
- remote image auto-load;
- video/embed web content;
- raw HTML execution;
- browser navigation inside NOTEZ;
- menambah `INTERNET` permission;
- schema migration notes DB;
- merilis ulang tag/APK `v0.1.0`.

### 5.2 Home card non-goals

Patch delete glyph tidak bertujuan untuk:

- real Recycle Bin/Recently Deleted;
- soft-delete schema;
- restore history selain undo snackbar existing;
- redesign total home screen;
- onboarding/hint drawer baru;
- mengubah search/drawer/music behavior.

---

## 6. External research summary

### 6.1 GitHub source gems

GitHub Flavored Markdown adalah dialect Markdown untuk GitHub user content dan merupakan strict superset dari CommonMark. GitHub juga melakukan post-processing dan sanitization setelah Markdown dikonversi ke HTML.

Source:

- GitHub Flavored Markdown Spec:
  https://github.github.com/gfm/

GitHub Docs menjelaskan table syntax:

- table dibuat dengan pipes `|` dan hyphens `-`;
- blank line sebelum table diperlukan agar render benar;
- pipes di ujung optional;
- cell width tidak perlu aligned sempurna;
- alignment pakai colon `:---`, `:---:`, `---:`;
- inline formatting/link/code bisa dipakai di cells;
- literal pipe dalam cell perlu escape `\|`.

Source:

- Organizing information with tables — GitHub Docs:
  https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/organizing-information-with-tables

GitHub Docs juga menjelaskan:

- headings dan section links;
- links dan auto-created links untuk valid URLs;
- images;
- task lists;
- footnotes;
- alerts/callouts dengan `> [!NOTE]`, `> [!TIP]`, `> [!IMPORTANT]`, `> [!WARNING]`, `> [!CAUTION]`.

Source:

- Basic writing and formatting syntax — GitHub Docs:
  https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax

Takeaway untuk NOTEZ:

```text
GitHub adalah baseline terbaik untuk README/SKILLS compatibility.
GFM table/link/code/callout behavior layak dijadikan target preview.
```

### 6.2 Obsidian source gems

Obsidian membagi pengalaman Markdown menjadi:

- **Reading view** — note bersih tanpa Markdown syntax;
- **Editing view**;
- **Live Preview** — formatted inline while editing;
- **Source mode** — raw Markdown penuh.

Source:

- Views and editing mode — Obsidian Help:
  https://obsidian.md/help/edit-and-read

Obsidian menyatakan mendukung kombinasi:

- CommonMark;
- GitHub Flavored Markdown;
- LaTeX;
- extension seperti internal links, embeds, footnotes, comments, strikethrough, highlights, task lists, callouts, tables.

Source:

- Obsidian Flavored Markdown — Obsidian Help:
  https://obsidian.md/help/obsidian-flavored-markdown

Obsidian table docs menunjukkan table sebagai fitur first-class; di Live Preview user bisa right-click table untuk add/delete columns/rows, sort, move, alignment. Ini terlalu besar untuk NOTEZ MVP, tapi membuktikan bahwa table UX perlu diperlakukan sebagai layout feature, bukan text decoration kecil.

Source:

- Advanced formatting syntax — Obsidian Help:
  https://obsidian.md/help/advanced-syntax

Obsidian developer docs menyatakan editor extension Obsidian adalah CodeMirror 6 extension, dan Obsidian memakai CodeMirror 6 untuk Markdown editor.

Source:

- Editor extensions — Obsidian Developer Documentation:
  https://docs.obsidian.md/Plugins/Editor/Editor+extensions

Takeaway untuk NOTEZ:

```text
Ambil Reading View/local-first feel dari Obsidian.
Jangan ambil full Live Preview dulu karena itu rewrite editor besar.
```

### 6.3 Markwon source gems

Markwon adalah renderer Android-native yang sudah dipakai NOTEZ. Markwon punya plugin table, linkify, image, recycler-table.

Source:

- Markwon install/modules:
  https://noties.io/Markwon/docs/v4/install.html
- Markwon tables extension:
  https://noties.io/Markwon/docs/v4/ext-tables/
- Markwon linkify:
  https://noties.io/Markwon/docs/v4/linkify/
- Markwon image:
  https://noties.io/Markwon/docs/v4/image/
- Markwon recycler-table:
  https://noties.io/Markwon/docs/v4/recycler-table/

Takeaway untuk NOTEZ:

```text
Markwon patch bisa jadi quick fix,
tapi TextView tetap bukan layout engine terbaik untuk table lebar/multiple columns.
```

### 6.4 markdown-it source gems

`markdown-it` adalah Markdown parser web yang fast/extensible, mengikuti CommonMark, punya syntax extensions/sugar seperti URL autolinking/typographer, configurable rules, plugin ecosystem, dan safe-by-default positioning.

Source:

- markdown-it official demo/docs:
  https://markdown-it.github.io/markdown-it/
- markdown-it repository:
  https://github.com/markdown-it/markdown-it
- npm package metadata/license:
  https://www.npmjs.com/package/markdown-it

Takeaway untuk NOTEZ:

```text
markdown-it cocok untuk WebView Reading View lokal,
terutama untuk GFM-like table/link/callout pipeline dan CSS-based layout.
```

### 6.5 flexmark-java source gems

`flexmark-java` adalah parser/renderer Markdown Java yang fleksibel dan extension-rich. Ia bisa parse Markdown di JVM/Kotlin lalu output HTML untuk WebView.

Source:

- flexmark-java wiki:
  https://github.com/vsch/flexmark-java/wiki
- flexmark-java extensions wiki:
  https://github.com/vsch/flexmark-java/wiki/Extensions

Takeaway untuk NOTEZ:

```text
flexmark-java memungkinkan WebView dengan JavaScript off,
tapi dependency dan custom pipeline kemungkinan lebih berat untuk NOTEZ MVP.
```

### 6.6 WebView security source gems

Android security guidance untuk WebView menekankan:

- disable JavaScript jika tidak perlu;
- jika JavaScript perlu, script harus controlled/owned by app;
- jangan eksekusi arbitrary JavaScript dari untrusted input;
- kunci file/content access jika tidak diperlukan.

Source:

- Android cross-app scripting / WebView mitigations:
  https://developer.android.com/privacy-and-security/risks/cross-app-scripting

Takeaway untuk NOTEZ:

```text
Jika WebView dipakai, harus ada security contract eksplisit.
Local renderer boleh, open browser bebas tidak boleh.
```

---

## 7. Option comparison

### 7.1 Option A — Markwon patch

```text
Markwon + ext-tables + linkify + optional recycler-table
```

Pros:

- perubahan kecil;
- tetap native TextView;
- tanpa WebView;
- risiko security rendah;
- dependency mental model sudah ada di repo.

Cons:

- table lebar tetap terbatas jika single TextView;
- GitHub/Obsidian-like visual polish sulit;
- CSS-like table/code styling tidak natural;
- image/HTML behavior tetap perlu keputusan;
- kalau pakai recycler-table, integrasi layout menjadi lebih kompleks dan tidak lagi sesederhana single TextView.

Best for:

```text
Quick patch low-risk, bukan target final untuk README/SKILLS yang rapi.
```

### 7.2 Option B — WebView + bundled markdown-it

```text
Markdown raw
→ markdown-it local JS
→ sanitized/controlled HTML
→ NOTEZ CSS
→ WebView Reading View
```

Pros:

- table/multiple columns bisa rapi dengan CSS;
- horizontal scroll natural;
- code block/blockquote/callout mudah dipoles;
- GitHub-like README rendering lebih realistis;
- bisa tetap no INTERNET dengan local assets;
- bisa menjadi fondasi future features: heading anchors, outline, wikilinks.

Cons:

- WebView lebih berat dari TextView;
- JavaScript perlu ON jika parser jalan di WebView;
- perlu hardening ketat;
- perlu test selection/copy/link/back behavior;
- perlu supply-chain handling untuk vendored JS.

Best for:

```text
Recommended path untuk NOTEZ Markdown Preview v2.
```

### 7.3 Option C — flexmark-java + WebView

```text
Markdown raw
→ flexmark Java/Kotlin renderer
→ HTML
→ WebView with JS off
```

Pros:

- WebView JavaScript bisa disabled;
- parser berjalan di native/JVM side;
- security boundary lebih mudah dijelaskan;
- tetap dapat CSS layout.

Cons:

- dependency lebih berat;
- konfigurasi extension bisa lebih verbose;
- visual benefit tetap berasal dari WebView/CSS;
- kemungkinan overkill untuk NOTEZ MVP.

Best for:

```text
Fallback jika user tidak nyaman dengan JS-on WebView,
atau jika markdown-it bundling dirasa kurang cocok.
```

### 7.4 Option D — Full Obsidian-like CodeMirror Live Preview

```text
Edit mode diganti CodeMirror 6 inside WebView
```

Pros:

- paling dekat ke Obsidian Live Preview;
- future rich editor lebih mungkin.

Cons:

- rewrite besar;
- Android keyboard/IME risk tinggi;
- autosave/cursor/selection/scroll risk tinggi;
- lifecycle jauh lebih kompleks;
- terlalu besar untuk problem awal.

Best for:

```text
Future research only, bukan MVP.
```

---

## 8. Proposed direction

Rekomendasi RFC:

```text
Adopt Option B:
WebView Reading View + bundled markdown-it + NOTEZ CSS
```

Dengan batasan keras:

```text
Edit mode tetap EditText native.
View mode saja yang memakai WebView.
Tidak ada INTERNET permission.
Tidak ada CDN.
Tidak ada remote script/CSS/font/image.
Tidak ada addJavascriptInterface.
Tidak ada navigasi internal WebView ke web bebas.
Raw HTML disabled/escaped.
Remote image menjadi placeholder/link.
```

Rationale:

- problem utama adalah layout, terutama table/multiple columns;
- GitHub/Obsidian sama-sama mengandalkan HTML/CSS/DOM untuk rendering kaya;
- TextView Markwon bagus untuk Markdown sederhana, tapi bukan target ideal untuk table docs;
- WebView local Reading View memberi manfaat besar tanpa mengganti editor;
- NOTEZ tetap local-first karena tidak perlu network permission.

---

## 9. Proposed architecture

### 9.1 High-level flow

```text
EditorActivity
  edit mode:
    EditText title + EditText body

  view mode:
    currentTitle/currentContent
      → MarkdownPreviewRenderer
      → local HTML shell
      → WebView render
```

### 9.2 New/changed components draft

Potential files:

```text
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/assets/markdown/markdown-it.min.js
app/src/main/assets/markdown/markdown-it.LICENSE.txt
app/src/main/res/raw/notez_markdown_preview.css OR inline Kotlin string
app/src/main/res/drawable/ic_notez_trash.xml
```

Alternative if avoiding assets:

```text
Inline minified markdown-it and CSS into generated HTML string.
```

But assets are cleaner for license/integrity review.

### 9.3 EditorActivity integration draft

Current:

```text
ScrollView → TextView view_body
markwon.setMarkdown(bodyView, currentContent)
```

Proposed:

```text
FrameLayout view container
  WebView markdown_preview_webview
  optional TextView fallback/error state
```

Pseudo-flow:

```kotlin
if (!editing) {
    titleView.text = currentTitle.ifBlank { getString(R.string.untitled_note) }
    if (currentContent.isBlank()) {
        showEmptyPreview()
    } else {
        markdownPreview.render(currentContent)
    }
}
```

### 9.4 WebView shell strategy

Recommended shell:

```text
loadDataWithBaseURL("https://notez.local/", html, "text/html", "UTF-8", null)
```

Where `html` contains:

- local CSS;
- local parser code or pre-rendered HTML;
- escaped Markdown payload;
- no external script/style/image URLs.

If loading from assets is chosen, ensure file access rules are compatible and safe. If enabling file access solely for `android_asset` becomes necessary, document the exact reason and lock down all other loads.

### 9.5 WebView settings draft

Recommended hardening:

```kotlin
webView.settings.javaScriptEnabled = true // only if markdown-it runs in WebView
webView.settings.allowFileAccess = false
webView.settings.allowContentAccess = false
webView.settings.domStorageEnabled = false
webView.settings.databaseEnabled = false
webView.settings.setSupportMultipleWindows(false)
webView.settings.javaScriptCanOpenWindowsAutomatically = false
webView.isLongClickable = true
```

Do not use:

```text
addJavascriptInterface
remote debugging in release
file:// unrestricted content
CDN script/style
```

Navigation policy:

```text
Internal notez.local render loads allowed.
http/https/mailto/tel taps open external intent if user taps a link.
All WebView navigation attempts to remote URLs are blocked.
```

Network policy:

```text
Remote image/script/style/font loads blocked by generated HTML and WebViewClient.
No INTERNET permission remains in manifest.
```

### 9.6 Parser config draft

For markdown-it:

```js
const md = window.markdownit({
  html: false,
  linkify: true,
  typographer: false,
  breaks: false
})
.enable(['table', 'strikethrough'])
```

Task lists/callouts/footnotes may need small plugins or custom preprocessing/postprocessing.

Recommended MVP order:

1. tables;
2. linkify;
3. strikethrough/task list;
4. code blocks;
5. callouts;
6. image placeholders;
7. heading anchors.

### 9.7 HTML/raw handling draft

MVP decision:

```text
Raw HTML allowlist is OUT of scope for Markdown Preview v2 MVP.
Raw HTML from note content is disabled/escaped by default.
HTML comments may be removed/hidden from preview if implementation can do it safely.
Safe HTML allowlist is future work via separate review/RFC.
```

Reason:

- GitHub allows some sanitized HTML, but implementing a correct allowlist/sanitizer is non-trivial;
- Obsidian intentionally does not render Markdown inside HTML elements for parser complexity/performance;
- NOTEZ should avoid accidental WebView script/HTML attack surface;
- user explicitly preferred keeping UAT and maintenance small by deferring raw HTML allowlist.

Future candidate allowlist, not MVP:

```text
<br>, <sub>, <sup>, <kbd>, <mark>, <details>, <summary>
```

Future blocked examples if an allowlist is designed later:

```text
<script>, <style>, <iframe>, <object>, <embed>, <form>, <input>, <button>, <meta>, <link>
on*, javascript:, srcdoc, unsafe data:, remote auto-load behavior
```

### 9.8 Image handling draft

For Markdown image syntax:

```md
![alt text](https://example.com/image.png)
```

MVP render:

```text
Image placeholder card
- alt text if present
- source URL text
- tap opens URL externally, if user chooses
```

Not allowed in MVP:

```text
Auto-fetch remote images inside NOTEZ.
Adding INTERNET permission solely for preview images.
```

Future local attachment support:

```text
User can attach/import local image via SAF.
NOTEZ stores persisted URI reference.
Preview can render local image via controlled ContentResolver/WebView bridge or native wrapper.
```

This is out of MVP.

---

## 10. Visual design contract

### 10.1 Overall style

Target feel:

```text
GitHub-readable + Obsidian-cozy + NOTEZ dark/minimal
```

Recommended CSS characteristics:

```css
body {
  margin: 0;
  padding: 0 0 24px;
  background: transparent;
  color: /* colorOnSurface-like */;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  line-height: 1.55;
  overflow-wrap: anywhere;
}

h1, h2, h3 {
  line-height: 1.25;
  margin-top: 1.2em;
  margin-bottom: .55em;
}

p {
  margin: .65em 0;
}
```

### 10.2 Tables

Requirements:

```text
- table has visible but calm borders;
- th background slightly distinct;
- cells have readable padding;
- table wrapper horizontal-scrolls when too wide;
- table does not destroy overall page width;
- alignment colons are respected.
```

CSS direction:

```css
.notez-table-wrap {
  overflow-x: auto;
  margin: 12px 0;
}

table {
  border-collapse: collapse;
  min-width: 100%;
  width: max-content;
}

th, td {
  border: 1px solid rgba(255,255,255,.16);
  padding: 8px 10px;
  vertical-align: top;
}

th {
  background: rgba(255,255,255,.06);
  font-weight: 700;
}
```

### 10.3 Code blocks

Requirements:

```text
- monospace;
- horizontal scroll;
- background block;
- rounded corners;
- does not wrap code into unreadable mush by default.
```

CSS direction:

```css
pre {
  overflow-x: auto;
  padding: 12px;
  border-radius: 10px;
  background: rgba(255,255,255,.06);
}

code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
}
```

### 10.4 Links

Requirements:

```text
- Markdown links and bare URLs are visibly clickable;
- long URLs wrap gracefully;
- tapping link never navigates WebView away from NOTEZ preview;
- external browser intent opens for http/https.
```

### 10.5 Callouts

Support GitHub-compatible callouts first:

```md
> [!NOTE]
> Useful information.

> [!TIP]
> Helpful advice.

> [!IMPORTANT]
> Key information.

> [!WARNING]
> Be careful.

> [!CAUTION]
> Dangerous/destructive action.
```

Visual style:

```text
NOTE      blue accent
TIP       green accent
IMPORTANT purple accent
WARNING   yellow/orange accent
CAUTION   red accent
```

No emoji dependence for functional meaning. Icons can be simple CSS labels or future vector/native assets.

### 10.6 Image placeholders

Example render:

```text
[Image]
alt: screenshot of app
source: https://example.com/screenshot.png
Tap to open externally
```

Visual:

```text
bordered card
small image glyph optional
muted source URL
```

---

## 11. Home screen/card polish contract

### 11.1 Delete glyph decision

Current issue:

```text
Delete action can visually read as X/close/cancel.
```

Proposed:

```text
Use app-owned vector drawable trash glyph.
```

Requirements:

- vector XML under `app/src/main/res/drawable/`;
- not copied from Windows Recycle Bin;
- simple outline style:
  - lid;
  - body;
  - two or three vertical lines;
  - optional slight rounded visual via vector paths;
- tint `?attr/colorOnSurfaceVariant` by default;
- no permanent red icon in normal state;
- keep button size `48dp x 48dp`;
- keep ripple/background;
- content description moved to string resource:

```xml
<string name="cd_delete_note">Hapus catatan</string>
```

### 11.2 Delete flow remains unchanged

Existing flow should remain:

```text
tap trash glyph
→ onDelete(note)
→ MainActivity.deleteWithUndo(note)
→ undo snackbar behavior as current
```

No DB/schema change.

### 11.3 Real Trash feature deferred

Do not implement now:

```text
Recently Deleted
Restore from trash
Delete forever
Auto purge 30 days
```

Reason:

- would need data model/schema or separate tombstone store;
- user only approved icon polish, not a destructive-flow redesign;
- current undo delete already covers immediate mistake recovery.

Future RFC can define real trash if desired.

### 11.4 Optional home visual direction, not part of first patch

Potential future home card v2 ideas:

```text
- keep clean list card layout;
- title strong, preview max 2 lines, meta/date muted;
- trash glyph less dominant;
- maybe small accent line or subtle paper-card feel;
- no onboarding/drawer hint text, per prior user preference.
```

These are not implementation requirements unless approved separately.

---

## 12. Dependency and supply-chain plan

### 12.1 If using markdown-it

Rules before adding/bundling:

- pin exact version;
- record upstream URL;
- record license;
- preserve license text in repo if vendored;
- record checksum of vendored artifact if possible;
- no CDN link;
- no `latest` dynamic install in build;
- inspect dependency/package contents before committing bundled file.

Candidate source:

```text
markdown-it npm package, MIT license
https://www.npmjs.com/package/markdown-it
https://github.com/markdown-it/markdown-it
```

Potential doc artifact:

```text
app/src/main/assets/markdown/THIRD_PARTY_MARKDOWN.md
```

Or add section to this RFC/implementation summary after exact version is chosen.

### 12.2 If using flexmark-java

Rules:

- add explicit Gradle dependency version;
- record modules/extensions used;
- measure APK size delta;
- verify transitive dependencies;
- keep WebView JS disabled if this route is chosen.

### 12.3 Markwon dependencies

If fallback Markwon patch is chosen instead of WebView v2:

```kotlin
implementation("io.noties.markwon:ext-tables:4.6.2")
implementation("io.noties.markwon:linkify:4.6.2")
```

Potential recycler-table route requires more layout work and should be separately scoped.

---

## 13. Implementation phases

### Phase 0 — RFC only

This document. No implementation.

### Phase 1 — Home delete glyph polish

Status after user said “gas”: **IMPLEMENTED LOCALLY**.

Small low-risk patch:

- add `ic_notez_trash.xml` vector drawable;
- add `cd_delete_note` string;
- update `item_note.xml` to use the drawable/string;
- do not alter `NoteAdapter` or `MainActivity` delete logic unless required;
- run XML/static checks.

Implemented files:

```text
app/src/main/res/drawable/ic_notez_trash.xml
app/src/main/res/values/strings.xml
app/src/main/res/layout/item_note.xml
```

### Phase 2 — Markdown Preview v2 foundation

Status after user said “gas”: **IMPLEMENTED LOCALLY**.

- add `MarkdownPreviewRenderer` abstraction;
- add WebView to view mode layout or swap `view_body` area carefully;
- load local HTML shell;
- apply WebView hardening settings;
- keep old Markwon path temporarily as fallback if practical;
- no advanced features yet beyond basic render.

### Phase 3 — GFM baseline

Status after user said “gas”: **IMPLEMENTED LOCALLY for baseline rendering, pending build/device verification**.

- tables;
- table wrapper horizontal scroll;
- linkify bare URLs;
- task lists;
- strikethrough;
- code blocks;
- blockquotes;
- heading style.

### Phase 4 — GitHub × Obsidian gems

Status after user said “gas”: **PARTIAL LOCAL IMPLEMENTATION**. Callouts and image placeholders are implemented; heading anchors/mini outline are still future.

- callouts/alerts;
- heading anchors;
- image placeholders;
- optional hidden HTML comments;
- optional mini outline research.

### Phase 5 — Hardening and UAT

- security source guards;
- no network permission guard;
- large README/SKILLS sample test;
- orientation/keyboard/text selection test;
- music drawer lifecycle regression test;
- device UAT.

---

## 14. Acceptance criteria

### 14.1 Markdown rendering acceptance

Given a README/SKILLS-style note containing:

```md
# Skills

Visit https://github.com/muzape28-blip/NOTEZ

| Skill | Level | Notes |
| :--- | :---: | ---: |
| Kotlin | Good | Android |
| Markdown | Good | README/SKILLS |

- [x] Build NOTEZ
- [ ] Polish preview

```kotlin
fun hello() = println("NOTEZ")
```

> [!WARNING]
> Jangan hapus catatan tanpa backup.

![logo](https://example.com/logo.png)
```

Mode view should:

- render `# Skills` as heading;
- render bare URL as clickable external link;
- render table as table, not pipe-text mush;
- allow horizontal table scroll if wide;
- respect left/center/right alignment if supported by parser;
- render task list checkboxes visually, view-only;
- render fenced code block with monospace block and horizontal scroll;
- render callout as styled card if callouts included in MVP phase;
- render remote image as placeholder/link, not auto-fetch;
- keep no `INTERNET` permission.

### 14.2 Security acceptance

- `AndroidManifest.xml` still has no `android.permission.INTERNET`.
- WebView does not use `addJavascriptInterface`.
- WebView blocks navigation away from local preview.
- WebView opens external links via Android intent only after user action.
- Raw `<script>` in note content is not executed.
- Raw HTML is disabled/escaped or sanitized according to final policy.
- Remote image URLs do not load inside WebView.
- No CDN/external font/external CSS/external JS in preview HTML.

### 14.3 Editor regression acceptance

- Edit mode still opens with native keyboard.
- Autosave still works.
- View/edit toggle still works.
- Title/body content preserved across toggles.
- Empty note hint still works.
- Existing music drawer in Editor remains accessible.
- Main ↔ Editor navigation unaffected.

### 14.4 Home delete glyph acceptance

- List card delete button shows custom trash glyph.
- Tap target remains at least 48dp.
- Default icon tint is muted/calm.
- Delete flow still supports undo as before.
- Content description is resource-backed.
- No real Trash/Recycle Bin behavior is implied in copy.

---

## 15. Test plan

### 15.1 Static/source checks

Proposed checks:

```bash
# no internet permission
grep -R "android.permission.INTERNET" app/src/main/AndroidManifest.xml app/src/main || true

# no WebView bridge
grep -R "addJavascriptInterface" app/src/main || true

# delete icon no longer uses Android built-in ic_delete in item_note.xml
grep -R "@android:drawable/ic_delete" app/src/main/res/layout/item_note.xml || true

# XML parse smoke test
python3 - <<'PY'
import xml.etree.ElementTree as ET
from pathlib import Path
for p in Path('app/src/main/res').rglob('*.xml'):
    ET.parse(p)
print('xml ok')
PY
```

### 15.2 Build checks

Preferred if Gradle wrapper exists later:

```bash
./gradlew assembleDebug --console=plain
```

Current known sandbox limitation:

```text
NOTEZ repo currently has no Gradle wrapper.
Previous local Android build attempts were not reliable in sandbox because dependency resolution failed before compile.
CI remains canonical build verification until wrapper/local environment is fixed.
```

### 15.3 Manual/UAT scenarios

Markdown preview:

```text
Paste README with simple headings/lists                 : PASS/FAIL
Paste table with 3 columns                              : PASS/FAIL
Paste wide table with many columns                      : PASS/FAIL
Horizontal scroll only table area                       : PASS/FAIL
Bare https URL clickable                                : PASS/FAIL
Tap link opens external browser                         : PASS/FAIL
WebView does not navigate away internally               : PASS/FAIL
Remote image becomes placeholder/link                   : PASS/FAIL
<script>alert(1)</script> does not execute              : PASS/FAIL
Raw HTML policy behaves as documented                   : PASS/FAIL
Code block horizontal scroll                            : PASS/FAIL
Task list render                                        : PASS/FAIL
Callout render if included                              : PASS/FAIL
View/Edit toggle preserves content                      : PASS/FAIL
Autosave after edit still works                         : PASS/FAIL
Long note scroll performance acceptable                 : PASS/FAIL
Landscape layout acceptable                             : PASS/FAIL
Music drawer still works in Editor                      : PASS/FAIL
No INTERNET permission in built APK manifest            : PASS/FAIL
```

Home card:

```text
Trash glyph visible on every note card                  : PASS/FAIL
Tap trash deletes note with undo behavior               : PASS/FAIL
Undo restores note                                      : PASS/FAIL
TalkBack/accessibility reads Hapus catatan              : PASS/FAIL
Icon not red/aggressive in normal state                 : PASS/FAIL
```

---

## 16. Risks and mitigations

### 16.1 WebView security risk

Risk:

```text
Untrusted note content rendered in WebView could become script/HTML attack surface.
```

Mitigation:

- raw HTML disabled/escaped;
- no `addJavascriptInterface`;
- local parser/script only;
- no CDN;
- no file/content access unless a documented asset-loading exception is required;
- block WebView navigation;
- no INTERNET permission;
- static guards.

### 16.2 Performance risk

Risk:

```text
WebView startup/render heavier than TextView/Markwon.
```

Mitigation:

- instantiate only in view mode or lazy-init;
- reuse WebView in activity lifecycle;
- debounce render if needed;
- keep edit mode native;
- test long notes.

### 16.3 Dependency/supply-chain risk

Risk:

```text
Bundled JS parser becomes untracked third-party code.
```

Mitigation:

- pin exact version;
- preserve license;
- record checksum/source URL;
- do not use remote install/CDN at runtime;
- inspect vendored artifact.

### 16.4 UX mismatch for images

Risk:

```text
User expects GitHub remote images to appear immediately.
```

Mitigation:

- document MVP behavior: image placeholders only;
- keep no-INTERNET promise;
- future local attachment/image support can be designed separately.

### 16.5 User expectation from trash/recycle glyph

Risk:

```text
Trash/recycle-bin icon may imply recoverable trash bin.
```

Mitigation:

- use generic trash glyph, not exact Windows Recycle Bin;
- keep existing undo snackbar;
- avoid copy like “Recycle Bin” until real trash exists;
- future RFC for Recently Deleted if desired.

---

## 17. Rollback plan

Implement in reversible commits:

1. `ui: replace note delete glyph with custom trash icon`
2. `docs/assets: add pinned markdown preview engine assets`
3. `feat: add local markdown preview renderer shell`
4. `feat: render editor view mode with markdown preview v2`
5. `fix: harden markdown preview navigation and images`

Rollback options:

- revert trash glyph patch independently;
- disable WebView preview and restore Markwon TextView path;
- keep Markwon dependencies until WebView v2 is stable;
- do not touch Room schema, release tag, or published APK.

---

## 18. Resolved/open questions before implementation

Default recommendation is marked with **Recommended**. Resolved decisions are recorded so implementation does not reopen scope accidentally.

1. Markdown engine route:
   - **Recommended:** bundled `markdown-it` + local WebView.
   - Alternative: `flexmark-java` + WebView JS off.
   - Alternative: Markwon patch only.

2. Callouts in MVP:
   - **Recommended:** include GitHub five alerts if implementation stays small.
   - Alternative: defer callouts to phase 2 after table/link foundation.

3. Image behavior:
   - **Recommended:** remote image placeholder/link only.
   - Alternative: ask user to approve `INTERNET` later if real remote images are desired.

4. Raw HTML behavior:
   - **Resolved for MVP:** disable/escape raw HTML; do not implement safe HTML allowlist now.
   - Future: sanitized subset may be designed later in a separate RFC/phase after preview v2 is stable.

5. Home polish scope:
   - **Recommended:** only trash glyph now.
   - Alternative: full card redesign later via separate RFC.

---

## 19. Approval checklist

User approved the direction in chat (“gas”). This checklist is retained as implementation and review guard:

- [x] Markdown Preview v2 may use local WebView for view mode.
- [x] Edit mode remains native EditText, not CodeMirror/Live Preview.
- [x] `markdown-it` bundled local is acceptable if license/version are recorded.
- [x] NOTEZ remains without `INTERNET` permission.
- [x] Remote images become placeholders/links in MVP.
- [x] External links open outside NOTEZ.
- [x] Raw HTML is disabled/escaped by default.
- [x] GitHub-style tables/autolinks/code blocks are MVP priority.
- [x] GitHub/Obsidian-style callouts are allowed if small enough.
- [x] Home delete icon can become a custom trash glyph.
- [x] No real Trash/Recently Deleted behavior in this small polish.
- [x] No release/tag/APK history changes for this work.

---

## 20. Current status

```text
Designed                                   : YES
Source inspected                           : YES
External sources recorded                  : YES
Raw HTML allowlist deferred by decision    : YES
Phase 1 home trash glyph implemented       : YES, local workspace only
Markdown Preview v2 implemented            : YES, local workspace only
Local XML/static checks                    : YES
Local JS parser smoke check                : YES
UAT kit added                              : YES
Local Android build verified               : NO — no Gradle wrapper/global gradle in sandbox
CI verified                                : NO
Device verified                            : NO
Released                                   : NO
```

Local checks run after implementation:

```text
XML parse for app/src/main/res/**/*.xml                 : PASS
No @android:drawable/ic_delete in item_note             : PASS
No android.permission.INTERNET grep                     : PASS
No addJavascriptInterface in uncommented source         : PASS
No Markwon source/deps after WebView migration          : PASS
markdown-it Node smoke: table/autolink/raw HTML escaped : PASS
git diff --check                                       : PASS
```

Implemented files/areas:

```text
app/src/main/res/drawable/ic_notez_trash.xml
app/src/main/res/values/strings.xml
app/src/main/res/layout/item_note.xml
app/src/main/res/layout/activity_editor.xml
app/src/main/java/com/zaba/notez/EditorActivity.kt
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
app/src/main/assets/markdown/markdown-it.umd.min.js
app/src/main/assets/markdown/markdown-it.LICENSE.txt
app/src/main/assets/markdown/THIRD_PARTY_MARKDOWN.md
docs/UAT_MARKDOWN_PREVIEW_V2_2026_09_26.md
docs/UAT_MARKDOWN_PREVIEW_V2_NOTEZ_BACKUP.json
app/build.gradle.kts
```

Next step after user review:

```text
Run CI/build on canonical Android environment, then device UAT.
If issues appear, rollback path is to restore Markwon TextView preview or patch WebView renderer.
```
