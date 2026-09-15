package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.secondaryTextColor
import com.github.simpleviews.internal.themeColor

/**
 * The classic "nothing here yet" screen: icon, title, message and an
 * optional action button.
 *
 * ```
 * <com.github.simpleviews.EmptyStateView
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:svTitle="No results"
 *     app:svMessage="Try a different search"
 *     app:svButtonText="Clear search" />
 * ```
 *
 * Toggle it with the plain `visible()` / `gone()` extensions.
 */
class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val iconView = ImageView(context)
    private val titleView = TextView(context)
    private val messageView = TextView(context)
    private val buttonView = SimpleButton(context)

    /** Called when the built-in button is tapped (only shown if svButtonText was set). */
    var onButtonClickListener: (() -> Unit)? = null

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        val pad = context.dp(32)
        setPadding(pad, pad, pad, pad)

        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyStateView)
        val iconRes = a.getResourceId(R.styleable.EmptyStateView_svIcon, R.drawable.sv_ic_empty_box)
        val title = a.getString(R.styleable.EmptyStateView_svTitle)
        val message = a.getString(R.styleable.EmptyStateView_svMessage)
        val buttonText = a.getString(R.styleable.EmptyStateView_svButtonText)
        a.recycle()

        val secondary = context.secondaryTextColor()

        val iconSize = context.dp(96)
        iconView.apply {
            setImageResource(iconRes)
            setColorFilter(secondary)
            layoutParams = LayoutParams(iconSize, iconSize)
        }
        addView(iconView)

        titleView.apply {
            text = title
            textSize = 18f
            setTextColor(context.themeColor(android.R.attr.textColorPrimary))
            gravity = Gravity.CENTER
            val margin = context.dp(12)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = margin
            }
            visibility = if (title == null) GONE else VISIBLE
        }
        addView(titleView)

        messageView.apply {
            text = message
            textSize = 14f
            setTextColor(secondary)
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = context.dp(4)
            }
            visibility = if (message == null) GONE else VISIBLE
        }
        addView(messageView)

        buttonView.apply {
            text = buttonText ?: ""
            visibility = if (buttonText == null) GONE else VISIBLE
            setOnClickListener { onButtonClickListener?.invoke() }
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = context.dp(24)
            }
        }
        addView(buttonView)
    }

    /** Sets the icon drawable shown above the title. */
    fun setIcon(iconRes: Int) {
        iconView.setImageResource(iconRes)
    }
}
