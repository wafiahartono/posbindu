package id.ac.uns.posbindu.home

import androidx.annotation.DrawableRes

data class Menu(
    val id: String,
    @DrawableRes val image: Int,
    val title: String,
)