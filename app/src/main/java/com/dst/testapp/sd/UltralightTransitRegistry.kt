package com.dst.testapp.sd


object UltralightTransitRegistry {
     val allFactories = listOf(
           /* TroikaUltralightTransitData.FACTORY,
            CompassUltralightTransitData.FACTORY,
            VentraUltralightTransitData.FACTORY,
            // This must be after the checks for known Nextfare MFU deployments.
            NextfareUnknownUltralightTransitData.FACTORY,
            ClipperUltralightTransitData.FACTORY,
            OvcUltralightTransitFactory(),
            MRTUltralightTransitFactory(),
            VeneziaUltralightTransitFactory(),
            PisaUltralightTransitFactory(),
            AmiiboTransitFactory,*/
            HSLUltralightTransitFactory,
            NdefUltralightTransitFactory,

            BlankUltralightTransitFactory,
            // This check must be LAST.
            //
            // This is to throw up a warning whenever there is a card with all locked sectors
            UnauthorizedUltralightTransitData.FACTORY)
}
