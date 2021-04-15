package id.ac.uns.posbindu.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialFadeThrough
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.databinding.FragmentNinBinding
import id.ac.uns.posbindu.etc.inputOrNull
import id.ac.uns.posbindu.etc.requireInput
import id.ac.uns.posbindu.etc.setError
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.navigation.Navigator
import id.ac.uns.posbindu.navigation.Screen
import id.ac.uns.posbindu.profile.UpdateProfileFragment.Companion.ARG_NIN
import javax.inject.Inject
import kotlinx.coroutines.launch

class NINFragment : Fragment() {
    enum class Mode {
        CREATE_SELF, CREATE_OTHER
    }

    private lateinit var mode: Mode

    private var _binding: FragmentNinBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var profileRepository: ProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        mode = Mode.values()[requireArguments().getInt(ARG_MODE)]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.next.setOnClickListener {
            val nin = binding.ninInputLayout
                .requireInput { binding.nin.inputOrNull() } ?: return@setOnClickListener
            viewLifecycleOwner.lifecycleScope.launch {
                var allowed = false
                try {
                    allowed = when (mode) {
                        Mode.CREATE_SELF -> true
                        Mode.CREATE_OTHER -> profileRepository.isAllowedToCreate(nin)
                    }
                } catch (e: Exception) {
                    binding.root.showUnexpectedErrorSnackbar(e)
                }
                if (allowed) navigator
                    .navigateTo(Screen.UPDATE_PROFILE, Bundle().apply {
                        putInt(ARG_MODE, when (mode) {
                            Mode.CREATE_SELF -> UpdateProfileFragment.Mode.CREATE_SELF
                            Mode.CREATE_OTHER -> UpdateProfileFragment.Mode.CREATE_OTHER
                        }.ordinal)
                        putString(ARG_NIN, nin)
                    })
                else binding.ninInputLayout
                    .setError(binding.root, R.string.create_profile_forbidden_msg)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_MODE = "mode"
    }
}