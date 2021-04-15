package id.ac.uns.posbindu.auth

abstract class AuthService {
    abstract val user: User?
    abstract suspend fun refreshUser()

    abstract suspend fun isPhoneRegistered(phone: String): Boolean
    abstract suspend fun sendVerificationSMS(phone: String)
    abstract suspend fun signUpWithPhone(phone: String, code: String)
    abstract suspend fun signInWithPhone(phone: String, code: String)

    abstract suspend fun isEmailRegistered(email: String): Boolean
    abstract suspend fun signUpWithEmail(email: String, password: String)
    abstract suspend fun sendVerificationEmail()
    abstract suspend fun signInWithEmail(email: String, password: String)
    abstract suspend fun sendPasswordResetEmail(email: String)

    abstract fun signOut()
}