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
 * A vertical stack where children are normalized: full width, wrap height,
 * with a uniform [svSpacing] gap between them — no layout_width, no
 * layout_height, no margins needed on children.
 *
 * ```
 * <com.github.simpleviews.VStack
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:padding="16dp"
 *     app:svSpacing="12dp">
 *
 *     <TextView android:text="Title" android:textSize="22sp" />
 *     <TextView android:text="Body" />
 *     <Button android:text="Continue" />
 * </com.github.simpleviews.VStack>
 * ```
 *
 * Children still have to declare layout_width/layout_height (Android
 * requires it in XML) but whatever they declare is overridden. To opt a
 * child out of normalization (e.g. to use layout_weight or a fixed height),
 * set app:svExactSize="true" on that child.
 */
class VStack @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var spacingPx: Int = 0
        internal set

    init {
        orientation = VERTICAL
        val a = context.obtainStyledAttributes(attrs, R.styleable.VStack)
        spacingPx = a.getDimensionPixelSize(R.styleable.VStack_svSpacing, 0)
        a.recycle()
    }

    /** Sets the uniform gap between children, in dp. */
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
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        reapplySpacing()
    }

    override fun onViewRemoved(child: View?) {
        super.onViewRemoved(child)
        reapplySpacing()
    }

    /** Margins are recomputed on every add/remove so the last child never
     *  carries a trailing gap. */
    private fun reapplySpacing() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            lp.bottomMargin = if (i == childCount - 1) 0 else spacingPx
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
}
