package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.rippleDrawable
import com.github.simpleviews.internal.secondaryTextColor

/**
 * The settings-row workhorse: icon (resource or URL), title, optional
 * subtitle, optional right-side value and a chevron, on a full-width ripple:
 *
 * ```
 * <com.github.simpleviews.ListItemView
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svIcon="@drawable/ic_notifications"
 *     app:svTitle="Notifications"
 *     app:svSubtitle="Push, email and SMS"
 *     app:svDivider="true" />
 * ```
 *
 * Handle taps with the standard `setOnClickListener`.
 */
class ListItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val iconView: UrlImageView
    private val titleView: TextView
    private val subtitleView: TextView
    private val valueView: TextView
    private val chevronView: ImageView
    private val dividerView: View

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ListItemView)
        val title = a.getString(R.styleable.ListItemView_svTitle)
        val subtitle = a.getString(R.styleable.ListItemView_svSubtitle)
        val iconRes = a.getResourceId(R.styleable.ListItemView_svIcon, 0)
        val iconUrl = a.getString(R.styleable.ListItemView_svIconUrl)
        val value = a.getString(R.styleable.ListItemView_svValue)
        val chevron = a.getBoolean(R.styleable.ListItemView_svChevron, true)
        val divider = a.getBoolean(R.styleable.ListItemView_svDivider, false)
        a.recycle()

        val secondary = context.secondaryTextColor()

        background = null
        foreground = rippleDrawable(null, 0x22000000, context.dp(12).toFloat())

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = context.dp(56)
            val padH = context.dp(16)
            val padV = context.dp(12)
            setPadding(padH, padV, padH, padV)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val iconSize = context.dp(40)
        var iconVisible = false
        iconView = UrlImageView(context).apply {
            if (iconRes != 0) {
                setImageResource(iconRes)
                iconVisible = true
            } else if (iconUrl != null) {
                url = iconUrl
                iconVisible = true
            }
            cornerRadiusPx = context.dp(8)
            visibility = if (iconVisible) VISIBLE else GONE
            layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
        }
        row.addView(iconView)

        val textColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                leftMargin = if (iconVisible) context.dp(16) else 0
            }
        }
        titleView = TextView(context).apply {
            text = title
            textSize = 16f
        }
        textColumn.addView(titleView)
        subtitleView = TextView(context).apply {
            text = subtitle
            textSize = 14f
            setTextColor(secondary)
            visibility = if (subtitle == null) GONE else VISIBLE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply { topMargin = context.dp(2) }
        }
        textColumn.addView(subtitleView)
        row.addView(textColumn)

        valueView = TextView(context).apply {
            text = value
            textSize = 15f
            visibility = if (value == null) GONE else VISIBLE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply { leftMargin = context.dp(8) }
        }
        row.addView(valueView)

        chevronView = ImageView(context).apply {
            setImageResource(R.drawable.sv_ic_chevron_right)
            setColorFilter(secondary)
            visibility = if (chevron) VISIBLE else GONE
            val size = context.dp(24)
            layoutParams = LinearLayout.LayoutParams(size, size).apply { leftMargin = context.dp(8) }
        }
        row.addView(chevronView)

        addView(row)

        dividerView = View(context).apply {
            setBackgroundColor(0x1F000000 or (secondary and 0xFFFFFF))
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, context.dp(1), Gravity.BOTTOM)
            visibility = if (divider) VISIBLE else GONE
        }
        addView(dividerView)

        contentDescription = listOfNotNull(title, subtitle, value).joinToString(", ")
    }

    /** Shows or hides the bottom divider line. */
    fun setDividerVisible(visible: Boolean) {
        dividerView.visibility = if (visible) VISIBLE else GONE
    }
}
