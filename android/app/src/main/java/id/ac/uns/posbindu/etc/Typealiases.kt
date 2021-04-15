package id.ac.uns.posbindu.etc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

typealias MutableResourceLiveData<T> = MutableLiveData<Resource<T>>
typealias ResourceLiveData<T> = LiveData<Resource<T>>
typealias MutableListResourceLiveData<T> = MutableLiveData<Resource<List<T>>>
typealias ListResourceLiveData<T> = LiveData<Resource<List<T>>>
typealias ListResource<T> = Resource<List<T>>
typealias MutableEventLiveData<T> = MutableLiveData<Event<T>>
typealias EventLiveData<T> = LiveData<Event<T>>