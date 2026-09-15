package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp

/**
 * The simple image page: a web image on top, a caption under it, centered
 * and padded — one self-closing tag:
 *
 * ```
 * <com.github.simpleviews.ImagePage
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:svImageUrl="https://picsum.photos/800/600"
 *     app:svText="Everything the light touches"
 *     app:svButtonText="Get started"
 *     app:svCentered="true" />
 * ```
 *
 * Wire the optional button with [onButtonClick].
 */
class ImagePage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var onButtonClick: (() -> Unit)? = null

    private val imageView: UrlImageView
    private val captionView: TextView
    private val buttonView: SimpleButton

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        val a = context.obtainStyledAttributes(attrs, R.styleable.ImagePage)
        val imageUrl = a.getString(R.styleable.ImagePage_svImageUrl)
        val captionText = a.getString(R.styleable.ImagePage_svText)
        val imageHeight = a.getDimensionPixelSize(
            R.styleable.ImagePage_svImageHeight, context.dp(240),
        )
        val imageCornerRadius = a.getDimensionPixelSize(R.styleable.ImagePage_svImageCornerRadius, 0)
        val pagePadding = a.getDimensionPixelSize(R.styleable.ImagePage_svPadding, context.dp(24))
        val centered = a.getBoolean(R.styleable.ImagePage_svCentered, false)
        val buttonLabel = a.getString(R.styleable.ImagePage_svButtonText)
        a.recycle()

        setPadding(pagePadding, pagePadding, pagePadding, pagePadding)
        if (centered) gravity = Gravity.CENTER

        imageView = UrlImageView(context).apply {
            url = imageUrl
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            if (imageCornerRadius > 0) cornerRadiusPx = imageCornerRadius
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, imageHeight)
        }
        addView(imageView)

        captionView = TextView(context).apply {
            text = captionText
            textSize = 16f
            gravity = Gravity.CENTER
            visibility = if (captionText == null) GONE else VISIBLE
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = context.dp(16)
            }
        }
        addView(captionView)

        buttonView = SimpleButton(context).apply {
            text = buttonLabel ?: ""
            visibility = if (buttonLabel == null) GONE else VISIBLE
            setOnClickListener { onButtonClick?.invoke() }
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = context.dp(24)
            }
        }
        addView(buttonView)
    }

    /** Updates the caption text. */
    fun setCaption(text: CharSequence) {
        captionView.text = text
        captionView.visible()
    }
}
