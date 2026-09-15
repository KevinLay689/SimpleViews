package com.github.simpleviews.internal

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.content.res.AppCompatResources

internal fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

internal fun Context.themeColor(attr: Int, fallback: Int = Color.GRAY): Int {
    val array = obtainStyledAttributes(intArrayOf(attr))
    val color = array.getColor(0, fallback)
    array.recycle()
    return color
}

/** The theme's standard secondary text color (adapts to dark mode). */
internal fun Context.secondaryTextColor(): Int {
    val value = TypedValue()
    if (!theme.resolveAttribute(android.R.attr.textColorSecondary, value, true)) return Color.GRAY
    if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
        return value.data
    }
    val id = value.resourceId
    if (id == 0) return Color.GRAY
    return try {
        AppCompatResources.getColorStateList(this, id).defaultColor
    } catch (e: Exception) {
        Color.GRAY
    }
}

/**
 * Reads a `color|reference` attribute: literal colors via getColor, @color/...
 * (including ColorStateList resources) via the resource id.
 */
internal fun TypedArray.colorOrReference(context: Context, index: Int, default: Int): Int {
    if (!hasValue(index)) return default
    return if (getType(index) == TypedValue.TYPE_STRING) {
        val id = getResourceId(index, 0)
        if (id != 0) {
            try {
                AppCompatResources.getColorStateList(context, id).defaultColor
            } catch (e: Exception) {
                default
            }
        } else default
    } else {
        getColor(index, default)
    }
}

internal fun roundedDrawable(
    fillColor: Int,
    radiusPx: Float,
    strokeColor: Int = Color.TRANSPARENT,
    strokeWidthPx: Int = 0,
): GradientDrawable = GradientDrawable().apply {
    setColor(fillColor)
    cornerRadius = radiusPx
    if (strokeWidthPx > 0) setStroke(strokeWidthPx, strokeColor)
}

internal fun rippleDrawable(
    content: Drawable?,
    pressedColor: Int,
    cornerRadiusPx: Float,
): RippleDrawable {
    val mask = GradientDrawable().apply {
        cornerRadius = cornerRadiusPx
        setColor(Color.WHITE)
    }
    val colors = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(),
        ),
        intArrayOf(pressedColor, Color.TRANSPARENT),
    )
    return RippleDrawable(colors, content, mask)
}

internal fun View.clipToRounded(radiusPx: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radiusPx)
        }
    }
}

internal fun View.clipToCircle() {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(0, 0, view.width, view.height)
        }
    }
}
