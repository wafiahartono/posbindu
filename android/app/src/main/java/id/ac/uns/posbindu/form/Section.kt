package id.ac.uns.posbindu.form

data class Section(
    val id: String,
    val title: String,
    val subtitle: String,
    val required: Boolean = true,
    val questions: List<Question>
)