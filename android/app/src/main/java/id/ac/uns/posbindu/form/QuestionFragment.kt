package id.ac.uns.posbindu.form

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.databinding.FragmentQuestionBinding
import id.ac.uns.posbindu.etc.*
import id.ac.uns.posbindu.form.Question.MultipleChoiceQuestion
import id.ac.uns.posbindu.form.Question.ShortAnswerQuestion
import id.ac.uns.posbindu.navigation.Navigator
import javax.inject.Inject

@AndroidEntryPoint
class QuestionFragment : Fragment() {
    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var navigator: Navigator
    private val formViewModel: FormViewModel by activityViewModels()
    private lateinit var questionNavigator: QuestionNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            showExitConfirmationDialog()
        }
        binding.apply {
            recyclerViewMultipleChoiceQuestion.apply {
                adapter = MultipleChoiceQuestionChoiceAdapter()
                addMarginItemDecoration(R.dimen.margin_m)
                setHasFixedSize(true)
            }
            buttonPreviousQuestion.setOnClickListener { previousQuestion() }
            buttonNextQuestion.setOnClickListener { nextQuestion() }
        }
        formViewModel.selectedForm.observe(viewLifecycleOwner) { checkSelectedFormResource(it) }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.fragment_question_exit_confirm_msg))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.yes) { _, _ -> navigator.navigatePrev() }
            .show()
    }

    private fun previousQuestion() {
        saveQuestionAnswer()
        if (!questionNavigator.previous()) showExitConfirmationDialog()
    }

    private fun nextQuestion() {
        saveQuestionAnswer()
        when {
            questionNavigator.next() -> Unit
            questionNavigator.navigateToUnanswered() -> Toast.makeText(
                context, R.string.question_not_answered_msg, Toast.LENGTH_SHORT
            ).show()
            else -> {
                MaterialAlertDialogBuilder(requireContext()).setMessage("Terima kasih").show()
                Log.d(
                    LOG_TAG,
                    "All question answered: ${
                        questionNavigator.finish().joinToString { it.toString() }
                    }"
                )
            }
        }
    }

    private fun saveQuestionAnswer() {
        val question = questionNavigator.question
        questionNavigator.setAnswer(
            when (question) {
                is MultipleChoiceQuestion -> (binding.recyclerViewMultipleChoiceQuestion.adapter
                        as MultipleChoiceQuestionChoiceAdapter).selection
                is ShortAnswerQuestion -> when (question.type) {
                    ShortAnswerQuestion.Type.INTEGER ->
                        binding.editTextShortAnswer.inputOrNull()?.toInt()
                    ShortAnswerQuestion.Type.STRING ->
                        binding.editTextShortAnswer.inputOrNull()
                }
            }
        )
    }

    private fun checkSelectedFormResource(resource: Resource<Form>?) {
        when (resource) {
            is Resource.Loading -> Unit
            is Resource.Success -> {
                resource.data.sections!!.find { it.id == formViewModel.selectedFormSectionId }
                    ?.let { section ->
                        binding.textViewSectionTitle.text = section.title
                        questionNavigator =
                            QuestionNavigator(section.questions, ::onQuestionNavigatorNavigate)
                        questionNavigator.start()
                    }
            }
            is Resource.Error -> binding.root.showUnexpectedErrorSnackbar(resource.exception)
        }
        val buttonIsEnabled = resource !is Resource.Loading
        binding.buttonPreviousQuestion.isEnabled = buttonIsEnabled
        binding.buttonNextQuestion.isEnabled = buttonIsEnabled
    }

    private fun onQuestionNavigatorNavigate(questionNavigator: QuestionNavigator) {
        val question = questionNavigator.question
        binding.apply {
            textViewQuestionNumber.text = resources.getString(
                R.string.question_number_txt,
                questionNavigator.number,
                questionNavigator.questions.size
            )
            when (question) {
                is MultipleChoiceQuestion -> {
                    recyclerViewMultipleChoiceQuestion.visibility = View.VISIBLE
                    textInputLayoutShortAnswer.visibility = View.GONE
                    (recyclerViewMultipleChoiceQuestion.adapter as MultipleChoiceQuestionChoiceAdapter)
                        .apply {
                            choices = question.choices
                            selection = questionNavigator.answer as? Int
                        }
                }
                is ShortAnswerQuestion -> {
                    recyclerViewMultipleChoiceQuestion.visibility = View.GONE
                    textInputLayoutShortAnswer.visibility = View.VISIBLE
                    textInputLayoutShortAnswer.helperText = question.helperText
                    editTextShortAnswer.inputType = question.getEditTextInputType()
                }
            }
            textViewQuestionText.text = question.text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}