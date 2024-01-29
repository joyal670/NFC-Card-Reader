/*
 * NFCVCard.kt
 *
 * Copyright 2016-2019 Michael Farrell <micolous+git@gmail.com>
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

import kotlinx.serialization.Serializable

/**
 * Utility class for reading NfcV / Vicinity /  ISO 15693
 *
 * Reference: https://www.ti.com/lit/an/sloa141/sloa141.pdf
 */
@Serializable
data class NFCVCard constructor(
    val sysInfo: ImmutableByteArray?,

    val pages: List<NFCVPage>,
    override val isPartialRead: Boolean = false) : CardProtocol() {
    override val rawData: List<ListItem>
        get() = listOf(ListItem("System info ${sysInfo?.toHexDump()}" )) +pages.mapIndexed { idx, sector ->
            val pageIndexString = idx.hexString

            if (sector.isUnauthorized) {
                ListItem("Page: $pageIndexString (unauthorized)", null)
            } else {
                ListItem("Page: $pageIndexString", sector.data.toHexDump())
            }
        }

    private fun findTransitFactory(): NFCVCardTransitFactory? {
        for (factory in NFCVTransitRegistry.allFactories) {
            try {
                if (factory.check(this))
                    return factory
            } catch (e: IndexOutOfBoundsException) {
                /* Not the right factory. Just continue  */
            } catch (e: UnauthorizedException) {
            }

        }
        return null
    }

    override fun parseTransitIdentity(): TransitIdentity? = findTransitFactory()?.parseTransitIdentity(this)

    override fun parseTransitData(): TransitData? = findTransitFactory()?.parseTransitData(this)

    fun getPage(index: Int): NFCVPage = pages[index]

    fun readPages(startPage: Int, pageCount: Int): ImmutableByteArray {
        var data = ImmutableByteArray.empty()
        for (index in startPage until startPage + pageCount) {
            data += getPage(index).data
        }
        return data
    }

    fun readBytes(start: Int, len: Int): ImmutableByteArray {
        val pageSize = pages[0].data.size
        val startPage = start / pageSize
        val endPage = (start + len + pageSize - 1) / pageSize
        return readPages(startPage, endPage - startPage)
            .sliceOffLen(start - startPage * pageSize, len)
    }
}
