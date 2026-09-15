package com.github.simpleviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.ColorUtils
import com.github.simpleviews.internal.colorOrReference
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.rippleDrawable
import com.github.simpleviews.internal.roundedDrawable
import com.github.simpleviews.internal.themeColor

/**
 * A button with rounded corners, ripple and a loading state — all without
 * Material Components. Rounded fills/strokes are drawn with framework
 * GradientDrawables; the spinner is drawn in onDraw.
 *
 * ```
 * <com.github.simpleviews.SimpleButton
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:text="Sign up"
 *     app:svCornerRadius="24dp"
 *     app:svFillColor="@color/purple" />
 * ```
 *
 * ```kotlin
 * button.loading = true   // text hides, spinner spins, clicks are swallowed
 * button.loading = false  // restored
 * ```
 */
class SimpleButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatButton(context, attrs, defStyleAttr) {

    var cornerRadiusPx: Int = 0
        private set
    var fillColor: Int = 0
        private set
    var pressedColor: Int = 0
        private set
    var strokeColor: Int = Color.TRANSPARENT
        private set
    var strokeWidthPx: Int = 0
        private set

    var loading: Boolean = false
        set(value) {
            field = value
            updateLoadingState()
        }

    private var originalText: CharSequence? = null
    private var spinnerAnimator: ValueAnimator? = null
    private var spinnerAngle = 0f
    private val spinnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleButton)
        cornerRadiusPx = a.getDimensionPixelSize(
            R.styleable.SimpleButton_svCornerRadius, context.dp(12),
        )
        fillColor = a.colorOrReference(
            context,
            R.styleable.SimpleButton_svFillColor,
            context.themeColor(android.R.attr.colorAccent, DEFAULT_FILL),
        )
        pressedColor = a.colorOrReference(
            context,
            R.styleable.SimpleButton_svPressedColor,
            ColorUtils.blendARGB(fillColor, Color.BLACK, 0.15f),
        )
        strokeColor = a.colorOrReference(
            context, R.styleable.SimpleButton_svStrokeColor, Color.TRANSPARENT,
        )
        strokeWidthPx = a.getDimensionPixelSize(R.styleable.SimpleButton_svStrokeWidth, 0)
        a.recycle()

        // Buttons are ALL CAPS under many themes; default to off unless the
        // caller explicitly asked for it via android:textAllCaps.
        val caps = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.textAllCaps))
        val capsSpecified = caps.hasValue(0)
        caps.recycle()
        if (!capsSpecified) transformationMethod = null

        applyBackground()
    }

    private fun applyBackground() {
        val content = roundedDrawable(fillColor, cornerRadiusPx.toFloat(), strokeColor, strokeWidthPx)
        background = rippleDrawable(content, pressedColor, cornerRadiusPx.toFloat())
    }

    /** Swallows clicks while loading instead of graying out. */
    override fun performClick(): Boolean = if (loading) true else super.performClick()

    private fun updateLoadingState() {
        if (loading) {
            originalText = text
            text = ""
            contentDescription = originalText
            startSpinner()
        } else {
            originalText?.let { text = it }
            originalText = null
            stopSpinner()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!loading) return
        val size = textSize * 1.2f
        val cx = width / 2f
        val cy = height / 2f
        val rect = RectF(cx - size / 2f, cy - size / 2f, cx + size / 2f, cy + size / 2f)
        spinnerPaint.color = currentTextColor
        spinnerPaint.strokeWidth = size * 0.16f
        canvas.drawArc(rect, spinnerAngle, 100f, false, spinnerPaint)
    }

    private fun startSpinner() {
        stopSpinner()
        spinnerAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 900
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                spinnerAngle = it.animatedValue as Float
                postInvalidateOnAnimation()
            }
            start()
        }
    }

    private fun stopSpinner() {
        spinnerAnimator?.cancel()
        spinnerAnimator = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (loading) startSpinner()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopSpinner()
    }

    override fun onSaveInstanceState(): Parcelable =
        SavedState(super.onSaveInstanceState() ?: Bundle()).apply { isLoading = loading }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            loading = state.isLoading
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : View.BaseSavedState {
        var isLoading = false

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            isLoading = source.readByte() == 1.toByte()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte(if (isLoading) 1 else 0)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel) = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    private companion object {
        val DEFAULT_FILL = 0xFF3F51B5.toInt()
    }
}
