/*
 * ISO7816Data.kt
 *
 * Copyright 2019 Google
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
 */
package com.dst.testapp.sd

object ISO7816Data {
    const val TAG_TRANSACTION_COUNTER = "9f36"
    const val TAG_PROPRIETARY_BER_TLV = "a5"
    const val TAG_DISCRETIONARY_DATA = "bf0c"
}