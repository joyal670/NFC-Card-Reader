


package com.dst.testapp.sd

import android.os.Parcelable

import kotlinx.serialization.Serializable

@Serializable
sealed class TransitBalance : Parcelable {
    abstract val balance: TransitCurrency

    open val validFrom: Timestamp?
        get() = null

    open val validTo: Timestamp?
        get() = null

    open val name: String?
        get() = null

    companion object {
        fun formatValidity(balance: TransitBalance): String? {
            val validFrom = balance.validFrom?.format()
            val validTo = balance.validTo?.format()

            return when {
                validFrom != null && validTo != null -> "valid_format, validFrom, validTo"
                validTo != null -> "valid_to_format, validTo"
                else -> null
            }
        }

    }
}
