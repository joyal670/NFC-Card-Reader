package com.dst.testapp.sd


class MfcCardImporter : CardImporter {
    fun readCard(bin: ByteArray, time: TimestampFull): Card =
            readCard(stream=ByteArrayInput(bin), time)
    fun readCard(stream: Input, time: TimestampFull): Card {
        // Read the blocks of the card.
        val sectors = mutableListOf<ClassicSector>()
        var uid: ImmutableByteArray? = null
        var maxSector = 0

        for (sectorNum in 0 until MAX_SECTORS) {
            val blockCount =
                if (sectorNum >= 32)
                    16
                else
                    4

            val sectorData = stream.readBytes(16 * blockCount)
            if (sectorData.isEmpty() && sectorNum != 0) {
                 // We got to the end of the file.
                 break
            }

            if (sectorData.size != 16 * blockCount) {
                throw Exception("Incomplete MFC read at sector $sectorNum (${sectorData.size} bytes)")
            }

            if (sectorNum == 0) {
                // Manufacturer data
                uid = block0ToUid(sectorData.sliceArray(0..15).toImmutable())
            }

            val lbOffset = 16 * (blockCount - 1)
            val keyA = sectorData.sliceArray((lbOffset+0)..(lbOffset+5)).toImmutable()
            val keyB = sectorData.sliceArray((lbOffset+10)..(lbOffset+15)).toImmutable()
            val blocks =
                (0 until blockCount).map {
                    sectorData.sliceArray((it*16)..(it*16+15)).toImmutable()
                }

            val raw = ClassicSectorRaw(blocks, keyA,
                    keyB, false, null)
            sectors.add(ClassicSector.create(raw))
            maxSector = sectorNum
        }

        // End of file, now see how many blocks we get
        when {
            maxSector <= 15 -> maxSector = 15 // 1K
            maxSector <= 31 -> maxSector = 31 // 2K
            maxSector <= 39 -> maxSector = 39 // 4K
        }

        // Fill missing sectors as "unauthorised".
        while (sectors.size <= maxSector) {
            sectors.add(UnauthorizedClassicSector())
        }

        return Card(uid!!, time, mifareClassic = ClassicCard(sectors))
    }

    override fun readCard(stream: Input): Card =
        readCard(stream, TimestampFull.now())

    companion object {
        private const val MAX_SECTORS = 40

        fun block0ToUid(block0: ImmutableByteArray) =
                if (block0[0] == 4.toByte() && block0.byteArrayToInt(8, 2) in listOf(0x0400, 0x4400))
                    block0.copyOfRange(0, 7)
                else
                    block0.copyOfRange(0, 4)
    }
}
