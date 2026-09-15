package com.github.simpleviews

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

/** Shows a toast. `context.toast("Saved")` */
fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

/** Shows an alert dialog with a single OK button. */
fun Context.alert(title: CharSequence, message: CharSequence, onOk: (() -> Unit)? = null) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
            onOk?.invoke()
        }
        .show()
}

/** Shows a confirmation dialog with Yes/No buttons. */
fun Context.confirm(
    title: CharSequence,
    message: CharSequence,
    onYes: () -> Unit,
    onNo: (() -> Unit)? = null,
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.yes) { dialog, _ ->
            dialog.dismiss()
            onYes()
        }
        .setNegativeButton(android.R.string.no) { dialog, _ ->
            dialog.dismiss()
            onNo?.invoke()
        }
        .show()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

/** Hides the soft keyboard for this activity. */
fun Activity.hideKeyboard() {
    val token = currentFocus?.windowToken ?: window.decorView.windowToken
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(token, 0)
}
