/*
 * UnauthorizedClassicTransitData.kt
 *
 * Copyright 2015-2018 Michael Farrell <micolous+git@gmail.com>
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

import com.dst.testapp.R
import kotlinx.parcelize.Parcelize


/**
 * Handle MIFARE Classic with no open sectors
 */
@Parcelize
class UnauthorizedClassicTransitData private constructor(val subtype: ClassicCard.SubType) : UnauthorizedTransitData() {

    override val cardName: String
        get() = "Locked MIFARE Classic card"

    override val isUnlockable: Boolean
        get() = subtype == ClassicCard.SubType.CLASSIC

    companion object {
        val FACTORY: ClassicCardTransitFactory = object : ClassicCardTransitFactory {
            /**
             * This should be the last executed MIFARE Classic check, after all the other checks are done.
             *
             *
             * This is because it will catch others' cards.
             *
             * @param card Card to read.
             * @return true if all sectors on the card are locked.
             */
            // check to see if all sectors are blocked
            override fun check(card: ClassicCard) = card.sectors.all { it is UnauthorizedClassicSector }

            override fun parseTransitIdentity(card: ClassicCard) =
                    TransitIdentity("Locked MIFARE Classic card")

            override fun parseTransitData(card: ClassicCard) =
                    UnauthorizedClassicTransitData(subtype = card.subType)
        }
    }
}
