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
                    HeaderListItem("R.string.hardware_information"),
                    ListItem("R.string.desfire_vendor_id", hwVendorID.toString()),
                    ListItem("R.string.desfire_type", hwType.toString()),
                    ListItem("R.string.desfire_subtype", hwSubType.toString()),
                    ListItem("R.string.desfire_major_version", hwMajorVersion.toString()),
                    ListItem("R.string.desfire_minor_version", hwMinorVersion.toString()),
                    ListItem("R.string.desfire_storage_size", hwStorageSize.toString()),
                    ListItem("R.string.desfire_protocol", hwProtocol.toString()),

                    HeaderListItem("R.string.software_information"),
                    ListItem("R.string.desfire_vendor_id", swVendorID.toString()),
                    ListItem("R.string.desfire_type", swType.toString()),
                    ListItem("R.string.desfire_subtype", swSubType.toString()),
                    ListItem("R.string.desfire_major_version", swMajorVersion.toString()),
                    ListItem("R.string.desfire_minor_version", swMinorVersion.toString()),
                    ListItem("R.string.desfire_storage_size", swStorageSize.toString()),
                    ListItem("R.string.desfire_protocol", swProtocol.toString()))

            if (!Preferences.hideCardNumbers) {
                items.add(HeaderListItem("R.string.desfire_general_info"))
                items.add(ListItem("R.string.calypso_serial_number", uid.toHexString()))
                items.add(ListItem("R.string.desfire_batch_number", batchNo.toHexString()))
                items.add(ListItem("R.string.manufacture_week", weekProd.toString(16)))
                items.add(ListItem("R.string.manufacture_year", yearProd.toString(16)))
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
