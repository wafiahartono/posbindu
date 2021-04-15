package id.ac.uns.posbindu.auth

class InvalidUserException(
    message: String? = null, cause: Throwable? = null
) : Exception(message, cause)