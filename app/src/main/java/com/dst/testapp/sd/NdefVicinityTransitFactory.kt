package com.dst.testapp.sd



object NdefVicinityTransitFactory : NFCVCardTransitFactory {
    override fun parseTransitIdentity(card: NFCVCard) =
        TransitIdentity(NdefData.NAME, null)

    override fun parseTransitData(card: NFCVCard): TransitData? =
        NdefData.parseNFCV(card)

    override fun check(card: NFCVCard): Boolean = NdefData.checkNFCV(card)

    override val allCards: List<CardInfo>
        get() = listOf(NdefData.CARD_INFO)
}