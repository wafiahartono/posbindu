package id.ac.uns.posbindu.profile

abstract class ProfileRepository {
    abstract suspend fun isAllowedToCreate(nin: String): Boolean
    abstract suspend fun addProfile(nin: String)
    abstract suspend fun createProfile(profile: Profile)
    abstract suspend fun updateProfile(profile: Profile)
    abstract suspend fun getProfile(): Profile?
    abstract suspend fun getProfile(nin: String): Profile?
}