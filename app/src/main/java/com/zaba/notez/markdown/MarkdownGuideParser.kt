package com.zaba.notez.markdown

object MarkdownGuideParser {

    private val fenceRegex = Regex("""^ {0,3}(`{3,}|~{3,})""")

    private fun markdownFenceMarker(line: String): String {
        val match = fenceRegex.find(line) ?: return ""
        return match.groupValues[1]
    }

    private fun nextFenceState(line: String, currentFence: String): String {
        val marker = markdownFenceMarker(line)
        if (marker.isEmpty()) return currentFence
        if (currentFence.isEmpty()) return marker
        return if (currentFence == marker) "" else currentFence
    }

    fun parse(rawMarkdown: String): List<MarkdownGuideSection> {
        val lines = rawMarkdown.lines()
        val sections = mutableListOf<Pair<String, MutableList<String>>>()

        var currentTitle = ""
        var currentLines = mutableListOf<String>()
        var fence = ""

        for (line in lines) {
            fence = nextFenceState(line, fence)

            if (fence.isEmpty() && line.startsWith("## ")) {
                val heading = line.removePrefix("## ").trim()
                if (!heading.lowercase().contains("daftar isi")) {
                    if (currentTitle.isNotBlank() && currentLines.isNotEmpty()) {
                        sections.add(currentTitle to currentLines)
                    }
                    currentTitle = heading
                    currentLines = mutableListOf()
                }
            } else {
                if (currentTitle.isNotBlank()) {
                    currentLines.add(line)
                }
            }
        }

        if (currentTitle.isNotBlank() && currentLines.isNotEmpty()) {
            sections.add(currentTitle to currentLines)
        }

        return sections.mapIndexed { idx, (title, contentLines) ->
            val id = slugify(title)
            val group = when {
                idx <= 5 || title.contains("cheat sheet", ignoreCase = true) -> "DASAR & TEKS"
                idx in 6..23 -> "STRUKTUR & TAMPILAN"
                else -> "LATIHAN, CONTOH & REFERENSI"
            }
            MarkdownGuideSection(
                id = id,
                index = idx,
                title = title,
                group = group,
                markdown = "## $title\n\n" + contentLines.joinToString("\n").trim()
            )
        }
    }

    private fun slugify(text: String): String {
        val slug = text.lowercase()
            .replace(Regex("""[^a-z0-9_\-\s]+"""), "")
            .replace(Regex("""[\s\-]+"""), "-")
            .trim('-')
        return slug.ifBlank { "section" }
    }
}
