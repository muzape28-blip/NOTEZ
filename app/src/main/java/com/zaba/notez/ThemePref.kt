package com.zaba.notez

import android.content.Context

/** Pilihan tema NOTEZ. Disimpan di SharedPreferences, diterapkan via setTheme(). */
object ThemePref {
    const val OLED = 0
    const val GITHUB_DARK = 1
    const val COBALT2 = 2

    private const val PREF = "notez"
    private const val KEY = "theme"

    val NAMES = arrayOf("OLED Hijau", "GitHub Dark", "Cobalt2")

    fun get(context: Context): Int =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(KEY, OLED)

    fun set(context: Context, value: Int) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putInt(KEY, value).apply()
    }

    fun styleOf(value: Int): Int = when (value) {
        GITHUB_DARK -> R.style.Theme_Notez_GithubDark
        COBALT2 -> R.style.Theme_Notez_Cobalt2
        else -> R.style.Theme_Notez
    }
}
