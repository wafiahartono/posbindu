package id.ac.uns.posbindu.form

import androidx.annotation.DrawableRes

data class Form(
    val id: String,
    @DrawableRes val image: Int,
    val title: String,
    val subtitle: String,
    val sections: List<Section>? = null
)