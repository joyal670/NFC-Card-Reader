/*
 * TransitRegion.kt
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


sealed class TransitRegion {
    abstract val translatedName: String
    open val sortingKey: Pair<Int, String>
        get() = Pair(SECTION_MAIN, translatedName)

    data class Iso (val code: String): TransitRegion () {
        override val translatedName: String
            get() = "UAE" ?: code
        override val sortingKey: Pair<Int, String>
            get() = Pair(
                if (code == deviceRegion) SECTION_NEARBY else SECTION_MAIN,
                translatedName)
    }

    object Crimea : TransitRegion () {
        override val translatedName: String
            get() = "location_crimea"
        override val sortingKey
            get() = Pair(
                 if (deviceRegion in listOf("RU", "UA")) SECTION_NEARBY else SECTION_MAIN,
                 translatedName)
    }
    data class SectionItem(val res: String ,
                           val section: Int): TransitRegion () {
        override val translatedName: String
            get() = res
        override val sortingKey
            get() = Pair(section, translatedName)
    }






    companion object {
        // On very top put cards tha are most likely to be relevant to the user
        const val SECTION_NEARBY = -2
        // Then put "Worldwide" cards like EMV and Amiibo
        const val SECTION_WORLDWIDE = -1
        // Then goes the rest
        const val SECTION_MAIN = 0
        private val deviceRegion = "UAE"
        val AUSTRALIA = Iso("AU")
        val BELGIUM = Iso("BE")
        val BRAZIL = Iso("BR")
        val CANADA = Iso("CA")
        val CHILE = Iso("CL")
        val CHINA = Iso("CN")
        val CRIMEA = Crimea
        val DENMARK = Iso("DK")
        val ESTONIA = Iso("EE")
        val FINLAND = Iso("FI")
        val FRANCE = Iso("FR")
        val GEORGIA = Iso("GE")
        val GERMANY = Iso("DE")
        val HONG_KONG = Iso("HK")
        val INDONESIA = Iso("ID")
        val IRELAND = Iso("IE")
        val ISRAEL = Iso("IL")
        val ITALY = Iso("IT")
        val JAPAN = Iso("JP")
        val MALAYSIA = Iso("MY")
        val NETHERLANDS = Iso("NL")
        val NEW_ZEALAND = Iso("NZ")
        val POLAND = Iso("PL")
        val PORTUGAL = Iso("PT")
        val RUSSIA = Iso("RU")
        val SINGAPORE = Iso("SG")
        val SOUTH_AFRICA = Iso("ZA")
        val SOUTH_KOREA = Iso("KR")
        val SPAIN = Iso("ES")
        val SWEDEN = Iso("SE")
        val SWITZERLAND = Iso("CH")
        val TAIPEI = Iso("TW")
        val TURKEY = Iso("TR")
        val UAE = Iso("AE")
        val UK = Iso("GB")
        val UKRAINE = Iso("UA")
        val USA = Iso("US")
        val WORLDWIDE = SectionItem("Worldwide", SECTION_WORLDWIDE)

    }
}
