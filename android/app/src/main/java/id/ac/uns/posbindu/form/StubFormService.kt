package id.ac.uns.posbindu.form

import javax.inject.Inject

class StubFormService @Inject constructor() : FormService {
    override suspend fun getFormList() =
        STUB_FORM_LIST.map { Form(it.id, it.image, it.title, it.subtitle) }

    override suspend fun getFormSectionList(formId: String) =
        STUB_FORM_LIST.find { it.id == formId }!!.sections!!
}