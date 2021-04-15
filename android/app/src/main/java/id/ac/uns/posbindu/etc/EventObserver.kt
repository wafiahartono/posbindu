package id.ac.uns.posbindu.etc

import androidx.lifecycle.Observer

class EventObserver<T>(private val onChanged: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContent()?.let(onChanged)
    }
}