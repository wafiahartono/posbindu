package id.ac.uns.posbindu.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ActivityContext
import id.ac.uns.posbindu.firebase.COL_USERS
import id.ac.uns.posbindu.firebase.FLD_USERS_ADDRESS
import id.ac.uns.posbindu.firebase.FLD_USERS_BIRTH_INFO
import id.ac.uns.posbindu.firebase.FLD_USERS_EDUCATION
import id.ac.uns.posbindu.firebase.FLD_USERS_EMAIL
import id.ac.uns.posbindu.firebase.FLD_USERS_MARITAL_STATUS
import id.ac.uns.posbindu.firebase.FLD_USERS_NAME
import id.ac.uns.posbindu.firebase.FLD_USERS_NIN
import id.ac.uns.posbindu.firebase.FLD_USERS_PHONE
import id.ac.uns.posbindu.firebase.FLD_USERS_PHOTO
import id.ac.uns.posbindu.firebase.FLD_USERS_SALARY
import id.ac.uns.posbindu.firebase.FLD_USERS_SEX
import id.ac.uns.posbindu.firebase.KEY_USERS_BIRTH_INFO_DATE
import id.ac.uns.posbindu.firebase.KEY_USERS_BIRTH_INFO_PLACE
import id.ac.uns.posbindu.firebase.REF_USER_PHOTOS
import java.io.ByteArrayOutputStream
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class FirebaseProfileRepository @Inject constructor(
    @ActivityContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : ProfileRepository() {
    private fun compressPhoto(uri: Uri): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val inputStream = context.contentResolver.openInputStream(uri).use { it }!!
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.use { it.toByteArray() }
    }

    private fun profileToMap(profile: Profile) = mutableMapOf<String, Any>().apply {
        profile.phone?.let { put(FLD_USERS_PHONE, it) }
        profile.email?.let { put(FLD_USERS_EMAIL, it) }
        profile.name?.let { put(FLD_USERS_NAME, it) }
        profile.photo?.let { put(FLD_USERS_PHOTO, it) }
        profile.nin?.let { put(FLD_USERS_NIN, it) }
        profile.sex?.let { put(FLD_USERS_SEX, it) }
        profile.birthInfo?.let {
            put(
                FLD_USERS_BIRTH_INFO, hashMapOf(
                    KEY_USERS_BIRTH_INFO_PLACE to it.place,
                    KEY_USERS_BIRTH_INFO_DATE to Timestamp(it.date)
                )
            )
        }
        profile.maritalStatus?.let { put(FLD_USERS_MARITAL_STATUS, it) }
        profile.address?.let { put(FLD_USERS_ADDRESS, it) }
        profile.education?.let { put(FLD_USERS_EDUCATION, it) }
        profile.salary?.let { put(FLD_USERS_SALARY, it) }
    }

    private fun docToProfile(doc: DocumentSnapshot) = doc.run {
        Profile(
            id = getString(id),
            phone = getString(FLD_USERS_PHONE),
            email = getString(FLD_USERS_EMAIL),
            name = getString(FLD_USERS_NAME),
            photo = getString(FLD_USERS_PHOTO)?.let { Uri.parse(it) },
            nin = getString(FLD_USERS_NIN),
            sex = getString(FLD_USERS_SEX)?.let { Sex.valueOf(it) },
            birthInfo = getField<Map<String, Any>>(FLD_USERS_BIRTH_INFO)?.let {
                BirthInfo(
                    it[KEY_USERS_BIRTH_INFO_PLACE] as? String ?: return@let null,
                    it[KEY_USERS_BIRTH_INFO_DATE] as? Date ?: return@let null
                )
            },
            maritalStatus = getString(FLD_USERS_MARITAL_STATUS)?.let { MaritalStatus.valueOf(it) },
            address = getString(FLD_USERS_ADDRESS),
            education = getString(FLD_USERS_EDUCATION)?.let { Education.valueOf(it) },
            salary = getString(FLD_USERS_SALARY)?.let { Salary.valueOf(it) }
        )
    }

    //TODO implement
    override suspend fun isAllowedToCreate(nin: String) = true
//        firestore.collection(COL_USERS).document(auth.uid!!).get().await()
//            .getField<List<String>>(FLD_USERS_ALLOWED_NIN)?.contains(nin) ?: false

    /*
    Create a new profile. This method should be invoked if and only if the profile has not already
    been created (i.e. two documents cannot have the same NIN field value). The check for duplicate
    NIN is not done here and should be checked before invoking this method.

    TODO Check if user has permission to write this profile (implement with sec. rule instead)

    Cases:
    1. Create profile for current user.
    2. Create profile for another user.

    profile.id == null -> create profile for another user (case 2)
               != null -> create profile for current user (case 1)
     */
    override suspend fun createProfile(profile: Profile) {
        if (profile.id != null) migrateProfile(profile.nin!!, profile.id)
        val docRef = if (profile.id == null) firestore.collection(COL_USERS).document()
        else firestore.collection(COL_USERS).document(profile.id)
        val photoRef = if (profile.photo == null) null
        else storage.reference.child(REF_USER_PHOTOS).child(docRef.id)
        val photoRefUri = if (profile.photo == null) null else Uri.parse(photoRef!!.path)
        awaitAll(
            profile.photo?.let { photoRef!!.putBytes(compressPhoto(it)).asDeferred() }
                ?: CompletableDeferred(Unit),
            if (profile.id == null && profile.name == null && profile.photo == null)
                CompletableDeferred(Unit)
            else auth.currentUser!!.updateProfile(
                UserProfileChangeRequest.Builder().apply {
                    profile.name?.let { displayName = it }
                    profile.photo?.let { photoUri = photoRefUri }
                }.build()
            ).asDeferred(),
            docRef.set(profileToMap(profile.copy(photo = photoRefUri)), SetOptions.merge())
                .asDeferred()
        )
    }

    private suspend fun migrateProfile(nin: String, id: String) {
        val data = firestore.collection(COL_USERS).whereEqualTo(FLD_USERS_NIN, nin).limit(1)
            .get().await().let { if (it.isEmpty) return else it.documents[0].data!! }
        firestore.collection(COL_USERS).document(id).set(data, SetOptions.merge()).await()
    }

    /*
    profile.id is firestore document id and, if a user associated with the profile exists,
    corresponding user id.

    Cases:
    1. Update current user profile.
    2. Update another user profile.

    profile.id!! == auth.uid!! -> case 1
                 != auth.uid!! -> case 2
     */
    override suspend fun updateProfile(profile: Profile) {
        val docRef = firestore.collection(COL_USERS).document(profile.id!!)
        val photoRef = if (profile.photo == null) null
        else storage.reference.child(REF_USER_PHOTOS).child(docRef.id)
        val photoRefUri = if (profile.photo == null) null else Uri.parse(photoRef!!.path)
        awaitAll(
            profile.photo?.let { photoRef!!.putBytes(compressPhoto(it)).asDeferred() }
                ?: CompletableDeferred(Unit),
            if (profile.id != auth.uid && profile.name == null && profile.photo == null)
                CompletableDeferred(Unit)
            else auth.currentUser!!.updateProfile(
                UserProfileChangeRequest.Builder().apply {
                    profile.name?.let { displayName = it }
                    profile.photo?.let { photoUri = photoRefUri }
                }.build()
            ).asDeferred(),
            docRef.set(profileToMap(profile.copy(photo = photoRefUri)), SetOptions.merge())
                .asDeferred()
        )
    }

    override suspend fun getProfile() = firestore.collection(COL_USERS).document(auth.uid!!)
        .get().await().let { if (it.exists()) docToProfile(it) else null }

    override suspend fun getProfile(nin: String) = firestore.collection(COL_USERS)
        .whereEqualTo(FLD_USERS_NIN, nin).limit(1).get().await()
        .let { if (it.isEmpty) null else docToProfile(it.documents[0]) }
}