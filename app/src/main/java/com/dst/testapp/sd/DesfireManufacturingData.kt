/*
 * DesfireManufacturingData.kt
 *
 * Copyright (C) 2011 Eric Butler
 *
 * Authors:
 * Eric Butler <eric@codebutler.com>
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


class DesfireManufacturingData(val data: ImmutableByteArray) {
    val info: List<ListItemInterface>
        get() {
            val items = mutableListOf(
                    HeaderListItem("Hardware info"),
                    ListItem("Vendor ID", hwVendorID.toString()),
                    ListItem("Type", hwType.toString()),
                    ListItem("Subtype", hwSubType.toString()),
                    ListItem("Major version", hwMajorVersion.toString()),
                    ListItem("Minor version", hwMinorVersion.toString()),
                    ListItem("Storage size", hwStorageSize.toString()),
                    ListItem("Protocol", hwProtocol.toString()),

                    HeaderListItem("Software info"),
                        ListItem("Vendor ID", swVendorID.toString()),
                    ListItem("Type", swType.toString()),
                    ListItem("Subtype", swSubType.toString()),
                    ListItem("Major version", swMajorVersion.toString()),
                    ListItem("Minor version", swMinorVersion.toString()),
                    ListItem("Storage size", swStorageSize.toString()),
                    ListItem("Protocol", swProtocol.toString()))

            if (!Preferences.hideCardNumbers) {
                items.add(HeaderListItem("General information"))
                items.add(ListItem("Serial number", uid.toHexString()))
                items.add(ListItem("Batch number", batchNo.toHexString()))
                items.add(ListItem("Week of manufacture", weekProd.toString(16)))
                items.add(ListItem("Year of manufacture", yearProd.toString(16)))
            }

            return items
        }

    private val hwVendorID get() = data[0].toInt()
    private val hwType get() = data[1].toInt()
    private val hwSubType get() = data[2].toInt()
    private val hwMajorVersion get() = data[3].toInt()
    private val hwMinorVersion get() = data[4].toInt()
    private val hwStorageSize get() = data[5].toInt()
    private val hwProtocol get() = data[6].toInt()

    private val swVendorID get() = data[7].toInt()
    private val swType get() = data[8].toInt()
    private val swSubType = data[9].toInt()
    private val swMajorVersion get() = data[10].toInt()
    private val swMinorVersion get() = data[11].toInt()
    private val swStorageSize get() = data[12].toInt()
    private val swProtocol get() = data[13].toInt()

    val uid: ImmutableByteArray get() = data.sliceOffLen(14, 7)
    // FIXME: This is returning a negative number. Probably is unsigned.
    private val batchNo: ImmutableByteArray get() = data.sliceOffLen(21, 5)

    private val weekProd get() = data[26].toInt()
    private val yearProd get() = data[27].toInt()
}
