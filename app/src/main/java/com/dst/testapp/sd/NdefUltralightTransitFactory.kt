package com.dst.testapp.sd



object NdefUltralightTransitFactory : UltralightCardTransitFactory {
    override fun parseTransitIdentity(card: UltralightCard) =
        TransitIdentity(NdefData.NAME, null)

    override fun parseTransitData(card: UltralightCard): TransitData? =
        NdefData.parseUltralight(card)

    override fun check(card: UltralightCard): Boolean = NdefData.checkUltralight(card)

    override val allCards: List<CardInfo>
        get() = listOf(NdefData.CARD_INFO)
}