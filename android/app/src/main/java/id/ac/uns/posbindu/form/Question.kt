package id.ac.uns.posbindu.form

import android.view.inputmethod.EditorInfo

sealed class Question {
    abstract val id: String
    abstract val text: String
    abstract val required: Boolean

    data class MultipleChoiceQuestion(
        override val id: String,
        override val text: String,
        override val required: Boolean = true,
        val choices: List<String>
    ) : Question()

    data class ShortAnswerQuestion(
        override val id: String,
        override val text: String,
        override val required: Boolean = true,
        val type: Type,
        val helperText: String
    ) : Question() {
        enum class Type {
            INTEGER, STRING
        }
    }
}