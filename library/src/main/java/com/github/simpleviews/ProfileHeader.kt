package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.secondaryTextColor
import com.github.simpleviews.internal.themeColor
/**
 * A profile header: circular avatar, name, optional subtitle and an
 * optional right-side action link:
 *
 * ```
 * <com.github.simpleviews.ProfileHeader
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svAvatarUrl="https://example.com/me.jpg"
 *     app:svName="Jane Doe"
 *     app:svSubtitle="Product designer"
 *     app:svActionText="Edit" />
 * ```
 */
class ProfileHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var onActionClick: (() -> Unit)? = null

    private val actionView = TextView(context)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val pad = context.dp(16)
        setPadding(pad, pad, pad, pad)

        val a = context.obtainStyledAttributes(attrs, R.styleable.ProfileHeader)
        val avatarUrl = a.getString(R.styleable.ProfileHeader_svAvatarUrl)
        val name = a.getString(R.styleable.ProfileHeader_svName)
        val subtitle = a.getString(R.styleable.ProfileHeader_svSubtitle)
        val actionText = a.getString(R.styleable.ProfileHeader_svActionText)
        a.recycle()

        val avatarSize = context.dp(56)
        val secondary = context.secondaryTextColor()
        val accent = context.themeColor(android.R.attr.colorAccent, DEFAULT_ACCENT)
        val avatar = AvatarView(context).apply {
            url = avatarUrl
            layoutParams = LayoutParams(avatarSize, avatarSize)
        }
        addView(avatar)

        val column = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply {
                leftMargin = context.dp(16)
            }
        }
        val nameView = TextView(context).apply {
            text = name
            textSize = 16f
            gravity = Gravity.CENTER_VERTICAL
        }
        column.addView(nameView)
        val subtitleView = TextView(context).apply {
            text = subtitle
            textSize = 14f
            setTextColor(secondary)
            visibility = if (subtitle == null) GONE else VISIBLE
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = context.dp(2)
            }
        }
        column.addView(subtitleView)
        addView(column)

        actionView.apply {
            text = actionText
            textSize = 15f
            setTextColor(accent)
            visibility = if (actionText == null) GONE else VISIBLE
            val padH = context.dp(8)
            setPadding(padH, padH / 2, padH, padH / 2)
            setOnClickListener { onActionClick?.invoke() }
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        addView(actionView)
    }

    private companion object {
        val DEFAULT_ACCENT = 0xFF3F51B5.toInt()
    }
}
