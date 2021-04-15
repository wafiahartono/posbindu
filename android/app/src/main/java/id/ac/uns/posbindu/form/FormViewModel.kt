package id.ac.uns.posbindu.form

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.ac.uns.posbindu.etc.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
) : ViewModel() {
    private val formService = StubFormService()

    private val _formList = MutableListResourceLiveData<Form>()
    val formList: ListResourceLiveData<Form> get() = _formList

    fun refreshFormList() {
        _formList.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            _formList.postValue(
                try {
                    Resource.Success(formService.getFormList())
                } catch (e: Exception) {
                    Resource.Error(e)
                }
            )
        }
    }

    private val selectedFormId = MutableLiveData<String>()

    fun selectFormId(id: String) {
        selectedFormId.value = id
    }

    val selectedForm: ResourceLiveData<Form> = selectedFormId.switchMap { id ->
        Log.d(LOG_TAG, "selectedFormId.switchMap: $id")
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading())
            emit(
                try {
                    Resource.Success(
                        (_formList.value as Resource.Success).data.find { it.id == id }!!
                            .copy(sections = formService.getFormSectionList(id)).also {
                                Log.d(LOG_TAG, "selectedFormId.switchMap form: $it")
                            }
                    )
                } catch (e: Exception) {
                    Resource.Error(e)
                }
            )
        }
    }

    private lateinit var _selectedFormSectionId: String
    val selectedFormSectionId get() = _selectedFormSectionId

    fun selectFormSectionId(id: String) {
        _selectedFormSectionId = id
    }

//    private val _formResponse = FormResponse(responses = hashMapOf())
//    val formResponse
//        get() = _formResponse.copy(
//            formId = mutableSelectedForm.value!!.id, timeTaken = Date()
//        )
//
//    fun getFormSection(sectionId: String) = selectedForm.sections.find { it.id == sectionId }!!
//
//    fun selectForm(form: Form) {
//        mutableSelectedForm.value = form
//    }
//
//    fun selectFormSection(section: Section) {
//        mutableSelectedFormSection.value = section
//    }
//
//    fun saveQuestionAnswer(questionId: String, answer: Any) {
//        _formResponse.responses!![questionId] = answer
//    }
}