/*
 * TransactionTrip.kt
 *
 * Copyright 2018 Google
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dst.testapp.sd

import android.os.Parcelable


abstract class Transaction : Parcelable, Comparable<Transaction> {
    abstract val isTapOff: Boolean

    /**
     * This method may be overridden to provide candidate line names associated with the
     * transaction. This is useful if there is a separate field on the card which encodes the route
     * or line taken, and that knowledge of the station alone is not generally sufficient to
     * determine the correct route.
     *
     * By default, this gets candidate route names from the Station.
     */
    open val routeNames: List<String>?
        get() = station?.lineNames ?: emptyList()

    /**
     * This method may be overridden to provide candidate line names associated with the
     * transaction. This is useful if there is a separate field on the card which encodes the route
     * or line taken, and that knowledge of the station alone is not generally sufficient to
     * determine the correct route.
     *
     * By default, this gets candidate route names from the Station.
     */
    open val humanReadableLineIDs: List<String>
        get() = station?.humanReadableLineIds ?: emptyList()

    open val vehicleID: String?
        get() = null

    open val machineID: String?
        get() = null

    open val passengerCount: Int
        get() = -1

    open val station: Station?
        get() = null


    abstract val fare: TransitCurrency?

    abstract val timestamp: Timestamp?

    open fun getRawFields(level: TransitData.RawLevel): String? = null

    open fun getAgencyName(isShort: Boolean) :  String? = null

    open val mode: Trip.Mode
        get() = Trip.Mode.OTHER

    open val isTransparent: Boolean
        get() = mode in listOf(Trip.Mode.TICKET_MACHINE, Trip.Mode.VENDING_MACHINE)


    open val isCancel: Boolean
        get() = false

    protected abstract val isTapOn: Boolean

    open val isTransfer: Boolean
        get() = false

    open val isRejected: Boolean
        get() = false



    open fun shouldBeMerged(other: Transaction): Boolean {
        return isTapOn && (other.isTapOff || other.isCancel) && isSameTrip(other)
    }

    protected abstract fun isSameTrip(other: Transaction): Boolean




    class Comparator : kotlin.Comparator<Transaction> {
        override fun compare(a: Transaction, b: Transaction): Int {
            return a.compareTo(b)
        }
    }
}
