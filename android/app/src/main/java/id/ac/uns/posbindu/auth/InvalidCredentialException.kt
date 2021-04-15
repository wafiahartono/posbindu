package id.ac.uns.posbindu.auth

class InvalidCredentialException(
    message: String? = null, cause: Throwable? = null
) : Exception(message, cause)