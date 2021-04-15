package id.ac.uns.posbindu.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.imageLoader
import coil.load
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.databinding.FragmentFormBinding
import id.ac.uns.posbindu.etc.LOG_TAG
import id.ac.uns.posbindu.etc.Resource
import id.ac.uns.posbindu.etc.addMarginItemDecoration
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.navigation.Navigator
import id.ac.uns.posbindu.navigation.Screen
import javax.inject.Inject

@AndroidEntryPoint
class FormFragment : Fragment() {
    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var navigator: Navigator
    private val formViewModel: FormViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewSectionCard.apply {
            adapter = FormSectionAdapter(emptyList()) { section ->
                formViewModel.selectFormSectionId(section.id)
                navigator.navigateTo(Screen.QUESTION)
            }
            addMarginItemDecoration(R.dimen.margin_m)
            setHasFixedSize(true)
        }
        formViewModel.selectedForm.observe(viewLifecycleOwner) { resource ->
            Log.d(LOG_TAG, "selectedForm.observe resource: $resource")
            binding.apply {
                when (resource) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        val form = resource.data
                        illustration.load(form.image, requireContext().imageLoader)
                        textViewTitle.text = form.title
                        subtitle.text = form.subtitle
                        (binding.recyclerViewSectionCard.adapter as? FormSectionAdapter)
                            ?.updateSections(form.sections!!)
                    }
                    is Resource.Error -> root.showUnexpectedErrorSnackbar(resource.exception)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}