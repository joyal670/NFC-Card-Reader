/*
 * CEPASCard.kt
 *
 * Copyright 2011 Sean Cross <sean@chumby.com>
 * Copyright 2013-2014 Eric Butler <eric@codebutler.com>
 * Copyright 2018 Michael Farrell <micolous+git@gmail.com>
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

@Serializable
data class CEPASApplication(
    override val generic: ISO7816ApplicationCapsule,
    private val purses: Map<Int, ImmutableByteArray>,
    private val histories: Map<Int, ImmutableByteArray>) : ISO7816Application() {

    override val rawData: List<ListItemInterface>?
        get() = purses.map { (key, value) ->
            ListItemRecursive.collapsedValue(
                "CEPAS purse number $key", value.toHexDump())
        } + histories.map { (key, value) ->
            ListItemRecursive.collapsedValue(
                "CEPAS purse number $key history", value.toHexDump())
        }

    // FIXME: What about other purses?
    override val manufacturingInfo: List<ListItemInterface>?
        get() {
            val purseRaw = getPurse(3) ?: return listOf(
                    HeaderListItem("Purse info"),
                    ListItem("Error", "unknown"))
            val purse = CEPASPurse(purseRaw)

            return listOf(
                    ListItem("CEPAS version", purse.cepasVersion.toString()),
                    ListItem("Purse ID", "3"),
                    ListItem("Purse status", purse.purseStatus.toString()),
                    ListItem("Purse balance", purse.purseBalance.toString()),

                   /* ListItem("R.string.cepas_purse_creation_date",
                            TimestampFormatter.longDateFormat(purse.purseCreationDate)),*/
                   /* ListItem("R.string.expiry_date",
                            TimestampFormatter.longDateFormat(purse.purseExpiryDate)),*/
                    ListItem("Autoload amount", purse.autoLoadAmount.toString()),
                    ListItem("Card Application Number (CAN)", purse.can.toHexDump()),
                    ListItem("Card Serial Number (CSN)", purse.csn.toHexDump()),

                    HeaderListItem("Info about last transaction"),
                    ListItem("Transaction Pointer (TRP)", purse.lastTransactionTRP.toString()),
                    ListItem("Credit Transaction Pointer (TRP)", purse.lastCreditTransactionTRP.toString()),
                    ListItem("Credit header", purse.lastCreditTransactionHeader.toHexDump()),
                    ListItem("Debit options", purse.lastTransactionDebitOptionsByte.toString()),

                    HeaderListItem("Other purse info"),
                    ListItem("Logfile record count", purse.logfileRecordCount.toString()),
                    ListItem("Issuer data length", purse.issuerDataLength.toString()),
                    ListItem("Issuer-specific data", purse.issuerSpecificData.toHexDump()))
        }


    /*override fun parseTransitIdentity(card: ISO7816Card): TransitIdentity? {
        return if (EZLinkTransitFactory.check(this)) EZLinkTransitFactory.parseTransitIdentity(this) else null
    }

    override fun parseTransitData(card: ISO7816Card): TransitData? {
        return if (EZLinkTransitFactory.check(this)) EZLinkTransitFactory.parseTransitData(this) else null
    }*/

    fun getPurse(purseId: Int): ImmutableByteArray? = purses[purseId]

    fun getHistory(purseId: Int): ImmutableByteArray? = histories[purseId]

    override val type: String
        get() = TYPE

    companion object {
        private const val TAG = "CepasApplication"
        const val TYPE = "cepas"

        private fun setProgress(feedbackInterface: TagReaderFeedbackInterface, value: Int) {
            feedbackInterface.updateProgressBar(value, 64)
        }

        /*fun dumpTag(iso7816Tag: ISO7816Protocol, capsule: ISO7816ApplicationMutableCapsule,
                    feedbackInterface: TagReaderFeedbackInterface): CEPASApplication? {
            val cepasPurses = mutableMapOf<Int, ImmutableByteArray>()
            val cepasHistories = mutableMapOf<Int, ImmutableByteArray>()
            var isValid = false
            val numPurses = 16
            val cepasTag = CEPASProtocol(iso7816Tag)

            try {
                iso7816Tag.selectById(0x4000)
            } catch (e: IllegalStateException) {
                Log.d(TAG, "CEPAS file not found [$e] -- this is expected for non-CEPAS ISO7816 cards")
                return null
            } catch (e: ISO7816Exception) {
                Log.d(TAG, "CEPAS file not found [$e] -- this is expected for non-CEPAS ISO7816 cards")
                return null
            }

            for (purseId in 0 until numPurses) {
                val purse = cepasTag.getPurse(purseId)
                if (purse != null) {
                    cepasPurses[purseId] = ImmutableByteArray(purse)
                    if (!isValid) {
                        val cardInfo = EZLinkTransitFactory.earlyCardInfo(purse)
                        feedbackInterface.updateStatusText("R.string.card_reading_type $cardInfo.name")
                        feedbackInterface.showCardType(cardInfo)
                    }
                    isValid = true
                }
                if (isValid)
                    setProgress(feedbackInterface, purseId)
            }

            if (!isValid)
                return null

            for (historyId in 0 until numPurses) {
                var history: ImmutableByteArray? = null
                if (cepasPurses.containsKey(historyId)) {
                    history = cepasTag.getHistory(historyId)
                }
                if (history != null)
                    cepasHistories[historyId] = ImmutableByteArray(history)
                setProgress(feedbackInterface, historyId + numPurses)
            }

            for (i in 0x0..31) {
                try {
                    capsule.dumpFile(iso7816Tag, ISO7816Selector.makeSelector(0x3f00, 0x4000, i), 0)
                } catch (ex: Exception) {

                    Log.d(TAG, "Couldn't read :3f00:4000:" + i.toString(16))
                }

                setProgress(feedbackInterface, i + 2 * numPurses)
            }
            return CEPASApplication(capsule.freeze(), cepasPurses, cepasHistories)
        }*/
    }
}
