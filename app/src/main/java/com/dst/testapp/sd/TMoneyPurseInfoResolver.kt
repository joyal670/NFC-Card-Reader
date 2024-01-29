/*
 * TMoneyPurseInfoResolver.kt
 *
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
 *
 * References: https://github.com/micolous/metrodroid/wiki/T-Money
 */
package com.dst.testapp.sd


/**
 * [KSX6924PurseInfoResolver] singleton for T-Money.
 *
 * This contains mapping for IDs on a T-Money card.
 *
 * See https://github.com/micolous/metrodroid/wiki/T-Money for more information.
 */
object TMoneyPurseInfoResolver : KSX6924PurseInfoResolver() {
    override val issuers : Map<Int, String> = mapOf(
            // 0x00: reserved
            0x01 to "Korea Financial Telecommunications and Clearings Institute",
            // 0x02: A-CASH (에이캐시) (Also used by Snapper)
            0x03 to "Mybi",
            // 0x04: reserved
            // 0x05: V-Cash (브이캐시)
            0x06 to "Mondex Korea",
            0x07 to "Korea Expressway Corporation",
            0x08 to "Korea Smart Card Corporation",
            0x09 to "KORAIL Networks",
            // 0x0a: reserved
            0x0b to "EB Card Corporation",
            0x0c to "Seoul Bus Transport Association",
            0x0d to "Cardnet"
    )

    override val userCodes : Map<Int, String > = mapOf(
            0x01 to "Regular",
            0x02 to "Child",
            // TTAK.KO 12.0240 disagrees
            0x03 to "Youth",
            // TTAK.KO 12.0240 disagrees
            0x04 to "Senior",
            // TTAK.KO 12.0240 disagrees
            0x05 to "Disabled",
            // Only in TTAK.KO 12.0240
            0x0f to "Test",
            0x11 to "Bus (road toll)",
            0x12 to "Lorry (road toll)",
            0xff to "Inactive"
    )

    override val disRates : Map<Int, String > = mapOf(
            0x00 to "No discount",
            0x10 to "Disabled, basic",
            0x11 to "Disabled, companion",
            // 0x12 - 0x1f: reserved
            0x20 to "Veteran, basic",
            0x21 to "Veteran, companion"
            // 0x22 - 0x2f: reserved
    )

    override val tCodes : Map<Int, String > = mapOf(
            0x00 to "None",
            0x01 to "SK Telecom",
            0x02 to "Korea Telecom",
            0x03 to "LG U+"
    )

    override val cCodes : Map<Int, String > = mapOf(
            0x00 to "None",
            0x01 to "KB Kookmin Bank",
            0x02 to "Nonghyup Bank",
            0x03 to "Lotte Card",
            0x04 to "BC Card",
            0x05 to "Samsung Card",
            0x06 to "Shinhan Bank",
            0x07 to "Citibank Korea",
            0x08 to "Korea Exchange Bank",
            0x09 to "Woori Bank",
            0x0a to "Hana SK Card",
            0x0b to "Hyundai Capital Services"
    )
}
