package id.ac.uns.posbindu.config

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ActivityContext
import id.ac.uns.posbindu.etc.LAST_UPDATE_CHECK
import id.ac.uns.posbindu.etc.LATEST_VER
import id.ac.uns.posbindu.etc.SHARED_PREF
import java.util.Date
import javax.inject.Inject

abstract class ConfigService {
    @Inject
    @ActivityContext
    protected lateinit var context: Context

    abstract suspend fun getLatestVersion(): Long

    suspend fun shouldUpdate(): Boolean {
        val ver = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        val sharedPref = context.getSharedPreferences(SHARED_PREF, AppCompatActivity.MODE_PRIVATE)
        if (sharedPref.getLong(LATEST_VER, 1) > ver) return true
        val now = Date().time
        if (now - Date(sharedPref.getLong(LAST_UPDATE_CHECK, 0)).time < 864_000) return false
        val latestVer = getLatestVersion()
        sharedPref.edit {
            putLong(LATEST_VER, latestVer)
            putLong(LAST_UPDATE_CHECK, now)
        }
        return latestVer > ver
    }
}