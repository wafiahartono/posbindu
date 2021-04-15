package id.ac.uns.posbindu.main

import android.app.Application
import android.net.Uri
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.map.Mapper
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.HiltAndroidApp
import io.github.rosariopfernandes.firecoil.StorageReferenceFetcher

@HiltAndroidApp
class PosbinduApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader() = ImageLoader.Builder(applicationContext)
        .componentRegistry {
            add(StorageReferenceFetcher())
            add(object : Mapper<Uri, StorageReference> {
                override fun map(data: Uri) = Firebase.storage.reference.child(data.toString())
            })
            add(SvgDecoder(applicationContext))
        }
        .build()
}