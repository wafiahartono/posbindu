package id.ac.uns.posbindu.etc

sealed class Resource<T> {
    class Loading<T>(val data: T? = null, val progress: Int = 0) : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val exception: Exception) : Resource<T>()
}