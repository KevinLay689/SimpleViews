package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.secondaryTextColor

/**
 * A splash/onboarding page: big title, subtitle and optional button,
 * centered on screen:
 *
 * ```
 * <com.github.simpleviews.TitlePage
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:svTitle="Welcome"
 *     app:svSubtitle="The simplest UI library for Android"
 *     app:svButtonText="Get started" />
 * ```
 */
class TitlePage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    var onButtonClick: (() -> Unit)? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TitlePage)
        val title = a.getString(R.styleable.TitlePage_svTitle)
        val subtitle = a.getString(R.styleable.TitlePage_svSubtitle)
        val buttonText = a.getString(R.styleable.TitlePage_svButtonText)
        a.recycle()

        val secondary = context.secondaryTextColor()
        val pad = context.dp(32)

        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(pad, pad, pad, pad)
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER,
            )
        }

        val titleView = TextView(context).apply {
            text = title
            textSize = 28f
            gravity = Gravity.CENTER
            visibility = if (title == null) GONE else VISIBLE
        }
        column.addView(titleView)

        val subtitleView = TextView(context).apply {
            text = subtitle
            textSize = 16f
            setTextColor(secondary)
            gravity = Gravity.CENTER
            visibility = if (subtitle == null) GONE else VISIBLE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply { topMargin = context.dp(8) }
        }
        column.addView(subtitleView)

        val button = SimpleButton(context).apply {
            text = buttonText ?: ""
            visibility = if (buttonText == null) GONE else VISIBLE
            setOnClickListener { onButtonClick?.invoke() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply { topMargin = context.dp(24) }
        }
        column.addView(button)

        addView(column)
    }
}
