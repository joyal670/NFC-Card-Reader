package com.dst.testapp.sd


import android.util.Log
import kotlinx.serialization.json.Json

object CardSerializer {
    fun load(importer: CardImporter, stream: Input): Card? {
        try {
            return importer.readCard(stream)
        } catch (ex: Exception) {
            Log.e("Card", "Failed to deserialize", ex)
            throw RuntimeException(ex)
        }
    }

    private fun fromJson(xml: String): Card = logAndSwiftWrap ("Card", "Failed to deserialize") {
        JsonKotlinFormat.readCard(xml)
    }

   /* @Throws(Throwable::class)
    @Suppress("unused") // Used from Swift
    fun fromAutoJson(json: String): Iterator<Card> = logAndSwiftWrap ("Card", "Failed to deserialize") {
        AutoJsonFormat.readCardList(json).iterator()
    }*/

    @Throws(Throwable::class)
    fun toJsonString(card: Card): String = logAndSwiftWrap ("Card", "Failed to serialize") {
        JsonKotlinFormat.makeCardString(card)
    }

    @Throws(Throwable::class)
    fun fromPersist(input: String): Card = fromJson(input)

    @Throws(Throwable::class)
    fun toPersist(card: Card): String = toJsonString(card)

    val jsonPlainStable get() = Json {
        useArrayPolymorphism = true
        isLenient = true
    }
}
