package com.zaba.notez

import android.content.Context

/** Pilihan tema NOTEZ. Disimpan di SharedPreferences, diterapkan via setTheme(). */
object ThemePref {
    const val OLED = 0
    const val GITHUB_DARK = 1
    const val COBALT2 = 2
    const val NOTEZ_YOU_DARK = 3

    private const val PREF = "notez"
    private const val KEY = "theme"

    data class ThemeOption(
        val value: Int,
        val name: String,
        val description: String,
        val styleRes: Int,
        val backgroundColorRes: Int,
        val surfaceColorRes: Int,
        val textColorRes: Int,
        val secondaryColorRes: Int,
        val accentColorRes: Int,
        val outlineColorRes: Int
    )

    val OPTIONS = arrayOf(
        ThemeOption(
            value = OLED,
            name = "OLED Black",
            description = "Hitam fokus dengan aksen hijau NOTEZ.",
            styleRes = R.style.Theme_Notez,
            backgroundColorRes = R.color.oled_bg,
            surfaceColorRes = R.color.oled_surface,
            textColorRes = R.color.oled_text,
            secondaryColorRes = R.color.oled_secondary,
            accentColorRes = R.color.oled_accent,
            outlineColorRes = R.color.oled_outline
        ),
        ThemeOption(
            value = GITHUB_DARK,
            name = "GitHub Dark",
            description = "Markdown/readme vibe yang seimbang.",
            styleRes = R.style.Theme_Notez_GithubDark,
            backgroundColorRes = R.color.github_bg,
            surfaceColorRes = R.color.github_surface,
            textColorRes = R.color.github_text,
            secondaryColorRes = R.color.github_secondary,
            accentColorRes = R.color.github_accent,
            outlineColorRes = R.color.github_outline
        ),
        ThemeOption(
            value = COBALT2,
            name = "Cobalt2",
            description = "Playful coder theme dengan aksen kuning.",
            styleRes = R.style.Theme_Notez_Cobalt2,
            backgroundColorRes = R.color.cobalt_bg,
            surfaceColorRes = R.color.cobalt_surface,
            textColorRes = R.color.cobalt_text,
            secondaryColorRes = R.color.cobalt_secondary,
            accentColorRes = R.color.cobalt_accent,
            outlineColorRes = R.color.cobalt_outline
        ),
        ThemeOption(
            value = NOTEZ_YOU_DARK,
            name = "NOTEZ You Dark",
            description = "Material-ish dark yang kalem dan private.",
            styleRes = R.style.Theme_Notez_NotezYouDark,
            backgroundColorRes = R.color.notez_you_bg,
            surfaceColorRes = R.color.notez_you_surface,
            textColorRes = R.color.notez_you_text,
            secondaryColorRes = R.color.notez_you_secondary,
            accentColorRes = R.color.notez_you_accent,
            outlineColorRes = R.color.notez_you_outline
        )
    )

    val NAMES: Array<String>
        get() = OPTIONS.map { it.name }.toTypedArray()

    fun get(context: Context): Int =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY, GITHUB_DARK)

    fun set(context: Context, value: Int) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putInt(KEY, value).apply()
    }

    fun optionOf(value: Int): ThemeOption =
        OPTIONS.firstOrNull { it.value == value } ?: OPTIONS.first { it.value == GITHUB_DARK }

    fun nameOf(value: Int): String = optionOf(value).name

    fun styleOf(value: Int): Int = optionOf(value).styleRes
}
