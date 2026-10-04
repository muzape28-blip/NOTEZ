package com.zaba.notez.diagnostics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zaba.notez.R
import com.zaba.notez.ThemePref
import com.zaba.notez.UiStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiagnosticsActivity : AppCompatActivity() {

    private lateinit var sessionMeta: TextView
    private lateinit var timelineContainer: LinearLayout
    private lateinit var emptyDiagnostics: TextView
    private lateinit var btnCopy: Button
    private lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemePref.styleOf(ThemePref.get(this)))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnostics)
        applyScreenPolish()

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener { finish() }

        sessionMeta = findViewById(R.id.session_meta)
        timelineContainer = findViewById(R.id.timeline_container)
        emptyDiagnostics = findViewById(R.id.empty_diagnostics)
        btnCopy = findViewById(R.id.btn_copy)
        btnClear = findViewById(R.id.btn_clear)

        btnCopy.setOnClickListener { copyBreadcrumbToClipboard() }
        btnClear.setOnClickListener {
            PerfStore.clear()
            renderDiagnostics()
            Toast.makeText(this, "Diagnostics dibersihkan", Toast.LENGTH_SHORT).show()
        }

        renderDiagnostics()
    }

    private fun applyScreenPolish() {
        findViewById<View>(R.id.diagnostics_toolbar).background =
            UiStyle.roundedBackground(this, fillAlpha = 78, strokeAlpha = 52, radiusDp = 18f)
        findViewById<TextView>(R.id.session_meta).background =
            UiStyle.roundedBackground(this, fillAlpha = 52, strokeAlpha = 42, radiusDp = 16f)
        findViewById<TextView>(R.id.session_meta).setPadding(
            UiStyle.dp(this, 14f),
            UiStyle.dp(this, 12f),
            UiStyle.dp(this, 14f),
            UiStyle.dp(this, 12f)
        )
        findViewById<TextView>(R.id.empty_diagnostics).background =
            UiStyle.roundedBackground(this, fillAlpha = 52, strokeAlpha = 42, radiusDp = 18f)
        findViewById<View>(R.id.diagnostics_actions).background =
            UiStyle.roundedBackground(this, fillAlpha = 42, strokeAlpha = 34, radiusDp = 16f)
        findViewById<View>(R.id.diagnostics_actions).setPadding(
            UiStyle.dp(this, 10f),
            UiStyle.dp(this, 6f),
            UiStyle.dp(this, 10f),
            UiStyle.dp(this, 6f)
        )
        findViewById<Button>(R.id.btn_copy).text = "SALIN"
        findViewById<Button>(R.id.btn_clear).text = "BERSIHKAN"
    }

    private fun renderDiagnostics() {
        val session = PerfStore.getLatestSession()
        timelineContainer.removeAllViews()

        if (session == null || session.events.isEmpty()) {
            sessionMeta.visibility = View.GONE
            timelineContainer.visibility = View.GONE
            emptyDiagnostics.visibility = View.VISIBLE
            btnCopy.isEnabled = false
            btnClear.isEnabled = false
            return
        }

        emptyDiagnostics.visibility = View.GONE
        sessionMeta.visibility = View.VISIBLE
        timelineContainer.visibility = View.VISIBLE
        btnCopy.isEnabled = true
        btnClear.isEnabled = true

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateStr = dateFormat.format(Date(session.startedAt))
        sessionMeta.text = "Session: ${session.id} • $dateStr\nNote ID: ${session.noteId ?: "N/A"}"

        session.events.forEachIndexed { _, event ->
            val itemView = layoutInflater.inflate(
                android.R.layout.simple_list_item_2,
                timelineContainer,
                false
            )
            val text1 = itemView.findViewById<TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<TextView>(android.R.id.text2)

            text1.text = "● ${event.name}"
            text1.setTextColor(getColor(ThemePref.optionOf(ThemePref.get(this)).textColorRes))
            text1.textSize = 15f

            text2.text = "+${event.elapsedMs} ms"
            text2.setTextColor(UiStyle.primary(this))
            text2.textSize = 13f

            itemView.background = UiStyle.roundedBackground(
                this,
                fillAlpha = 42,
                strokeAlpha = 34,
                radiusDp = 14f
            )
            itemView.setPadding(
                UiStyle.dp(this, 14f),
                UiStyle.dp(this, 10f),
                UiStyle.dp(this, 14f),
                UiStyle.dp(this, 10f)
            )
            timelineContainer.addView(
                itemView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = UiStyle.dp(this@DiagnosticsActivity, 8f) }
            )
        }
    }

    private fun copyBreadcrumbToClipboard() {
        val session = PerfStore.getLatestSession() ?: return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateStr = dateFormat.format(Date(session.startedAt))

        val sb = StringBuilder()
        sb.appendLine("NOTEZ Diagnostics")
        sb.appendLine("Session: ${session.id}")
        sb.appendLine("Started: $dateStr")
        sb.appendLine("Note ID: ${session.noteId ?: "N/A"}")
        sb.appendLine()

        session.events.forEach { event ->
            sb.appendLine("${event.name} +${event.elapsedMs}ms")
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("NOTEZ Diagnostics", sb.toString())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Diagnostics tersalin ke clipboard", Toast.LENGTH_SHORT).show()
    }
}
