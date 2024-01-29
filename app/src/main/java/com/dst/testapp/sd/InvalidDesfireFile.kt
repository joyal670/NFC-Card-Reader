/*
 * InvalidDesfireFile.kt
 *
 * Copyright (C) 2014 Eric Butler <eric@codebutler.com>
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

import kotlinx.serialization.Transient

class InvalidDesfireFile (override val fileSettings: DesfireFileSettings,
                          override val raw: RawDesfireFile
): DesfireFile() {
    private val errorMessage: String
        get () = raw.error ?: "Data is null"

    @Transient
    override val data: ImmutableByteArray
        get() = throw IllegalStateException("Invalid file: $errorMessage")

    override fun getRawData(id: Int): ListItem {
        return ListItem(id.hexString,
                errorMessage)
    }
}
