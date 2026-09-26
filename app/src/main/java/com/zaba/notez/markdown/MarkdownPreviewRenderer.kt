package com.zaba.notez.markdown

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.zaba.notez.R
import java.io.ByteArrayInputStream
import java.util.Locale
import org.json.JSONObject

/**
 * Local-only Markdown reading view.
 *
 * Security contract:
 * - markdown-it is loaded from APK assets and inlined into the HTML shell;
 * - no CDN/external script/style/font;
 * - raw HTML is disabled by markdown-it (`html: false`);
 * - no native JavaScript bridge;
 * - WebView navigation to remote URLs is blocked and opened externally instead;
 * - remote images are rendered as placeholders, not fetched in WebView.
 */
class MarkdownPreviewRenderer(
    private val activity: Activity,
    private val webView: WebView
) {
    private val markdownItJs: String by lazy {
        activity.assets.open("markdown/markdown-it.umd.min.js").bufferedReader().use { it.readText() }
    }

    init {
        configureWebView()
    }

    fun render(markdown: String) {
        webView.loadDataWithBaseURL(
            NOTEZ_BASE_URL,
            buildHtml(markdown),
            "text/html",
            "UTF-8",
            null
        )
    }

    fun destroy() {
        webView.stopLoading()
        webView.loadUrl("about:blank")
        webView.destroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = false

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = false
            databaseEnabled = false
            cacheMode = WebSettings.LOAD_NO_CACHE
            allowFileAccess = false
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            blockNetworkLoads = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                safeBrowsingEnabled = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url ?: return true
                if (isLocalPreviewUrl(uri)) return false
                return openExternalOrBlock(uri)
            }

            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val uri = request.url ?: return blockedResponse()
                if (isLocalPreviewUrl(uri)) return null
                if (uri.scheme.equals("about", ignoreCase = true)) return null
                return blockedResponse()
            }
        }
    }

    private fun buildHtml(markdown: String): String {
        val colors = PreviewColors.from(activity)
        val markdownJson = JSONObject.quote(markdown)
        return """
            <!doctype html>
            <html>
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes">
              <style>${css(colors)}</style>
            </head>
            <body>
              <main id="preview" class="markdown-body"></main>
              <script>$markdownItJs</script>
              <script>
                (function () {
                  'use strict';

                  var source = $markdownJson;
                  var safeExternalLink = /^(https?:|mailto:|tel:)/i;
                  var md = window.markdownit({
                    html: false,
                    linkify: true,
                    typographer: false,
                    breaks: false
                  }).enable(['table', 'strikethrough']);

                  function escapeHtml(value) {
                    return String(value || '').replace(/[&<>"']/g, function (ch) {
                      return ({
                        '&': '&amp;',
                        '<': '&lt;',
                        '>': '&gt;',
                        '"': '&quot;',
                        "'": '&#39;'
                      })[ch];
                    });
                  }

                  function safeHref(value) {
                    var href = String(value || '').trim();
                    return safeExternalLink.test(href) ? href : '';
                  }

                  var defaultLinkOpen = md.renderer.rules.link_open || function (tokens, idx, options, env, self) {
                    return self.renderToken(tokens, idx, options);
                  };

                  md.renderer.rules.link_open = function (tokens, idx, options, env, self) {
                    var href = tokens[idx].attrGet('href') || '';
                    if (!safeHref(href)) {
                      tokens[idx].attrSet('href', '#');
                      tokens[idx].attrJoin('class', 'notez-unsafe-link');
                    } else {
                      tokens[idx].attrSet('target', '_self');
                      tokens[idx].attrSet('rel', 'nofollow noopener noreferrer');
                    }
                    return defaultLinkOpen(tokens, idx, options, env, self);
                  };

                  md.renderer.rules.image = function (tokens, idx, options, env, self) {
                    var token = tokens[idx];
                    var rawSrc = token.attrGet('src') || '';
                    var href = safeHref(rawSrc);
                    var alt = token.content || '';
                    if (!alt && token.children) {
                      alt = self.renderInlineAsText(token.children, options, env);
                    }
                    var sourceText = escapeHtml(rawSrc || '(no source)');
                    var altText = escapeHtml(alt || 'image');
                    var open = href ? '<a class="notez-image-placeholder" href="' + escapeHtml(href) + '">' : '<div class="notez-image-placeholder">';
                    var close = href ? '</a>' : '</div>';
                    return open +
                      '<span class="notez-image-kicker">Image</span>' +
                      '<strong>' + altText + '</strong>' +
                      '<code>' + sourceText + '</code>' +
                      '<small>Remote images stay as links because NOTEZ has no INTERNET permission.</small>' +
                      close;
                  };

                  var preview = document.getElementById('preview');
                  preview.innerHTML = md.render(source);
                  wrapTables();
                  enhanceTaskLists();
                  enhanceCallouts();
                  hardenLinks();

                  function wrapTables() {
                    Array.prototype.slice.call(preview.querySelectorAll('table')).forEach(function (table) {
                      if (table.parentNode && table.parentNode.classList && table.parentNode.classList.contains('notez-table-wrap')) return;
                      var wrapper = document.createElement('div');
                      wrapper.className = 'notez-table-wrap';
                      table.parentNode.insertBefore(wrapper, table);
                      wrapper.appendChild(table);
                    });
                  }

                  function enhanceTaskLists() {
                    Array.prototype.slice.call(preview.querySelectorAll('li')).forEach(function (li) {
                      var first = li.firstChild;
                      if (!first || first.nodeType !== Node.TEXT_NODE) return;
                      var match = first.nodeValue.match(/^\[( |x|X)\]\s+/);
                      if (!match) return;
                      first.nodeValue = first.nodeValue.slice(match[0].length);
                      var checkbox = document.createElement('input');
                      checkbox.type = 'checkbox';
                      checkbox.disabled = true;
                      checkbox.checked = match[1].toLowerCase() === 'x';
                      checkbox.setAttribute('aria-hidden', 'true');
                      li.classList.add('task-list-item');
                      li.insertBefore(checkbox, li.firstChild);
                    });
                  }

                  function enhanceCallouts() {
                    Array.prototype.slice.call(preview.querySelectorAll('blockquote')).forEach(function (block) {
                      var first = block.querySelector('p:first-child');
                      if (!first) return;
                      var match = (first.textContent || '').trim().match(/^\[!(NOTE|TIP|IMPORTANT|WARNING|CAUTION)\]/i);
                      if (!match) return;
                      var type = match[1].toLowerCase();
                      block.classList.add('notez-callout', 'notez-callout-' + type);
                      first.innerHTML = first.innerHTML.replace(/^\s*\[!(NOTE|TIP|IMPORTANT|WARNING|CAUTION)\]\s*(<br\s*\/?>)?\s*/i, '');
                      var title = document.createElement('div');
                      title.className = 'notez-callout-title';
                      title.textContent = match[1].toUpperCase();
                      block.insertBefore(title, block.firstChild);
                    });
                  }

                  function hardenLinks() {
                    Array.prototype.slice.call(preview.querySelectorAll('a[href]')).forEach(function (a) {
                      var href = a.getAttribute('href') || '';
                      if (!safeHref(href)) {
                        a.removeAttribute('href');
                        a.classList.add('notez-unsafe-link');
                      }
                    });
                  }
                })();
              </script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun css(colors: PreviewColors): String = """
        :root {
          --notez-text: ${colors.text};
          --notez-muted: ${colors.muted};
          --notez-surface: ${colors.surface};
          --notez-accent: ${colors.accent};
          --notez-danger: ${colors.danger};
          --notez-border: rgba(255, 255, 255, 0.16);
          --notez-soft: rgba(255, 255, 255, 0.06);
        }
        html, body {
          margin: 0;
          padding: 0;
          background: transparent;
          color: var(--notez-text);
          font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
          font-size: 16px;
          line-height: 1.55;
          overflow-wrap: anywhere;
        }
        body { padding: 0 0 24px; }
        .markdown-body > :first-child { margin-top: 0; }
        .markdown-body > :last-child { margin-bottom: 0; }
        h1, h2, h3, h4, h5, h6 {
          line-height: 1.25;
          margin: 1.25em 0 .55em;
          font-weight: 700;
          color: var(--notez-text);
        }
        h1 { font-size: 1.75em; padding-bottom: .3em; border-bottom: 1px solid var(--notez-border); }
        h2 { font-size: 1.45em; padding-bottom: .25em; border-bottom: 1px solid var(--notez-border); }
        h3 { font-size: 1.2em; }
        p, ul, ol, blockquote, pre, .notez-table-wrap, .notez-image-placeholder { margin: .75em 0; }
        ul, ol { padding-left: 1.45em; }
        li + li { margin-top: .25em; }
        a { color: var(--notez-accent); text-decoration: none; }
        a:active { opacity: .75; }
        .notez-unsafe-link { color: var(--notez-muted); text-decoration: line-through; }
        code {
          font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace;
          font-size: .92em;
          background: var(--notez-soft);
          border-radius: 6px;
          padding: .12em .36em;
        }
        pre {
          overflow-x: auto;
          -webkit-overflow-scrolling: touch;
          background: var(--notez-soft);
          border: 1px solid var(--notez-border);
          border-radius: 10px;
          padding: 12px;
        }
        pre code {
          display: block;
          padding: 0;
          background: transparent;
          border-radius: 0;
          white-space: pre;
        }
        blockquote {
          border-left: 4px solid var(--notez-border);
          color: var(--notez-muted);
          padding: .05em 0 .05em 1em;
        }
        .notez-table-wrap {
          overflow-x: auto;
          -webkit-overflow-scrolling: touch;
        }
        table {
          border-collapse: collapse;
          min-width: 100%;
          width: max-content;
        }
        th, td {
          border: 1px solid var(--notez-border);
          padding: 8px 10px;
          text-align: left;
          vertical-align: top;
        }
        th {
          background: var(--notez-soft);
          font-weight: 700;
        }
        tr:nth-child(even) td { background: rgba(255, 255, 255, 0.025); }
        .task-list-item {
          list-style-type: none;
          margin-left: -1.2em;
        }
        .task-list-item input {
          margin: 0 .55em 0 0;
          transform: translateY(1px);
          accent-color: var(--notez-accent);
        }
        .notez-callout {
          border-left-width: 4px;
          border-radius: 10px;
          padding: 10px 12px;
          color: var(--notez-text);
          background: var(--notez-soft);
        }
        .notez-callout > p { margin: .35em 0 0; }
        .notez-callout-title {
          font-size: .82em;
          font-weight: 800;
          letter-spacing: .04em;
          margin-bottom: .25em;
        }
        .notez-callout-note { border-left-color: #58A6FF; }
        .notez-callout-tip { border-left-color: #3FB950; }
        .notez-callout-important { border-left-color: #A371F7; }
        .notez-callout-warning { border-left-color: #D29922; }
        .notez-callout-caution { border-left-color: var(--notez-danger); }
        .notez-image-placeholder {
          display: block;
          border: 1px dashed var(--notez-border);
          border-radius: 10px;
          padding: 10px 12px;
          color: var(--notez-text);
          background: rgba(255, 255, 255, 0.035);
        }
        .notez-image-placeholder strong,
        .notez-image-placeholder code,
        .notez-image-placeholder small {
          display: block;
          margin-top: 4px;
        }
        .notez-image-placeholder code {
          white-space: normal;
          word-break: break-all;
        }
        .notez-image-placeholder small { color: var(--notez-muted); }
        .notez-image-kicker {
          display: inline-block;
          color: var(--notez-muted);
          font-size: .78em;
          font-weight: 700;
          letter-spacing: .05em;
          text-transform: uppercase;
        }
        hr {
          border: 0;
          border-top: 1px solid var(--notez-border);
          margin: 1.35em 0;
        }
    """.trimIndent()

    private fun isLocalPreviewUrl(uri: Uri): Boolean =
        uri.scheme.equals("https", ignoreCase = true) && uri.host.equals(NOTEZ_HOST, ignoreCase = true)

    private fun openExternalOrBlock(uri: Uri): Boolean {
        val scheme = uri.scheme?.lowercase(Locale.US) ?: return true
        if (scheme !in EXTERNAL_SCHEMES) return true
        return try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
            true
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(activity, "Link tidak bisa dibuka", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun blockedResponse(): WebResourceResponse = WebResourceResponse(
        "text/plain",
        "UTF-8",
        ByteArrayInputStream(ByteArray(0))
    )

    private data class PreviewColors(
        val surface: String,
        val text: String,
        val muted: String,
        val accent: String,
        val danger: String
    ) {
        companion object {
            fun from(activity: Activity): PreviewColors = PreviewColors(
                surface = activity.themeColor(R.attr.colorSurface),
                text = activity.themeColor(R.attr.colorOnSurface),
                muted = activity.themeColor(R.attr.colorOnSurfaceVariant),
                accent = activity.themeColor(R.attr.colorPrimary),
                danger = activity.themeColor(R.attr.colorError)
            )
        }
    }

    private companion object {
        private const val NOTEZ_HOST = "notez.local"
        private const val NOTEZ_BASE_URL = "https://notez.local/"
        private val EXTERNAL_SCHEMES = setOf("http", "https", "mailto", "tel")
    }
}

private fun Activity.themeColor(attr: Int): String {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return String.format(Locale.US, "#%06X", 0xFFFFFF and typedValue.data)
}
