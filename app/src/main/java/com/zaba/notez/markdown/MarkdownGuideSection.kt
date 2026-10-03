package com.zaba.notez.markdown

data class MarkdownGuideSection(
    val id: String,
    val index: Int,
    val title: String,
    val group: String,
    val markdown: String
)
