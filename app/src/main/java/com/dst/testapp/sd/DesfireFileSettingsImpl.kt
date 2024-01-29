/*
 * DesfireFileSettings.kt
 *
 * Copyright 2011 Eric Butler <eric@codebutler.com>
 * Copyright 2016 Michael Farrell <micolous+git@gmail.com>
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

abstract class DesfireFileSettingsImpl(val raw: ImmutableByteArray) : DesfireFileSettings {
    val fileType get() = raw[0]
    val commSetting get() = raw[1]
    val accessRights get() = raw.sliceOffLen(2, 2)

    val fileTypeString: String
        get() = when (fileType) {
            DesfireFileSettings.STANDARD_DATA_FILE ->"desfire_standard_file"
            DesfireFileSettings.BACKUP_DATA_FILE -> "desfire_backup_file"
            DesfireFileSettings.VALUE_FILE -> "desfire_value_file"
            DesfireFileSettings.LINEAR_RECORD_FILE -> "desfire_linear_record"
            DesfireFileSettings.CYCLIC_RECORD_FILE -> "desfire_cyclic_record"
            else -> "desfire_unknown_file"
        }
}