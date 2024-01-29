/*
 * SnapperTransitData.kt
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
 * Reference: https://github.com/micolous/metrodroid/wiki/Snapper
 */
package com.dst.testapp.sd

import com.dst.testapp.R


class SnapperTransitData(card: KSX6924Application) : TMoneyTransitData(
    card.balance.byteArrayToInt(),
    card.purseInfo,
    TransactionTrip.merge(getSnapperTransactionRecords(card).map {
        SnapperTransaction.parseTransaction(it.first, it.second)
    })
) {
    override val balance: TransitBalance?
        get() = mPurseInfo.buildTransitBalance(TransitCurrency.NZD(mBalance))

    override val cardName
        get() = NAME

    override val purseInfoResolver
        get() = SnapperPurseInfoResolver

    companion object {
        private const val NAME = "Snapper"

        val CARD_INFO = CardInfo(
                name = "R.string.card_name_snapper",
                locationId = "R.string.location_wellington_nz",

                cardType = CardType.ISO7816,

                region = TransitRegion.NEW_ZEALAND
        )

        val FACTORY: KSX6924CardTransitFactory = object : KSX6924CardTransitFactory {
            override fun parseTransitIdentity(card: KSX6924Application) =
                    TransitIdentity(NAME, card.serial)

            override fun parseTransitData(card: KSX6924Application) = SnapperTransitData(card)

            override val allCards = listOf(CARD_INFO)

            // Snapper cards have a slightly different record format: some bytes are always FF.
            override fun check(card: KSX6924Application) = card.getSfiFile(4)?.recordList?.all {
                    it.size == 46 && it.sliceArray(26 until 46).isAllFF()
                } ?: false
        }

        private fun getSnapperTransactionRecords(card: KSX6924Application)
                : List<Pair<ImmutableByteArray, ImmutableByteArray>> {
            val trips = card.getSfiFile(3) ?: return emptyList()
            val balances = card.getSfiFile(4) ?: return emptyList()

            return trips.recordList zip balances.recordList
        }
    }
}
