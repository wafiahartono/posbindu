package id.ac.uns.posbindu.profile

import android.net.Uri

data class Profile(
    val id: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val name: String? = null,
    val photo: Uri? = null,
    val nin: String? = null,
    val sex: Sex? = null,
    val birthInfo: BirthInfo? = null,
    val maritalStatus: MaritalStatus? = null,
    val address: String? = null,
    val education: Education? = null,
    val salary: Salary? = null,
)