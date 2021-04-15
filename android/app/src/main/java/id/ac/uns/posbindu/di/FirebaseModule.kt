package id.ac.uns.posbindu.di

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import id.ac.uns.posbindu.etc.LOG_TAG

@Module
@InstallIn(ActivityComponent::class)
object FirebaseModule {
    private const val EMULATOR_HOST = "192.168.43.37"

    @ActivityScoped
    @Provides
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance().apply {
        Log.d(LOG_TAG, "${this::class.simpleName} : providing auth (${hashCode()})")
//        if (BuildConfig.DEBUG) {
//            useEmulator(EMULATOR_HOST, 9099)
//        }
    }

    @ActivityScoped
    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore.apply {
        Log.d(LOG_TAG, "${this::class.simpleName} : providing firestore (${hashCode()})")
//        if (BuildConfig.DEBUG) {
//            firestoreSettings = FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(false)
//                .build()
//            useEmulator(EMULATOR_HOST, 8080)
//        }
    }

    @ActivityScoped
    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage.also {
        Log.d(LOG_TAG, "${this::class.simpleName} : providing storage (${it.hashCode()})")
    }
}