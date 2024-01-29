/*
 * IntercodeLookup.kt
 *
 * Copyright 2018 Google
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


interface En1545Lookup {

    val timeZone: MetroTimeZone
    fun getRouteName(routeNumber: Int?, routeVariant: Int?, agency: Int?, transport: Int?): String?

    fun getHumanReadableRouteId(routeNumber: Int?,
                                     routeVariant: Int?,
                                     agency: Int?,
                                     transport: Int?): String? {
        if (routeNumber == null)
            return null
        var routeReadable = NumberUtils.intToHex(routeNumber)
        if (routeVariant != null) {
            routeReadable += "/" + NumberUtils.intToHex(routeVariant)
        }
        return routeReadable
    }

    fun getAgencyName(agency: Int?, isShort: Boolean): String?

    fun getStation(station: Int, agency: Int?, transport: Int?): Station?

    fun getSubscriptionName(agency: Int?, contractTariff: Int?): String?

    fun parseCurrency(price: Int): TransitCurrency

    // Only relevant if EventCode is not present.
    fun getMode(agency: Int?, route: Int?): Trip.Mode
}
