package id.ac.uns.posbindu.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.databinding.FragmentPhoneVerificationBinding
import id.ac.uns.posbindu.etc.LOG_TAG
import id.ac.uns.posbindu.etc.showSnackbar
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.navigation.Navigator
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhoneVerificationFragment : Fragment() {
    enum class Mode {
        SIGN_IN, SIGN_UP
    }

    private lateinit var mode: Mode
    private lateinit var phone: String

    private var _binding: FragmentPhoneVerificationBinding? = null
    private val binding get() = _binding!!

    private var _verificationCodeEditText: VerificationCodeEditText? = null
    private val verificationCodeEditText get() = _verificationCodeEditText!!

    private var resendCodeCountDownTimer = object : CountDownTimer(60_000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.sendCode.text =
                getString(R.string.send_verification_code_timer, millisUntilFinished / 1000)
        }

        override fun onFinish() {
            Log.d(LOG_TAG, "PhoneVerificationFragment resendCodeCountDownTimer onFinish")
            binding.sendCode.isEnabled = true
            binding.sendCode.setText(R.string.send_verification_code)
        }
    }

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        mode = Mode.values()[requireArguments().getInt(ARG_MODE)]
        phone = PhoneNumberUtils.formatNumberToE164(requireArguments().getString(ARG_PHONE)!!, "ID")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhoneVerificationBinding.inflate(inflater, container, false)
        _verificationCodeEditText = VerificationCodeEditText(binding.codeInputLayout, ::processCode)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.subtitle.text = getString(R.string.fragment_phone_verification_subtitle, phone)
        binding.sendCode.setOnClickListener { resendCode() }
        resendCode()
    }

    private fun resendCode() {
        fun cancelTimer() {
            resendCodeCountDownTimer.cancel()
            binding.sendCode.isEnabled = true
            binding.sendCode.setText(R.string.send_verification_code)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            resendCodeCountDownTimer.start()
            binding.sendCode.isEnabled = false
            binding.progressIndicator.show()
            var success = false
            try {
                authService.sendVerificationSMS(phone)
                success = true
            } catch (e: InvalidCredentialException) {
                cancelTimer()
                binding.root.showSnackbar(R.string.invalid_phone_number_msg)
            } catch (e: Exception) {
                cancelTimer()
                binding.root.showUnexpectedErrorSnackbar(e)
            }
            if (success) binding.root.showSnackbar(R.string.verification_code_sent_msg)
            binding.progressIndicator.hide()
        }
    }

    private fun processCode(code: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            verificationCodeEditText.enabled = false
            binding.progressIndicator.show()
            var success = false
            try {
                when (mode) {
                    Mode.SIGN_IN -> authService.signInWithPhone(phone, code)
                    Mode.SIGN_UP -> authService.signUpWithPhone(phone, code)
                }
                success = true
            } catch (e: Exception) {
                binding.root.showSnackbar(R.string.invalid_verification_code_msg)
            } catch (e: Exception) {
                binding.root.showUnexpectedErrorSnackbar(e)
            }
            if (success) {
                navigator.navigateAuth(authService.user)
                return@launch
            }
            verificationCodeEditText.enabled = true
            binding.progressIndicator.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resendCodeCountDownTimer.cancel()
        _verificationCodeEditText = null
        _binding = null
    }

    companion object {
        const val ARG_MODE = "mode"
        const val ARG_PHONE = "phone"
    }
}