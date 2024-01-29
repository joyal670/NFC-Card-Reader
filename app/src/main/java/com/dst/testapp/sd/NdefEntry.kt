package com.dst.testapp.sd

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


sealed class NdefEntry: Parcelable {
    abstract val tnf: Int
    abstract val type: ImmutableByteArray
    abstract val id: ImmutableByteArray?
    abstract val payload: ImmutableByteArray

    private val headInfo: List<ListItemInterface>
        get() = listOfNotNull(
            HeaderListItem(name),
            id?.let {
                ListItem(
                    "R.string.ndef_id",
                    if (it.isASCII()) it.readASCII() else it.toHexDump()
                )
            }
        )
    val info: List<ListItemInterface>
        get() = headInfo + payloadInfo
    open val payloadInfo: List<ListItemInterface>
        get() = listOf(
            ListItem(
               "R.string.ndef_type",
                if (type.isASCII()) type.readASCII() else type.toHexDump()
            ),
            ListItem(
                "R.string.ndef_payload",
                payload.toHexDump()
            ),
        )
    protected abstract val name: String

    override fun toString(): String = "[name=$name, id=$id, tnf=$tnf, type=$type, payload=$payload]"
}

@Parcelize
data class NdefEmpty(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefEntry() {
    override val name: String
        get() = "R.string.ndef_empty_record"
}

sealed class NdefRTD : NdefEntry()

@Parcelize
data class NdefUnknownRTD(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefRTD() {
    override val name: String
        get() = "NFC Forum well-known type"
}

@Parcelize
data class NdefUri(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefRTD() {
    override val name: String
        get() = "URI record"

    override val payloadInfo: List<ListItemInterface>
        get() = listOf(
            UriListItem(
                "URI",
                uri, uri
            )
        )

    private val uriSuffix: String
        get() = payload.readUTF8(start = 1)

    private val uriPrefix: String
        get() = when (payload[0].toInt() and 0xff) {
            0x00 -> ""
            0x01 -> "http://www."
            0x02 -> "https://www."
            0x03 -> "http://"
            0x04 -> "https://"
            0x05 -> "tel:"
            0x06 -> "mailto:"
            0x07 -> "ftp://anonymous:anonymous@"
            0x08 -> "ftp://ftp."
            0x09 -> "ftps://"
            0x0A -> "sftp://"
            0x0B -> "smb://"
            0x0C -> "nfs://"
            0x0D -> "ftp://"
            0x0E -> "dav://"
            0x0F -> "news:"
            0x10 -> "telnet://"
            0x11 -> "imap:"
            0x12 -> "rtsp://"
            0x13 -> "urn:"
            0x14 -> "pop:"
            0x15 -> "sip:"
            0x16 -> "sips:"
            0x17 -> "tftp:"
            0x18 -> "btspp://"
            0x19 -> "btl2cap://"
            0x1A -> "btgoep://"
            0x1B -> "tcpobex://"
            0x1C -> "irdaobex://"
            0x1D -> "file://"
            0x1E -> "urn:epc:id:"
            0x1F -> "urn:epc:tag:"
            0x20 -> "urn:epc:pat:"
            0x21 -> "urn:epc:raw:"
            0x22 -> "urn:epc:"
            0x23 -> "urn:nfc:"
            else -> "[${payload[0].toInt()}]:"
        }

    val uri: String
        get() = "$uriPrefix$uriSuffix"

    override fun toString(): String = "[URL: id=$id, value=$uri]"
}

@Parcelize
data class NdefText(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefRTD() {
    override val name: String
        get() = "Text record"

    override val payloadInfo: List<ListItemInterface>
        get() = listOf(
            ListItem(
                "R.string.ndef_text_encoding",
                if (isUTF16) "UTF-16" else "UTF-8"
            ),
            ListItem(
               "R.string.ndef_text_language",
                language
            ),
            ListItem(
               "R.string.ndef_text",
               "$text $languageCode"
            )
        )

    val isUTF16: Boolean
        get() = payload[0].toInt() and 0x80 != 0

    val languageCode: String
        get() = payload.sliceOffLen(1, langLen).readASCII()

    val language: String
        get() = languageCodeToName(languageCode) ?: languageCode

    private val langLen get() = payload[0].toInt() and 0x3f

    val text: String
        get() = if (isUTF16) payload.readUTF16BOM(
            isLittleEndianDefault = false,
            langLen + 1
        ) else payload.readUTF8(langLen + 1)

    override fun toString(): String =
        "[Text: id=$id, language=$language, isUTF16=$isUTF16, value=$text]"
}

sealed class NdefMIME : NdefEntry()

@Parcelize
data class NdefUnknownMIME(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefMIME() {
    override val name: String
        get() = "Unknown MIME-typed entry"
}

@Parcelize
data class NdefWifi(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefMIME() {
    override val name: String
        get() = "Wi-Fi record"

    data class Record(val type: Int, val value: ImmutableByteArray)

    override val payloadInfo
        get() = flatEntries.map {
            when (it.type) {
                0x1001 -> ListItem(
                   "R.string.ndef_wifi_ap_channel",
                    it.value.byteArrayToInt(0, 2).toString()
                )
                0x1003 -> ListItem(
                    "R.string.ndef_wifi_auth_types",
                    formatBitmap(it, authTypes, 2)
                )
                0x100f -> ListItem(
                    "R.string.ndef_wifi_enc_types",
                    formatBitmap(it, encTypes, 2)
                )
                0x1011 -> ListItem(
                    "R.string.ndef_wifi_device_name",
                    it.value.readUTF8()
                )
                0x1020 -> ListItem(
                    "R.string.ndef_wifi_mac_address",
                    it.value.joinToString(":") { it2 ->
                        NumberUtils.zeroPad((it2.toInt() and 0xff).toString(16), 2)
                    })

                0x1021 -> ListItem(
                    "R.string.ndef_wifi_manufacturer",
                it.value.readLatin1()
                )
                0x1023 -> ListItem(
                    "R.string.ndef_wifi_model_name",
                    it.value.readLatin1()
                )
                0x1024 -> ListItem(
                    "R.string.ndef_wifi_model_number",
                    it.value.readLatin1()
                )
                0x1026 -> ListItem(
                   "R.string.ndef_wifi_network_index",
                    it.value.byteArrayToInt(0, 1).toString()
                )
                0x1027 -> ListItem(
                    "R.string.ndef_wifi_password",
                    it.value.readLatin1()
                )
                0x103c -> ListItem(
                    "R.string.ndef_wifi_bands",
                    formatBitmap(it, bands, 1)
                )
                0x1042 -> ListItem(
                    "R.string.ndef_wifi_serial_number",
                    it.value.readLatin1()
                )
                0x1045 -> ListItem(
                    "R.string.ndef_wifi_ssid",
                    it.value.readLatin1()
                )
                0x1047 -> ListItem(
                   "R.string.ndef_wifi_uuid_enrollee",
                    formatUUID(it)
                )
                0x1048 -> ListItem(
                    "R.string.ndef_wifi_uuid_registrar",
                    formatUUID(it)
                )
                0x1049 -> if (it.value.size >= 5
                    && it.value.byteArrayToInt(0, 3) == 0x372A) {
                    ListItemRecursive("R.string.ndef_wifi_wfa_extension", null, infoWfaExtension(it.value))
                } else {
                    ListItem(
                        "Unknown vendor extension", it.value.toHexDump()
                    )
                }
                0x104a -> ListItem(
                    "Version1",
                    ("${(it.value[0].toInt() and 0xf0) shr 4}.${it.value[0].toInt() and 0xf}").toString()
                )
                0x1061 -> ListItem(
                    "ndef_wifi_key_provided_automatically",
                    if (it.value[0] != 0.toByte()) "Yes" else "No"
                )
                else -> ListItem(
                    "Unknown ${it.type.toString(16)}" , it.value.toHexDump()
                )
            }
        }.toList()

    val entries: Sequence<Record>
        get() = entriesFromBytes(payload)

    val flatEntries: Sequence<Record>
        get() = entries.flatMap { parent ->
            if (parent.type == 0x100e) {
                entriesFromBytes(parent.value)
            } else {
                listOf(parent).asSequence()
            }
        }

    companion object {
        private fun infoWfaExtension(payload: ImmutableByteArray): List<ListItemInterface> =
            entriesFromBytes(payload.drop(3), 1).map {
                when (it.type) {
                    0 -> ListItem(
                       "R.string.ndef_wifi_version2",
                        ("${(it.value[0].toInt() and 0xf0) shr 4}.${it.value[0].toInt() and 0xf}").toString()
                    )
                    2 -> ListItem(
                        "R.string.ndef_wifi_key_is_shareable",
                        if (it.value[0].toInt() != 0)
                            "R.string.ndef_wifi_key_is_shareable_yes"
                        else
                            "R.string.ndef_wifi_key_is_shareable_no"
                    )
                    else -> ListItem(
                        "R.string.ndef_wifi_unknown"+ it.type.toString(16) , it.value.toHexDump()
                    )
                }
            }.toList()
        private fun entriesFromBytes(bytes: ImmutableByteArray, fieldLen: Int = 2): Sequence<Record> = sequence {
            var ptr = 0
            while (ptr + 2 * fieldLen <= bytes.size) {
                val l = bytes.byteArrayToInt(ptr + fieldLen, fieldLen)
                if (ptr + l + 2 * fieldLen > bytes.size)
                    break
                yield(
                    Record(
                        type = bytes.byteArrayToInt(ptr, fieldLen),
                        value = bytes.sliceOffLen(ptr + 2 * fieldLen, l)
                    )
                )
                ptr += 2 * fieldLen + l
            }
        }

        private fun formatUUID(record: Record) =
            NumberUtils.groupString(record.value.toHexString(), "-", 8, 4, 4, 4)

        private fun formatBitmap(
            record: Record,
            bitmapDefinition: List<String>,
            len: Int
        ): String {
            val builder = StringBuilder()
            val bitmap = record.value.byteArrayToInt(0, len)

            for (i in bitmapDefinition.indices) {
                if (bitmap and (1 shl i) == 0) {
                    continue
                }
                if (builder.isNotEmpty()) {
                    builder.append(", ")
                }
                builder.append(bitmapDefinition[i])
            }

            for (i in bitmapDefinition.size until (8 * len)) {
                if (bitmap and (1 shl i) == 0) {
                    continue
                }
                if (builder.isNotEmpty()) {
                    builder.append(", ")
                }
                builder.append("R.string.ndef_wifi_bitmap_unknown, $i")
            }

            return builder.toString()
        }

        private val authTypes = listOf(
            "authtype_open",
            "WPA personal",
            "WEP shared",
            "WPA enterprise",
            "WPA2 enterprise",
            "WPA2 personal"
        )
        private val encTypes = listOf(
            "Unencrypted",
            "WEP",
            "TKIP",
            "AES"
        )
        private val bands = listOf(
            "2.4 GHz",
            "5 GHz",
            "60 GHz"
        )
    }
}

@Parcelize 
data class NdefUriType(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefEntry() {
    override val name: String
        get() = "Unknown URI-typed entry"
}

sealed class NdefExtType : NdefEntry()

@Parcelize 
data class NdefUnknownExtType(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefExtType() {
    override val name: String
        get() = "Unknown NFC Forum external type"
}

@Parcelize 
data class NdefAndroidPkg(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefExtType() {
    override val name: String
        get() = "Android package record"

    override val payloadInfo: List<ListItemInterface>
        get() = listOf(
            ListItem(
                "",
            pkgName
            )
        )
    val pkgName: String
        get() = payload.readUTF8()

    override fun toString(): String = "[Android pkg: $pkgName]"
}

@Parcelize 
data class NdefBinaryType(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefEntry() {
    override val name: String
        get() = "Android package record"
}

@Parcelize 
data class NdefInvalidType(
    override val tnf: Int, override val type: ImmutableByteArray,
    override val id: ImmutableByteArray?,
    override val payload: ImmutableByteArray
) : NdefEntry() {
    override val name: String
        get() = "Invalid NDEF record"

    override val payloadInfo: List<ListItemInterface>
        get() = listOf(ListItem("TNF", "$tnf")) + super.payloadInfo
}
