/*
 * UnauthorizedUltralightTransitData.kt
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
 * Handle MIFARE Ultralight with no open pages
 */
@Parcelize
class UnauthorizedUltralightTransitData : UnauthorizedTransitData() {
    override val cardName: String
        get() = "Locked MIFARE Ultralight card"

    companion object {
        val FACTORY: UltralightCardTransitFactory = object : UltralightCardTransitFactory {

            /**
             * This should be the last executed MIFARE Ultralight check, after all the other checks are done.
             *
             *
             * This is because it will catch others' cards.
             *
             * @param card Card to read.
             * @return true if all sectors on the card are locked.
             */
            override fun check(card: UltralightCard) =
                // check to see if all sectors are blocked
                // User memory is page 4 and above
                card.pages.withIndex().all { (idx, p) -> idx < 4 || p.isUnauthorized }

            override fun parseTransitData(card: UltralightCard) = UnauthorizedUltralightTransitData()

            override fun parseTransitIdentity(card: UltralightCard) =
                    TransitIdentity("Locked MIFARE Ultralight card", null)
        }
    }
}
