package id.ac.uns.posbindu.profile

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ArrayRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.databinding.FragmentUpdateProfileBinding
import id.ac.uns.posbindu.etc.*
import id.ac.uns.posbindu.navigation.Navigator
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {
    enum class Mode {
        CREATE_SELF, UPDATE_SELF, CREATE_OTHER, UPDATE_OTHER
    }

    private lateinit var mode: Mode
    private val viewModel: UpdateProfileViewModel by viewModels()
    private val profile = mutableMapOf<String, Any>()

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var navigator: Navigator

    private val getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it == null) return@registerForActivityResult
        profile[KEY_PHOTO] = it
        binding.photo.load(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        mode = Mode.values()[requireArguments().getInt(ARG_MODE)]
        viewModel.setNin(requireArguments().getString(ARG_NIN)!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photo.setOnClickListener { getImageLauncher.launch("image/*") }
        binding.nin.setText(requireArguments().getString(ARG_NIN))
        binding.sex.setAdapter(createArrayAdapter(R.array.sex))
        binding.sex.setOnItemClickListener { _, _, i, _ ->
            val sex = Sex.values()[i]
            if (!profile.containsKey(KEY_PHOTO)) binding.photo.load(
                when (sex) {
                    Sex.MALE -> R.drawable.illustration_male_avatar
                    Sex.FEMALE -> R.drawable.illustration_female_avatar
                }
            )
            profile[KEY_SEX] = sex
        }
        binding.birthdateInputLayout.setStartIconOnClickListener { showBirthdatePicker() }
        binding.birthdate.inputType = InputType.TYPE_NULL
        binding.maritalStatus.setAdapter(createArrayAdapter(R.array.marital_status))
        binding.maritalStatus.setOnItemClickListener { _, _, i, _ ->
            profile[KEY_MARITAL_STATUS] = MaritalStatus.values()[i]
        }
        binding.education.setAdapter(createArrayAdapter(R.array.education_level))
        binding.education.setOnItemClickListener { _, _, i, _ ->
            profile[KEY_EDUCATION] = Education.values()[i]
        }
        binding.salary.setAdapter(createArrayAdapter(R.array.monthly_salary_level))
        binding.salary.setOnItemClickListener { _, _, i, _ ->
            profile[KEY_SALARY] = Salary.values()[i]
        }
        binding.save.setOnClickListener { save() }
        viewModel.profile.observe(viewLifecycleOwner) { resource ->
            if (resource !is Resource.Success) return@observe
            if (resource.data == null) return@observe
            resource.data.photo?.let {
                profile[KEY_PHOTO] = it
                binding.photo.load(it)
            }
            resource.data.name?.let { binding.name.setText(it) }
            resource.data.sex?.let {
                profile[KEY_SEX] = it
                binding.sex.setSelection(it.ordinal)
            }
            resource.data.birthInfo?.let {
                binding.birthplace.setText(it.place)
                profile[KEY_BIRTHDATE] = it.date
                binding.birthdate.setText(DATE_FORMAT.format(it.date))
            }
            resource.data.maritalStatus?.let {
                profile[KEY_MARITAL_STATUS] = it
                binding.maritalStatus.setSelection(it.ordinal)
            }
            resource.data.address?.let { binding.address.setText(it) }
            resource.data.education?.let {
                profile[KEY_EDUCATION] = it
                binding.education.setSelection(it.ordinal)
            }
            resource.data.salary?.let {
                profile[KEY_SALARY] = it
                binding.salary.setSelection(it.ordinal)
            }
        }
    }

    private fun createArrayAdapter(@ArrayRes resId: Int) = ArrayAdapter(
        requireContext(), R.layout.list_item, resources.getStringArray(resId)
    )

    private fun showBirthdatePicker() {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.select_birth_date)
            .setSelection(946_684_800_000)
            .build().apply {
                addOnPositiveButtonClickListener { date ->
                    val birthdate = Calendar.getInstance().apply {
                        time = Date(date)
                        set(Calendar.HOUR, 0)
                    }.time
                    profile[KEY_BIRTHDATE] = birthdate
                    binding.editTextDateOfBirth.setText(DATE_FORMAT.format(birthdate))
                }
            }
            .show(childFragmentManager, null)
    }

    private fun save() {
        userData = userData.copy(
            name = textInputLayoutName.requireInput(root) {
                editTextName.inputOrNull()
            } ?: return@setOnClickListener
        )
        textInputLayoutSex.requireInput(root) {
            userData.sex
        } ?: return@setOnClickListener
        userData = userData.copy(
            placeOfBirth = textInputLayoutPlaceOfBirth.requireInput(root) {
                editTextPlaceOfBirth.inputOrNull()
            } ?: return@setOnClickListener
        )
        textInputLayoutDateOfBirth.requireInput(root) {
            userData.dateOfBirth
        } ?: return@setOnClickListener
        textInputLayoutMaritalStatus.requireInput {
            userData.maritalStatus
        } ?: return@setOnClickListener
        userData = userData.copy(
            address = textInputLayoutAddress.requireInput {
                editTextAddress.inputOrNull()
            } ?: return@setOnClickListener
        )
        textInputLayoutRecentEducationLevel.requireInput {
            userData.recentEducationLevel
        } ?: return@setOnClickListener
        textInputLayoutMonthlySalary.requireInput {
            userData.monthlySalary
        } ?: return@setOnClickListener
        userData = userData.copy(
            telephoneNumber = textInputLayoutTelephoneNumber.requireInput {
                editTextTelephoneNumber.inputOrNull()
            } ?: return@setOnClickListener
        )
        viewLifecycleOwner.lifecycleScope.launch {
            buttonSave.isEnabled = false
            progressIndicator.show()
            try {
                authService.updateUserData(userData)
                navigator.navigateTo(Action.FINISH_AUTHENTICATE)
                return@launch
            } catch (e: Exception) {
                root.showUnexpectedErrorSnackbar(e)
            }
            buttonSave.isEnabled = true
            progressIndicator.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_MODE = "mode"
        const val ARG_NIN = "nin"
        private const val KEY_PHOTO = "photo"
        private const val KEY_SEX = "sex"
        private const val KEY_BIRTHDATE = "birthdate"
        private const val KEY_MARITAL_STATUS = "marital_status"
        private const val KEY_EDUCATION = "education"
        private const val KEY_SALARY = "salary"
    }
}