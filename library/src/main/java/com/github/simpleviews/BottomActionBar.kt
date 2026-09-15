package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.github.simpleviews.internal.dp

/**
 * A page with a content area (anything you put inside this tag) and a
 * full-width button pinned to the bottom — the login/onboarding pattern:
 *
 * ```
 * <com.github.simpleviews.BottomActionBar
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:svButtonText="Continue">
 *
 *     <!-- content, fills the space above the button -->
 *     <TextView android:text="Terms apply" ... />
 * </com.github.simpleviews.BottomActionBar>
 * ```
 *
 * Wire the button with [onButtonClick]. The button only appears when
 * svButtonText is set.
 */
class BottomActionBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var onButtonClick: (() -> Unit)? = null

    /** The area that holds XML children, filling the space above the button. */
    val content = FrameLayout(context)

    val button = SimpleButton(context)

    init {
        orientation = VERTICAL
        val pad = context.dp(16)
        setPadding(pad, pad, pad, pad)

        val a = context.obtainStyledAttributes(attrs, R.styleable.BottomActionBar)
        val buttonText = a.getString(R.styleable.BottomActionBar_svButtonText)
        a.recycle()

        // Added via super.addView with an explicit index so these land on
        // BottomActionBar itself, not through the addView funnel below.
        super.addView(content, -1, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))
        button.apply {
            text = buttonText ?: ""
            visibility = if (buttonText == null) GONE else VISIBLE
            setOnClickListener { onButtonClick?.invoke() }
        }
        super.addView(button, -1, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    /** Children declared in XML land in the content area above the button. */
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        val safeIndex = if (index in 0..content.childCount) index else -1
        content.addView(child, safeIndex, params)
    }
}
