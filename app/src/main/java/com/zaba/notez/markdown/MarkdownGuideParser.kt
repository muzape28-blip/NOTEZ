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

        val parsedSections = sections.mapIndexed { idx, (title, contentLines) ->
            val id = slugify(title)
            val group = when {
                idx <= 5 || title.contains("cheat sheet", ignoreCase = true) -> "DASAR & TEKS"
                idx in 6..23 -> "STRUKTUR & TAMPILAN"
                else -> "LATIHAN, CONTOH & REFERENSI"
            }
            MarkdownGuideSection(
                id = id,
                index = idx + 1,
                title = title,
                group = group,
                summary = summaryOf(contentLines, title),
                markdown = "## $title\n\n" + contentLines.joinToString("\n").trim()
            )
        }

        // The preface used to be discarded, which made the guide start abruptly
        // at the cheat sheet. Keep it as a real first section so a new user gets
        // context before being dropped into syntax examples.
        val introMarkdown = rawMarkdown.substringBefore("\n## Daftar isi").trim()
        val introLines = introMarkdown.lines().drop(1)
        val intro = MarkdownGuideSection(
            id = "mulai-dari-sini",
            index = 0,
            title = "Mulai dari sini",
            group = "MULAI",
            summary = summaryOf(introLines, "Panduan Markdown"),
            markdown = introMarkdown
        )
        return listOf(intro) + parsedSections
    }

    private fun summaryOf(lines: List<String>, title: String): String {
        val candidate = lines.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filterNot { it == "---" || it.startsWith("```") || it.startsWith("|") }
            .filterNot { it.startsWith("#") }
            .firstOrNull()
            ?: "Panduan praktis tentang $title agar catatanmu tetap rapi."

        return candidate
            .replace(Regex("\\*\\*|__|`"), "")
            .replace(Regex("\\[([^]]+)]\\([^)]*\\)"), "\$1")
            .replace(Regex("\\s+"), " ")
            .trim()
            .ifBlank { "Panduan praktis untuk syntax ini di NOTEZ." }
            .let { text -> if (text.length > 132) text.take(129).trimEnd() + "…" else text }
    }

    private fun slugify(text: String): String {
        val slug = text.lowercase()
            .replace(Regex("""[^a-z0-9_\-\s]+"""), "")
            .replace(Regex("""[\s\-]+"""), "-")
            .trim('-')
        return slug.ifBlank { "section" }
    }
}
