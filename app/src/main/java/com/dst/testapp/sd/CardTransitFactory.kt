package com.dst.testapp.sd

interface CardTransitFactory<T> {
    val allCards: List<CardInfo>
        get() = emptyList()

    fun parseTransitIdentity(card: T): TransitIdentity?

    fun check(card: T): Boolean

    fun parseTransitData(card: T): TransitData?
}
