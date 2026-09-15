package com.github.simpleviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import com.github.simpleviews.internal.colorOrReference
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.rippleDrawable
import com.github.simpleviews.internal.roundedDrawable
import com.github.simpleviews.internal.clipToRounded
import com.github.simpleviews.internal.themeColor

/**
 * A rounded, borderable container without Material Components:
 * a GradientDrawable background plus outline clipping so children
 * are clipped to the corners too.
 *
 * ```
 * <com.github.simpleviews.SimpleCard
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svCornerRadius="16dp"
 *     app:svElevation="2dp"
 *     app:svRipple="true">
 *
 *     <!-- any children -->
 * </com.github.simpleviews.SimpleCard>
 * ```
 */
class SimpleCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleCard)
        val cornerRadius = a.getDimensionPixelSize(
            R.styleable.SimpleCard_svCornerRadius, context.dp(12),
        )
        val fillColor = a.colorOrReference(
            context,
            R.styleable.SimpleCard_svFillColor,
            context.themeColor(android.R.attr.colorBackground, Color.WHITE),
        )
        val strokeColor = a.colorOrReference(
            context, R.styleable.SimpleCard_svStrokeColor, Color.TRANSPARENT,
        )
        val strokeWidth = a.getDimensionPixelSize(R.styleable.SimpleCard_svStrokeWidth, 0)
        elevation = a.getDimensionPixelSize(R.styleable.SimpleCard_svElevation, 0).toFloat()
        val ripple = a.getBoolean(R.styleable.SimpleCard_svRipple, false)
        a.recycle()

        background = roundedDrawable(fillColor, cornerRadius.toFloat(), strokeColor, strokeWidth)
        if (ripple) {
            foreground = rippleDrawable(null, strokeColor.takeIf { it != Color.TRANSPARENT } ?: DEFAULT_PRESSED, cornerRadius.toFloat())
        }
        clipToRounded(cornerRadius.toFloat())
    }

    private companion object {
        val DEFAULT_PRESSED = 0x22000000
    }
}
