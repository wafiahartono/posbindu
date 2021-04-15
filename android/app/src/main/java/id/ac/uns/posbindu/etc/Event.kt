package id.ac.uns.posbindu.etc

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContent(): T? = if (hasBeenHandled) null else {
        hasBeenHandled = true
        content
    }

    fun peekContent(): T = content
}