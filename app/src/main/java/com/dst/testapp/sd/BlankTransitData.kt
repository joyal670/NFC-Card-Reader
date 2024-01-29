/*
 * BlankTransitData.kt
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
 * Base class for all types of cards that are blank.
 */
@Parcelize
class BlankTransitData(private val cardNameRes: String ): TransitData() {
    override val cardName: String
        get() = cardNameRes

    override val serialNumber: String?
        get() = null

    override val info: List<ListItemInterface>?
        get() = listOf(
                HeaderListItem("Blank card", headingLevel = 1),
                TextListItem("R.string.fully_blank_desc")
        )
}

abstract class BlankTransitFactory<T> (private val name: String ) : CardTransitFactory<T> {
    override fun parseTransitIdentity(card: T) =
        TransitIdentity(name)

    override fun parseTransitData(card: T) = BlankTransitData(name)
}
