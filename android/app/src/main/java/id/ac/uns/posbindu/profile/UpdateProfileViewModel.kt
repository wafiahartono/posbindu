package id.ac.uns.posbindu.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.uns.posbindu.etc.Resource
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    private var nin: MutableLiveData<String?> = MutableLiveData()

    val profile = nin.switchMap {
        return@switchMap liveData<Resource<Profile?>>(
            viewModelScope.coroutineContext + Dispatchers.IO
        ) {
            if (it == null) {
                emit(Resource.Success(null))
                return@liveData
            }
            emit(Resource.Loading())
            try {
                emit(Resource.Success(profileRepository.getProfile(it)))
            } catch (e: Exception) {
                emit(Resource.Error(e))
            }
        }
    }

    fun setNin(nin: String) {
        this.nin.value = nin
    }
}