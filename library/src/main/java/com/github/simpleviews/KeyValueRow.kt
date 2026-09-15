package com.github.simpleviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.github.simpleviews.internal.dp

/**
 * A detail-screen row: label on the left, bold value on the right:
 *
 * ```
 * <com.github.simpleviews.KeyValueRow
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:svLabel="Order total"
 *     app:svValue="$42.00" />
 * ```
 */
class KeyValueRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val valueView: TextView

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        minimumHeight = context.dp(48)
        val pad = context.dp(16)
        setPadding(pad, pad / 2, pad, pad / 2)

        val a = context.obtainStyledAttributes(attrs, R.styleable.KeyValueRow)
        val label = a.getString(R.styleable.KeyValueRow_svLabel)
        val value = a.getString(R.styleable.KeyValueRow_svValue)
        a.recycle()

        val labelView = TextView(context).apply {
            text = label
            textSize = 15f
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
        addView(labelView)

        valueView = TextView(context).apply {
            text = value
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.END
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftMargin = context.dp(16)
            }
        }
        addView(valueView)
    }

    /** Updates the value text. */
    fun setValue(value: CharSequence) {
        valueView.text = value
    }
}
