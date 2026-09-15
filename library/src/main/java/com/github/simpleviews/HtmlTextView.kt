package com.github.simpleviews

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat

/**
 * A TextView that renders HTML declared in XML:
 *
 * ```
 * <com.github.simpleviews.HtmlTextView
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svHtml="&lt;b&gt;Hello&lt;/b&gt; world" />
 * ```
 *
 * Or from code: `textView.setHtml("<b>Hello</b>")`.
 */
class HtmlTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        context.obtainStyledAttributes(attrs, R.styleable.HtmlTextView).apply {
            getString(R.styleable.HtmlTextView_svHtml)?.let { setHtml(it) }
            recycle()
        }
    }

    fun setHtml(html: String) {
        text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        if (html.contains("href")) movementMethod = LinkMovementMethod.getInstance()
    }
}
