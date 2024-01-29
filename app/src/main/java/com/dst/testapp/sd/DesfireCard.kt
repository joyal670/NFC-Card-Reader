/*
 * DesfireCard.kt
 *
 * Copyright 2011-2015 Eric Butler <eric@codebutler.com>
 * Copyright 2015-2019 Michael Farrell <micolous+git@gmail.com>
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

@Serializable
data class DesfireCard constructor(


    val manufacturingData: ImmutableByteArray,

    val applications: Map<Int, DesfireApplication>,
    override val isPartialRead: Boolean = false,
    val appListLocked: Boolean = false) : CardProtocol() {

    override val manufacturingInfo: List<ListItemInterface>
        get() = DesfireManufacturingData(manufacturingData).info

    override val rawData: List<ListItemInterface>
        get() = applications.map { (id,app) ->
                ListItemRecursive(makeName(id),
                        null, app.rawData)
            }

    private fun makeName(id: Int): String {
        val mifareAID = DesfireApplication.getMifareAID(id)
        if (mifareAID != null) {
            return "MIFARE Classic AID: "+mifareAID.first.hexString+ mifareAID.second
        }

        return "Application: "+ id.hexString
    }

    override fun parseTransitIdentity(): TransitIdentity? {
        for (f in DesfireCardTransitRegistry.allFactories)
            if (f.check(this))
                return f.parseTransitIdentity(this)

        return null
    }

    override fun parseTransitData(): TransitData? {
        for (f in DesfireCardTransitRegistry.allFactories)
            if (f.check(this))
                return f.parseTransitData(this)
        return null
    }

    fun getApplication(appId: Int): DesfireApplication? = applications[appId]
}
