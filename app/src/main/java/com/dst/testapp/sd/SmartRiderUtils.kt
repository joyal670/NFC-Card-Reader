/*
 * SmartRiderUtils.kt
 *
 * Copyright 2016-2022 Michael Farrell <micolous+git@gmail.com>
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


import android.os.Build
import androidx.annotation.RequiresApi
import com.dst.testapp.R
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Month

@RequiresApi(Build.VERSION_CODES.O)
val SMARTRIDER_EPOCH = Epoch.utc(2000, MetroTimeZone.PERTH, -8 * 60)
@RequiresApi(Build.VERSION_CODES.O)
val MYWAY_EPOCH = Epoch.utc(2000, MetroTimeZone.SYDNEY, -11 * 60) // Canberra
val SMARTRIDER_STR = "smartrider"

// "We've had one epoch, yes... but what about *second* epoch?"
@RequiresApi(Build.VERSION_CODES.O)
val DATE_EPOCH = Daystamp(1997, Month.JANUARY, 1)

enum class SmartRiderType constructor(val friendlyName: String ) {
    UNKNOWN("Unknown"),
    SMARTRIDER("SmartRider"),
    MYWAY("MyWay")
}

class SmartRiderTripBitfield(smartRiderType: SmartRiderType, bitfield: Int) {
    val mode = when (bitfield and 0x03) {
        0x00 -> Trip.Mode.BUS
        0x01 -> when (smartRiderType) {
            SmartRiderType.MYWAY -> Trip.Mode.TRAM
            else -> Trip.Mode.TRAIN
        }
        0x02 -> Trip.Mode.FERRY
        else -> Trip.Mode.OTHER
    }
    val isSynthetic = bitfield and 0x04 == 0x04
    val isTransfer = bitfield and 0x08 == 0x08
    val isTapOn = bitfield and 0x10 == 0x10
    val isAutoLoadDiscount = bitfield and 0x40 == 0x40
    val isBalanceNegative = bitfield and 0x80 == 0x80

    override fun toString(): String {
        return "mode=$mode, isSynthetic=$isSynthetic, isTransfer=$isTransfer, isTapOn=$isTapOn, " +
            "isAutoLoadDiscount=$isAutoLoadDiscount, isBalanceNegative=$isBalanceNegative"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertTime(epochTime: Long, smartRiderType: SmartRiderType): Timestamp =
    when (smartRiderType) {
        SmartRiderType.MYWAY -> MYWAY_EPOCH.seconds(epochTime)
        SmartRiderType.SMARTRIDER -> SMARTRIDER_EPOCH.seconds(epochTime)
        else -> SMARTRIDER_EPOCH.seconds(epochTime)
    }

@RequiresApi(Build.VERSION_CODES.O)
fun convertDate(days: Int): Daystamp = DATE_EPOCH + DatePeriod(days=days)
