package com.dst.testapp.sd

import java.io.InputStream

interface CardMultiImporter {
    /**
     * Reads cards from the given stream.
     *
     * Implementations should read the file incrementally (lazy), to save memory.
     *
     * @param stream Stream to read the card content from.
     */
    fun readCards(stream: InputStream): Iterator<Card>?
}

// Used by android variant. Warning gets issued for jvmCli variant
@Suppress("unused")
class CardMultiImportAdapter (private val base: CardImporter): CardMultiImporter {
    override fun readCards(stream: InputStream): Iterator<Card>? {
        val card = base.readCard(JavaStreamInput(stream))
        return if (card == null) {
            null
        } else {
            listOf(card).iterator()
        }
    }
}
