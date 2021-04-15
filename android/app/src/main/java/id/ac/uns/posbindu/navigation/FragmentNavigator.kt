package id.ac.uns.posbindu.navigation

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.auth.EmailVerificationFragment
import id.ac.uns.posbindu.auth.PasswordResetFragment
import id.ac.uns.posbindu.auth.PhoneVerificationFragment
import id.ac.uns.posbindu.auth.SignInFragment
import id.ac.uns.posbindu.auth.SignUpFragment
import id.ac.uns.posbindu.auth.User
import id.ac.uns.posbindu.form.FormFragment
import id.ac.uns.posbindu.form.QuestionFragment
import id.ac.uns.posbindu.home.HomeFragment
import id.ac.uns.posbindu.profile.NINFragment
import id.ac.uns.posbindu.profile.ProfileFragment
import id.ac.uns.posbindu.report.ReportFragment
import javax.inject.Inject

class FragmentNavigator @Inject constructor(
    activity: FragmentActivity,
) : Navigator {
    private val fragmentManager = activity.supportFragmentManager

    override fun navigateTo(action: Action, bundle: Bundle?) {
        val popBackStack = when (action) {
            Action.AUTHENTICATE -> true
            Action.VERIFY_PHONE -> false
            Action.VERIFY_EMAIL -> true
            Action.CREATE_USER_PROFILE -> true
            Action.FINISH_AUTHENTICATE -> true
        }
        val fragment = when (action) {
            Action.AUTHENTICATE -> SignInFragment()
            Action.VERIFY_PHONE -> PhoneVerificationFragment()
            Action.VERIFY_EMAIL -> EmailVerificationFragment()
            Action.CREATE_USER_PROFILE -> NINFragment()
            Action.FINISH_AUTHENTICATE -> HomeFragment()
        }
        bundle?.let { fragment.arguments = it }
        if (popBackStack) fragmentManager.popBackStack(null, 0)
        fragmentManager.commit {
            replace(R.id.fragment_container, fragment, fragment::class.simpleName)
        }
    }

    override fun navigateTo(screen: Screen, bundle: Bundle?) {
        val fragment = when (screen) {
            Screen.PASSWORD_RESET -> PasswordResetFragment()
            Screen.SIGN_UP -> SignUpFragment()
            Screen.FORM -> FormFragment()
            Screen.QUESTION -> QuestionFragment()
            else -> return
        }
        bundle?.let { fragment.arguments = it }
        fragmentManager.commit {
            addToBackStack(fragment::class.simpleName)
            replace(R.id.fragment_container, fragment, fragment::class.simpleName)
        }
    }

    override fun navigatePrev() = fragmentManager.popBackStack()

    override fun navigateAuth(user: User?) {
        val pair = when {
            user == null -> Pair(Action.AUTHENTICATE, null)
            user.email != null && !user.emailIsVerified -> Pair(Action.VERIFY_EMAIL, null)
            !user.profileIsComplete -> Pair(Action.CREATE_USER_PROFILE, Bundle().apply {
                putBoolean(NINFragment.ARG_MODE, true)
            })
            else -> Pair(Action.FINISH_AUTHENTICATE, null)
        }
        navigateTo(pair.first, pair.second)
    }

    override fun isBottomNavMenu(fragment: Fragment) =
        fragment is HomeFragment || fragment is ReportFragment || fragment is ProfileFragment

    override fun navigateBottomNav(menu: MenuItem) {
        val screen = when (menu.itemId) {
            R.id.menu_home -> Screen.HOME
            R.id.menu_report -> Screen.REPORT
            R.id.menu_profile -> Screen.PROFILE
            else -> throw IllegalArgumentException("Menu item ${menu.title} is not bottom nav menu")
        }
        val fragment = fragmentManager.findFragmentByTag(
            when (screen) {
                Screen.HOME -> HomeFragment::class.simpleName
                Screen.REPORT -> ReportFragment::class.simpleName
                Screen.PROFILE -> ProfileFragment::class.simpleName
                else -> throw IllegalArgumentException("Screen $screen is not bottom nav menu")
            }
        ) ?: when (screen) {
            Screen.HOME -> HomeFragment()
            Screen.REPORT -> ReportFragment()
            Screen.PROFILE -> ProfileFragment()
            else -> throw IllegalArgumentException("Screen $screen is not bottom nav menu")
        }
        fragmentManager.commit {
            replace(R.id.fragment_container, fragment, fragment::class.simpleName)
        }
    }
}