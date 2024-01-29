
package com.dst.testapp.sd

interface CardKeysRetriever {
    fun forTagID(tagID: ImmutableByteArray): CardKeys?

    fun forClassicStatic(): ClassicStaticKeys?

    fun forID(id: Int): CardKeys?
}