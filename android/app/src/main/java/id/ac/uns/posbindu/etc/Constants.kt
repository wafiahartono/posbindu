package id.ac.uns.posbindu.etc

import java.text.SimpleDateFormat
import java.util.Locale

const val LOG_TAG = "posbindu_log_tag"
const val SHARED_PREF = "app"
const val LAST_UPDATE_CHECK = "last_update_check"
const val LATEST_VER = "latest_ver"
val DATE_FORMAT = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
val PHONE_REGEX = Regex("^[0-9]+$")