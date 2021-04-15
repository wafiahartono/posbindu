package id.ac.uns.posbindu.config

import com.google.firebase.firestore.FirebaseFirestore
import id.ac.uns.posbindu.firebase.COL_META
import id.ac.uns.posbindu.firebase.DOC_META_ANDROID
import id.ac.uns.posbindu.firebase.FLD_META_ANDROID_VERSION
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseConfigService @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ConfigService() {
    override suspend fun getLatestVersion() = firestore
        .collection(COL_META).document(DOC_META_ANDROID).get().await()
        .getLong(FLD_META_ANDROID_VERSION) ?: 1
}