package id.ac.uns.posbindu.form

import java.util.*
import kotlin.collections.HashMap

data class FormResponse(
    val formId: String? = null,
    val timeTaken: Date? = null,
    val responses: HashMap<String, Any>? = null
)