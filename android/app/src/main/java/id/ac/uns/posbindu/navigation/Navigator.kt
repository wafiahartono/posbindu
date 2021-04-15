package id.ac.uns.posbindu.navigation

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import id.ac.uns.posbindu.auth.User

interface Navigator {
    fun navigateTo(action: Action, bundle: Bundle? = null)
    fun navigateTo(screen: Screen, bundle: Bundle? = null)
    fun navigatePrev()
    fun navigateAuth(user: User?)
    fun isBottomNavMenu(fragment: Fragment): Boolean
    fun navigateBottomNav(menu: MenuItem)
}