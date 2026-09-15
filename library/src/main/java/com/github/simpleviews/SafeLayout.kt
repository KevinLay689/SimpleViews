package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * A root layout that pads itself by the system bar insets — the one-line
 * answer to Android 15's enforced edge-to-edge:
 *
 * ```
 * <com.github.simpleviews.SafeLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:svFitSystemBars="both">
 *     <!-- your content, never under the status bar -->
 * </com.github.simpleviews.SafeLayout>
 * ```
 *
 * User-declared padding (android:padding) is preserved and the insets are
 * added on top of it.
 */
class SafeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    var fitMode: Int = FIT_BOTH
        set(value) {
            field = value
            applyInsets(lastInsets)
        }

    private var lastInsets: WindowInsetsCompat? = null
    private var baseLeft = 0
    private var baseTop = 0
    private var baseRight = 0
    private var baseBottom = 0

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SafeLayout)
        fitMode = a.getInt(R.styleable.SafeLayout_svFitSystemBars, FIT_BOTH)
        a.recycle()
        // Captured after the constructor applied android:padding.
        baseLeft = paddingLeft
        baseTop = paddingTop
        baseRight = paddingRight
        baseBottom = paddingBottom
        if (!isInEditMode) {
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                lastInsets = insets
                applyInsets(insets)
                WindowInsetsCompat.CONSUMED
            }
        }
    }

    private fun applyInsets(insets: WindowInsetsCompat?) {
        insets ?: return
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
        )
        val top = if (fitMode == FIT_TOP || fitMode == FIT_BOTH) bars.top else 0
        val bottom = if (fitMode == FIT_BOTTOM || fitMode == FIT_BOTH) bars.bottom else 0
        setPadding(baseLeft, baseTop + top, baseRight, baseBottom + bottom)
    }

    companion object {
        const val FIT_NONE = 0
        const val FIT_TOP = 1
        const val FIT_BOTTOM = 2
        const val FIT_BOTH = 3
    }
}
