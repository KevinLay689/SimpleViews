package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.github.simpleviews.internal.dp

/**
 * A horizontal stack where children are normalized to wrap/wrap with a
 * uniform [svSpacing] gap. `app:svDistribute="spread"` gives every child
 * equal width (weight 1).
 *
 * ```
 * <com.github.simpleviews.HStack
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svSpacing="8dp">
 *
 *     <Button android:text="Cancel" />
 *     <Button android:text="OK" />
 * </com.github.simpleviews.HStack>
 * ```
 *
 * Opt a child out with app:svExactSize="true" on the child.
 */
class HStack @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var distributeSpread: Boolean = false
        private set

    var spacingPx: Int = 0
        internal set

    init {
        orientation = HORIZONTAL
        val a = context.obtainStyledAttributes(attrs, R.styleable.HStack)
        spacingPx = a.getDimensionPixelSize(R.styleable.HStack_svSpacing, 0)
        distributeSpread = a.getInt(R.styleable.HStack_svDistribute, DISTRIBUTE_PACKED) == DISTRIBUTE_SPREAD
        a.recycle()
    }

    fun spacing(valueDp: Int) {
        spacingPx = context.dp(valueDp)
        reapplySpacing()
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams =
        LayoutParams(context, attrs)

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams = when (p) {
        is LayoutParams -> p
        is MarginLayoutParams -> LayoutParams(p)
        else -> LayoutParams(p)
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        val lp = child.layoutParams as? LayoutParams
        if (lp?.exactSize != true) {
            child.updateLayoutParams<LinearLayout.LayoutParams> {
                when {
                    distributeSpread -> {
                        width = 0
                        weight = 1f
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    else -> {
                        width = ViewGroup.LayoutParams.WRAP_CONTENT
                        height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                }
            }
        }
        reapplySpacing()
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        reapplySpacing()
    }

    private fun reapplySpacing() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            lp.rightMargin = if (i == childCount - 1) 0 else spacingPx
            child.layoutParams = lp
        }
    }

    class LayoutParams : LinearLayout.LayoutParams {
        @JvmField
        var exactSize = false

        constructor(width: Int, height: Int) : super(width, height)
        constructor(src: ViewGroup.LayoutParams) : super(src)
        constructor(src: MarginLayoutParams) : super(src)

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.StackChild)
            exactSize = a.getBoolean(R.styleable.StackChild_svExactSize, false)
            a.recycle()
        }
    }

    companion object {
        const val DISTRIBUTE_PACKED = 0
        const val DISTRIBUTE_SPREAD = 1
    }
}
