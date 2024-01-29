/*
 * IteratorUtils.kt
 *
 * Copyright 2018 Michael Farrell <micolous+git@gmail.com>
 * Copyright 2021 Google
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

class FlatIterator<T>(private val outerIterator: Iterator<Iterator<T>>)
        : Iterator<T> {
            private var innerIterator: Iterator<T>? = null
    override fun hasNext(): Boolean {
        ensureInner()
        return innerIterator != null
    }

    override fun next(): T {
        ensureInner()
        return innerIterator!!.next()
    }

    private fun ensureInner() {
        while (innerIterator?.hasNext() != true) {
            if (!outerIterator.hasNext()) {
                innerIterator = null
                return
            }
            innerIterator = outerIterator.next()
        }
    }
}

fun <T> Iterator<Iterator<T>>.flatten() = FlatIterator(this)

/**
 * IteratorTransformer converts an [Iterator] of type [T] into one of type [R].
 *
 * This implements similar functionality to Guice's Iterators.transform method.
 *
 * @param iterator The iterator to consume
 * @param transform A function that transforms type [T] into [R].
 * @param T The source type
 * @param R The destination type
 */
class IteratorTransformer<T, out R>(
    private val iterator: Iterator<T>,
    private val transform: (T) -> R
) : Iterator<R> {
    override fun hasNext() = iterator.hasNext()
    override fun next(): R = transform(iterator.next())
}

// Same but filter out nulls
class IteratorTransformerNotNull<T, out R>(
    private val iterator: Iterator<T>,
    private val transform: (T) -> R?
) : Iterator<R> {
    private var peek: R? = null
    override fun hasNext(): Boolean {
        if (peek != null)
            return true
        while (iterator.hasNext()) {
            peek = transform(iterator.next())
            if (peek != null)
                return true
        }
        return false
    }
    override fun next(): R {
        if (peek == null)
            hasNext()
        val ret = peek!!
        peek = null
        return ret
    }
}

fun <T, R> Iterator<T>.map(transform: (T) -> R) = IteratorTransformer(this, transform)
fun <T, R> Iterator<T>.mapNotNull(transform: (T) -> R) = IteratorTransformerNotNull(this, transform)
