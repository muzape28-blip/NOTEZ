package com.zaba.notez.markdown

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.zaba.notez.R
import com.zaba.notez.ThemePref
import java.io.ByteArrayInputStream
import java.util.Locale
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
                  var safeAnchorLink = /^#[A-Za-z0-9][A-Za-z0-9_-]*$/;
                  var allowedRawHtmlTags = {
                    br: true,
                    sub: true,
                    sup: true,
                    kbd: true,
                    mark: true,
                    u: true,
                    s: true,
                    small: true,
                    details: true,
                    summary: true,
                    abbr: true,
                    cite: true,
                    dl: true,
                    dt: true,
                    dd: true
                  };
                  var allowedRenderedTags = {
                    a: true,
                    abbr: true,
                    blockquote: true,
                    br: true,
                    cite: true,
                    code: true,
                    dd: true,
                    del: true,
                    dl: true,
                    dt: true,
                    details: true,
                    div: true,
                    em: true,
                    h1: true,
                    h2: true,
                    h3: true,
                    h4: true,
                    h5: true,
                    h6: true,
                    hr: true,
                    kbd: true,
                    li: true,
                    mark: true,
                    ol: true,
                    p: true,
                    pre: true,
                    s: true,
                    small: true,
                    span: true,
                    strong: true,
                    sub: true,
                    summary: true,
                    sup: true,
                    table: true,
                    tbody: true,
                    td: true,
                    th: true,
                    thead: true,
                    tr: true,
                    u: true,
                    ul: true
                  };
                  var allowedNotezClasses = {
                    'notez-image-placeholder': true,
                    'notez-image-kicker': true,
                    'notez-unsafe-link': true
                  };
                  var md = window.markdownit({
                    html: true,
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

                  function escapeAttribute(value) {
                    return escapeHtml(value).replace(/`/g, '&#96;');
                  }

                  function safeClassTokens(value, tagName) {
                    return String(value || '').split(/\s+/).filter(function (name) {
                      if (!name) return false;
                      if (allowedNotezClasses[name]) return true;
                      if (tagName === 'code' && /^language-[A-Za-z0-9_.+-]{1,40}$/.test(name)) return true;
                      return false;
                    }).join(' ');
                  }

                  function isSafeTextAlign(value) {
                    return /^\s*text-align\s*:\s*(left|right|center)\s*;?\s*$/i.test(String(value || ''));
                  }

                  function normalizedTextAlign(value) {
                    var match = String(value || '').match(/text-align\s*:\s*(left|right|center)/i);
                    return match ? 'text-align:' + match[1].toLowerCase() : '';
                  }

                  function renderSafeRawAttributes(node, tagName) {
                    var html = '';
                    if (tagName === 'details' && node.hasAttribute('open')) {
                      html += ' open';
                    }
                    if (tagName === 'abbr' && node.hasAttribute('title')) {
                      html += ' title="' + escapeAttribute(node.getAttribute('title') || '') + '"';
                    }
                    return html;
                  }

                  function renderSafeRawNodes(nodes) {
                    var html = '';
                    Array.prototype.slice.call(nodes).forEach(function (node) {
                      html += renderSafeRawNode(node);
                    });
                    return html;
                  }

                  function renderSafeRawNode(node) {
                    if (node.nodeType === Node.TEXT_NODE) {
                      return escapeHtml(node.nodeValue || '');
                    }
                    if (node.nodeType === Node.COMMENT_NODE) {
                      return escapeHtml('<!--' + (node.nodeValue || '') + '-->');
                    }
                    if (node.nodeType !== Node.ELEMENT_NODE) {
                      return '';
                    }
                    var tagName = node.tagName.toLowerCase();
                    if (!allowedRawHtmlTags[tagName]) {
                      return escapeHtml(node.outerHTML || '');
                    }
                    if (tagName === 'br') {
                      return '<br>';
                    }
                    return '<' + tagName + renderSafeRawAttributes(node, tagName) + '>' +
                      renderSafeRawNodes(node.childNodes) +
                      '</' + tagName + '>';
                  }

                  function sanitizeRawHtml(raw) {
                    var template = document.createElement('template');
                    template.innerHTML = String(raw || '');
                    return renderSafeRawNodes(template.content.childNodes);
                  }

                  function isSafeRenderedElement(element, tagName) {
                    if (tagName === 'div') {
                      return (element.getAttribute('class') || '').split(/\s+/).some(function (name) {
                        return name === 'notez-image-placeholder';
                      });
                    }
                    if (tagName === 'span') {
                      return (element.getAttribute('class') || '').split(/\s+/).some(function (name) {
                        return name === 'notez-image-kicker';
                      });
                    }
                    return true;
                  }

                  function sanitizeRenderedAttributes(element, tagName) {
                    Array.prototype.slice.call(element.attributes || []).forEach(function (attr) {
                      var name = attr.name.toLowerCase();
                      var value = attr.value || '';
                      var keep = false;
                      var nextValue = value;
                      if (name === 'href' && tagName === 'a') {
                        keep = !!safeHref(value);
                      } else if (name === 'target' && tagName === 'a') {
                        keep = value === '_self';
                      } else if (name === 'rel' && tagName === 'a') {
                        keep = true;
                        nextValue = 'nofollow noopener noreferrer';
                      } else if (name === 'class') {
                        nextValue = safeClassTokens(value, tagName);
                        keep = nextValue.length > 0;
                      } else if (name === 'style' && (tagName === 'th' || tagName === 'td') && isSafeTextAlign(value)) {
                        keep = true;
                        nextValue = normalizedTextAlign(value);
                      } else if (name === 'start' && tagName === 'ol' && /^\d{1,6}$/.test(value)) {
                        keep = true;
                      } else if (name === 'title' && tagName === 'abbr') {
                        keep = true;
                      } else if (name === 'open' && tagName === 'details') {
                        keep = true;
                        nextValue = '';
                      }

                      if (keep) {
                        element.setAttribute(attr.name, nextValue);
                      } else {
                        element.removeAttribute(attr.name);
                      }
                    });
                  }

                  function replaceWithEscapedOuterHtml(node) {
                    var text = node.nodeType === Node.COMMENT_NODE
                      ? '<!--' + (node.nodeValue || '') + '-->'
                      : (node.outerHTML || node.textContent || '');
                    node.parentNode.replaceChild(document.createTextNode(text), node);
                  }

                  function sanitizeRenderedDom(root) {
                    Array.prototype.slice.call(root.childNodes).forEach(function (node) {
                      if (node.nodeType === Node.TEXT_NODE) return;
                      if (node.nodeType === Node.COMMENT_NODE) {
                        replaceWithEscapedOuterHtml(node);
                        return;
                      }
                      if (node.nodeType !== Node.ELEMENT_NODE) {
                        node.parentNode.removeChild(node);
                        return;
                      }
                      var tagName = node.tagName.toLowerCase();
                      if (!allowedRenderedTags[tagName] || !isSafeRenderedElement(node, tagName)) {
                        replaceWithEscapedOuterHtml(node);
                        return;
                      }
                      sanitizeRenderedAttributes(node, tagName);
                      sanitizeRenderedDom(node);
                    });
                  }

                  function setSafePreviewHtml(html) {
                    var template = document.createElement('template');
                    template.innerHTML = String(html || '');
                    sanitizeRenderedDom(template.content);
                    while (preview.firstChild) preview.removeChild(preview.firstChild);
                    preview.appendChild(template.content);
                  }

                  function isSafeAnchor(href) {
                    return safeAnchorLink.test(String(href || '').trim());
                  }

                  function safeExternalHref(value) {
                    var href = String(value || '').trim();
                    return safeExternalLink.test(href) ? href : '';
                  }

                  function safeHref(value) {
                    var href = String(value || '').trim();
                    if (isSafeAnchor(href)) return href;
                    return safeExternalHref(href);
                  }

                  function normalizeFootnoteLabel(label) {
                    return String(label || '').trim().toLowerCase();
                  }

                  function notezFootnoteIdPart(label) {
                    var slug = slugifyHeading(normalizeFootnoteLabel(label));
                    return slug === 'section' ? 'note' : slug;
                  }

                  function notezFootnoteSlug(label) {
                    return 'notez-fn-' + notezFootnoteIdPart(label);
                  }

                  function notezFootnoteRefSlug(label, count) {
                    return 'notez-fnref-' + notezFootnoteIdPart(label) + '-' + count;
                  }

                  function notezFootnoteIndex(env, label) {
                    env.footnoteNumbers = env.footnoteNumbers || Object.create(null);
                    env.footnoteOrder = env.footnoteOrder || [];
                    if (!env.footnoteNumbers[label]) {
                      env.footnoteNumbers[label] = env.footnoteOrder.length + 1;
                      env.footnoteOrder.push(label);
                    }
                    return env.footnoteNumbers[label];
                  }

                  function markdownFenceMarker(line) {
                    var match = String(line || '').match(/^ {0,3}(`{3,}|~{3,})/);
                    return match ? match[1].charAt(0) : '';
                  }

                  function nextFenceState(line, currentFence) {
                    var marker = markdownFenceMarker(line);
                    if (!marker) return currentFence;
                    if (!currentFence) return marker;
                    return currentFence === marker ? '' : currentFence;
                  }

                  function extractFootnotes(markdown) {
                    var lines = String(markdown || '').split(/\r?\n/);
                    var output = [];
                    var definitions = Object.create(null);
                    var definitionOrder = [];
                    var fence = '';
                    for (var i = 0; i < lines.length; i++) {
                      if (fence || markdownFenceMarker(lines[i])) {
                        output.push(lines[i]);
                        fence = nextFenceState(lines[i], fence);
                        continue;
                      }
                      var match = lines[i].match(/^\[\^([^\]]+)\]:\s*(.*)$/);
                      if (!match) {
                        output.push(lines[i]);
                        continue;
                      }
                      var label = normalizeFootnoteLabel(match[1]);
                      if (!label) {
                        output.push(lines[i]);
                        continue;
                      }
                      var body = [match[2] || ''];
                      while (i + 1 < lines.length && /^(?: {4}|\t)/.test(lines[i + 1])) {
                        i += 1;
                        body.push(lines[i].replace(/^(?: {4}|\t)/, ''));
                      }
                      if (!definitions[label]) definitionOrder.push(label);
                      definitions[label] = body.join('\n').trim();
                    }
                    return {
                      markdown: output.join('\n'),
                      definitions: definitions,
                      definitionOrder: definitionOrder
                    };
                  }

                  function canStartDefinitionTerm(line) {
                    var value = String(line || '').trim();
                    if (!value) return false;
                    if (/^(?:#{1,6}\s|[-*+]\s|\d+\.\s|>|```|~~~|\||<)/.test(value)) return false;
                    if (/^\[\^([^\]]+)\]:/.test(value)) return false;
                    return true;
                  }

                  function preprocessDefinitionLists(markdown) {
                    var lines = String(markdown || '').split(/\r?\n/);
                    var output = [];
                    var fence = '';
                    for (var i = 0; i < lines.length; i++) {
                      if (fence || markdownFenceMarker(lines[i])) {
                        output.push(lines[i]);
                        fence = nextFenceState(lines[i], fence);
                        continue;
                      }
                      if (
                        i + 1 < lines.length &&
                        canStartDefinitionTerm(lines[i]) &&
                        /^:\s+/.test(lines[i + 1])
                      ) {
                        var terms = [lines[i].trim()];
                        var definitions = [];
                        i += 1;
                        while (i < lines.length && /^:\s+/.test(lines[i])) {
                          definitions.push(lines[i].replace(/^:\s+/, '').trim());
                          while (i + 1 < lines.length && /^(?: {4}|\t)/.test(lines[i + 1])) {
                            i += 1;
                            definitions[definitions.length - 1] += '\n' + lines[i].replace(/^(?: {4}|\t)/, '').trim();
                          }
                          i += 1;
                          if (i < lines.length && lines[i].trim() && !/^:\s+/.test(lines[i])) break;
                        }
                        i -= 1;
                        output.push('');
                        output.push('<dl>');
                        terms.forEach(function (term) {
                          output.push('<dt>' + escapeHtml(term) + '</dt>');
                        });
                        definitions.forEach(function (definition) {
                          output.push('<dd>' + escapeHtml(definition) + '</dd>');
                        });
                        output.push('</dl>');
                        output.push('');
                        continue;
                      }
                      output.push(lines[i]);
                    }
                    return output.join('\n');
                  }

                  function findClosingDelimiter(src, marker, start) {
                    var index = start;
                    while (index < src.length) {
                      index = src.indexOf(marker, index);
                      if (index < 0) return -1;
                      if (src.charAt(index - 1) === '\\') {
                        index += marker.length;
                        continue;
                      }
                      if (marker === '~' && (src.charAt(index - 1) === '~' || src.charAt(index + 1) === '~')) {
                        index += marker.length;
                        continue;
                      }
                      return index;
                    }
                    return -1;
                  }

                  function addSimpleDelimitedRule(ruleName, marker, tagName) {
                    md.inline.ruler.before('emphasis', ruleName, function (state, silent) {
                      var pos = state.pos;
                      var src = state.src;
                      if (src.slice(pos, pos + marker.length) !== marker) return false;
                      if (marker === '~' && src.charAt(pos + 1) === '~') return false;
                      var end = findClosingDelimiter(src, marker, pos + marker.length);
                      if (end < 0) return false;
                      var content = src.slice(pos + marker.length, end);
                      if (!content || /^\s|\s$/.test(content) || content.indexOf('\n') >= 0) return false;
                      if (silent) return false;
                      var token = state.push(ruleName, '', 0);
                      token.content = content;
                      state.pos = end + marker.length;
                      return true;
                    });
                    md.renderer.rules[ruleName] = function (tokens, idx) {
                      return '<' + tagName + '>' + escapeHtml(tokens[idx].content) + '</' + tagName + '>';
                    };
                  }

                  addSimpleDelimitedRule('notez_mark', '==', 'mark');
                  addSimpleDelimitedRule('notez_sup', '^', 'sup');
                  addSimpleDelimitedRule('notez_sub', '~', 'sub');

                  md.inline.ruler.after('escape', 'notez_footnote_ref', function (state, silent) {
                    var pos = state.pos;
                    var src = state.src;
                    if (src.charAt(pos) !== '[' || src.charAt(pos + 1) !== '^') return false;
                    var end = src.indexOf(']', pos + 2);
                    if (end < 0) return false;
                    var label = normalizeFootnoteLabel(src.slice(pos + 2, end));
                    if (!label || !state.env || !state.env.footnotes || !state.env.footnotes[label]) return false;
                    if (silent) return false;
                    var token = state.push('notez_footnote_ref', '', 0);
                    token.meta = { label: label };
                    state.pos = end + 1;
                    return true;
                  });

                  md.renderer.rules.notez_footnote_ref = function (tokens, idx, options, env) {
                    var label = tokens[idx].meta.label;
                    var index = notezFootnoteIndex(env, label);
                    env.footnoteRefCounts = env.footnoteRefCounts || Object.create(null);
                    env.footnoteRefCounts[label] = (env.footnoteRefCounts[label] || 0) + 1;
                    return '<sup><a href="#' + notezFootnoteSlug(label) + '">[' + index + ']</a></sup>';
                  };

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
                    var href = safeExternalHref(rawSrc);
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

                  md.renderer.rules.html_inline = function (tokens, idx) {
                    return sanitizeRawHtml(tokens[idx].content || '');
                  };

                  md.renderer.rules.html_block = function (tokens, idx) {
                    return sanitizeRawHtml(tokens[idx].content || '');
                  };

                  var preview = document.getElementById('preview');
                  var footnoteExtraction = extractFootnotes(source);
                  var preparedSource = preprocessDefinitionLists(footnoteExtraction.markdown);
                  var renderEnv = {
                    footnotes: footnoteExtraction.definitions,
                    footnoteOrder: [],
                    footnoteNumbers: Object.create(null),
                    footnoteRefCounts: Object.create(null)
                  };
                  setSafePreviewHtml(md.render(preparedSource, renderEnv));
                  addHeadingAnchors();
                  wrapTables();
                  enhanceTaskLists();
                  enhanceCodeBlocks();
                  enhanceCallouts();
                  enhanceFootnoteRefs();
                  appendFootnotes(renderEnv);
                  hardenLinks();

                  function slugifyHeading(text) {
                    var slug = String(text || '').toLowerCase()
                      .replace(/[^a-z0-9_\-\s]+/g, '')
                      .replace(/[\s\-]+/g, '-')
                      .replace(/^-+|-+$/g, '');
                    return slug || 'section';
                  }

                  function addHeadingAnchors() {
                    var seen = Object.create(null);
                    Array.prototype.slice.call(preview.querySelectorAll('h1,h2,h3,h4,h5,h6')).forEach(function (heading) {
                      var base = slugifyHeading(heading.textContent || '');
                      var count = (seen[base] || 0) + 1;
                      seen[base] = count;
                      heading.id = count === 1 ? base : base + '-' + count;
                    });
                  }

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

                  function prettyLanguageName(lang) {
                    var normalized = normalizeLanguage(lang);
                    return ({
                      js: 'JS',
                      ts: 'TS',
                      javascript: 'JS',
                      typescript: 'TS',
                      kotlin: 'KOTLIN',
                      kt: 'KOTLIN',
                      java: 'JAVA',
                      python: 'PYTHON',
                      py: 'PYTHON',
                      bash: 'SHELL',
                      sh: 'SHELL',
                      shell: 'SHELL',
                      json: 'JSON',
                      xml: 'XML',
                      html: 'HTML',
                      css: 'CSS',
                      md: 'MD',
                      markdown: 'MD'
                    })[normalized] || String(lang || 'CODE').toUpperCase();
                  }

                  function normalizeLanguage(lang) {
                    var value = String(lang || '').toLowerCase().replace(/^language-/, '');
                    if (value === 'kt') return 'kotlin';
                    if (value === 'js' || value === 'jsx') return 'javascript';
                    if (value === 'ts' || value === 'tsx') return 'typescript';
                    if (value === 'py') return 'python';
                    if (value === 'sh' || value === 'shell' || value === 'zsh') return 'bash';
                    if (value === 'htm') return 'html';
                    if (value === 'md') return 'markdown';
                    return value;
                  }

                  function languageFromCodeClass(code) {
                    var match = (code.getAttribute('class') || '').match(/(?:^|\s)language-([A-Za-z0-9_.+-]{1,40})(?:\s|$)/);
                    return match ? normalizeLanguage(match[1]) : '';
                  }

                  function syntaxSpan(className, text) {
                    return '<span class="ntz-syntax-' + className + '">' + escapeHtml(text) + '</span>';
                  }

                  function stickyRule(pattern, className) {
                    var flags = (pattern.ignoreCase ? 'i' : '') + (pattern.multiline ? 'm' : '') + 'y';
                    return { regex: new RegExp(pattern.source, flags), className: className };
                  }

                  function highlightByRules(text, rules) {
                    var sourceText = String(text || '');
                    var output = '';
                    var index = 0;
                    while (index < sourceText.length) {
                      var matched = false;
                      for (var i = 0; i < rules.length; i++) {
                        var rule = rules[i];
                        rule.regex.lastIndex = index;
                        var match = rule.regex.exec(sourceText);
                        if (match && match.index === index && match[0]) {
                          output += syntaxSpan(rule.className, match[0]);
                          index += match[0].length;
                          matched = true;
                          break;
                        }
                      }
                      if (!matched) {
                        output += escapeHtml(sourceText.charAt(index));
                        index += 1;
                      }
                    }
                    return output;
                  }

                  function codeRules(language) {
                    var commonStrings = /(?:"(?:\\.|[^"\\])*"|'(?:\\.|[^'\\])*'|`(?:\\.|[^`\\])*`)/;
                    var commonNumber = /\b(?:0x[0-9a-f]+|\d+(?:\.\d+)?)\b/i;
                    var cComment = /(?:\/\/[^\n]*|\/\*[\s\S]*?\*\/)/;
                    var pyComment = /#[^\n]*/;
                    var cOperator = /[{}()[\].,;:+\-*\/%=!<>|&?]+/;
                    var xmlTag = /<\/?[A-Za-z][^>]*\/?>/;
                    var rulesByLanguage = {
                      kotlin: [
                        stickyRule(cComment, 'comment'),
                        stickyRule(commonStrings, 'string'),
                        stickyRule(/\b(?:as|break|class|continue|do|else|false|for|fun|if|in|interface|is|null|object|package|return|super|this|throw|true|try|typealias|typeof|val|var|when|while|by|catch|constructor|delegate|dynamic|field|file|finally|get|import|init|param|property|receiver|set|setparam|where|actual|abstract|annotation|companion|const|crossinline|data|enum|expect|external|final|infix|inline|inner|internal|lateinit|noinline|open|operator|out|override|private|protected|public|reified|sealed|suspend|tailrec|vararg)\b/, 'keyword'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(/\b[A-Z][A-Za-z0-9_]*\b/, 'type'),
                        stickyRule(/\b[A-Za-z_][A-Za-z0-9_]*(?=\s*\()/, 'function'),
                        stickyRule(cOperator, 'operator')
                      ],
                      java: [
                        stickyRule(cComment, 'comment'),
                        stickyRule(commonStrings, 'string'),
                        stickyRule(/\b(?:abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|exports|extends|false|final|finally|float|for|if|implements|import|instanceof|int|interface|long|module|native|new|null|open|opens|package|private|protected|provides|public|requires|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|to|transient|true|try|uses|var|void|volatile|while|with)\b/, 'keyword'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(/\b[A-Z][A-Za-z0-9_]*\b/, 'type'),
                        stickyRule(/\b[A-Za-z_][A-Za-z0-9_]*(?=\s*\()/, 'function'),
                        stickyRule(cOperator, 'operator')
                      ],
                      javascript: [
                        stickyRule(cComment, 'comment'),
                        stickyRule(commonStrings, 'string'),
                        stickyRule(/\b(?:await|async|break|case|catch|class|const|continue|debugger|default|delete|do|else|export|extends|false|finally|for|from|function|if|import|in|instanceof|let|new|null|of|return|static|super|switch|this|throw|true|try|typeof|undefined|var|void|while|yield)\b/, 'keyword'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(/\b[A-Z][A-Za-z0-9_]*\b/, 'type'),
                        stickyRule(/\b[A-Za-z_$][A-Za-z0-9_$]*(?=\s*\()/, 'function'),
                        stickyRule(cOperator, 'operator')
                      ],
                      typescript: [
                        stickyRule(cComment, 'comment'),
                        stickyRule(commonStrings, 'string'),
                        stickyRule(/\b(?:abstract|any|as|asserts|async|await|boolean|break|case|catch|class|const|continue|debugger|declare|default|delete|do|else|enum|export|extends|false|finally|for|from|function|if|implements|import|in|infer|instanceof|interface|is|keyof|let|module|namespace|never|new|null|number|object|of|private|protected|public|readonly|return|static|string|super|switch|symbol|this|throw|true|try|type|typeof|undefined|unique|unknown|var|void|while|yield)\b/, 'keyword'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(/\b[A-Z][A-Za-z0-9_]*\b/, 'type'),
                        stickyRule(/\b[A-Za-z_$][A-Za-z0-9_$]*(?=\s*\()/, 'function'),
                        stickyRule(cOperator, 'operator')
                      ],
                      python: [
                        stickyRule(pyComment, 'comment'),
                        stickyRule(commonStrings, 'string'),
                        stickyRule(/\b(?:and|as|assert|async|await|break|class|continue|def|del|elif|else|except|False|finally|for|from|global|if|import|in|is|lambda|None|nonlocal|not|or|pass|raise|return|True|try|while|with|yield)\b/, 'keyword'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(/\b[A-Z][A-Za-z0-9_]*\b/, 'type'),
                        stickyRule(/\b[A-Za-z_][A-Za-z0-9_]*(?=\s*\()/, 'function'),
                        stickyRule(cOperator, 'operator')
                      ],
                      bash: [
                        stickyRule(pyComment, 'comment'),
                        stickyRule(commonStrings, 'string'),
                        stickyRule(/\b(?:case|do|done|elif|else|esac|export|fi|for|function|if|in|local|readonly|return|select|then|until|while)\b/, 'keyword'),
                        stickyRule(/\$[A-Za-z_][A-Za-z0-9_]*|\$\{[^}]+\}/, 'variable'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(cOperator, 'operator')
                      ],
                      json: [
                        stickyRule(/"(?:\\.|[^"\\])*"(?=\s*:)/, 'key'),
                        stickyRule(/"(?:\\.|[^"\\])*"/, 'string'),
                        stickyRule(/\b(?:true|false|null)\b/, 'keyword'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(/[{}[\],:]/, 'operator')
                      ],
                      xml: [
                        stickyRule(/<!--[\s\S]*?-->/, 'comment'),
                        stickyRule(xmlTag, 'tag'),
                        stickyRule(/&[A-Za-z0-9#]+;/, 'entity')
                      ],
                      html: [
                        stickyRule(/<!--[\s\S]*?-->/, 'comment'),
                        stickyRule(xmlTag, 'tag'),
                        stickyRule(/&[A-Za-z0-9#]+;/, 'entity')
                      ],
                      css: [
                        stickyRule(/\/\*[\s\S]*?\*\//, 'comment'),
                        stickyRule(commonStrings, 'string'),
                        stickyRule(/#[0-9a-f]{3,8}\b/i, 'number'),
                        stickyRule(/\b(?:align-items|background|border|color|display|font|font-size|font-weight|gap|grid|height|justify-content|line-height|margin|padding|position|width)\b/, 'keyword'),
                        stickyRule(commonNumber, 'number'),
                        stickyRule(/[{}()[\].,;:+\-*\/%=!<>|&?]+/, 'operator')
                      ],
                      markdown: [
                        stickyRule(/^#{1,6}[^\n]*/m, 'keyword'),
                        stickyRule(/^>[^\n]*/m, 'comment'),
                        stickyRule(/^\s*(?:[-*+] |\d+\. )/m, 'operator'),
                        stickyRule(/`[^`]*`/, 'string'),
                        stickyRule(/\*\*[^*]+\*\*|__[^_]+__/, 'keyword'),
                        stickyRule(/\[[^\]]+\]\([^)]*\)/, 'function')
                      ]
                    };
                    return rulesByLanguage[language] || [];
                  }

                  function highlightCode(text, language) {
                    var rules = codeRules(language);
                    return rules.length ? highlightByRules(text, rules) : escapeHtml(text);
                  }

                  function enhanceCodeBlocks() {
                    Array.prototype.slice.call(preview.querySelectorAll('pre > code')).forEach(function (code) {
                      var language = languageFromCodeClass(code);
                      var pre = code.parentNode;
                      var rawText = code.textContent || '';
                      pre.classList.add('notez-code-card');
                      if (language) {
                        var label = document.createElement('div');
                        label.className = 'notez-code-label';
                        label.textContent = prettyLanguageName(language);
                        pre.insertBefore(label, code);
                      }
                      code.classList.add('notez-code-highlighted');
                      code.innerHTML = highlightCode(rawText, language);
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

                  function enhanceFootnoteRefs() {
                    var seen = Object.create(null);
                    Array.prototype.slice.call(preview.querySelectorAll('sup > a[href^="#notez-fn-"]')).forEach(function (link) {
                      var labelPart = (link.getAttribute('href') || '').replace(/^#notez-fn-/, '');
                      if (!labelPart) return;
                      seen[labelPart] = (seen[labelPart] || 0) + 1;
                      var sup = link.parentNode;
                      sup.classList.add('notez-footnote-ref');
                      sup.id = 'notez-fnref-' + labelPart + '-' + seen[labelPart];
                    });
                  }

                  function appendSanitizedInline(parent, html) {
                    var template = document.createElement('template');
                    template.innerHTML = String(html || '');
                    sanitizeRenderedDom(template.content);
                    parent.appendChild(template.content);
                  }

                  function appendFootnotes(env) {
                    if (!env || !env.footnoteOrder || !env.footnoteOrder.length) return;
                    var section = document.createElement('section');
                    section.className = 'notez-footnotes';
                    var title = document.createElement('div');
                    title.className = 'notez-footnotes-title';
                    title.textContent = 'Footnotes';
                    var list = document.createElement('ol');
                    env.footnoteOrder.forEach(function (label) {
                      var item = document.createElement('li');
                      item.id = notezFootnoteSlug(label);
                      appendSanitizedInline(item, md.renderInline(env.footnotes[label] || '', env));
                      var back = document.createElement('a');
                      back.href = '#' + notezFootnoteRefSlug(label, 1);
                      back.className = 'notez-footnote-backref';
                      back.textContent = '↩';
                      item.appendChild(document.createTextNode(' '));
                      item.appendChild(back);
                      list.appendChild(item);
                    });
                    section.appendChild(title);
                    section.appendChild(list);
                    preview.appendChild(section);
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
        html {
          scroll-behavior: smooth;
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
          scroll-margin-top: 12px;
        }
        h1 { font-size: 1.75em; padding-bottom: .3em; border-bottom: 1px solid var(--notez-border); }
        h2 { font-size: 1.45em; padding-bottom: .25em; border-bottom: 1px solid var(--notez-border); }
        h3 { font-size: 1.2em; }
        p, ul, ol, dl, blockquote, pre, .notez-table-wrap, .notez-image-placeholder, .notez-footnotes { margin: .75em 0; }
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
          background: linear-gradient(145deg, rgba(255, 255, 255, 0.075), rgba(255, 255, 255, 0.035));
          border: 1px solid var(--notez-border);
          border-radius: 14px;
          padding: 12px;
          box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.045);
        }
        pre.notez-code-card {
          position: relative;
          padding-top: 38px;
        }
        pre code {
          display: block;
          padding: 0;
          background: transparent;
          border-radius: 0;
          white-space: pre;
        }
        .notez-code-label {
          position: absolute;
          top: 9px;
          right: 10px;
          max-width: 42%;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: var(--notez-muted);
          border: 1px solid var(--notez-border);
          border-radius: 999px;
          background: rgba(0, 0, 0, 0.18);
          padding: 2px 8px;
          font-size: .68em;
          font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace;
          font-weight: 800;
          letter-spacing: .06em;
        }
        .notez-code-highlighted .ntz-syntax-comment { color: #8B949E; font-style: italic; }
        .notez-code-highlighted .ntz-syntax-keyword { color: #FF7B72; font-weight: 700; }
        .notez-code-highlighted .ntz-syntax-string { color: #A5D6FF; }
        .notez-code-highlighted .ntz-syntax-number { color: #79C0FF; }
        .notez-code-highlighted .ntz-syntax-function { color: #D2A8FF; }
        .notez-code-highlighted .ntz-syntax-type { color: #FFA657; }
        .notez-code-highlighted .ntz-syntax-operator { color: #FF7B72; }
        .notez-code-highlighted .ntz-syntax-variable { color: #FFA657; }
        .notez-code-highlighted .ntz-syntax-key { color: #7EE787; }
        .notez-code-highlighted .ntz-syntax-tag { color: #7EE787; }
        .notez-code-highlighted .ntz-syntax-entity { color: #D2A8FF; }
        blockquote {
          border-left: 4px solid var(--notez-border);
          color: var(--notez-muted);
          padding: .08em 0 .08em 1em;
        }
        .notez-table-wrap {
          overflow-x: auto;
          -webkit-overflow-scrolling: touch;
          border: 1px solid var(--notez-border);
          border-radius: 12px;
          background: rgba(255, 255, 255, 0.025);
        }
        table {
          border-collapse: separate;
          border-spacing: 0;
          min-width: 100%;
          width: max-content;
        }
        th, td {
          border-right: 1px solid var(--notez-border);
          border-bottom: 1px solid var(--notez-border);
          padding: 8px 10px;
          text-align: left;
          vertical-align: top;
        }
        th:last-child, td:last-child { border-right: 0; }
        tr:last-child td { border-bottom: 0; }
        th {
          background: var(--notez-soft);
          font-weight: 800;
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
          border-radius: 12px;
          padding: 10px 12px;
          color: var(--notez-text);
          background: linear-gradient(145deg, rgba(255, 255, 255, 0.075), rgba(255, 255, 255, 0.032));
          box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);
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
        dl {
          border-left: 3px solid var(--notez-border);
          padding-left: 12px;
        }
        dt {
          color: var(--notez-text);
          font-weight: 800;
          margin-top: .55em;
        }
        dd {
          color: var(--notez-muted);
          margin: .2em 0 .55em 1em;
        }
        .notez-footnote-ref {
          font-size: .78em;
          line-height: 0;
        }
        .notez-footnote-ref a {
          border: 1px solid var(--notez-border);
          border-radius: 999px;
          padding: 0 .28em;
          background: rgba(255, 255, 255, 0.05);
        }
        .notez-footnotes {
          border-top: 1px solid var(--notez-border);
          color: var(--notez-muted);
          font-size: .9em;
          margin-top: 1.6em;
          padding-top: .9em;
        }
        .notez-footnotes-title {
          color: var(--notez-text);
          font-size: .78em;
          font-weight: 900;
          letter-spacing: .08em;
          text-transform: uppercase;
        }
        .notez-footnotes ol { padding-left: 1.4em; }
        .notez-footnotes li { margin: .35em 0; }
        .notez-footnote-backref {
          color: var(--notez-accent);
          font-size: .9em;
          text-decoration: none;
        }
        kbd {
          display: inline-block;
          border: 1px solid var(--notez-border);
          border-bottom-color: rgba(255, 255, 255, 0.26);
          border-radius: 7px;
          background: rgba(255, 255, 255, 0.08);
          color: var(--notez-text);
          box-shadow: inset 0 -1px 0 rgba(0, 0, 0, 0.22);
          font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace;
          font-size: .84em;
          padding: .08em .45em;
          white-space: nowrap;
        }
        mark {
          border-radius: 5px;
          background: rgba(255, 212, 77, 0.26);
          color: var(--notez-text);
          padding: .02em .22em;
        }
        details {
          border: 1px solid var(--notez-border);
          border-radius: 12px;
          background: rgba(255, 255, 255, 0.035);
          margin: .85em 0;
          padding: 10px 12px;
        }
        summary {
          cursor: pointer;
          color: var(--notez-text);
          font-weight: 800;
        }
        details[open] summary { margin-bottom: .45em; }
        abbr[title] {
          text-decoration: underline dotted;
          text-underline-offset: .16em;
        }
        sub, sup { line-height: 0; }
        small { color: var(--notez-muted); }
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
            fun from(activity: Activity): PreviewColors {
                val option = ThemePref.optionOf(ThemePref.get(activity))
                return PreviewColors(
                    surface = activity.colorResource(option.surfaceColorRes),
                    text = activity.colorResource(option.textColorRes),
                    muted = activity.colorResource(option.secondaryColorRes),
                    accent = activity.colorResource(option.accentColorRes),
                    danger = activity.colorResource(option.dangerColorRes)
                )
            }
        }
    }

    private companion object {
        private const val NOTEZ_HOST = "notez.local"
        private const val NOTEZ_BASE_URL = "https://notez.local/"
        private val EXTERNAL_SCHEMES = setOf("http", "https", "mailto", "tel")
    }
}

private fun Activity.colorResource(colorRes: Int): String =
    String.format(Locale.US, "#%06X", 0xFFFFFF and getColor(colorRes))
