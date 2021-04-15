package id.ac.uns.posbindu.auth

import android.app.Activity
import android.content.Context
import android.telephony.PhoneNumberUtils
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ActivityContext
import id.ac.uns.posbindu.etc.LOG_TAG
import id.ac.uns.posbindu.firebase.COL_USERS
import id.ac.uns.posbindu.firebase.FLD_USERS_EMAIL
import id.ac.uns.posbindu.firebase.FLD_USERS_PHONE
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.tasks.await

class FirebaseAuthService @Inject constructor(
    @ActivityContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthService() {
    private var _user: User? = null
    override val user: User? get() = _user

    private var phoneVerificationId: String? = null

    init {
        updateUserField()
    }

    private fun updateUserField() {
        _user = auth.currentUser?.run {
            User(uid, phoneNumber, email, displayName?.ifBlank { null }, photoUrl).apply {
                emailIsVerified = isEmailVerified
                profileIsComplete = name?.isNotBlank() == true
            }
        }
    }

    override suspend fun refreshUser() {
        auth.currentUser?.reload()?.await() ?: return
        updateUserField()
    }

    override suspend fun isPhoneRegistered(phone: String) =
        !firestore.collection(COL_USERS).whereEqualTo(FLD_USERS_PHONE, phone).limit(1).get().await()
            .isEmpty

    override suspend fun sendVerificationSMS(phone: String) = suspendCoroutine<Unit> {
        val options = PhoneAuthOptions.newBuilder()
            .setActivity(context as Activity)
            .setPhoneNumber(PhoneNumberUtils.formatNumberToE164(phone, "ID"))
            .setTimeout(0, TimeUnit.SECONDS)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String, token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    phoneVerificationId = verificationId
                    it.resume(Unit)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

                override fun onVerificationFailed(e: FirebaseException) {
                    when (e) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Log.e(LOG_TAG, e.message, e)
                            it.resumeWithException(InvalidCredentialException(e.message, e))
                        }
                        else -> it.resumeWithException(e)
                    }
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun signUpWithPhone(phone: String, code: String) {
        signInWithPhone(phone, code)
        try {
            firestore.collection(COL_USERS).document(auth.uid!!)
                .set(mapOf(FLD_USERS_PHONE to phone)).await()
        } catch (e: Exception) {
            auth.signOut()
            throw e
        }
    }

    override suspend fun signInWithPhone(phone: String, code: String) {
        val credentials = PhoneAuthProvider.getCredential(phoneVerificationId!!, code)
        try {
            auth.signInWithCredential(credentials).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(LOG_TAG, e.message, e)
            throw InvalidCredentialException(e.message, e)
        }
        phoneVerificationId = null
        updateUserField()
    }

    override suspend fun isEmailRegistered(email: String) =
        !firestore.collection(COL_USERS).whereEqualTo(FLD_USERS_EMAIL, email).limit(1).get().await()
            .isEmpty

    override suspend fun signUpWithEmail(email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(LOG_TAG, e.message, e)
            throw InvalidCredentialException(e.message, e)
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e(LOG_TAG, e.message, e)
            throw UserCollisionException(e.message, e)
        }
        try {
            firestore.collection(COL_USERS).document(auth.uid!!)
                .set(mapOf(FLD_USERS_EMAIL to email)).await()
        } catch (e: Exception) {
            auth.signOut()
            throw e
        }
        try {
            auth.currentUser!!.sendEmailVerification().await()
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message, e)
        }
        updateUserField()
    }

    override suspend fun sendVerificationEmail() {
        auth.currentUser!!.sendEmailVerification().await()
    }

    override suspend fun signInWithEmail(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(LOG_TAG, e.message, e)
            throw InvalidCredentialException(e.message, e)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e(LOG_TAG, e.message, e)
            throw InvalidCredentialException(e.message, e)
        }
        updateUserField()
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e(LOG_TAG, e.message, e)
            throw InvalidUserException(e.message, e)
        }
    }

    override fun signOut() {
        auth.signOut()
        updateUserField()
    }
}