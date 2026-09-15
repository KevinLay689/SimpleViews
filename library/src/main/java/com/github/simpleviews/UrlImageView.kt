package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.github.simpleviews.internal.clipToCircle
import com.github.simpleviews.internal.clipToRounded

/**
 * An ImageView that loads from a web URL declared right in XML via Coil:
 *
 * ```
 * <com.github.simpleviews.UrlImageView
 *     android:layout_width="match_parent"
 *     android:layout_height="200dp"
 *     app:svLoad="https://example.com/photo.jpg"
 *     app:svCircle="true"
 *     app:svAlt="Photo of a mountain" />
 * ```
 *
 * Or from code: `imageView.url = "https://..."` — both go through the same
 * render path. Rounded/circle clipping is done with `ViewOutlineProvider`
 * (no transformations, no extra libraries), so placeholders clip too.
 */
open class UrlImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var url: String? = null
        set(value) {
            field = value
            render()
        }

    var circle: Boolean = false
        set(value) {
            field = value
            applyClip()
        }

    var cornerRadiusPx: Int = 0
        set(value) {
            field = value
            applyClip()
        }

    var crossfadeEnabled: Boolean = true
    var placeholderRes: Int = 0
        private set
    var errorRes: Int = 0
        private set

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.UrlImageView)
        val loadedUrl = a.getString(R.styleable.UrlImageView_svLoad)
        placeholderRes = a.getResourceId(R.styleable.UrlImageView_svPlaceholder, 0)
        errorRes = a.getResourceId(R.styleable.UrlImageView_svError, 0)
        crossfadeEnabled = a.getBoolean(R.styleable.UrlImageView_svCrossfade, true)
        a.getString(R.styleable.UrlImageView_svAlt)?.let { contentDescription = it }
        circle = a.getBoolean(R.styleable.UrlImageView_svCircle, false)
        cornerRadiusPx = a.getDimensionPixelSize(R.styleable.UrlImageView_svRounded, 0)
        a.recycle()
        // Assigned last so the first render sees every field already set.
        url = loadedUrl
    }

    /** Same code path as the `app:svLoad` XML attribute. */
    fun loadUrl(url: String) {
        this.url = url
    }

    internal open fun render() {
        if (isInEditMode) {
            // Never start real loads inside the layout preview.
            setImageResource(placeholderRes.takeIf { it != 0 } ?: R.drawable.sv_ic_image)
            return
        }
        val target = url ?: return
        load(target) {
            if (placeholderRes != 0) placeholder(placeholderRes)
            if (errorRes != 0) error(errorRes)
            if (crossfadeEnabled) crossfade(true)
        }
    }

    private fun applyClip() {
        when {
            circle -> clipToCircle()
            cornerRadiusPx > 0 -> clipToRounded(cornerRadiusPx.toFloat())
            else -> {
                clipToOutline = false
                outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
            }
        }
    }
}
