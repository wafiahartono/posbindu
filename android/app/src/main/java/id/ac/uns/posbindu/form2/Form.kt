package id.ac.uns.posbindu.form2

data class Form(
    val id: String,
    val name: String,
    val sections: List<Section>
)

data class Section(
    val id: String,
    val name: String,
    val description: String,
    val questions: List<Question>
)

sealed class Question {
    abstract val id: String
    abstract val question: String
    abstract val description: String
    abstract val required: Boolean

    data class MultipleChoice(
        override val id: String,
        override val question: String,
        override val description: String,
        override val required: Boolean,
        val choices: List<Choice>,
        val other: Boolean
    ) : Question() {
        data class Choice(
            val id: String,
            val nextSectionId: String,
        )
    }
}

class FormNavigator(form: Form, )