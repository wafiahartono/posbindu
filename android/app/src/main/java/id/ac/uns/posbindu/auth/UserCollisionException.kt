package id.ac.uns.posbindu.auth

class UserCollisionException(
    message: String? = null, cause: Throwable? = null
) : Exception(message, cause)