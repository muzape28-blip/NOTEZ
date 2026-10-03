package com.zaba.notez

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zaba.notez.markdown.MarkdownGuideParser
import com.zaba.notez.markdown.MarkdownGuideSection
import com.zaba.notez.markdown.MarkdownPreviewRenderer

class MarkdownGuideActivity : AppCompatActivity() {

    private lateinit var markdownPreview: MarkdownPreviewRenderer
    private lateinit var indexContainer: View
    private lateinit var readerContainer: View
    private lateinit var indexList: LinearLayout
    private lateinit var guideTitle: TextView
    private lateinit var pageIndicator: TextView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button

    private var sections: List<MarkdownGuideSection> = emptyList()
    private var currentSectionIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_guide)

        guideTitle = findViewById(R.id.guide_title)
        pageIndicator = findViewById(R.id.guide_page_indicator)
        indexContainer = findViewById(R.id.index_container)
        readerContainer = findViewById(R.id.reader_container)
        indexList = findViewById(R.id.index_list)
        btnPrev = findViewById(R.id.btn_prev_section)
        btnNext = findViewById(R.id.btn_next_section)

        findViewById<ImageButton>(R.id.guide_back).setOnClickListener {
            handleBackNavigation()
        }

        val webView = findViewById<WebView>(R.id.guide_body_web)
        markdownPreview = MarkdownPreviewRenderer(this, webView)

        val guideMarkdown = assets.open("help/markdown_guide.md").bufferedReader().use { it.readText() }
        sections = MarkdownGuideParser.parse(guideMarkdown)

        renderIndex()

        btnPrev.setOnClickListener {
            if (currentSectionIndex > 0) {
                openSection(currentSectionIndex - 1)
            }
        }

        btnNext.setOnClickListener {
            if (currentSectionIndex < sections.lastIndex) {
                openSection(currentSectionIndex + 1)
            }
        }
    }

    private fun renderIndex() {
        indexList.removeAllViews()
        val grouped = sections.groupBy { it.group }

        grouped.forEach { (groupName, groupSections) ->
            val groupHeader = TextView(this).apply {
                text = groupName
                setTextColor(getColor(R.color.github_accent))
                setPadding(8, 24, 8, 12)
                textSize = 13f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            indexList.addView(groupHeader)

            groupSections.forEach { section ->
                val cardView = layoutInflater.inflate(R.layout.item_note, indexList, false)
                val titleView = cardView.findViewById<TextView>(R.id.item_title)
                val bodyView = cardView.findViewById<TextView>(R.id.item_preview)
                val timeView = cardView.findViewById<TextView>(R.id.item_meta)

                titleView.text = section.title
                bodyView.text = section.content.take(120).replace("\n", " ") + "..."
                timeView.text = "Bagian ${section.index + 1} dari ${sections.size}"

                cardView.setOnClickListener {
                    openSection(section.index)
                }

                indexList.addView(cardView)
            }
        }

        showIndexView()
    }

    private fun showIndexView() {
        currentSectionIndex = -1
        indexContainer.visibility = View.VISIBLE
        readerContainer.visibility = View.GONE
        pageIndicator.visibility = View.GONE
        guideTitle.text = getString(R.string.markdown_guide_title)
    }

    private fun openSection(index: Int) {
        if (index !in sections.indices) return
        currentSectionIndex = index
        val section = sections[index]

        guideTitle.text = section.title
        pageIndicator.text = "${index + 1}/${sections.size}"
        pageIndicator.visibility = View.VISIBLE

        indexContainer.visibility = View.GONE
        readerContainer.visibility = View.VISIBLE

        btnPrev.isEnabled = index > 0
        btnNext.isEnabled = index < sections.lastIndex

        markdownPreview.render("## ${section.title}\n\n${section.content}")
    }

    private fun handleBackNavigation() {
        if (readerContainer.visibility == View.VISIBLE) {
            showIndexView()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        handleBackNavigation()
    }

    override fun onDestroy() {
        if (::markdownPreview.isInitialized) markdownPreview.destroy()
        super.onDestroy()
    }
}
