/*
 * Station.kt
 *
 * Copyright (C) 2011 Eric Butler <eric@codebutler.com>
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
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class Station (val humanReadableId: String, val companyName: String? = null,
               val lineNames: List< String>? = emptyList(),
               private val stationNameRaw: String?,
               private val shortStationNameRaw: String? = null,
               val latitude: Float? = null,
               val longitude: Float? = null,
               val isUnknown: Boolean = false,
               val humanReadableLineIds: List<String> = emptyList(),
               private val attributes: MutableList<String> = mutableListOf()): Parcelable {

    fun getStationName(isShort: Boolean): String {
        var ret: String
        if (isShort)
            ret = shortStationNameRaw ?: stationNameRaw ?: "unknown_format"
        else
            ret = stationNameRaw ?: shortStationNameRaw ?: "unknown_format"
        if (showRawId() && stationNameRaw != null /*&& stationNameRaw.unformatted != humanReadableId*/)
            ret += " [$humanReadableId]"
        for (attribute in attributes)
            ret += ", $attribute"
        return ret
    }

    val stationName: String? get() = getStationName(false)
    val shortStationName: String? get() = getStationName(true)

    fun hasLocation(): Boolean = (latitude != null && longitude != null)

    fun addAttribute(s: String): Station {
        attributes.add(s)
        return this
    }

    companion object {
        private fun showRawId() = true

        fun unknown(id: String) = Station(humanReadableId = id,
                    stationNameRaw = null,
                    isUnknown = true)

       // fun unknown(id: Int) = unknown(NumberUtils.intToHex(id))

        fun nameOnly(name: String) = Station(stationNameRaw = name, humanReadableId = name)
    }
}
