package com.dst.testapp.sd



object NFCVTransitRegistry {
    val allFactories = listOf(
         NdefVicinityTransitFactory,
         BlankNFCVTransitFactory
     )
}
