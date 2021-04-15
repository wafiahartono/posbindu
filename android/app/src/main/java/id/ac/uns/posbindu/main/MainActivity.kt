package id.ac.uns.posbindu.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.SlideDistanceProvider
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.auth.AuthService
import id.ac.uns.posbindu.config.ConfigService
import id.ac.uns.posbindu.databinding.ActivityMainBinding
import id.ac.uns.posbindu.etc.LOG_TAG
import id.ac.uns.posbindu.navigation.Navigator
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var configService: ConfigService

    @Inject
    lateinit var authService: AuthService

    private val fragmentLifecycleCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?,
        ) {
            setBottomNavVisibility(navigator.isBottomNavMenu(f))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallback, false)
        binding.bottomNav.setOnNavigationItemReselectedListener { }
        binding.bottomNav.setOnNavigationItemSelectedListener {
            navigator.navigateBottomNav(it)
            return@setOnNavigationItemSelectedListener true
        }
        checkForUpdate()
    }

    private fun setBottomNavVisibility(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        if (visibility == binding.bottomNav.visibility) return
        TransitionManager.beginDelayedTransition(binding.root, MaterialFade().apply {
            secondaryAnimatorProvider =
                SlideDistanceProvider(if (visible) Gravity.BOTTOM else Gravity.TOP)
        })
        binding.bottomNav.visibility = visibility
    }

    private fun checkForUpdate() {
        lifecycleScope.launch {
            var shouldUpdate = false
            try {
                shouldUpdate = configService.shouldUpdate()
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message, e)
            }
            if (shouldUpdate) MaterialAlertDialogBuilder(this@MainActivity)
                .setCancelable(false)
                .setTitle(R.string.update_dialog_title)
                .setMessage(R.string.update_dialog_msg)
                .setPositiveButton(R.string.update) { _, _ ->
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.update_link)))
                    )
                    finish()
                }
                .setNegativeButton(R.string.later) { _, _ -> finish() }
                .show()
            else navigator.navigateAuth(authService.user)
        }
    }
}