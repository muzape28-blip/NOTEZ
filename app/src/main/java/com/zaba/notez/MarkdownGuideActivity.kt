package com.zaba.notez

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.zaba.notez.markdown.MarkdownPreviewRenderer

class MarkdownGuideActivity : AppCompatActivity() {

    private lateinit var markdownPreview: MarkdownPreviewRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_guide)

        findViewById<android.widget.ImageButton>(R.id.guide_back).setOnClickListener { finish() }

        val webView = findViewById<WebView>(R.id.guide_body_web)
        markdownPreview = MarkdownPreviewRenderer(this, webView)
        val guideMarkdown = assets.open("help/markdown_guide.md").bufferedReader().use { it.readText() }
        markdownPreview.render(guideMarkdown)
    }

    override fun onDestroy() {
        if (::markdownPreview.isInitialized) markdownPreview.destroy()
        super.onDestroy()
    }
}
