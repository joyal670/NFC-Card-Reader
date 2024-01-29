package com.dst.testapp.sd


interface IntercodeLookup : En1545Lookup {
    // We pass a function rather than env itself because most lookups do not need any aditional
    // info and so in most cases we can avoid completely parsing ticketing environment just to get card
    fun cardInfo(env: () -> En1545Parsed): CardInfo?
    val allCards: List<CardInfo>

    override val timeZone: MetroTimeZone
        get() = MetroTimeZone.PARIS

    override fun parseCurrency(price: Int) = TransitCurrency.EUR(price)
}

interface IntercodeLookupSingle : IntercodeLookup {
    val cardInfo: CardInfo?

    override fun cardInfo(env: () -> En1545Parsed): CardInfo? = cardInfo
    override val allCards: List<CardInfo>
        get() = listOfNotNull(cardInfo)
}