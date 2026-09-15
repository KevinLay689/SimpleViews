package com.github.simpleviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.github.simpleviews.internal.colorOrReference

/**
 * A circular avatar with an optional border ring. Shows a person placeholder
 * until a URL is set.
 *
 * ```
 * <com.github.simpleviews.AvatarView
 *     android:layout_width="56dp"
 *     android:layout_height="56dp"
 *     app:svAvatarUrl="https://example.com/me.jpg"
 *     app:svBorderWidth="2dp"
 *     app:svBorderColor="@android:color/white" />
 * ```
 */
class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : UrlImageView(context, attrs, defStyleAttr) {

    var borderWidthPx: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var borderColor: Int = Color.WHITE

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AvatarView)
        borderWidthPx = a.getDimensionPixelSize(R.styleable.AvatarView_svBorderWidth, 0)
        borderColor = a.colorOrReference(context, R.styleable.AvatarView_svBorderColor, Color.WHITE)
        a.recycle()
        circle = true
        scaleType = ScaleType.CENTER_CROP
    }

    override fun render() {
        if (url == null && !isInEditMode) {
            setImageResource(R.drawable.sv_ic_person)
            setBackgroundColor(DEFAULT_BACKGROUND)
            return
        }
        super.render()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (borderWidthPx > 0 && width > 0 && height > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidthPx.toFloat()
            val radius = minOf(width, height) / 2f - borderWidthPx / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, borderPaint)
        }
    }

    private companion object {
        val DEFAULT_BACKGROUND = 0xFFE0E0E0.toInt()
    }
}
