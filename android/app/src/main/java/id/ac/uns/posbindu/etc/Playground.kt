package id.ac.uns.posbindu.etc

import java.util.concurrent.TimeUnit

fun main() {
    println(TimeUnit.DAYS.toMillis(1))
}

//SignUpFragment
//            val nationalIdentityNumber = textInputLayoutNationalIdentityNumber.requireInput {
//                editTextNationalIdentityNumber.inputOrNull()
//            } ?: return
//            if (!textInputLayoutNationalIdentityNumber.validateInput(
//                    resId = R.string.national_identity_number_is_not_valid
//                ) { nationalIdentityNumber.length == 16 }
//            ) return

//        if (!firestore.collection(COLLECTION_USERS)
//                .whereEqualTo(FIELD_USERS_NATIONAL_IDENTITY_NUMBER, nationalIdentityNumber)
//                .get().await()
//                .isEmpty
//        ) throw NINCollisionException()

//        while (
//            try {
//                firestore.collection(COLLECTION_USERS).document(auth.uid!!)
//                    .update(FIELD_USERS_NATIONAL_IDENTITY_NUMBER, nationalIdentityNumber)
//                    .await()
//                false
//            } catch (e: Exception) {
//                Log.e(LOG_TAG, e)
//                true
//            }
//        ) delay(300)
