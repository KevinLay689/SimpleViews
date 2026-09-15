package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.roundedDrawable

/**
 * A one-line colored message bar (info / success / warning / error):
 *
 * ```
 * <com.github.simpleviews.BannerView
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svSeverity="warning"
 *     app:svMessage="Your trial ends in 3 days"
 *     app:svDismissible="true" />
 * ```
 */
class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var onDismissed: (() -> Unit)? = null

    private val messageView = TextView(context)
    private val dismissView = TextView(context)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val a = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        val severity = a.getInt(R.styleable.BannerView_svSeverity, SEVERITY_INFO)
        val message = a.getString(R.styleable.BannerView_svMessage)
        val dismissible = a.getBoolean(R.styleable.BannerView_svDismissible, false)
        a.recycle()

        val (bgColor, fgColor) = PALETTES[severity.coerceIn(0, PALETTES.lastIndex)]
        val pad = context.dp(12)
        setPadding(pad, pad, pad, pad)
        background = roundedDrawable(bgColor, context.dp(10).toFloat())

        messageView.apply {
            text = message
            textSize = 14f
            setTextColor(fgColor)
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
        addView(messageView)

        dismissView.apply {
            text = "✕"
            textSize = 14f
            setTextColor(fgColor)
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftMargin = context.dp(8)
            }
            visibility = if (dismissible) VISIBLE else GONE
            setOnClickListener {
                visibility = GONE
                onDismissed?.invoke()
            }
        }
        addView(dismissView)
    }

    fun setMessage(message: CharSequence) {
        messageView.text = message
    }

    companion object {
        const val SEVERITY_INFO = 0
        const val SEVERITY_SUCCESS = 1
        const val SEVERITY_WARNING = 2
        const val SEVERITY_ERROR = 3

        // Background to foreground pairs, intentionally theme-independent.
        private val PALETTES = arrayOf(
            0xFFE3F2FD.toInt() to 0xFF0D47A1.toInt(), // info
            0xFFE8F5E9.toInt() to 0xFF1B5E20.toInt(), // success
            0xFFFFF8E1.toInt() to 0xFF7F6003.toInt(), // warning
            0xFFFFEBEE.toInt() to 0xFFB71C1C.toInt(), // error
        )
    }
}
