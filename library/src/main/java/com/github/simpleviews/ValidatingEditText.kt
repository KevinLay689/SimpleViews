package com.github.simpleviews

import android.content.Context
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText

/**
 * An EditText with declarative validation rules:
 *
 * ```
 * <com.github.simpleviews.ValidatingEditText
 *     android:id="@+id/email"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     android:hint="Email"
 *     android:inputType="textEmailAddress"
 *     app:svValidate="required|email" />
 * ```
 *
 * ```kotlin
 * if (email.validateOrShowError()) { submit() }
 * ```
 *
 * Rules combine as flags: required, email, phone, number. svMinLength adds a
 * minimum length check. Error messages come from library string resources,
 * which apps may override, or from svErrorMessage.
 */
class ValidatingEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var validateMode: Int = VALIDATE_NONE
        private set

    var minLength: Int = 0
        private set

    var customErrorMessage: String? = null
        private set

    /** Fired on every text change with the current validity. */
    var onValidityChanged: ((Boolean) -> Unit)? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ValidatingEditText)
        validateMode = a.getInt(R.styleable.ValidatingEditText_svValidate, VALIDATE_NONE)
        minLength = a.getInt(R.styleable.ValidatingEditText_svMinLength, 0)
        customErrorMessage = a.getString(R.styleable.ValidatingEditText_svErrorMessage)
        a.recycle()
    }

    fun isValid(): Boolean {
        val value = text?.toString()?.trim().orEmpty()
        if (validateMode and VALIDATE_REQUIRED != 0 && value.isEmpty()) return false
        if (validateMode and VALIDATE_EMAIL != 0 && !Patterns.EMAIL_ADDRESS.matcher(value).matches()) return false
        if (validateMode and VALIDATE_PHONE != 0 && value.isNotEmpty() && !Patterns.PHONE.matcher(value).matches()) return false
        if (validateMode and VALIDATE_NUMBER != 0 && value.isNotEmpty() && value.toDoubleOrNull() == null) return false
        if (minLength > 0 && value.length < minLength) return false
        return true
    }

    /** Validates and shows an error message when invalid. */
    fun validateOrShowError(): Boolean {
        val valid = isValid()
        error = if (valid) null else customErrorMessage ?: defaultErrorMessage()
        return valid
    }

    fun clearError() {
        error = null
    }

    private fun defaultErrorMessage(): String = when {
        validateMode and VALIDATE_REQUIRED != 0 && text.isNullOrBlank() ->
            context.getString(R.string.sv_error_required)
        validateMode and VALIDATE_EMAIL != 0 -> context.getString(R.string.sv_error_email)
        validateMode and VALIDATE_PHONE != 0 -> context.getString(R.string.sv_error_phone)
        validateMode and VALIDATE_NUMBER != 0 -> context.getString(R.string.sv_error_number)
        minLength > 0 -> context.getString(R.string.sv_error_min_length, minLength)
        else -> context.getString(R.string.sv_error_required)
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        onValidityChanged?.invoke(isValid())
    }

    companion object {
        const val VALIDATE_NONE = 0
        const val VALIDATE_REQUIRED = 1
        const val VALIDATE_EMAIL = 2
        const val VALIDATE_PHONE = 4
        const val VALIDATE_NUMBER = 8
    }
}
