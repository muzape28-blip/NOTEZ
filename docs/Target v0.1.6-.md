# GUIDE — Implementasi NOTEZ v0.1.7 via ACODE + Termux

>
> [!NOTE]
> Status: **panduan implementasi**, belum menjalankan patch source app.  
> Target branch stabil: `ZAQIxNOTEZ`.  
> Scope v0.1.7 dikunci untuk:
>
> ```text
> 1. Panduan Markdown sectioned docs browser.
> 2. Editor drawer manual Save file / Save As.
> ```

Panduan ini dibuat supaya saat buka ACODE, lu tahu:

```text
mulai dari mana
file mana yang diedit
cari teks apa
sekitar baris berapa
paste snippet apa
kenapa perubahan itu dilakukan
cara test-nya bagaimana
```

---

## 0. Prinsip penting sebelum mulai

Jangan masukin eksperimen renderer shell pool / `PreviewWebViewPool` ke branch ini.

Untuk v0.1.7 stabil:

```text
JANGAN sentuh:
app/src/main/java/com/zaba/notez/markdown/MarkdownPreviewRenderer.kt
```

Alasan:

```text
Delay Panduan Markdown diselesaikan dengan cara render per-section,
bukan rewrite renderer utama.
```

---

## 1. Persiapan di Termux

Jalankan dulu sebelum edit di ACODE:

```bash
cd ~/NOTEZ

git checkout ZAQIxNOTEZ
git fetch origin
git pull --ff-only origin ZAQIxNOTEZ

git status -sb
```

Expected aman:

```text
## ZAQIxNOTEZ...origin/ZAQIxNOTEZ
```

Kalau muncul file modified yang lu nggak yakin, **stop dulu** dan review.

---

## 2. Urutan kerja yang disarankan

Kerjakan berurutan:

```text
A. Panduan Markdown sectioned docs browser
   1. activity_markdown_guide.xml
   2. MarkdownGuideActivity.kt
   3. test Panduan Markdown

B. Editor Save file / Save As
   4. activity_editor.xml
   5. NoteFileTargetStore.kt
   6. EditorActivity.kt
   7. test Save As / Save file

C. Commit / push
```

Kenapa Panduan Markdown dulu?

```text
- Tidak menyentuh data user.
- Risiko lebih kecil.
- Langsung menyelesaikan delay Panduan Markdown.
- Tidak mengubah renderer utama.
```

---

# PART A — Panduan Markdown sectioned docs browser

## A1. Edit layout Panduan Markdown

### File target

```text
app/src/main/res/layout/activity_markdown_guide.xml
```

### Cari di ACODE

Cari:

```text
guide_body_web
```

Di versi sekarang, file ini pendek, sekitar 44 baris. Cara paling aman: **replace seluruh isi file** dengan source di bawah.

### Tujuan perubahan

Sebelumnya:

```text
Panduan Markdown langsung punya 1 WebView besar.
Saat dibuka, semua markdown_guide.md dirender sekaligus.
```

Sesudah:

```text
Layout punya 2 mode:
1. index native daftar isi
2. section reader yang punya WebView + PREV/NEXT
```

### Source final `activity_markdown_guide.xml`

> Label: **source target XML**.  
> Replace seluruh isi file dengan ini.

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="12dp">

    <LinearLayout
        android:id="@+id/guide_header"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_vertical"
        android:orientation="horizontal">

        <ImageButton
            android:id="@+id/guide_back"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="@string/cd_back"
            android:src="@drawable/ic_arrow_back"
            android:tint="?attr/colorOnSurface" />

        <TextView
            android:id="@+id/guide_title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:ellipsize="end"
            android:maxLines="1"
            android:paddingStart="8dp"
            android:paddingEnd="8dp"
            android:text="@string/markdown_guide_title"
            android:textColor="?attr/colorOnSurface"
            android:textSize="20sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/guide_page"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:paddingStart="8dp"
            android:paddingEnd="4dp"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:textSize="13sp"
            android:visibility="gone" />
    </LinearLayout>

    <FrameLayout
        android:id="@+id/guide_content_frame"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <ScrollView
            android:id="@+id/guide_index_container"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:fillViewport="true"
            android:overScrollMode="ifContentScrolls"
            android:scrollbars="vertical">

            <LinearLayout
                android:id="@+id/guide_index_list"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingTop="8dp"
                android:paddingBottom="16dp" />
        </ScrollView>

        <LinearLayout
            android:id="@+id/guide_section_container"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:visibility="gone">

            <TextView
                android:id="@+id/guide_section_loading"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:padding="12dp"
                android:text="Merender bagian ini..."
                android:textColor="?attr/colorOnSurfaceVariant"
                android:textSize="13sp"
                android:visibility="gone" />

            <android.webkit.WebView
                android:id="@+id/guide_body_web"
                android:layout_width="match_parent"
                android:layout_height="0dp"
                android:layout_weight="1"
                android:background="@android:color/transparent"
                android:overScrollMode="ifContentScrolls"
                android:scrollbars="vertical" />

            <LinearLayout
                android:id="@+id/guide_section_nav"
                android:layout_width="match_parent"
                android:layout_height="52dp"
                android:gravity="center_vertical"
                android:orientation="horizontal">

                <TextView
                    android:id="@+id/guide_prev"
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1"
                    android:background="?attr/selectableItemBackground"
                    android:clickable="true"
                    android:focusable="true"
                    android:gravity="center_vertical"
                    android:text="‹ PREV"
                    android:textColor="?attr/colorPrimary"
                    android:textSize="14sp"
                    android:textStyle="bold" />

                <TextView
                    android:id="@+id/guide_next"
                    android:layout_width="0dp"
                    android:layout_height="match_parent"
                    android:layout_weight="1"
                    android:background="?attr/selectableItemBackground"
                    android:clickable="true"
                    android:focusable="true"
                    android:gravity="center_vertical|end"
                    android:text="NEXT ›"
                    android:textColor="?attr/colorPrimary"
                    android:textSize="14sp"
                    android:textStyle="bold" />
            </LinearLayout>
        </LinearLayout>
    </FrameLayout>
</LinearLayout>
```

### Setelah edit XML, cek cepat

Cari id ini di ACODE:

```text
guide_back
guide_title
guide_page
guide_index_container
guide_index_list
guide_section_container
guide_body_web
guide_prev
guide_next
```

Semua harus ada.

---

## A2. Edit `MarkdownGuideActivity.kt`

### File target

```text
app/src/main/java/com/zaba/notez/MarkdownGuideActivity.kt
```

### Cari di ACODE

Cari:

```text
markdownPreview.render(guideMarkdown)
```

File ini pendek. Cara paling aman: **replace seluruh isi file** dengan source di bawah.

### Tujuan perubahan

Sebelumnya:

```kotlin
val guideMarkdown = assets.open("help/markdown_guide.md").bufferedReader().use { it.readText() }
markdownPreview.render(guideMarkdown)
```

Masalah:

```text
Seluruh panduan 1.700+ baris dirender sekaligus.
```

Sesudah:

```text
- markdown_guide.md tetap satu file.
- Activity parse heading section utama.
- Index page native dibuat dari hasil parse.
- WebView hanya render section yang dipilih.
```

### Source final `MarkdownGuideActivity.kt`

> Label: **source target Kotlin**.  
> Replace seluruh isi file dengan ini.

```kotlin
package com.zaba.notez

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zaba.notez.markdown.MarkdownPreviewRenderer

class MarkdownGuideActivity : AppCompatActivity() {

    private enum class GuideGroup(val label: String) {
        DAILY_USE("DAILY USE"),
        ADVANCED("KONTEN LANJUTAN"),
        REFERENCE("REFERENSI & CONTOH")
    }

    private data class GuideSection(
        val id: String,
        val title: String,
        val indexLabel: String,
        val group: GuideGroup,
        val markdown: String
    )

    private lateinit var markdownPreview: MarkdownPreviewRenderer
    private lateinit var guideBack: ImageButton
    private lateinit var guideTitle: TextView
    private lateinit var guidePage: TextView
    private lateinit var indexContainer: View
    private lateinit var indexList: LinearLayout
    private lateinit var sectionContainer: View
    private lateinit var sectionLoading: TextView
    private lateinit var guidePrev: TextView
    private lateinit var guideNext: TextView

    private var sections: List<GuideSection> = emptyList()
    private var currentSectionIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_guide)

        guideBack = findViewById(R.id.guide_back)
        guideTitle = findViewById(R.id.guide_title)
        guidePage = findViewById(R.id.guide_page)
        indexContainer = findViewById(R.id.guide_index_container)
        indexList = findViewById(R.id.guide_index_list)
        sectionContainer = findViewById(R.id.guide_section_container)
        sectionLoading = findViewById(R.id.guide_section_loading)
        guidePrev = findViewById(R.id.guide_prev)
        guideNext = findViewById(R.id.guide_next)

        val webView = findViewById<WebView>(R.id.guide_body_web)
        markdownPreview = MarkdownPreviewRenderer(this, webView)

        val guideMarkdown = assets.open("help/markdown_guide.md").bufferedReader().use { it.readText() }
        sections = parseGuideSections(guideMarkdown)
        renderIndex()
        showIndex()

        guideBack.setOnClickListener { finish() }
    }

    private fun renderIndex() {
        indexList.removeAllViews()
        indexList.addView(introText())

        val groupOrder = listOf(GuideGroup.DAILY_USE, GuideGroup.ADVANCED, GuideGroup.REFERENCE)
        groupOrder.forEach { group ->
            val groupSections = sections.withIndex().filter { it.value.group == group }
            if (groupSections.isEmpty()) return@forEach
            indexList.addView(groupHeader(group.label))
            groupSections.forEach { indexed ->
                indexList.addView(sectionRow(indexed.value, indexed.index))
            }
        }
    }

    private fun introText(): View = TextView(this).apply {
        text = "Markdown adalah cara menulis teks biasa dengan tanda sederhana, supaya catatan tetap rapi saat dibaca.\n\n" +
            "Panduan ini sengaja dibuat lengkap. Kamu tidak perlu membaca semuanya sekali duduk — pilih topik di bawah."
        setTextColor(getColor(ThemePref.optionOf(ThemePref.get(this@MarkdownGuideActivity)).secondaryColorRes))
        textSize = 14f
        lineSpacingMultiplier = 1.12f
        setPadding(dp(8), dp(10), dp(8), dp(14))
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun groupHeader(label: String): View = TextView(this).apply {
        text = label
        setTextColor(getColor(ThemePref.optionOf(ThemePref.get(this@MarkdownGuideActivity)).accentColorRes))
        textSize = 12f
        typeface = Typeface.DEFAULT_BOLD
        letterSpacing = 0.08f
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(8), dp(18), dp(8), dp(6))
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun sectionRow(section: GuideSection, index: Int): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        isFocusable = true
        setBackgroundResource(selectableItemBackground())
        setPadding(dp(8), 0, dp(8), 0)
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(52)
        )

        val title = TextView(this@MarkdownGuideActivity).apply {
            text = "${section.id}. ${section.indexLabel}"
            setTextColor(getColor(ThemePref.optionOf(ThemePref.get(this@MarkdownGuideActivity)).textColorRes))
            textSize = 15f
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
        }

        val arrow = TextView(this@MarkdownGuideActivity).apply {
            text = "›"
            setTextColor(getColor(ThemePref.optionOf(ThemePref.get(this@MarkdownGuideActivity)).secondaryColorRes))
            textSize = 20f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(dp(28), ViewGroup.LayoutParams.MATCH_PARENT)
        }

        addView(title)
        addView(arrow)
        setOnClickListener { showSection(index) }
    }

    private fun showIndex() {
        currentSectionIndex = -1
        guideBack.visibility = View.VISIBLE
        guideTitle.text = getString(R.string.markdown_guide_title)
        guidePage.visibility = View.GONE
        indexContainer.visibility = View.VISIBLE
        sectionContainer.visibility = View.GONE
    }

    private fun showSection(index: Int) {
        val section = sections.getOrNull(index) ?: return
        currentSectionIndex = index
        guideBack.visibility = View.GONE
        guideTitle.text = "${section.id}. ${section.indexLabel}"
        guidePage.text = "${index + 1}/${sections.size}"
        guidePage.visibility = View.VISIBLE
        indexContainer.visibility = View.GONE
        sectionContainer.visibility = View.VISIBLE

        sectionLoading.visibility = View.VISIBLE
        markdownPreview.render(section.markdown)
        sectionLoading.visibility = View.GONE
        updatePrevNext()
    }

    private fun updatePrevNext() {
        val hasPrev = currentSectionIndex > 0
        val hasNext = currentSectionIndex >= 0 && currentSectionIndex < sections.lastIndex

        guidePrev.isEnabled = hasPrev
        guideNext.isEnabled = hasNext
        guidePrev.alpha = if (hasPrev) 1f else 0.38f
        guideNext.alpha = if (hasNext) 1f else 0.38f

        guidePrev.setOnClickListener { if (hasPrev) showSection(currentSectionIndex - 1) }
        guideNext.setOnClickListener { if (hasNext) showSection(currentSectionIndex + 1) }
    }

    private fun parseGuideSections(markdown: String): List<GuideSection> {
        val lines = markdown.split('\n')
        val starts = mutableListOf<Pair<Int, MatchResult>>()
        val sectionRegex = Regex("^##\\s+(\\d+[a-z]?)\\.\\s+(.+)$")
        var fence: Char? = null

        lines.forEachIndexed { index, line ->
            val marker = fenceMarker(line)
            if (fence != null) {
                if (marker == fence) fence = null
                return@forEachIndexed
            }
            if (marker != null) {
                fence = marker
                return@forEachIndexed
            }

            val match = sectionRegex.find(line) ?: return@forEachIndexed
            val id = match.groupValues[1]
            if (guideGroupFor(id) != null) {
                starts += index to match
            }
        }

        return starts.mapIndexed { position, (startLine, match) ->
            val endLine = starts.getOrNull(position + 1)?.first ?: lines.size
            val id = match.groupValues[1]
            val title = match.groupValues[2].trim()
            val body = lines.subList(startLine, endLine).joinToString("\n").trim()
            GuideSection(
                id = id,
                title = title,
                indexLabel = guideIndexLabel(id, title),
                group = guideGroupFor(id) ?: GuideGroup.REFERENCE,
                markdown = body
            )
        }
    }

    private fun fenceMarker(line: String): Char? {
        val trimmed = line.trimStart()
        return when {
            trimmed.startsWith("```") -> '`'
            trimmed.startsWith("~~~") -> '~'
            else -> null
        }
    }

    private fun guideGroupFor(id: String): GuideGroup? = when (id) {
        "1", "2", "3", "4", "5", "5a", "6", "7", "8", "9", "10", "11", "12" -> GuideGroup.DAILY_USE
        "13", "14", "15", "15a", "15b", "16", "16a", "16b", "17", "18", "19" -> GuideGroup.ADVANCED
        "20", "21", "22", "23", "24", "25", "26", "27" -> GuideGroup.REFERENCE
        else -> null
    }

    private fun guideIndexLabel(id: String, title: String): String = when (id) {
        "5" -> "Mencoret teks"
        "5a" -> "Notasi dan anotasi"
        "19" -> "Escape karakter"
        "20" -> "Yang tidak didukung"
        "24" -> "Contoh catatan"
        "26" -> "Batasan NOTEZ"
        else -> title
    }

    private fun selectableItemBackground(): Int {
        val out = TypedValue()
        theme.resolveAttribute(android.R.attr.selectableItemBackground, out, true)
        return out.resourceId
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (currentSectionIndex >= 0) {
            showIndex()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::markdownPreview.isInitialized) markdownPreview.destroy()
        super.onDestroy()
    }
}
```

### Kenapa page indicator pakai `index + 1 / sections.size`?

Contoh:

```text
2. Teks tebal                             2/27
5a. Notasi dan anotasi                    6/27
```

`5a` adalah label section, sedangkan kanan atas adalah nomor halaman aktual di urutan baca.

---

## A3. Test cepat Panduan Markdown

Setelah build/install:

```text
1. Buka Panduan Markdown.
2. Pastikan muncul daftar isi native, bukan langsung WebView panjang.
3. Tap "2. Teks tebal".
4. Pastikan header: "2. Teks tebal" dan kanan atas "2/27".
5. Tap NEXT → pindah section 3.
6. Tap PREV → balik section 2.
7. Tekan back Android dari section → balik daftar isi.
8. Tekan back Android dari daftar isi → keluar.
9. Tap "16a. Footnote" → footnote tetap interaktif.
10. Tap "24. Contoh catatan" → render sebagai section biasa.
```

Jika ini PASS, lanjut Part B.

---

# PART B — Editor drawer Save file / Save As

## B1. Edit drawer editor layout

### File target

```text
app/src/main/res/layout/activity_editor.xml
```

### Cari di ACODE

Cari:

```xml
<include layout="@layout/include_music_drawer" />
```

Sekitar line:

```text
135
```

### Tujuan perubahan

Tambahkan FILE section tepat di atas music:

```text
FILE ▲
Simpan file
Simpan sebagai...

MUSIK ...
```

### Snippet yang ditempel

> Label: **source XML snippet**.  
> Paste tepat **sebelum** `<include layout="@layout/include_music_drawer" />`.

```xml
            <LinearLayout
                android:id="@+id/file_section"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical">

                <TextView
                    android:id="@+id/file_header"
                    android:layout_width="match_parent"
                    android:layout_height="48dp"
                    android:background="?attr/selectableItemBackground"
                    android:clickable="true"
                    android:focusable="true"
                    android:gravity="center_vertical"
                    android:paddingStart="20dp"
                    android:paddingEnd="20dp"
                    android:text="FILE ▲"
                    android:textColor="?attr/colorOnSurface"
                    android:textSize="15sp" />

                <LinearLayout
                    android:id="@+id/file_submenu"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:paddingStart="12dp"
                    android:paddingEnd="12dp"
                    android:paddingBottom="8dp"
                    android:visibility="visible">

                    <TextView
                        android:id="@+id/file_save"
                        android:layout_width="match_parent"
                        android:layout_height="44dp"
                        android:background="?attr/selectableItemBackground"
                        android:clickable="true"
                        android:focusable="true"
                        android:gravity="center_vertical"
                        android:paddingStart="8dp"
                        android:paddingEnd="8dp"
                        android:text="Simpan file"
                        android:textColor="?attr/colorOnSurface"
                        android:textSize="14sp" />

                    <TextView
                        android:id="@+id/file_save_as"
                        android:layout_width="match_parent"
                        android:layout_height="44dp"
                        android:background="?attr/selectableItemBackground"
                        android:clickable="true"
                        android:focusable="true"
                        android:gravity="center_vertical"
                        android:paddingStart="8dp"
                        android:paddingEnd="8dp"
                        android:text="Simpan sebagai..."
                        android:textColor="?attr/colorPrimary"
                        android:textSize="14sp"
                        android:textStyle="bold" />
                </LinearLayout>
            </LinearLayout>
```

### Alasan

```text
- FILE ada di atas Music sesuai keputusan UI.
- Default expanded karena cuma 2 item dan fitur baru.
- Expand/collapse nanti dikontrol dari EditorActivity.
```

---

## B2. Buat `NoteFileTargetStore.kt`

### File baru

```text
app/src/main/java/com/zaba/notez/NoteFileTargetStore.kt
```

### Tujuan

Menyimpan URI target Save As per note.

Kenapa SharedPreferences?

```text
- Tidak perlu Room migration untuk v0.1.7.
- Scope kecil.
- Target file eksternal bukan source utama note.
- Full Space nanti bisa desain model data sendiri.
```

### Source final

> Label: **source target Kotlin**.  
> Buat file baru dan isi dengan ini.

```kotlin
package com.zaba.notez

import android.content.Context
import android.net.Uri

object NoteFileTargetStore {
    private const val PREF = "note_file_targets"
    private fun key(noteId: Long) = "note_file_uri_$noteId"

    fun get(context: Context, noteId: Long): Uri? {
        if (noteId <= 0) return null
        val raw = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(key(noteId), null)
            ?: return null
        return runCatching { Uri.parse(raw) }.getOrNull()
    }

    fun set(context: Context, noteId: Long, uri: Uri) {
        if (noteId <= 0) return
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(key(noteId), uri.toString())
            .apply()
    }

    fun clear(context: Context, noteId: Long) {
        if (noteId <= 0) return
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .remove(key(noteId))
            .apply()
    }
}
```

---

## B3. Edit `EditorActivity.kt`

### File target

```text
app/src/main/java/com/zaba/notez/EditorActivity.kt
```

Jangan replace seluruh file. Ikuti langkah kecil di bawah.

---

### B3.1 Tambah imports

Di bagian atas file, sekitar line 3–14.

Cari:

```kotlin
import android.content.Context
```

Tambahkan imports ini:

```kotlin
import android.app.Activity
import android.content.Intent
```

Cari area import widget:

```kotlin
import android.widget.TextView
```

Tambahkan:

```kotlin
import android.widget.Toast
```

Hasil import minimal yang penting:

```kotlin
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
...
import android.widget.TextView
import android.widget.Toast
```

Alasan:

```text
Activity.RESULT_OK dipakai oleh Create Document callback.
Intent dipakai untuk ACTION_CREATE_DOCUMENT.
Toast dipakai untuk feedback save sukses/gagal.
```

---

### B3.2 Tambah launcher Create Document

Cari:

```kotlin
private val openMusic = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
    if (::musicDrawer.isInitialized) musicDrawer.onMusicPicked(uris)
}
```

Sekitar line:

```text
67
```

Tempel snippet ini **setelah** `openMusic` block:

```kotlin
    private val createMarkdownFile = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val uri = result.data?.data ?: return@registerForActivityResult

        val flags = result.data?.flags ?: 0
        val takeFlags = flags and (
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        if (takeFlags != 0) {
            runCatching {
                contentResolver.takePersistableUriPermission(uri, takeFlags)
            }
        }

        saveCurrentNoteToExternalFile(uri, rememberTarget = true)
    }
```

Alasan:

```text
Save As butuh Android document picker.
StartActivityForResult dipakai supaya callback bisa baca URI dan permission flags.
```

---

### B3.3 Panggil setup FILE drawer

Cari:

```kotlin
markdownPreview = MarkdownPreviewRenderer(this, bodyWebView)
```

Sekitar line:

```text
93
```

Setelah baris itu, tambahkan:

```kotlin
        setupFileDrawer()
```

Hasilnya:

```kotlin
        markdownPreview = MarkdownPreviewRenderer(this, bodyWebView)
        setupFileDrawer()
```

Alasan:

```text
Setelah layout dan views tersedia, tombol FILE bisa dipasang listener.
```

---

### B3.4 Tambah fungsi `setupFileDrawer()`

Cari fungsi ini:

```kotlin
private fun switchToView() {
```

Sekitar line:

```text
123
```

Tempel fungsi berikut **di atas** `switchToView()`:

```kotlin
    private fun setupFileDrawer() {
        val header = findViewById<TextView>(R.id.file_header)
        val submenu = findViewById<View>(R.id.file_submenu)
        var expanded = true

        fun applyExpandedState() {
            submenu.visibility = if (expanded) View.VISIBLE else View.GONE
            header.text = if (expanded) "FILE ▲" else "FILE ▼"
        }

        header.setOnClickListener {
            expanded = !expanded
            applyExpandedState()
        }

        findViewById<TextView>(R.id.file_save).setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
            saveCurrentFile()
        }

        findViewById<TextView>(R.id.file_save_as).setOnClickListener {
            drawer.closeDrawer(GravityCompat.START)
            launchSaveAs()
        }

        applyExpandedState()
    }
```

Alasan:

```text
- FILE section expandable.
- Default expanded.
- Save/Save As menutup drawer dulu lalu menjalankan action.
```

---

### B3.5 Tambah fungsi Save/Save As

Cari:

```kotlin
private fun updateCounter(bodyLength: Int, suffix: String) {
```

Sekitar line:

```text
168
```

Tempel block ini **di atas** `updateCounter()`:

```kotlin
    private fun saveCurrentFile() {
        val target = NoteFileTargetStore.get(this, noteId)
        if (target == null) {
            launchSaveAs()
        } else {
            saveCurrentNoteToExternalFile(target, rememberTarget = false)
        }
    }

    private fun launchSaveAs() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/markdown"
            putExtra(Intent.EXTRA_TITLE, suggestedMarkdownFileName())
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
        }
        createMarkdownFile.launch(intent)
    }

    private fun saveCurrentNoteToExternalFile(uri: Uri, rememberTarget: Boolean) {
        val body = currentBodySnapshot()
        lifecycleScope.launch(Dispatchers.IO) {
            val result = runCatching {
                contentResolver.openOutputStream(uri, "wt")?.use { output ->
                    output.write(body.toByteArray(Charsets.UTF_8))
                } ?: error("OutputStream kosong")
            }
            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    if (rememberTarget) NoteFileTargetStore.set(this@EditorActivity, noteId, uri)
                    Toast.makeText(this@EditorActivity, "File disimpan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@EditorActivity,
                        "Tidak bisa menyimpan file. Gunakan Simpan sebagai... lagi.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun currentTitleSnapshot(): String =
        if (isEditing) titleEdit.text?.toString().orEmpty() else currentTitle

    private fun currentBodySnapshot(): String =
        if (isEditing) bodyEdit.text?.toString().orEmpty() else currentContent
```

Alasan:

```text
Simpan file:
- pakai target lama kalau ada.
- kalau belum ada, buka Save As.

Simpan sebagai:
- selalu buka Create Document.

Write mode:
- "wt" berarti write + truncate.
- File lama di-overwrite, bukan append.
```

---

### B3.6 Tambah filename suggestion helpers

Masih di `EditorActivity.kt`.

Cari fungsi:

```kotlin
private fun updateCounter(bodyLength: Int, suffix: String) {
```

Tempel block ini **di atas** `updateCounter()` juga, boleh setelah block Save/Save As tadi:

```kotlin
    private fun suggestedMarkdownFileName(): String {
        val title = currentTitleSnapshot().trim()
        val source = if (title.isNotBlank()) title else firstWordsFromBody(currentBodySnapshot())
        val base = sanitizeFileName(source.ifBlank { "NOTEZ note" })
        return if (base.endsWith(".md", ignoreCase = true)) base else "$base.md"
    }

    private fun firstWordsFromBody(body: String): String {
        val firstLine = body.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.isNotBlank() }
            .orEmpty()
            .replace(Regex("^[#>*`_\\-\\[\\]\\(\\)!.]+"), "")
            .trim()

        return firstLine
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(3)
            .joinToString(" ")
    }

    private fun sanitizeFileName(value: String): String {
        val cleaned = value
            .replace(Regex("[\\\\/:*?\"<>|]+"), "-")
            .replace(Regex("\\s+"), " ")
            .trim(' ', '.')
            .take(60)
            .trim(' ', '.')
        return cleaned.ifBlank { "NOTEZ note" }
    }
```

Alasan:

```text
Filename suggestion:
1. pakai title kalau ada.
2. fallback 3 kata awal body.
3. fallback terakhir NOTEZ note.md.
4. bersihkan karakter ilegal file name.
```

Contoh expected:

```text
Title: Meeting NOTEZ
→ Meeting NOTEZ.md

Title kosong, body: Belanja susu telur roti
→ Belanja susu telur.md

Title kosong, body: # Rencana Release
→ Rencana Release.md
```

---

## B4. Test Save file / Save As

Setelah build/install:

```text
1. Buka note dengan title "Meeting NOTEZ".
2. Buka drawer editor.
3. Pastikan FILE ada di atas Music dan default expanded.
4. Tap FILE header → collapse.
5. Tap lagi → expand.
6. Tap Simpan sebagai...
7. Android file picker harus muncul dengan suggestion Meeting NOTEZ.md.
8. Save ke folder pilihan.
9. Buka file dari file manager / editor lain.
10. Isi file harus sama dengan body note.
11. Ubah body note di NOTEZ.
12. Tap Simpan file.
13. File eksternal harus ter-overwrite dengan body terbaru.
```

Test fallback name:

```text
1. Buat note title kosong.
2. Isi body: Belanja susu telur roti.
3. Tap Simpan sebagai...
4. Expected suggestion: Belanja susu telur.md.
```

Test cancel:

```text
1. Tap Simpan sebagai...
2. Cancel picker.
3. App tidak crash.
4. Tidak perlu toast berlebihan.
```

---

# PART C — Build, commit, push

## C1. Cek status

```bash
cd ~/NOTEZ
git status -sb
```

Expected changed files setelah implement penuh:

```text
M app/src/main/java/com/zaba/notez/MarkdownGuideActivity.kt
M app/src/main/res/layout/activity_markdown_guide.xml
M app/src/main/res/layout/activity_editor.xml
M app/src/main/java/com/zaba/notez/EditorActivity.kt
A app/src/main/java/com/zaba/notez/NoteFileTargetStore.kt
```

Kalau lu juga commit RFC/guide docs:

```text
A docs/RFC-NOTEZv0.1.7-PANDUAN-MARKDOWN-SAVE-FILE.md
A docs/GUIDE_NOTEZ_V0.1.7_IMPLEMENTATION_ACODE.md
```

Jangan pakai `git add -A` kalau ada file lain yang belum jelas.

---

## C2. Add file spesifik

```bash
git add app/src/main/java/com/zaba/notez/MarkdownGuideActivity.kt
git add app/src/main/res/layout/activity_markdown_guide.xml
git add app/src/main/res/layout/activity_editor.xml
git add app/src/main/java/com/zaba/notez/EditorActivity.kt
git add app/src/main/java/com/zaba/notez/NoteFileTargetStore.kt
```

Kalau RFC/guide mau ikut:

```bash
git add docs/RFC-NOTEZv0.1.7-PANDUAN-MARKDOWN-SAVE-FILE.md
git add docs/GUIDE_NOTEZ_V0.1.7_IMPLEMENTATION_ACODE.md
```

---

## C3. Commit

Saran commit dipisah kalau mau rapi:

```bash
git commit -m "feat: split markdown guide into section browser"
```

Lalu add/commit Save file:

```bash
git commit -m "feat: add editor save file actions"
```

Kalau mau satu commit saja:

```bash
git commit -m "feat: add NOTEZ v0.1.7 guide sections and file save"
```

---

## C4. Push

```bash
git push origin ZAQIxNOTEZ
```

Setelah push:

```bash
git fetch origin
git diff --name-status origin/main..origin/ZAQIxNOTEZ
```

Expected kira-kira:

```text
M app/src/main/java/com/zaba/notez/MarkdownGuideActivity.kt
M app/src/main/res/layout/activity_markdown_guide.xml
M app/src/main/res/layout/activity_editor.xml
M app/src/main/java/com/zaba/notez/EditorActivity.kt
A app/src/main/java/com/zaba/notez/NoteFileTargetStore.kt
A docs/RFC-NOTEZv0.1.7-PANDUAN-MARKDOWN-SAVE-FILE.md
A docs/GUIDE_NOTEZ_V0.1.7_IMPLEMENTATION_ACODE.md
```

Kalau ada file aneh, jangan merge dulu.

---

# PART D — Checklist UAT v0.1.7

## D1. Panduan Markdown

```text
[ ] Buka Panduan Markdown cepat.
[ ] Index native muncul.
[ ] Intro muncul.
[ ] Group DAILY USE muncul.
[ ] Group KONTEN LANJUTAN muncul.
[ ] Group REFERENSI & CONTOH muncul.
[ ] Tap section 2 → render Teks tebal.
[ ] Indicator 2/27 muncul.
[ ] NEXT pindah section.
[ ] PREV pindah balik.
[ ] Android back dari section kembali index.
[ ] Android back dari index keluar.
[ ] Section 16a Footnote tetap interaktif.
[ ] Section 15 Code block tetap rapi.
[ ] Section 24 render sebagai section biasa.
```

## D2. Save file / Save As

```text
[ ] FILE section muncul di atas Music.
[ ] FILE default expanded.
[ ] FILE bisa collapse/expand.
[ ] Save As title "Meeting NOTEZ" suggest Meeting NOTEZ.md.
[ ] Save As title kosong + body "Belanja susu telur roti" suggest Belanja susu telur.md.
[ ] File .md berhasil dibuat.
[ ] Isi file sama dengan body note.
[ ] Simpan file overwrite target yang sama.
[ ] Cancel picker tidak crash.
[ ] Autosave internal NOTEZ tetap jalan.
[ ] Reading View note user tetap render normal.
```

---

# PART E — Kalau error, kirim ini ke gw

Kalau build gagal, kirim:

```bash
git status -sb
```

Lalu error Gradle/GitHub Actions bagian paling bawah, terutama line yang ada:

```text
e: file.kt:line:column
```

Kalau app install tapi crash, kirim:

```text
1. Langkah yang dilakukan.
2. Screenshot kalau ada.
3. Apakah crash terjadi saat buka Panduan Markdown atau saat Save As.
4. Device/Android version.
```

---

# Final reminder

Untuk v0.1.7 ini:

```text
Fokus: Panduan Markdown + Save file.
Jangan campur renderer shell pool.
Jangan campur Space workspace.
Jangan pakai git add -A kalau status belum bersih.
```
# NOTEZ: preview Markdown cepat (render bertahap + pre-warm)

Paket Langkah A + B dalam satu commit. Ditulis untuk snapshot `NOTEZ.zip` yang Anda unggah (folder `NOTEZ-main`).

## 0. Status pengujian

Sudah diuji di sandbox:
- Sintaks JavaScript shell lolos `node --check`.
- Render per blok menghasilkan HTML yang **identik** dengan render penuh, diuji memakai `markdown-it.umd.min.js` asli proyek. Isi ujinya: heading, list bersarang, code fence berisi baris kosong, tabel, kutipan, garis, HTML block, dan link referensi yang definisinya ada di akhir dokumen.
- Skrip di Langkah 2 berjalan pada snapshot asli dan **berhenti dengan pesan `GAGAL [...]`** bila teks yang dicari tidak cocok (misalnya file sudah Anda ubah).

Belum diuji:
- Kompilasi Kotlin (tidak ada Android SDK di sandbox) dan jalan di HP. Build pertama bisa menampilkan error kecil. Kirim lognya, saya perbaiki.

## 1. Apa yang berubah

| | Sebelum | Sesudah |
|---|---|---|
| Buka preview | WebView dibuat bersama Activity, lalu HTML penuh dimuat (markdown-it 115KB + ±1.000 baris JS + CSS di-parse ulang) | WebView + shell sudah siap dari pool; catatan dikirim lewat `evaluateJavascript` |
| Pindah edit ke lihat | Reload halaman penuh | Satu panggilan JS, tanpa reload |
| Catatan panjang | `md.render` seluruh isi + 8 pass DOM sekaligus | Layar pertama (±60 baris) dulu, sisanya per ±160 baris tiap 30 ms |
| Unduh gambar | Reload halaman penuh | Render ulang lewat JS (tetap dari atas, sama seperti sebelumnya) |

## 2. File yang berubah

| File | Perubahan |
|---|---|
| `markdown/PreviewWebViewPool.kt` | **Baru.** Pool satu WebView + shell (Langkah 1) |
| `markdown/MarkdownPreviewRenderer.kt` | Shell dipisah dari isi catatan, render bertahap, memakai pool (Langkah 2) |
| `EditorActivity.kt` | `WebView` diganti `FrameLayout` penampung (Langkah 2) |
| `MainActivity.kt` | Memanggil pre-warm di `onResume` (Langkah 2) |
| `res/layout/activity_editor.xml` | `<WebView>` diganti `<FrameLayout>` (Langkah 2) |

## 3. Langkah 0: persiapan

```bash
cd ~/NOTEZ                      # sesuaikan dengan lokasi repo Anda
git status                      # harus bersih
git checkout -b perf/preview-bertahap
python3 --version               # kalau belum ada: pkg install python
```

## 4. Langkah 1: buat `PreviewWebViewPool.kt`

Buat file `app/src/main/java/com/zaba/notez/markdown/PreviewWebViewPool.kt` dengan isi berikut.

```kotlin
package com.zaba.notez.markdown

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.MutableContextWrapper
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.zaba.notez.ThemePref
import java.io.ByteArrayInputStream

/**
 * Satu WebView preview yang sudah memuat "shell" Markdown (markdown-it + sanitizer + CSS), dipakai ulang
 * antar catatan supaya membuka preview tidak membayar biaya cold start WebView + parse JS lagi.
 *
 * Semua fungsi publik dipanggil dari main thread.
 *
 * - [warmUpWhenIdle]: dipanggil MainActivity; membuat WebView saat main thread idle.
 * - [acquire] / [release]: dipakai [MarkdownPreviewRenderer] per Activity editor.
 * - Context WebView adalah [MutableContextWrapper]: diarahkan ke Activity saat dipakai, dikembalikan ke
 *   application context saat dilepas, sehingga Activity tidak bocor.
 * - Warna tema ditanam di CSS shell, jadi WebView standby dibuang dan dibuat ulang bila tema berubah.
 */
object PreviewWebViewPool {

    private const val WARM_UP_DELAY_MS = 1500L
    private const val TRIM_DELAY_MS = 60_000L

    internal class Entry(
        val app: Context,
        val wrapper: MutableContextWrapper,
        val webView: WebView,
        val client: ShellClient,
        val themeKey: Int
    ) {
        var leased = false
        var destroyed = false
    }

    class Lease internal constructor(
        internal val entry: Entry,
        internal val pooled: Boolean
    ) {
        val webView: WebView get() = entry.webView
        internal val client: ShellClient get() = entry.client
        val shellReady: Boolean get() = entry.client.shellReady
    }

    /** WebViewClient tunggal milik WebView; perilaku navigasi didelegasikan ke renderer yang sedang memakai. */
    internal class ShellClient : WebViewClient() {
        @Volatile var shellReady = false
        @Volatile var delegate: WebViewClient? = null
        var owner: Entry? = null

        override fun onPageFinished(view: WebView, url: String?) {
            shellReady = true
            delegate?.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
            delegate?.shouldOverrideUrlLoading(view, request) ?: true

        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            val d = delegate
            if (d != null) return d.shouldInterceptRequest(view, request)
            val uri = request.url ?: return blocked()
            val local = uri.scheme.equals("https", ignoreCase = true) &&
                uri.host.equals(MarkdownPreviewRenderer.NOTEZ_HOST, ignoreCase = true)
            if (local || uri.scheme.equals("about", ignoreCase = true)) return null
            return blocked()
        }

        // Renderer WebView bisa dimatikan sistem saat memori sempit, termasuk saat standby di pool.
        // Tanpa penanganan ini aplikasi ikut ditutup paksa.
        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
            delegate?.onRenderProcessGone(view, detail)
            owner?.let { PreviewWebViewPool.rendererGone(it) }
            return true
        }

        private fun blocked() = WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
    }

    private val handler = Handler(Looper.getMainLooper())
    private val trimRunnable = Runnable { trim() }
    private var entry: Entry? = null
    private var warmUpScheduled = false
    private var callbacksRegistered = false

    /** Jadwalkan pembuatan WebView standby setelah main thread idle (hindari mengganggu splash/animasi). */
    fun warmUpWhenIdle(context: Context, delayMs: Long = WARM_UP_DELAY_MS) {
        val app = context.applicationContext
        handler.removeCallbacks(trimRunnable) // app kembali dipakai: batalkan pembuangan terjadwal
        if (warmUpScheduled) return
        warmUpScheduled = true
        handler.postDelayed({
            Looper.myQueue().addIdleHandler {
                warmUpScheduled = false
                warmUp(app)
                false
            }
        }, delayMs)
    }

    fun warmUp(context: Context) {
        val app = context.applicationContext
        registerTrimCallbacks(app)
        val theme = ThemePref.get(app)
        val standby = entry
        if (standby != null) {
            if (standby.leased || standby.themeKey == theme) return
            entry = null
            destroyEntry(standby)
        }
        // Gagal membuat WebView (mis. provider WebView tidak ada) tidak boleh menjatuhkan app saat idle.
        entry = runCatching { createEntry(app, theme) }.getOrNull()
    }

    fun acquire(activity: Activity): Lease {
        val app = activity.applicationContext
        val theme = ThemePref.get(activity)

        val standby = entry
        if (standby != null && !standby.leased && standby.themeKey != theme) {
            entry = null
            destroyEntry(standby)
        }

        val current = entry
        if (current == null) {
            // Jalur dingin (pre-warm belum jalan): sama seperti perilaku lama, hanya WebView-nya kini disimpan.
            val fresh = createEntry(app, theme)
            entry = fresh
            return lease(fresh, activity, pooled = true)
        }
        if (!current.leased) return lease(current, activity, pooled = true)

        // Pool sedang dipakai Activity lain: buat WebView mandiri, dibuang saat release.
        return lease(createEntry(app, theme), activity, pooled = false)
    }

    fun release(lease: Lease) {
        val e = lease.entry
        if (e.destroyed) return
        e.client.delegate = null
        (e.webView.parent as? ViewGroup)?.removeView(e.webView)
        e.wrapper.baseContext = e.app
        if (lease.pooled && entry === e) {
            if (e.client.shellReady) e.webView.evaluateJavascript("notezClear();", null)
            e.leased = false
        } else {
            if (entry === e) entry = null
            destroyEntry(e)
        }
    }

    /** Buang WebView standby (tidak menyentuh yang sedang dipakai). */
    fun trim() {
        val e = entry ?: return
        if (e.leased) return
        entry = null
        destroyEntry(e)
    }

    private fun lease(e: Entry, activity: Activity, pooled: Boolean): Lease {
        e.leased = true
        e.wrapper.baseContext = activity
        return Lease(e, pooled)
    }

    private fun createEntry(app: Context, theme: Int): Entry {
        val wrapper = MutableContextWrapper(app)
        val web = WebView(wrapper)
        configure(web)
        val client = ShellClient()
        web.webViewClient = client
        val e = Entry(app, wrapper, web, client, theme)
        client.owner = e
        MarkdownPreviewRenderer.loadShell(web, app)
        return e
    }

    private fun destroyEntry(e: Entry) {
        if (e.destroyed) return
        e.destroyed = true
        e.client.delegate = null
        e.client.owner = null
        (e.webView.parent as? ViewGroup)?.removeView(e.webView)
        e.wrapper.baseContext = e.app
        e.webView.destroy()
    }

    internal fun rendererGone(e: Entry) {
        if (entry === e) entry = null
        destroyEntry(e)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetJavaScriptEnabled")
    private fun configure(web: WebView) {
        web.setBackgroundColor(Color.TRANSPARENT)
        web.isVerticalScrollBarEnabled = true
        web.isHorizontalScrollBarEnabled = false
        web.overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS

        with(web.settings) {
            javaScriptEnabled = true
            domStorageEnabled = false
            databaseEnabled = false
            cacheMode = WebSettings.LOAD_NO_CACHE
            allowFileAccess = false
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            blockNetworkLoads = true
            safeBrowsingEnabled = true
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
        }
        // WebView dibuat dengan application context, jadi tema Activity tidak ikut menentukan force-dark.
        // Warna preview sudah penuh diatur CSS tema NOTEZ, jadi matikan di Android 10-12 agar konsisten.
        if (Build.VERSION.SDK_INT in 29..32) {
            web.settings.forceDark = WebSettings.FORCE_DARK_OFF
        }
    }

    @Suppress("DEPRECATION")
    private fun registerTrimCallbacks(app: Context) {
        if (callbacksRegistered) return
        callbacksRegistered = true
        app.registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onTrimMemory(level: Int) {
                when {
                    // Tekanan memori nyata: buang sekarang.
                    level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
                        level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> trim()
                    // App tak terlihat: buang bila tidak kembali dalam 1 menit.
                    level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ->
                        handler.postDelayed(trimRunnable, TRIM_DELAY_MS)
                }
            }

            override fun onConfigurationChanged(newConfig: Configuration) = Unit

            override fun onLowMemory() = trim()
        })
    }
}
```

## 5. Langkah 2: jalankan skrip perubahan

Mengedit `MarkdownPreviewRenderer.kt` (±1.700 baris) dengan tangan di HP rawan salah, karena sebagian besar perubahannya memindahkan blok ±1.100 baris. Skrip di bawah melakukannya secara mekanis.

Simpan sebagai `~/apply_notez_render.py` (di luar repo), lalu jalankan:

```bash
python3 ~/apply_notez_render.py ~/NOTEZ
```

Isi skrip:

```python
import re, sys, pathlib

repo = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else '.').resolve()
ROOT = repo / 'app/src/main'
if not (ROOT / 'java/com/zaba/notez/EditorActivity.kt').exists():
    sys.exit(f'Bukan akar repo NOTEZ: {repo}')
KT = ROOT / 'java/com/zaba/notez'
REN = KT / 'markdown/MarkdownPreviewRenderer.kt'


def once(text, old, new, label):
    n = text.count(old)
    if n != 1:
        sys.exit(f'GAGAL [{label}]: ditemukan {n}x, seharusnya 1x')
    return text.replace(old, new)


if not (KT / 'markdown/PreviewWebViewPool.kt').exists():
    sys.exit('Buat dulu markdown/PreviewWebViewPool.kt (Langkah 1 di panduan).')

# ---------------------------------------------------------------- renderer
src = REN.read_text()
lines = src.split('\n')

# 1) JS: ganti blok render sekali-jalan dengan notezRender/notezClear bertahap
start = next(i for i, l in enumerate(lines) if l.strip() == "var preview = document.getElementById('preview');")
end = next(i for i, l in enumerate(lines) if i > start and l.strip() == 'addImagePlaceholderHandlers();')
JS_NEW = r"""                  var preview = document.getElementById('preview');
                  var job = null;
                  var FIRST_BATCH_LINES = 60;
                  var NEXT_BATCH_LINES = 160;

                  // Pecah token di batas blok level-0 supaya code fence, tabel, dan list tidak terpotong.
                  // Link referensi dan footnote aman: seluruh dokumen di-parse sekali dengan env yang sama.
                  function splitTopLevel(tokens) {
                    var blocks = [];
                    var cur = [];
                    for (var i = 0; i < tokens.length; i++) {
                      cur.push(tokens[i]);
                      if (tokens[i].level === 0 && tokens[i].nesting <= 0) {
                        blocks.push(cur);
                        cur = [];
                      }
                    }
                    if (cur.length) blocks.push(cur);
                    return blocks;
                  }

                  function blockLines(block) {
                    var map = block[0] && block[0].map;
                    return map ? Math.max(1, map[1] - map[0]) : 1;
                  }

                  function renderBatch(j, maxLines) {
                    var html = '';
                    var count = 0;
                    while (j.index < j.blocks.length && (count === 0 || count < maxLines)) {
                      var block = j.blocks[j.index++];
                      count += blockLines(block);
                      html += md.renderer.render(block, md.options, j.env);
                    }
                    var last = j.index >= j.blocks.length;

                    var template = document.createElement('template');
                    template.innerHTML = html;
                    sanitizeRenderedDom(template.content);

                    // Semua pass bekerja lewat `preview`; arahkan sementara ke fragment batch ini.
                    var realPreview = preview;
                    preview = template.content;
                    try {
                      addHeadingAnchors(j.seenHeadings);
                      wrapTables();
                      enhanceTaskLists();
                      enhanceCodeBlocks();
                      enhanceCallouts();
                      enhanceFootnoteRefs(j.seenFootnoteRefs);
                      if (last) appendFootnotes(j.env);
                      hardenLinks();
                      addImagePlaceholderHandlers();
                    } finally {
                      preview = realPreview;
                    }
                    preview.appendChild(template.content);
                  }

                  function scheduleNext(j) {
                    if (j.index >= j.blocks.length) return;
                    j.timer = setTimeout(function () {
                      if (job !== j) return;
                      renderBatch(j, NEXT_BATCH_LINES);
                      scheduleNext(j);
                    }, 30);
                  }

                  function flushAll() {
                    if (job && job.index < job.blocks.length) {
                      clearTimeout(job.timer);
                      renderBatch(job, Infinity);
                    }
                  }

                  // Tap link #anchor ke bagian yang belum dirender: selesaikan sisanya dulu.
                  document.addEventListener('click', function (event) {
                    var a = event.target && event.target.closest ? event.target.closest('a[href^="#"]') : null;
                    if (a) flushAll();
                  }, true);

                  window.notezClear = function () {
                    if (job) { clearTimeout(job.timer); job = null; }
                    while (preview.firstChild) preview.removeChild(preview.firstChild);
                    window.scrollTo(0, 0);
                  };

                  window.notezRender = function (src, base, cached) {
                    if (job) clearTimeout(job.timer);
                    source = src;
                    githubRawBase = base;
                    cachedImages = cached;

                    var extraction = extractFootnotes(source);
                    var prepared = preprocessDefinitionLists(extraction.markdown);
                    var env = {
                      footnotes: extraction.definitions,
                      footnoteOrder: [],
                      footnoteNumbers: Object.create(null),
                      footnoteRefCounts: Object.create(null)
                    };
                    job = {
                      env: env,
                      blocks: splitTopLevel(md.parse(prepared, env)),
                      index: 0,
                      seenHeadings: Object.create(null),
                      seenFootnoteRefs: Object.create(null),
                      timer: 0
                    };
                    while (preview.firstChild) preview.removeChild(preview.firstChild);
                    window.scrollTo(0, 0);
                    renderBatch(job, FIRST_BATCH_LINES);
                    scheduleNext(job);
                  };"""
lines[start:end + 1] = JS_NEW.split('\n')
src = '\n'.join(lines)

# 2) JS: state penomoran dibawa dari luar
src = once(src,
           "                  function addHeadingAnchors() {\n                    var seen = Object.create(null);\n",
           "                  function addHeadingAnchors(seen) {\n", 'addHeadingAnchors')
src = once(src,
           "                  function enhanceFootnoteRefs() {\n                    var seen = Object.create(null);\n",
           "                  function enhanceFootnoteRefs(seen) {\n", 'enhanceFootnoteRefs')

# 3) JS: variabel awal tidak lagi di-inline per catatan
src = once(src, "                  var source = $markdownJson;\n", "                  var source = '';\n", 'source')
src = once(src, "                  var githubRawBase = $githubRawBaseJson;\n", "                  var githubRawBase = '';\n", 'githubRawBase')
src = once(src, "                  var cachedImages = $cachedImagesJson;\n", "                  var cachedImages = {};\n", 'cachedImages')

# 4) pindahkan buildHtml + css ke companion sebagai buildShellHtml
b_start = src.index('    private fun buildHtml(markdown: String): String {')
b_end = src.index('    private fun cachedImagesJson(')
moved = src[b_start:b_end].rstrip('\n') + '\n'
src = src[:b_start] + src[b_end:]

moved = once(moved,
             "    private fun buildHtml(markdown: String): String {\n"
             "        val colors = PreviewColors.from(activity)\n"
             "        val markdownJson = JSONObject.quote(markdown)\n"
             "        val githubRawBaseJson = JSONObject.quote(githubRawBase(markdown))\n"
             "        val cachedImagesJson = cachedImagesJson(markdown)\n",
             "    private fun buildShellHtml(context: Context): String {\n"
             "        val colors = PreviewColors.from(context)\n"
             "        val markdownItJs = loadMarkdownItJs(context)\n", 'buildShellHtml header')

# 5) ganti kepala kelas (import + konstruktor + configureWebView lama)
h_start = src.index('package com.zaba.notez.markdown')
h_end = src.index('    private fun cachedImagesJson(')
HEAD = r'''package com.zaba.notez.markdown

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.zaba.notez.ThemePref
import java.io.ByteArrayInputStream
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.json.JSONObject

/**
 * Local-only Markdown reading view.
 *
 * Security contract:
 * - markdown-it is loaded from APK assets and inlined into the HTML shell;
 * - no CDN/external script/style/font;
 * - raw HTML is enabled only through a small sanitized allowlist;
 * - no native JavaScript bridge;
 * - same-document #anchor links are allowed for local table-of-contents jumps;
 * - WebView navigation to remote URLs is blocked and opened externally instead;
 * - remote images are placeholders by default;
 * - user-triggered image downloads happen natively, then cached files are served back to WebView
 *   through https://notez.local/cache/image/... only.
 *
 * Performa: WebView + "shell" HTML (markdown-it, sanitizer, CSS) dimuat SEKALI dan dipakai ulang lewat
 * [PreviewWebViewPool]. Isi catatan dikirim lewat evaluateJavascript(notezRender(...)) dan dirender
 * bertahap per blok, jadi tidak ada reload halaman per catatan.
 */
class MarkdownPreviewRenderer(
    private val activity: Activity,
    private val host: FrameLayout
) {
    private val lease: PreviewWebViewPool.Lease = PreviewWebViewPool.acquire(activity)
    private val webView: WebView = lease.webView
    private val remoteImageCache = RemoteImageCache(activity)
    private val imageExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    @Volatile private var destroyed = false
    private var rendererDead = false
    private var currentMarkdown: String = ""
    private var pendingMarkdown: String? = null

    init {
        lease.client.delegate = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url ?: return true
                if (isImageLoadRequest(uri)) {
                    confirmAndLoadImage(uri)
                    return true
                }
                if (isLocalPreviewUrl(uri)) return false
                return openExternalOrBlock(uri)
            }

            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val uri = request.url ?: return blockedResponse()
                if (RemoteImageCache.isCacheUri(uri)) {
                    return remoteImageCache.responseFor(uri) ?: blockedResponse()
                }
                if (isLocalPreviewUrl(uri)) return null
                if (uri.scheme.equals("about", ignoreCase = true)) return null
                return blockedResponse()
            }

            // Shell selesai dimuat (hanya terjadi jika pre-warm belum selesai saat editor dibuka).
            override fun onPageFinished(view: WebView, url: String?) {
                if (destroyed || rendererDead) return
                pendingMarkdown?.let { pendingMarkdown = null; pushToShell(it) }
            }

            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                rendererDead = true
                if (!destroyed && !activity.isFinishing) {
                    Toast.makeText(activity, "Preview berhenti (memori habis). Buka ulang catatan.", Toast.LENGTH_LONG).show()
                }
                return true
            }
        }
        host.addView(
            webView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun render(markdown: String) {
        if (destroyed || rendererDead) return
        currentMarkdown = markdown
        if (lease.shellReady) pushToShell(markdown) else pendingMarkdown = markdown
    }

    fun destroy() {
        destroyed = true
        imageExecutor.shutdownNow()
        PreviewWebViewPool.release(lease)
    }

    private fun pushToShell(markdown: String) {
        val js = "notezRender(" +
            JSONObject.quote(markdown) + "," +
            JSONObject.quote(githubRawBase(markdown)) + "," +
            cachedImagesJson(markdown) + ");"
        webView.evaluateJavascript(js, null)
    }

'''
src = src[:h_start] + HEAD + src[h_end:]

# 6) PreviewColors & colorResource -> Context
src = once(src, "fun from(activity: Activity): PreviewColors {", "fun from(context: Context): PreviewColors {", 'PreviewColors.from')
src = once(src, "ThemePref.optionOf(ThemePref.get(activity))", "ThemePref.optionOf(ThemePref.get(context))", 'ThemePref.get')
for name in ('surfaceColorRes', 'textColorRes', 'secondaryColorRes', 'accentColorRes', 'dangerColorRes'):
    src = once(src, f"activity.colorResource(option.{name})", f"context.colorResource(option.{name})", name)
src = once(src, "private fun Activity.colorResource(colorRes: Int): String =", "private fun Context.colorResource(colorRes: Int): String =", 'colorResource')

# 7) companion: publik (internal) + shell loader + blok yang dipindah
src = once(src, "    private companion object {\n        private const val NOTEZ_HOST = \"notez.local\"\n        private const val NOTEZ_BASE_URL = \"https://notez.local/\"\n",
           "    companion object {\n        internal const val NOTEZ_HOST = \"notez.local\"\n        internal const val NOTEZ_BASE_URL = \"https://notez.local/\"\n", 'companion head')

anchor = '        private val GITHUB_REPO_PATTERN = Regex('
a = src.index(anchor)
line_end = src.index('\n', a) + 1
assert src[line_end:].startswith('    }\n}\n'), 'ujung companion tidak seperti yang diduga'
COMPANION_ADD = '''
        @Volatile private var markdownItJsCache: String? = null

        private fun loadMarkdownItJs(context: Context): String =
            markdownItJsCache ?: context.applicationContext.assets
                .open("markdown/markdown-it.umd.min.js")
                .bufferedReader()
                .use { it.readText() }
                .also { markdownItJsCache = it }

        /** Memuat shell statis (CSS + markdown-it + sanitizer). Dipanggil sekali per WebView oleh [PreviewWebViewPool]. */
        internal fun loadShell(webView: WebView, context: Context) {
            webView.loadDataWithBaseURL(NOTEZ_BASE_URL, buildShellHtml(context), "text/html", "UTF-8", null)
        }

'''
src = src[:line_end] + COMPANION_ADD + moved + src[line_end:]

REN.write_text(src)
print('renderer OK')

# ---------------------------------------------------------------- EditorActivity
ed = (KT / 'EditorActivity.kt').read_text()
ed = once(ed, "import android.webkit.WebView\n", "import android.widget.FrameLayout\n", 'ed import')
ed = once(ed, "private lateinit var bodyWebView: WebView", "private lateinit var bodyWebHost: FrameLayout", 'ed decl')
ed = once(ed, "bodyWebView = findViewById(R.id.view_body_web)", "bodyWebHost = findViewById(R.id.view_body_web)", 'ed find')
ed = once(ed, "MarkdownPreviewRenderer(this, bodyWebView)", "MarkdownPreviewRenderer(this, bodyWebHost)", 'ed ctor')
ed = once(ed, "bodyWebView.visibility =", "bodyWebHost.visibility =", 'ed vis')
assert 'bodyWebView' not in ed
(KT / 'EditorActivity.kt').write_text(ed)
print('editor OK')

# ---------------------------------------------------------------- MainActivity
ma = (KT / 'MainActivity.kt').read_text()
m = re.search(r'^import com\.zaba\.notez\.music\.MusicDrawerController\n', ma, re.M)
assert m, 'import MusicDrawerController tidak ditemukan'
ma = ma[:m.start()] + 'import com.zaba.notez.markdown.PreviewWebViewPool\n' + ma[m.start():]
ma = once(ma,
          "        observe()\n        lifecycleScope.launch(Dispatchers.IO) {\n            if (BackupHelper.autoBackupIfDue(",
          "        observe()\n        PreviewWebViewPool.warmUpWhenIdle(applicationContext)\n        lifecycleScope.launch(Dispatchers.IO) {\n            if (BackupHelper.autoBackupIfDue(", 'ma onResume')
(KT / 'MainActivity.kt').write_text(ma)
print('main OK')

# ---------------------------------------------------------------- layout
lay_p = ROOT / 'res/layout/activity_editor.xml'
lay = lay_p.read_text()
old = '''            <android.webkit.WebView
                android:id="@+id/view_body_web"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:visibility="gone"
                android:background="@android:color/transparent"
                android:overScrollMode="ifContentScrolls"
                android:scrollbars="vertical" />
'''
new = '''            <!-- Wadah untuk WebView preview dari PreviewWebViewPool (dipasang lewat kode). -->
            <FrameLayout
                android:id="@+id/view_body_web"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:visibility="gone" />
'''
lay = once(lay, old, new, 'layout')
lay_p.write_text(lay)
print('layout OK')
```

Yang dikerjakan skrip:

| Edit | Tujuan |
|---|---|
| Ganti `init`, `render()`, `destroy()` renderer; tambah `pushToShell()` | Renderer meminjam WebView dari pool, tidak membuat sendiri |
| `buildHtml(markdown)` jadi `buildShellHtml(context)` di `companion object` | Pool bisa memuat shell tanpa instance renderer |
| Tiga variabel JS (`source`, `githubRawBase`, `cachedImages`) tidak lagi di-inline | HTML shell sama untuk semua catatan |
| Blok render JS diganti `notezRender` / `notezClear` + batching | Render bertahap, pecah di batas blok level-0 |
| `addHeadingAnchors(seen)` dan `enhanceFootnoteRefs(seen)` | Penomoran id/footnote lanjut antar batch |
| `PreviewColors.from(Context)` dan `Context.colorResource` | Warna tema bisa dibaca tanpa Activity |
| `EditorActivity`, `MainActivity`, `activity_editor.xml` | Penampung WebView + pemicu pre-warm |

Jika skrip menampilkan `GAGAL [...]`, kembalikan lalu kirim pesannya ke saya:

```bash
git checkout -- .
```

## 6. Langkah 3: build dan uji

Ukur di build mirip release, bukan debug. Jika belum ada, tambahkan di `buildTypes` pada `app/build.gradle.kts`:

```kotlin
create("benchmark") {
    initWith(getByName("release"))
    signingConfig = signingConfigs.getByName("debug")
    isDebuggable = false
    matchingFallbacks += listOf("release")
}
```

Lalu `gradle assembleBenchmark` (atau `./gradlew assembleBenchmark` bila ada wrapper).

Daftar uji:

- [ ] Aplikasi terbuka normal dan splash tidak tersendat.
- [ ] Buka catatan 1–2 paragraf: tampil langsung, hasil sama seperti sebelumnya.
- [ ] Buka catatan panjang (tempel teks panjang berulang): layar pertama cepat, sisanya menyusul.
- [ ] Buka editor **langsung setelah aplikasi dibuka** (sebelum pre-warm selesai): tetap tampil, hanya belum secepat biasanya.
- [ ] Buka catatan kedua, ketiga: isi catatan sebelumnya tidak tersisa.
- [ ] Pindah edit ke lihat berulang cepat: tidak ada isi dobel atau campur.
- [ ] Dua heading berjudul sama di batch berbeda: id `judul` dan `judul-2`.
- [ ] Footnote: nomor berurutan, bagian footnote di paling bawah, tap `[^1]` melompat benar.
- [ ] Tap link daftar isi ke bagian paling bawah catatan panjang: langsung melompat.
- [ ] Tabel, code block berwarna, callout, task list, raw HTML: tampilan sama.
- [ ] Link referensi `[teks][ref]` dengan `[ref]: url` di bawah: tetap jadi link.
- [ ] Placeholder gambar remote: dialog muncul sekali per tap; setelah diunduh gambar tampil.
- [ ] Ganti tema di pengaturan lalu buka preview: warna preview mengikuti tema baru.
- [ ] Putar layar atau tutup lalu buka editor berkali-kali: tidak ada crash.

## 7. Langkah 4: commit dan push

```bash
git add -A
git commit -m "perf(preview): shell WebView dimuat sekali, render bertahap, pre-warm pool"
git push -u origin perf/preview-bertahap     # atau nama branch Anda
```

## 8. Perilaku yang perlu diketahui

- **Pre-warm** berjalan 1,5 detik setelah `MainActivity` tampil, saat main thread idle. Jika gagal membuat WebView, aplikasi tidak crash; editor memakai jalur lama (membuat WebView saat dibuka).
- **Pool dibuang** saat tema berubah (warna ditanam di CSS shell), saat memori sempit (`TRIM_MEMORY_BACKGROUND` ke atas dan `RUNNING_CRITICAL` langsung; `UI_HIDDEN` setelah 1 menit), dan saat proses renderer WebView dimatikan sistem. `MainActivity.onResume` membuatnya lagi.
- **WebView standby memakai RAM**, perkiraan umum puluhan MB. Di RAM 4GB inilah alasan pool dibuang saat memori sempit. Angka pastinya perlu Anda ukur di HP.
- **Editor kedua** yang terbuka saat pool sedang dipakai mendapat WebView mandiri yang dibuang saat ditutup.
- **Tidak ada kebocoran Activity**: konteks WebView adalah `MutableContextWrapper` yang dikembalikan ke application context saat dilepas, dan delegasi client dikosongkan.
- **Force dark dimatikan di Android 10–12.** Ini perubahan yang disengaja, karena WebView dibuat dengan application context sehingga tema Activity tidak lagi menentukannya, dan warna preview sudah penuh diatur CSS tema NOTEZ. Di Android 13+ tidak ada efeknya.
- **Timer batch** hanya berjalan wajar saat WebView terlihat. Aman karena `applyMode` menampilkan penampung sebelum `render()`.
- **Tap link `#anchor`** menyelesaikan sisa batch dulu agar target lompatan sudah ada.

## 9. Rollback

```bash
git checkout main && git branch -D perf/preview-bertahap
```
