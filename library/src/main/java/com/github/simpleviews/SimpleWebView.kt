package com.github.simpleviews

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * A WebView that loads its URL straight from XML:
 *
 * ```
 * <com.github.simpleviews.SimpleWebView
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:svUrl="https://example.com" />
 * ```
 *
 * JavaScript is enabled by default (svJavaScriptEnabled="false" to turn off).
 * For back navigation call goBackOrExit(activity) from your back handler.
 */
class SimpleWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    /** The page loaded from app:svUrl (renamed to avoid clashing with WebView.getUrl). */
    var pageUrl: String? = null
        private set

    var javaScriptEnabled: Boolean = true
        private set

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleWebView)
        pageUrl = a.getString(R.styleable.SimpleWebView_svUrl)
        javaScriptEnabled = a.getBoolean(R.styleable.SimpleWebView_svJavaScriptEnabled, true)
        a.recycle()
        if (!isInEditMode) render()
    }

    private fun render() {
        settings.javaScriptEnabled = javaScriptEnabled
        webViewClient = WebViewClient()
        pageUrl?.let { loadUrl(it) }
    }

    /** Goes back in web history if possible, otherwise finishes the activity. */
    fun goBackOrExit(activity: Activity) {
        if (canGoBack()) goBack() else activity.finish()
    }
}
