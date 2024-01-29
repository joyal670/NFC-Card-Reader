/*
 * UtilsJvm.kt
 *
 * Copyright 2011 Eric Butler <eric@codebutler.com>
 * Copyright 2015-2019 Michael Farrell <micolous+git@gmail.com>
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

import java.io.InputStream
import java.io.OutputStream
import java.io.PushbackInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * Gets an error message for a [Throwable], preferring a localized message if available.
 */
fun getErrorMessage(ex: Throwable?): String {
    if (ex == null)
        return "unknown error"
    val exCause = ex.cause ?: ex
    val errorMessage = exCause.localizedMessage?.ifEmpty { null } ?: exCause.message?.ifEmpty { null } ?: exCause.toString()
    return exCause.javaClass.simpleName + ": " + errorMessage
}

fun PushbackInputStream.peekAndSkipSpace(): Byte {
    while (true) {
        val c: Int = this.read()
        if (!Character.isSpaceChar(c.toChar())) {
            this.unread(c)
            return c.toByte()
        }
    }
}

// Used by android variant. Warning gets issued for jvmCli variant
@Suppress("unused")
fun PushbackInputStream.peek(): Byte {
    val c = this.read()
    this.unread(c)
    return c.toByte()
}

class JavaStreamInput (private val stream: InputStream): Input {
    override fun readBytes(sz: Int): ByteArray{
        val ba = ByteArray(sz)
        val actual = stream.read(ba)
        if (actual <= 0)
            return byteArrayOf()
        if (actual == sz)
            return ba
        return ba.sliceArray(0 until actual)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun readToString(): String = stream.readBytes().decodeToString()

    override fun close() {
        stream.close()
    }
}

class JavaStreamOutput (private val stream: OutputStream): Output {
    override fun write(b: ByteArray) {
        stream.write(b)
    }
}

class ZipIterator(private val zi: ZipInputStream)
    : Iterator<Pair<ZipEntry, InputStream>> {
    constructor(stream: InputStream) : this(ZipInputStream(stream))

    private var needAdvance = true
    private var nextEntry: ZipEntry? = null
    override fun hasNext(): Boolean {
        advance()
        return nextEntry != null
    }

    override fun next(): Pair<ZipEntry, InputStream> {
        advance()
        needAdvance = true
        return Pair(nextEntry!!, zi)
    }

    private fun advance() {
        if (!needAdvance)
            return
        needAdvance = false
        nextEntry = zi.nextEntry
    }
}
