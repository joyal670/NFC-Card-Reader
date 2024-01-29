/*
 * SnapperTransaction.kt
 *
 * Copyright 2018 Google
 * Copyright 2019 Michael Farrell <micolous+git@gmail.com>
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
 *
 * Reference: https://github.com/micolous/metrodroid/wiki/Snapper
 */
package com.dst.testapp.sd

import android.util.Log
import com.dst.testapp.sd.KSX6924Utils.parseHexDateTime
import kotlinx.parcelize.Parcelize


@Parcelize
class SnapperTransaction(
        val journeyId: Int,
        val seq: Int,
        override val isTapOn: Boolean,
        val type: Int,
        val cost: Int,
        val time: Long,
        val operator: String) : Transaction() {

    override fun compareTo(other: Transaction): Int {
        if (other is SnapperTransaction) {
            // Implement your comparison logic here
            // For example, compare based on timestamp or any other criteria
            return this.timestamp!!.compareTo(other.timestamp!!)
        }
        // Handle the case where the comparison is not supported
        throw IllegalArgumentException("Cannot compare different transaction types")
    }

    override val isTapOff get() = !isTapOn

    // TODO: Implement this properly
    override val station get() = Station.nameOnly("$journeyId / $seq")

    override val mode get() = when (type) {
        2 -> Trip.Mode.BUS
        else -> Trip.Mode.TROLLEYBUS
    }

    override fun isSameTrip(other: Transaction): Boolean {
        val o = other as SnapperTransaction
        return journeyId == o.journeyId && seq == o.seq
    }

    override val timestamp get() = parseHexDateTime(time, TZ)

    override val fare get() = TransitCurrency.NZD(cost)

    override val isTransfer get() = seq != 0

    companion object {
        private val TZ = MetroTimeZone.AUCKLAND
        private const val TAG = "SnapperTransaction"

        fun parseTransaction(trip : ImmutableByteArray, balance : ImmutableByteArray) : SnapperTransaction {
            val journeyId = trip[5].toInt()
            val seq = trip[4].toInt()

            val time = trip.byteArrayToLong(13, 7)

            val tapOn = (trip[51].toInt() and 0x10) == 0x10

            val type = balance[0].toInt()
            val bal = balance.byteArrayToInt(2, 4)
            var cost = balance.byteArrayToInt(10, 4)
            if (type == 2)
                cost = -cost

            val operator = balance.sliceOffLen(14, 5).getHexString().substring(0, 9)

            Log.d(TAG, "Transaction: journey=$journeyId/$seq type=$type, cost=$cost, bal=$bal, time=$time, operator=$operator, tapOn=$tapOn")
            Log.d(TAG, " trip: ${trip.getHexString()}")
            Log.d(TAG, "  bal: ${balance.getHexString()}")

            return SnapperTransaction(journeyId, seq, tapOn, type, cost, time, operator)
        }
    }

}
