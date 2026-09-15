package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams

/**
 * A FrameLayout that centers its children:
 *
 * ```
 * <com.github.simpleviews.CenterLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent">
 *
 *     <ProgressBar android:layout_width="wrap_content"
 *         android:layout_height="wrap_content" />
 * </com.github.simpleviews.CenterLayout>
 * ```
 *
 * Set app:svCenterVerticalOnly="true" to keep horizontal positions.
 */
class CenterLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private var centerVerticalOnly = false

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CenterLayout)
        centerVerticalOnly = a.getBoolean(R.styleable.CenterLayout_svCenterVerticalOnly, false)
        a.recycle()
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        child.updateLayoutParams<LayoutParams> {
            gravity = if (centerVerticalOnly) Gravity.CENTER_VERTICAL else Gravity.CENTER
        }
    }
}
