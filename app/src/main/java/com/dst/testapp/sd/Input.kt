package com.dst.testapp.sd

interface Input {
    fun readBytes(sz: Int): ByteArray
    fun readToString(): String
    fun close()
}
