package com.zaba.notez.diagnostics

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zaba.notez.R
import com.zaba.notez.ThemePref
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
            text1.setTextColor(getColor(R.color.github_text))
            text1.textSize = 15f

            text2.text = "+${event.elapsedMs} ms"
            text2.setTextColor(getColor(R.color.github_accent))
            text2.textSize = 13f

            itemView.setPadding(0, 8, 0, 8)
            timelineContainer.addView(itemView)
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
