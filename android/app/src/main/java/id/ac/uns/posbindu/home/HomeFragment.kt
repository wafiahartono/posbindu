package id.ac.uns.posbindu.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.auth.AuthService
import id.ac.uns.posbindu.databinding.FragmentHomeBinding
import id.ac.uns.posbindu.etc.ListResource
import id.ac.uns.posbindu.etc.Resource
import id.ac.uns.posbindu.etc.addMarginItemDecoration
import id.ac.uns.posbindu.etc.showUnexpectedErrorSnackbar
import id.ac.uns.posbindu.form.Form
import id.ac.uns.posbindu.form.FormViewModel
import id.ac.uns.posbindu.navigation.Navigator
import id.ac.uns.posbindu.navigation.Screen
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authService: AuthService
    @Inject
    lateinit var navigator: Navigator
    private val formViewModel: FormViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            root.setOnRefreshListener { formViewModel.refreshFormList() }
            textViewTitle.text = getGreetings(authService.user!!.name!!)
            recyclerViewFormCard.apply {
                adapter = MenuAdapter(emptyList()) { menu ->
                    if (menu.id == "AIjioGQIlukXpxjLbuLY") {
                        Toast.makeText(context, "Jadwal kontrol", Toast.LENGTH_LONG).show()
                    } else {
                        formViewModel.selectFormId(menu.id)
                        navigator.navigateTo(Screen.FORM)
                    }
                }
                addMarginItemDecoration(R.dimen.margin_m)
                setHasFixedSize(true)
            }
        }
        formViewModel.formList.observe(viewLifecycleOwner) { checkFormListResource(it) }
        if (formViewModel.formList.value == null) formViewModel.refreshFormList()
    }

    private fun getGreetings(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return resources.getString(
            when {
                hour < 11 -> R.string.greeting_morning_txt
                hour < 15 -> R.string.greeting_noon_txt
                hour < 19 -> R.string.greeting_afternoon_txt
                else -> R.string.greeting_evening_txt
            },
            name.substringBefore(' ')
        )
    }

    private fun checkFormListResource(resource: ListResource<Form>) {
        when (resource) {
            is Resource.Loading -> Unit
            is Resource.Success ->
                (binding.recyclerViewFormCard.adapter as? MenuAdapter)?.updateMenu(
                    resource.data
                        .map { Menu(it.id, it.image, it.title) }
                        .plus(
                            Menu(
                                "AIjioGQIlukXpxjLbuLY",
                                R.drawable.illustration_events,
                                "Jadwal kontrol"
                            )
                        )
                )
            is Resource.Error -> binding.root.showUnexpectedErrorSnackbar(resource.exception)
        }
        binding.root.isRefreshing = resource is Resource.Loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}