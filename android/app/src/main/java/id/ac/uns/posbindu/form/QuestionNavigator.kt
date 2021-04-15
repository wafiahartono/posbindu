package id.ac.uns.posbindu.form

class QuestionNavigator(
    val questions: List<Question>,
    internal val navigationListener: (QuestionNavigator) -> Unit
) {
    private var index = 0
    private val answers = MutableList<Any?>(questions.size) { null }
    val number get() = index + 1
    val question get() = questions[index]
    val answer get() = answers[index]

    fun start() {
        navigationListener(this)
    }

    fun setAnswer(answer: Any?) {
        answers[index] = answer
    }

    fun next(): Boolean {
        if (index + 1 == questions.size) return false
        index++
        navigationListener(this)
        return true
    }

    fun previous(): Boolean {
        if (index - 1 == -1) return false
        index--
        navigationListener(this)
        return true
    }

    fun navigateToUnanswered(): Boolean {
        questions.forEachIndexed { i, question ->
            if (question.required && answers[i] == null) {
                index = i
                navigationListener(this)
                return true
            }
        }
        return false
    }

    fun finish(): List<Any> = answers.map { it!! }
}