/*
 * KSX6924PurseInfo.kt
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


import android.os.Parcelable
import com.dst.testapp.R
import com.dst.testapp.sd.KSX6924Utils.parseHexDate
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * `EFPURSE_INFO` -- FCI tag b0
 */
@Serializable
@Parcelize
class KSX6924PurseInfo constructor(val purseInfoData: ImmutableByteArray) : Parcelable {

    val cardType : Byte
        get() = purseInfoData[0]

    val alg : Byte
        get() = purseInfoData[1]

    val vk : Byte
        get() = purseInfoData[2]

    val idCenter : Byte
        get() = purseInfoData[3]

    val csn : String
        get() = purseInfoData.getHexString(4, 8)

    val idtr : Long
        get() = purseInfoData.convertBCDtoLong(12, 5)

    val issueDate : Daystamp?
        get() = parseHexDate(purseInfoData.byteArrayToLong(17, 4))

    val expiryDate : Daystamp?
        get() = parseHexDate(purseInfoData.byteArrayToLong(21, 4))

    val userCode : Byte
        get() = purseInfoData[26]

    val disRate : Byte
        get() = purseInfoData[27]

    val balMax : Long
        get() = purseInfoData.byteArrayToLong(27, 4)

    val bra : Int
        get() = purseInfoData.convertBCDtoInteger(31, 2)

    val mmax : Long
        get() = purseInfoData.byteArrayToLong(33, 4)

    val tcode : Byte
        get() = purseInfoData[37]

    val ccode : Byte
        get() = purseInfoData[38]

    val rfu : ImmutableByteArray
        get() = purseInfoData.sliceOffLen(39, 8)

    // Convenience functionality
    val serial : String
        get() = NumberUtils.groupString(csn, " ", 4, 4, 4)

    fun buildTransitBalance(balance: TransitCurrency, label: String? = null) : TransitBalance =
            TransitBalanceStored(balance, label, issueDate, expiryDate)

    fun getInfo(resolver: KSX6924PurseInfoResolver = KSX6924PurseInfoDefaultResolver)
            : List<ListItem>? = listOf(
            ListItem("Card payment terms", resolver.resolveCardType(cardType)),
            ListItem("Encryption algorithm", resolver.resolveCryptoAlgo(alg)),
            ListItem("Encryption key version", vk.hexString),
            ListItem("Issuer", resolver.resolveIssuer(idCenter)),
            ListItem("Authentication ID", idtr.hexString),
            ListItem("Ticket type", resolver.resolveUserCode(userCode)),
            ListItem("Discount type", resolver.resolveDisRate(disRate)),
            ListItem("Maximum balance", balMax.toString()),
            ListItem("Branch code", bra.hexString),
            ListItem("One-time transaction limit", mmax.toString()),
            ListItem("Mobile carrier", resolver.resolveTCode(tcode)),
            ListItem("Financial institution name", resolver.resolveCCode(ccode)),
            ListItem("Reserved for future use", rfu.getHexString()))
}