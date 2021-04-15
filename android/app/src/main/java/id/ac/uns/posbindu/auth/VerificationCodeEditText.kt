package id.ac.uns.posbindu.auth

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import id.ac.uns.posbindu.databinding.VerificationCodeInputBinding

class VerificationCodeEditText(
    binding: VerificationCodeInputBinding,
    private val listener: (code: String) -> Unit
) {
    private val editTextList: List<EditText>

    var enabled: Boolean = true
        set(value) {
            editTextList.forEach { it.isEnabled = value }
            field = value
        }

    private val code: String?
        get() {
            val code = editTextList.map { it.text?.toString() }.joinToString("")
            return if (code.length == 6) code else null
        }

    init {
        editTextList =
            listOf(binding.d1, binding.d2, binding.d3, binding.d4, binding.d5, binding.d6)
        editTextList.forEachIndexed { i, editText ->
            when (i) {
                0 -> editText.doAfterTextChanged {
                    if (it?.isNotBlank() == true) editTextList[1].requestFocus()
                }
                editTextList.lastIndex -> editText.doAfterTextChanged {
                    if (it?.isBlank() == true) editTextList[i - 1].requestFocus()
                    else code?.let { code -> listener(code) }
                }
                else -> editText.doAfterTextChanged {
                    editTextList[i + (if (it?.isBlank() == true) -1 else 1)].requestFocus()
                }
            }
        }
    }
}