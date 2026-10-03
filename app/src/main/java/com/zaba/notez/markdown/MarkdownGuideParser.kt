package com.zaba.notez.markdown

object MarkdownGuideParser {
    fun parse(rawMarkdown: String): List<MarkdownGuideSection> {
        val lines = rawMarkdown.lines()
        val rawSections = mutableListOf<Pair<String, MutableList<String>>>()
        var currentTitle = "Pengantar"
        var currentLines = mutableListOf<String>()

        for (line in lines) {
            if (line.startsWith("## ")) {
                if (currentLines.isNotEmpty()) {
                    rawSections.add(currentTitle to currentLines)
                }
                currentTitle = line.removePrefix("## ").trim()
                currentLines = mutableListOf()
            } else {
                currentLines.add(line)
            }
        }
        if (currentLines.isNotEmpty()) {
            rawSections.add(currentTitle to currentLines)
        }

        val filtered = rawSections.filterNot { it.first.lowercase().contains("daftar isi") }

        return filtered.mapIndexed { idx, (title, contentLines) ->
            val group = when {
                idx <= 6 || title.contains("cheat sheet", ignoreCase = true) -> "DASAR & TEKS"
                idx in 7..17 || title.contains("heading", ignoreCase = true) || title.contains("table", ignoreCase = true) -> "STRUKTUR & TAMPILAN"
                else -> "LATIHAN, CONTOH & REFERENSI"
            }
            MarkdownGuideSection(
                index = idx,
                title = title,
                group = group,
                content = contentLines.joinToString("\n").trim()
            )
        }
    }
}
