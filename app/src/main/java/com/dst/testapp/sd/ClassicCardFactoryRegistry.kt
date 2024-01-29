package com.dst.testapp.sd



object ClassicCardFactoryRegistry {
    val classicFactories = listOf(
         /*   OVChipTransitData.FACTORY,

            // ERG
            ManlyFastFerryTransitData.FACTORY,
            ChcMetrocardTransitData.FACTORY,
            // ERG Fallback
            ErgTransitData.FALLBACK_FACTORY,

            // Cubic Nextfare
            SeqGoTransitData.FACTORY,
            LaxTapTransitData.FACTORY,
            MspGotoTransitData.FACTORY,
            // Cubic Nextfare Fallback
            NextfareTransitData.FALLBACK_FACTORY,

            SmartRiderTransitData.FACTORY,
            TroikaHybridTransitData.FACTORY,
            PodorozhnikTransitData.FACTORY,
            StrelkaTransitData.FACTORY,
            CharlieCardTransitData.FACTORY,
            RicaricaMiTransitData.FACTORY,
            BilheteUnicoSPTransitData.FACTORY,
            KievTransitData.FACTORY,
            MetroQTransitData.FACTORY,
            EasyCardTransitData.FACTORY,
            SelectaFranceTransitData.FACTORY,
            SunCardTransitData.FACTORY,
            ZolotayaKoronaTransitData.FACTORY,
            RkfTransitData.FACTORY,
            OtagoGoCardTransitFactory,
            WaikatoCardTransitFactory,
            TouchnGoTransitFactory,
            KomuterLinkTransitFactory,
            BonobusTransitFactory,
            GautrainTransitFactory,
            MetroMoneyTransitFactory,
            OysterTransitData.FACTORY,
            KazanTransitFactory,
            UmarshTransitFactory,
            ChileBipTransitFactory,
            WarsawTransitData.FACTORY,
            CifialTransitFactory,
            YarGorTransitFactory,

            TartuTransitFactory, // Must be before NDEF as it's a special case of Ndef*/
            NdefClassicTransitFactory,

            // This check must be THIRD TO LAST.
            //
            // This is to throw up a warning whenever there is a card with all locked sectors
            UnauthorizedClassicTransitData.FACTORY,
            // This check must be SECOND TO LAST.
            //
            // This is to throw up a warning whenever there is a card with all empty sectors
            BlankClassicTransitFactory,
            // This check must be LAST.
            //
            // This is for agencies who don't have identifying "magic" in their card.
            FallbackFactory
    )

    val plusFactories = listOf(
           /* KievDigitalTransitFactory,*/
            NdefClassicTransitFactory,

            // This check must be THIRD TO LAST.
            //
            // This is to throw up a warning whenever there is a card with all locked sectors
            UnauthorizedClassicTransitData.FACTORY,
            // This check must be SECOND TO LAST.
            //
            // This is to throw up a warning whenever there is a card with all empty sectors
            BlankClassicTransitFactory
    )
    val allFactories = classicFactories + plusFactories
}
