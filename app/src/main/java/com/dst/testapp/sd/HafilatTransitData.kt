


package com.dst.testapp.sd

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize

@Parcelize
class HafilatTransitData (
    override val trips: List<TransactionTripAbstract>,
    override val subscriptions: List<HafilatSubscription>?,
    private val purse: HafilatSubscription?,
    private val serial: Long,
    private val parsed : En1545Parsed
): En1545TransitData(parsed) {
    override val lookup: HafilatLookup
        get() = HafilatLookup

    override val serialNumber: String?
        get() = formatSerial(serial)

    override val cardName: String
        get() = NAME

    override val info: List<ListItemInterface>?
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val items = mutableListOf<ListItem>()
            if (purse != null) {
                items.add(ListItem("Ticket type", purse.subscriptionName))

                if (purse.machineId != null) {
                    items.add(ListItem("Machine ID",
                        purse.machineId.toString()))
                }

                val purchaseTS = purse.purchaseTimestamp
                if (purchaseTS != null) {
                    items.add(ListItem("Date of issue", purchaseTS.format()))
                }

                val purseId = purse.id
                if (purseId != null)
                    items.add(ListItem("Purse serial number", purseId.toString(16)))
            }
            return super.info.orEmpty() + items
        }

    companion object {
        private fun parse(card: DesfireCard): HafilatTransitData {
            val app = card.getApplication(APP_ID)

            // This is basically mapped from Intercode
            // 0 = TICKETING_ENVIRONMENT
            val parsed = En1545Parser.parse(app!!.getFile(0)!!.data,
                IntercodeTransitData.TICKET_ENV_FIELDS)

            // 1 is 0-record file on all cards we've seen so far

            // 2 = TICKETING_CONTRACT_LIST, not really useful to use

            val transactionList = mutableListOf<Transaction>()

            // 3-6: TICKETING_LOG
            // 8 is "ABU DHABI" and some indexing
            // 9, 0xd: zero-filled
            // a:???
            for (fileId in intArrayOf(3, 4, 5, 6)) {
                val data = app.getFile(fileId)?.data ?: continue
                if (data.getBitsFromBuffer(0, 14) == 0)
                    continue
                transactionList.add(HafilatTransaction(data))
            }

            // c-f: locked counters
            val subs = mutableListOf<HafilatSubscription>()
            var purse: HafilatSubscription? = null
            // 10-13: contracts
            for (fileId in intArrayOf(0x10, 0x11, 0x12, 0x13)) {
                val data = app.getFile(fileId)?.data ?: continue
                if (data.getBitsFromBuffer(0, 7) == 0)
                    continue
                val sub = HafilatSubscription(data)
                if (sub.isPurse)
                    purse = sub
                else
                    subs.add(sub)
            }

            // 14-17: zero-filled
            // 18: ??
            // 19: zero-filled
            // 1b-1c: locked
            // 1d: empty record file
            // 1e: const

            return HafilatTransitData(purse = purse,
                serial = getSerial(card.tagId),
                subscriptions = if (subs.isNotEmpty()) subs else null,
                trips = TransactionTrip.merge(transactionList),
                parsed = parsed)
        }

        private const val APP_ID = 0x107f2
        private val NAME = "Hafilat"

        private val CARD_INFO = CardInfo(
            name = NAME,
            locationId = "Abu Dhabi, UAE",
            cardType = CardType.MifareDesfire,
            region = TransitRegion.UAE,
            )

        val FACTORY: DesfireCardTransitFactory = object : DesfireCardTransitFactory {
            override val allCards: List<CardInfo>
                get() = listOf(CARD_INFO)

            override fun parseTransitIdentity(card: DesfireCard) =
                TransitIdentity(NAME, formatSerial(getSerial(card.tagId)))

            override fun earlyCheck(appIds: IntArray) = APP_ID in appIds

            override fun parseTransitData(card: DesfireCard) = parse(card)
        }

        private fun formatSerial(serial: Long) = "01-" + NumberUtils.zeroPad(serial, 15)

        private fun getSerial(tagId: ImmutableByteArray) =
            tagId.byteArrayToLongReversed(1, 6)
    }
}

