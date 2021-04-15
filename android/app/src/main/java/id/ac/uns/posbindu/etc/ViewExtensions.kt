package id.ac.uns.posbindu.etc

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import id.ac.uns.posbindu.R
import java.io.PrintWriter
import java.io.StringWriter

fun EditText.inputOrNull() = text?.toString()?.trim()?.ifBlank { null }

fun RecyclerView.addMarginItemDecoration(@DimenRes resId: Int) =
    addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(resId)))

fun TextInputLayout.setError(scrollView: NestedScrollView? = null, @StringRes resId: Int?) {
    if (resId == null) error = null
    else {
        error = context.getString(resId)
        scrollView?.smoothScrollTo(
            scrollX, top - resources.getDimensionPixelSize(R.dimen._16_dp)
        )
    }
}

inline fun <T> TextInputLayout.requireInput(
    scrollView: NestedScrollView? = null, getInput: () -> T?,
) = getInput().let { input ->
    if (input == null) setError(scrollView, R.string.field_required_msg)
    else error = null
    return@let input
}

inline fun TextInputLayout.validateInput(
    scrollView: NestedScrollView? = null, @StringRes resId: Int, validate: () -> Boolean,
) = validate().let { valid ->
    if (valid) error = null
    else setError(scrollView, resId)
    return@let valid
}

fun View.showSnackbar(@StringRes textResId: Int) = Snackbar
    .make(this, textResId, Snackbar.LENGTH_LONG)
    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
    .show()

fun View.showUnexpectedErrorSnackbar(e: Exception) {
    Log.e(LOG_TAG, e.message, e)
    Snackbar
        .make(this, R.string.crash_msg, Snackbar.LENGTH_LONG)
        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
        .setAction(R.string.report_crash_button_txt) {
            val stringWriter = StringWriter()
            e.printStackTrace(PrintWriter(stringWriter))
            val email = Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_EMAIL,
                    resources.getStringArray(R.array.crash_report_recipients)
                )
                putExtra(
                    Intent.EXTRA_SUBJECT, context.getString(R.string.crash_report_email_subject)
                )
                putExtra(Intent.EXTRA_TEXT, stringWriter.toString())
                type = "text/plain"
            }
            startActivity(
                it.context,
                Intent.createChooser(
                    email, context.getString(R.string.crash_report_activity_picker_title)
                ),
                null
            )
        }
        .show()
}