package id.ac.uns.posbindu.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.auth.PhoneVerificationFragment.Companion.ARG_MODE
import id.ac.uns.posbindu.auth.PhoneVerificationFragment.Companion.ARG_PHONE
import id.ac.uns.posbindu.auth.PhoneVerificationFragment.Mode
import id.ac.uns.posbindu.databinding.FragmentSignInBinding
import id.ac.uns.posbindu.etc.PHONE_REGEX
import id.ac.uns.posbindu.etc.inputOrNull
import id.ac.uns.posbindu.etc.requireInput
import id.ac.uns.posbindu.etc.setError
import id.ac.uns.posbindu.etc.showSnackbar
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.navigation.Action
import id.ac.uns.posbindu.navigation.Navigator
import id.ac.uns.posbindu.navigation.Screen
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.credential.doAfterTextChanged {
            val text = it?.toString()?.trim()
            val visibility = if (text?.matches(PHONE_REGEX) == true) View.GONE else View.VISIBLE
            if (visibility == binding.passwordInputLayout.visibility) return@doAfterTextChanged
            TransitionManager.beginDelayedTransition(binding.root)
            binding.passwordInputLayout.visibility = visibility
        }
        binding.signIn.setOnClickListener { signIn() }
        binding.resetPassword.setOnClickListener { navigator.navigateTo(Screen.PASSWORD_RESET) }
        binding.signUp.setOnClickListener { navigator.navigateTo(Screen.SIGN_UP) }
    }

    private fun signIn() {
        val credential = binding.credentialInputLayout
            .requireInput(binding.root) { binding.credential.inputOrNull() } ?: return
        val password = if (credential.matches(PHONE_REGEX)) null else binding.passwordInputLayout
            .requireInput(binding.root) { binding.password.inputOrNull() } ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            binding.signIn.isEnabled = false
            binding.progressIndicator.show()
            if (password == null) signInWithPhone(credential)
            else signInWithEmail(credential, password)
        }
    }

    private suspend fun signInWithPhone(phone: String) {
        var success = false
        try {
            authService.sendVerificationSMS(phone)
            success = true
        } catch (e: InvalidCredentialException) {
            binding.root.showSnackbar(R.string.invalid_phone_number_msg)
        } catch (e: Exception) {
            binding.root.showUnexpectedErrorSnackbar(e)
        }
        if (success) {
            navigator.navigateTo(Action.VERIFY_PHONE, Bundle().apply {
                putInt(ARG_MODE, Mode.SIGN_IN.ordinal)
                putString(ARG_PHONE, phone)
            })
            return
        }
        binding.signIn.isEnabled = true
        binding.progressIndicator.hide()
    }

    private suspend fun signInWithEmail(email: String, password: String) {
        var success = false
        try {
            authService.signInWithEmail(email, password)
            success = true
        } catch (e: InvalidCredentialException) {
            binding.credentialInputLayout.error = ""
            binding.passwordInputLayout
                .setError(binding.root, R.string.invalid_email_or_password_msg)
        } catch (e: Exception) {
            binding.root.showUnexpectedErrorSnackbar(e)
        }
        if (success) {
            navigator.navigateAuth(authService.user)
            return
        }
        binding.signIn.isEnabled = true
        binding.progressIndicator.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}