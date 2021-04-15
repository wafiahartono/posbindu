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
import id.ac.uns.posbindu.databinding.FragmentEmailVerificationBinding
import id.ac.uns.posbindu.etc.showSnackbar
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.navigation.Navigator
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmailVerificationFragment : Fragment() {
    private var _binding: FragmentEmailVerificationBinding? = null
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signIn.setOnClickListener { signIn() }
        binding.sendVerificationEmail.setOnClickListener { sendVerificationEmail() }
        binding.signOut.setOnClickListener { signOut() }
    }

    private fun signIn() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.signIn.isEnabled = false
            binding.progressIndicator.show()
            var success = false
            try {
                authService.refreshUser()
                success = true
            } catch (e: Exception) {
                binding.root.showUnexpectedErrorSnackbar(e)
            }
            if (success) {
                if (authService.user!!.emailIsVerified) {
                    navigator.navigateAuth(authService.user)
                    return@launch
                } else binding.root.showSnackbar(R.string.email_not_verified_msg)
            }
            binding.signIn.isEnabled = true
            binding.progressIndicator.hide()
        }
    }

    private fun sendVerificationEmail() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.sendVerificationEmail.isEnabled = false
            binding.progressIndicator.show()
            var success = false
            try {
                authService.sendVerificationEmail()
                success = true
            } catch (e: Exception) {
                binding.root.showUnexpectedErrorSnackbar(e)
            }
            if (success) binding.root.showSnackbar(R.string.verification_email_sent_msg)
            binding.sendVerificationEmail.isEnabled = true
            binding.progressIndicator.hide()
        }
    }

    private fun signOut() {
        authService.signOut()
        navigator.navigateAuth(authService.user)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}