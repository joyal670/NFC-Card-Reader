/*
 * ClassicSectorValid.kt
 *
 * Copyright 2012-2015 Eric Butler <eric@codebutler.com>
 * Copyright 2012 Wilbert Duijvenvoorde <w.a.n.duijvenvoorde@gmail.com>
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


class ClassicSectorValid(override val raw: ClassicSectorRaw) : ClassicSector() {
    override val blocks get() = raw.blocks.map { ClassicBlock(it) }
    fun isEmpty(index: Int): Boolean {
        try {
            for ((blockidx, block) in blocks.withIndex()) {
                if (index == 0 && blockidx == 0)
                    continue
                if (blockidx == blocks.size - 1)
                    continue
                if (!block.isEmpty)
                    return false
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }


    override fun getRawData(idx: Int): ListItemInterface {
        val sectorIndex = idx.hexString
        val keyStrA = keyA?.key?.let { "R.string.classic_key_format_a"+ it.toHexString() }
        val keyStrB = keyB?.key?.let { "R.string.classic_key_format_b"+ it.toHexString() }
        val keyStr = when {
            keyStrA != null && keyStrB != null -> "$keyStrA, $keyStrB"
            keyStrA != null -> keyStrA
            else -> keyStrB
        }
        val acs = try {
            ClassicAccessBits(blocks[blocks.size - 1].data.sliceOffLen(6, 3))
        } catch (e: Exception) {
            null
        }

        val bli = mutableListOf<ListItemInterface>()
        for ((blockidx, block) in blocks.withIndex()) {
            val acsSlot = if (blocks.size == 4) blockidx else blockidx / 5
            val acsDescription = acs?.getSlotString(acsSlot)
            if (block.isUnauthorized)
                bli.add(ListItem("R.string.block_title_format_unauthorized"+ blockidx.hexString + acsDescription))
            else
                bli.add(ListItemRecursive( blockidx.hexString, acsDescription, listOf(ListItem(null, block.data.toHexDump())))
                )
        }
        if (isEmpty(idx)) {
            return ListItemRecursive("R.string.sector_title_format_empty$sectorIndex", keyStr, bli)
        }

        return ListItemRecursive("R.string.sector_title_format$sectorIndex", keyStr, bli)

    }
}