package com.zaba.notez

import android.graphics.Typeface
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
                setTextColor(UiStyle.primary(this@MarkdownGuideActivity))
                setPadding(dp(16), dp(20), dp(16), dp(8))
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                letterSpacing = 0.08f
            }
            indexList.addView(groupHeader)

            groupSections.forEach { section ->
                val rowView = layoutInflater.inflate(R.layout.item_guide_toc, indexList, false)
                val titleView = rowView.findViewById<TextView>(R.id.toc_title)
                val summaryView = rowView.findViewById<TextView>(R.id.toc_summary)

                titleView.text = section.title
                summaryView.text = section.summary
                rowView.background = UiStyle.roundedBackground(
                    this,
                    fillAlpha = 58,
                    strokeAlpha = 42,
                    radiusDp = 14f
                )
                val rippleAttrs = obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
                rowView.foreground = rippleAttrs.getDrawable(0)
                rippleAttrs.recycle()
                (rowView.layoutParams as? LinearLayout.LayoutParams)?.apply {
                    setMargins(dp(8), 0, dp(8), dp(6))
                }

                rowView.setOnClickListener {
                    openSectionById(section.id)
                }

                indexList.addView(rowView)
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

    private fun openSectionById(id: String) {
        val idx = sections.indexOfFirst { it.id == id }
        if (idx >= 0) {
            openSection(idx)
        }
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

        markdownPreview.render(section.markdown)
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

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        if (::markdownPreview.isInitialized) markdownPreview.destroy()
        super.onDestroy()
    }
}
