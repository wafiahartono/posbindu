package id.ac.uns.posbindu.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.databinding.FragmentPasswordResetBinding
import id.ac.uns.posbindu.etc.inputOrNull
import id.ac.uns.posbindu.etc.requireInput
import id.ac.uns.posbindu.etc.setError
import id.ac.uns.posbindu.etc.showSnackbar
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordResetFragment : Fragment() {
    private var _binding: FragmentPasswordResetBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authService: AuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendPasswordResetEmail.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {
        val email = binding.emailInputLayout.requireInput { binding.email.inputOrNull() } ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            binding.sendPasswordResetEmail.isEnabled = false
            binding.progressIndicator.show()
            var success = false
            try {
                authService.sendPasswordResetEmail(email)
                success = true
            } catch (e: InvalidCredentialException) {
                binding.emailInputLayout.setError(binding.root, R.string.email_not_valid_msg)
            } catch (e: InvalidUserException) {
                binding.emailInputLayout.setError(binding.root, R.string.email_not_registered_msg)
            } catch (e: Exception) {
                binding.root.showUnexpectedErrorSnackbar(e)
            }
            if (success) binding.root.showSnackbar(R.string.password_reset_email_sent_msg)
            binding.sendPasswordResetEmail.isEnabled = true
            binding.progressIndicator.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}