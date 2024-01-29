/*
 * HSLLookup.kt
 *
 * Copyright 2019 Google
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

import com.dst.testapp.R


object HSLLookup : En1545LookupUnknown() {
    override fun parseCurrency(price: Int) = TransitCurrency.EUR(price)
    override val timeZone: MetroTimeZone
        get() = MetroTimeZone.HELSINKI

    fun contractWalttiZoneName(prefix: String) = "${prefix}WalttiZone"

    fun contractWalttiRegionName(prefix: String) = "${prefix}WalttiRegion"

    fun contractAreaTypeName(prefix: String) = "${prefix}AreaType"

    fun contractAreaName(prefix: String) = "${prefix}Area"

    fun languageCode(input: Int?) = when (input) {
        0 -> "Finnish"
        1 -> "Swedish"
        2 -> "English"
        else -> "Unknown ${input.toString()}"
    }
    
    private val areaMap = mapOf(
        Pair(0, 1) to "Helsinki",
        Pair(0, 2) to "Espoo",
        Pair(0, 4) to "Vantaa",
        Pair(0, 5) to "Region (Helsinki+Espoo-Vantaa)",
        Pair(0, 6) to "Kirkkonummi-Siuntio",
        Pair(0, 7) to "Vihti",
        Pair(0, 8) to "Nurmijärvi",
        Pair(0, 9) to "Kerava-Sipoo-Tuusula",
        Pair(0, 10) to "Sipoo",
        Pair(0, 14) to "Surrounding country 2 (ESP+VAN+KIR+KER+SIP)",
        Pair(0, 15) to "Surrounding country 3 (HEL+ESP+VAN+KIR+KER+SIP)",
        Pair(1, 1) to "Bus",
        Pair(1, 2) to "Bus 2",
        Pair(1, 3) to "Bus 3",
        Pair(1, 4) to "Bus 4",
        Pair(1, 5) to "Tram",
        Pair(1, 6) to "Metro",
        Pair(1, 7) to "Train",
        Pair(1, 8) to "Ferry",
        Pair(1, 9) to "U-line"
    )

    private val walttiValiditySplit = listOf(Pair(0, 0)) + (1..10).map { Pair(it, it) } + (1..10).flatMap { start -> ((start+1)..10).map { Pair(start, it) } }

    private const val WALTTI_OULU = 229
    private const val WALTTI_LAHTI = 223
    const val CITY_UL_TAMPERE = 1

    private val lahtiZones = listOf(
        "A", "B", "C", "D", "E", "F1", "F2", "G", "H", "I"
    )
    private val ouluZones = listOf(
        "City A", "A", "B", "C", "D", "E", "F", "G", "H", "I"
    )

    private fun mapWalttiZone(region: Int, id: Int): String = when (region) {
        WALTTI_OULU -> lahtiZones[id - 1]
        WALTTI_LAHTI -> ouluZones[id - 1]
        else -> charArrayOf('A' + id - 1).concatToString()
    }

    private fun walttiNameRegion(id: Int): String? = StationTableReader.getOperatorName("waltti_region", id, true)

    fun getArea(parsed: En1545Parsed, prefix: String, isValidity: Boolean,
                walttiRegion: Int? = null, ultralightCity: Int? = null): String? {
        if (parsed.getInt(contractAreaName(prefix)) == null && parsed.getInt(contractWalttiZoneName(prefix)) != null) {
            val region = walttiRegion ?: parsed.getIntOrZero(contractWalttiRegionName(prefix))
            val regionName = walttiNameRegion(region) ?: region.toString()
            val zone = parsed.getIntOrZero(contractWalttiZoneName(prefix))
            if (zone == 0) {
                return null
            }
            if (!isValidity && zone in 1..10) {
                return "$regionName zone ${mapWalttiZone(region, zone)} "
            }
            val (start, end) = walttiValiditySplit[zone]
            return "$regionName zones ${mapWalttiZone(region, start)} + \" - \" + ${mapWalttiZone(region, end)}"
        }
        val type = parsed.getIntOrZero(contractAreaTypeName(prefix))
        val value = parsed.getIntOrZero(contractAreaName(prefix))
        if (type in 0..1 && value == 0) {
            return null
        }
        if (ultralightCity == CITY_UL_TAMPERE && type == 0) {
            val from = value % 6
            if (isValidity) {
                val to = value / 6
                val num = to - from + 1
                val zones = (from..to).map { 'A' + it }.toCharArray().concatToString()
                return "zone $num, $zones, $num"
            } else {
                return "Zone ${charArrayOf('A' + from).concatToString()}"
            }
        }
        if (type == 2) {
            val to = value and 7
            if (isValidity) {
                val from = value shr 3
                val num = to - from + 1
                val zones = (from..to).map { 'A' + it }.toCharArray().concatToString()
                return "zone $num, $zones, $num"
            } else {
                return "Zone ${ charArrayOf('A' + to).concatToString()}"
            }
        }
        return areaMap[Pair(type, value)]?.let {
            it
        } ?: "Unknown  ${type / value}"
    }
}
