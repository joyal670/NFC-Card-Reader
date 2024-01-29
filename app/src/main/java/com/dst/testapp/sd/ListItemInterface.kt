package com.dst.testapp.sd

import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
sealed class ListItemInterface: Parcelable {
    abstract val text1: String?
    abstract val text2: String?
}
