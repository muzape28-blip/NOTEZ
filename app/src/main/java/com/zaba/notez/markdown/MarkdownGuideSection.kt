package com.zaba.notez.markdown

data class MarkdownGuideSection(
    val id: String,
    val index: Int,
    val title: String,
    val group: String,
    val summary: String,
    val markdown: String
)
