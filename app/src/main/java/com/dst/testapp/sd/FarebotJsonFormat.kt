/*
 * FarebotJsonFormat.kt
 *
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



import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

abstract class CardImporterString : CardImporter {
    override fun readCard(stream: Input): Card? =
            readCardList(stream.readToString()).firstOrNull()

    override fun readCard(input: String): Card? =
            readCardList(input).firstOrNull()

    abstract fun readCardList(input: String): List<Card>
}

object FarebotJsonFormat : CardImporterString() {
    override fun readCardList(input: String): List<Card> =
            CardSerializer.jsonPlainStable.decodeFromString(FarebotCards.serializer(),
                input).convert()
    
    fun readCards(input: JsonElement): List<Card> =
            CardSerializer.jsonPlainStable.decodeFromJsonElement(FarebotCards.serializer(), input).convert()
}

object AutoJsonFormat : CardImporterString() {
    override fun readCardList(input: String): List<Card> =
            readCards(CardSerializer.jsonPlainStable.parseToJsonElement(input), input)

    private fun readCards(input: JsonElement, plain: String): List<Card> =
        if (input.jsonObjectOrNull?.containsKey("cards") == true &&
                input.jsonObjectOrNull?.containsKey("scannedAt") != true &&
                input.jsonObjectOrNull?.containsKey("tagId") != true)
            FarebotJsonFormat.readCards(input)
        else
            // Kotlin 1.3.40 has trouble parsing polymorphs in an abstract tree
            listOf(JsonKotlinFormat.readCard(plain))
}

@Serializable
data class FarebotDataWrapper(val data: String)

@Serializable
data class FarebotDesfireError(val type: Int, val message: String)

@Serializable
data class FarebotDesfireFile(
    val fileId: Int,
    val fileSettings: FarebotDataWrapper,
    val fileData: String? = null,
    val error: FarebotDesfireError? = null) {
    fun convert() = RawDesfireFile(
        data = fileData?.let { ImmutableByteArray.fromBase64(it) },
        settings = ImmutableByteArray.fromBase64(fileSettings.data),
        error = error?.message, isUnauthorized = error?.type == 1)
}

@Serializable
data class FarebotDesfireApplication(
    val appId: Int,
    val files: List<FarebotDesfireFile>) {
    fun convert() = DesfireApplication(files = files.map {it.fileId to it.convert() }.toMap())
}

@Serializable
data class FarebotUltralightPage(val index: Int, val data: String) {
    fun convert() = UltralightPage(ImmutableByteArray.fromBase64(data))
}

@Serializable
data class FarebotClassicBlock(val index: Int, val data: String) {
    fun convert() = ImmutableByteArray.fromBase64(data)
}

@Serializable
data class FarebotClassicSector(
    val type: String,
    val index: Int,
    val blocks: List<FarebotClassicBlock>?,
    val errorMessage: String? = null) {
    fun convert() = ClassicSectorRaw(
        blocks = blocks.orEmpty().sortedBy {it.index}.map { it.convert() },
        error = when (type) {
            "data" -> null
            "unauthorized" -> "Unauthorized"
            else -> errorMessage
        },
        isUnauthorized = type == "unauthorized"
    )
}

@Serializable
data class FarebotFelicaBlock(
    val address: Int,
    val data: String
) {
    fun convert() = Pair(address, FelicaBlock(ImmutableByteArray.fromBase64(data)))
}

@Serializable
data class FarebotFelicaService(
    val serviceCode: Int,
    val blocks: List<FarebotFelicaBlock>
) {
    fun convert() = FelicaService(blocksMap = blocks.map { it.convert() }.toMap())
}



@Serializable
data class FarebotCard(
    // Common
    val tagId: String, val scannedAt: Long, val cardType: String,

    // Desfire
    val manufacturingData: FarebotDataWrapper? = null,
    val applications: List<FarebotDesfireApplication>? = null,

    // Classic
    val sectors: List<FarebotClassicSector>? = null,

) {
    fun convertScannedAt() = TimestampFull(timeInMillis = scannedAt,
            tz = MetroTimeZone.UNKNOWN)
    fun convert() = when(cardType) {
        "MifareDesfire" -> Card(
            tagId = ImmutableByteArray.fromBase64(tagId),
            scannedAt = convertScannedAt(),
            mifareDesfire = DesfireCard(
                    manufacturingData = ImmutableByteArray.fromBase64(manufacturingData?.data ?: ""),
                    applications = applications.orEmpty().map { it.appId to it.convert() }.toMap())
            )

        "MifareClassic" -> Card(
            tagId = ImmutableByteArray.fromBase64(tagId),
            scannedAt = convertScannedAt(),
            mifareClassic = ClassicCard(
                    sectors = sectors.orEmpty().sortedBy {it.index}.
                            map { ClassicSector.create(it.convert()) })
            )

        else -> throw IllegalArgumentException("Unknown card $cardType")
    }
}

@Serializable
class FarebotCards(val cards: List<FarebotCard>,
                   val versionCode: Int? = null,
                   val versionName: String? = null) {
    fun convert() = cards.map { it.convert() }
}
