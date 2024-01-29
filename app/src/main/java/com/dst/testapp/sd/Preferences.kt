/*
 * Preferences.kt
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

import kotlin.reflect.KProperty


object Preferences {
    const val PREF_LAST_READ_ID = "last_read_id"
    const val PREF_LAST_READ_AT = "last_read_at"
    val rawLevel: TransitData.RawLevel = TransitData.RawLevel.ALL
    var obfuscateBalance: Boolean = false
    var obfuscateTripFares: Boolean = false
    var hideCardNumbers: Boolean = false
    var showRawStationIds: Boolean = false
    var obfuscateTripDates: Boolean = false
    val convertTimezone: Boolean = false
    val mfcFallbackReader: String = ""
    val mfcAuthRetry: Int = 0
    var retrieveLeapKeys: Boolean = false
    var showBothLocalAndEnglish: Boolean = false
    val language: String = ""
    val region: String? = null
    var obfuscateTripTimes: Boolean = false
    val debugSpans: Boolean = false
    val localisePlaces: Boolean = false
    val metrodroidVersion: String = "1.0" // Provide a default version
    var useIsoDateTimeStamps: Boolean = false
    val speakBalance by BoolDelegate("pref_key_speak_balance", false)

    class BoolDelegate(private val preference: String, private val defaultSetting: Boolean) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
            true

    }
}

