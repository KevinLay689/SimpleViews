package com.github.simpleviews

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.secondaryTextColor

/**
 * A password field with a built-in show/hide eye toggle — no TextInputLayout
 * required. Cursor position survives the inputType switch and so does the
 * visibility state across rotation.
 *
 * ```
 * <com.github.simpleviews.PasswordEditText
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:hint="Password" />
 * ```
 *
 * Do not set android:inputType; the component manages it.
 */
class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var showPassword: Boolean = false
        set(value) {
            field = value
            val typefaceBefore = typeface
            val start = selectionStart
            val end = selectionEnd
            inputType = if (value) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            typeface = typefaceBefore
            val length = text?.length ?: 0
            if (start in 0..length && end in 0..length) setSelection(minOf(start, end))
            refreshEyeIcon()
        }

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        refreshEyeIcon()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val icon = compoundDrawables[2]
            if (icon != null && event.x >= width - compoundPaddingRight - context.dp(4)) {
                showPassword = !showPassword
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun refreshEyeIcon() {
        val res = if (showPassword) R.drawable.sv_ic_eye else R.drawable.sv_ic_eye_off
        val icon = AppCompatResources.getDrawable(context, res)?.mutate()
        icon?.setTint(context.secondaryTextColor())
        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
    }

    override fun onSaveInstanceState(): Parcelable =
        SavedState(super.onSaveInstanceState() ?: Bundle()).apply { showsPassword = showPassword }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            showPassword = state.showsPassword
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : View.BaseSavedState {
        var showsPassword = false

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel) : super(source) {
            showsPassword = source.readByte() == 1.toByte()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte(if (showsPassword) 1 else 0)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel) = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}
