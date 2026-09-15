package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView

/**
 * A scrollable VStack — children are declared directly on it and routed to
 * the internal stack, so the "ScrollView can host only one direct child"
 * crash becomes impossible:
 *
 * ```
 * <com.github.simpleviews.ScrollVStack
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:padding="16dp"
 *     app:svSpacing="12dp">
 *
 *     <!-- any number of children -->
 * </com.github.simpleviews.ScrollVStack>
 * ```
 */
class ScrollVStack @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ScrollView(context, attrs, defStyleAttr) {

    val stack = VStack(context)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ScrollVStack)
        val spacing = a.getDimensionPixelSize(R.styleable.ScrollVStack_svSpacing, 0)
        val fillViewport = a.getBoolean(R.styleable.ScrollVStack_svFillViewport, true)
        a.recycle()
        stack.spacingPx = spacing
        isFillViewport = fillViewport
        // Index -1 keeps this on the super implementation and out of the
        // addView funnel below, which routes XML children into the stack.
        super.addView(
            stack, -1,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
        )
    }

    /** Access to the inner VStack for programmatic tweaks. */
    fun spacing(valueDp: Int) = stack.spacing(valueDp)

    /** FrameLayout-typed params (ScrollView's family) that remember svExactSize. */
    class ChildLayoutParams(c: Context, attrs: AttributeSet) : FrameLayout.LayoutParams(c, attrs) {
        var exactSize = false

        init {
            val a = c.obtainStyledAttributes(attrs, R.styleable.StackChild)
            exactSize = a.getBoolean(R.styleable.StackChild_svExactSize, false)
            a.recycle()
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): FrameLayout.LayoutParams =
        ChildLayoutParams(context, attrs)

    override fun generateDefaultLayoutParams(): FrameLayout.LayoutParams =
        FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    // ScrollView overrides all four addView variants with a "one direct
    // child" guard; route every one of them into the internal stack so
    // declarative children can never trigger that crash.
    override fun addView(child: View) {
        addView(child, -1, generateDefaultLayoutParams())
    }

    override fun addView(child: View, index: Int) {
        addView(child, index, generateDefaultLayoutParams())
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        addView(child, -1, params)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        // Convert to VStack params so the stack keeps the exactSize opt-out.
        val stackParams = if (params is ChildLayoutParams) {
            VStack.LayoutParams(params).apply { exactSize = true }
        } else {
            params
        }
        val safeIndex = if (index in 0..stack.childCount) index else -1
        stack.addView(child, safeIndex, stackParams)
    }
}
