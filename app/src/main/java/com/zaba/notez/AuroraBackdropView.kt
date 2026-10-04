package com.zaba.notez

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.google.android.material.R as MaterialR

/**
 * A deliberately quiet ambient background. The blobs are static: the list can
 * scroll without an animation invalidating every frame, while still feeling
 * warmer than a flat page of cards.
 */
class AuroraBackdropView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val baseColor = UiStyle.windowBackground(context)
    private val primaryColor = UiStyle.primary(context)
    private val secondaryColor = UiStyle.resolveColor(
        context,
        MaterialR.attr.colorPrimaryContainer,
        primaryColor
    )
    private val surfaceColor = UiStyle.surface(context)

    init {
        isClickable = false
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(baseColor)

        val width = width.toFloat().coerceAtLeast(1f)
        val height = height.toFloat().coerceAtLeast(1f)
        val radius = (width.coerceAtLeast(height) * 0.72f).coerceAtLeast(240f)

        drawAurora(canvas, width * 0.06f, height * 0.12f, radius, primaryColor, 74)
        drawAurora(canvas, width * 0.98f, height * 0.46f, radius * 0.82f, secondaryColor, 50)
        drawAurora(canvas, width * 0.42f, height * 1.02f, radius * 0.76f, surfaceColor, 42)
    }

    private fun drawAurora(
        canvas: Canvas,
        x: Float,
        y: Float,
        radius: Float,
        color: Int,
        alpha: Int
    ) {
        paint.shader = RadialGradient(
            x,
            y,
            radius,
            intArrayOf(color.withAlpha(alpha), color.withAlpha(alpha / 3), color.withAlpha(0)),
            floatArrayOf(0f, 0.48f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(x, y, radius, paint)
        paint.shader = null
    }

    private fun Int.withAlpha(alpha: Int): Int = (this and 0x00FFFFFF) or (alpha.coerceIn(0, 255) shl 24)
}
