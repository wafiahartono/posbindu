package id.ac.uns.posbindu.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.imageLoader
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.auth.AuthService
import id.ac.uns.posbindu.databinding.FragmentProfileBinding
import id.ac.uns.posbindu.etc.DATE_FORMAT
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.navigation.Action
import id.ac.uns.posbindu.navigation.Navigator
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var navigator: Navigator

    private var profile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnRefreshListener { refreshUserData() }
        binding.buttonSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.sign_out_confirm_msg)
                .setPositiveButton(R.string.sign_out) { _, _ ->
                    authService.signOut()
                    navigator.navigateTo(Action.AUTHENTICATE)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
        if (profile == null) refreshUserData()
    }

    private fun refreshUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
//            profile = try {
////                authService.getUserData()
//            } catch (e: Exception) {
//                binding.root.showUnexpectedErrorSnackbar(e)
//                return@launch
//            }
            updateUserDataViews()
        }
    }

    private fun updateUserDataViews() {
        binding.apply {
            val user = authService.user!!
            val userData = this@ProfileFragment.profile!!
            if (user.photo == null) {
                imageViewProfilePicture.load(
                    when (userData.sex!!) {
                        Sex.MALE -> R.drawable.illustration_male_avatar
                        Sex.FEMALE -> R.drawable.illustration_female_avatar
                    },
                    requireContext().imageLoader
                )
            } else imageViewProfilePicture.load(user.photo, requireContext().imageLoader)
            textViewName.text = user.name
            textViewNationalIdentityNumber.text = userData.nin
            textViewEmailAddress.text = user.email
            textViewSex.text = resources.getStringArray(R.array.sex)[userData.sex!!.ordinal]
//            textViewPlaceAndDateOfBirth.text = resources.getString(
//                R.string.birthplace_birthdate_txt,
//                userData.birthPlace,
//                DATE_FORMAT.format(userData.birthDate!!)
//            )
            textViewMaritalStatus.text = resources
                .getStringArray(R.array.marital_status)[userData.maritalStatus!!.ordinal]
            textViewAddress.text = userData.address
            textViewRecentEducationLevel.text = resources
                .getStringArray(R.array.education_level)[userData.education!!.ordinal]
            textViewMonthlySalary.text = resources
                .getStringArray(R.array.monthly_salary_level)[userData.salary!!.ordinal]
            root.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}