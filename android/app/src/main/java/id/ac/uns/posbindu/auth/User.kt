package id.ac.uns.posbindu.auth

import android.net.Uri

data class User(
    val id: String,
    val phone: String? = null,
    val email: String? = null,
    val name: String? = null,
    val photo: Uri? = null
) {
    var emailIsVerified: Boolean = false
    var profileIsComplete: Boolean = false
}