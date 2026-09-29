package com.zaba.notez

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.zaba.notez.markdown.MarkdownPreviewRenderer

class AboutNotezActivity : AppCompatActivity() {

    private lateinit var markdownPreview: MarkdownPreviewRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_notez)

        findViewById<android.widget.ImageButton>(R.id.about_back).setOnClickListener { finish() }

        val webView = findViewById<WebView>(R.id.about_body_web)
        markdownPreview = MarkdownPreviewRenderer(this, webView)
        val aboutMarkdown = assets.open("help/about_notez.md").bufferedReader().use { it.readText() }
        markdownPreview.render(aboutMarkdown)
    }

    override fun onDestroy() {
        if (::markdownPreview.isInitialized) markdownPreview.destroy()
        super.onDestroy()
    }
}
