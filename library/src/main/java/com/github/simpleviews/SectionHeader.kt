package com.github.simpleviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.secondaryTextColor
import com.github.simpleviews.internal.themeColor

/**
 * A small uppercase section label with an optional right-side action link:
 *
 * ```
 * <com.github.simpleviews.SectionHeader
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svTitle="Account"
 *     app:svActionText="See all" />
 * ```
 */
class SectionHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var onActionClick: (() -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(context.dp(16), context.dp(24), context.dp(16), context.dp(8))

        val a = context.obtainStyledAttributes(attrs, R.styleable.SectionHeader)
        val title = a.getString(R.styleable.SectionHeader_svTitle)
        val actionText = a.getString(R.styleable.SectionHeader_svActionText)
        a.recycle()

        val secondary = context.secondaryTextColor()
        val accent = context.themeColor(android.R.attr.colorAccent, DEFAULT_ACCENT)

        val titleView = TextView(context).apply {
            text = title
            textSize = 13f
            setTextColor(secondary)
            setTypeface(typeface, Typeface.BOLD)
            isAllCaps = true
            letterSpacing = 0.08f
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
        addView(titleView)

        val actionView = TextView(context).apply {
            text = actionText
            textSize = 14f
            setTextColor(accent)
            setTypeface(typeface, Typeface.BOLD)
            visibility = if (actionText == null) GONE else VISIBLE
            val pad = context.dp(8)
            setPadding(pad, pad / 2, 0, pad / 2)
            setOnClickListener { onActionClick?.invoke() }
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        addView(actionView)
    }

    private companion object {
        val DEFAULT_ACCENT = 0xFF3F51B5.toInt()
    }
}
