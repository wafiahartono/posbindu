package id.ac.uns.posbindu.auth

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.auth.PhoneVerificationFragment.Mode
import id.ac.uns.posbindu.databinding.FragmentSignUpBinding
import id.ac.uns.posbindu.etc.PHONE_REGEX
import id.ac.uns.posbindu.etc.inputOrNull
import id.ac.uns.posbindu.etc.requireInput
import id.ac.uns.posbindu.etc.setError
import id.ac.uns.posbindu.etc.showSnackbar
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.etc.validateInput
import id.ac.uns.posbindu.navigation.Action
import id.ac.uns.posbindu.navigation.Navigator
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
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
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
            binding.passwordConfirmInputLayout.visibility = visibility
        }
        binding.userAgreement.movementMethod = LinkMovementMethod.getInstance()
        binding.signUp.setOnClickListener { signUp() }
        binding.signIn.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun signUp() {
        val credential = binding.credentialInputLayout
            .requireInput { binding.credential.inputOrNull() } ?: return
        val password: String?
        if (credential.matches(PHONE_REGEX)) password = null
        else {
            password = binding.passwordInputLayout
                .requireInput { binding.password.inputOrNull() } ?: return
            if (!binding.passwordInputLayout.validateInput(binding.root, R.string.weak_password_msg)
                { password.length > 7 }
            ) return
            val passwordConfirm = binding.passwordConfirmInputLayout
                .requireInput { binding.passwordConfirm.inputOrNull() } ?: return
            if (!binding.passwordConfirmInputLayout.validateInput(
                    binding.root, R.string.password_confirm_not_match_msg
                ) { passwordConfirm == password }
            ) return
        }
        if (!binding.userAgreement.isChecked) {
            Toast.makeText(context, R.string.user_agreement_required_msg, Toast.LENGTH_LONG)
                .show()
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            binding.signUp.isEnabled = false
            binding.progressIndicator.show()
            if (password == null) signUpWithPhone(credential)
            else signUpWithEmail(credential, password)
        }
    }

    private suspend fun signUpWithPhone(phone: String) {
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
                putInt(PhoneVerificationFragment.ARG_MODE, Mode.SIGN_UP.ordinal)
                putString(PhoneVerificationFragment.ARG_PHONE, phone)
            })
            return
        }
        binding.signUp.isEnabled = true
        binding.progressIndicator.hide()
    }

    private suspend fun signUpWithEmail(email: String, password: String) {
        var success = false
        try {
            authService.signUpWithEmail(email, password)
            success = true
        } catch (e: InvalidCredentialException) {
            binding.credentialInputLayout.setError(binding.root, R.string.email_not_valid_msg)
        } catch (e: UserCollisionException) {
            binding.credentialInputLayout.setError(binding.root, R.string.email_collision_msg)
        } catch (e: Exception) {
            binding.root.showUnexpectedErrorSnackbar(e)
        }
        if (success) {
            navigator.navigateAuth(authService.user)
            return
        }
        binding.signUp.isEnabled = true
        binding.progressIndicator.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}