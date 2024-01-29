/*
 * En1545Container.kt
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

/**
 * EN1545 Container
 *
 * This consists of a concatenation of all fields inside of it, with no additional data.
 */
class En1545Container(private vararg val fields: En1545Field) : En1545Field {
    override fun parseField(b: ImmutableByteArray, off: Int, path: String, holder: En1545Parsed, bitParser: En1545Bits): Int {
        var currentOffset = off
        for (el in fields) {
            currentOffset = el.parseField(b, currentOffset, path, holder, bitParser)
        }
        return currentOffset
    }
}
