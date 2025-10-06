package com.truongtx.progresstest1

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Painting(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var author: String,
    var price: Double,
    var imageUri: String? = null,
    var description: String = "",
    var isNew: Boolean = false,
    var isOnSale: Boolean = false
) : Parcelable