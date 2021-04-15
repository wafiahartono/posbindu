package id.ac.uns.posbindu.form

interface FormService {
    suspend fun getFormList(): List<Form>
    suspend fun getFormSectionList(formId: String): List<Section>
}