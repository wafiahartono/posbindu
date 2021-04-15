package id.ac.uns.posbindu.etc

import android.view.inputmethod.EditorInfo
import id.ac.uns.posbindu.form.Question.ShortAnswerQuestion
import id.ac.uns.posbindu.form.Question.ShortAnswerQuestion.Type

fun ShortAnswerQuestion.getEditTextInputType(): Int = when (type) {
    Type.INTEGER -> EditorInfo.TYPE_CLASS_NUMBER
    Type.STRING -> EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES
}