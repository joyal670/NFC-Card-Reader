package com.dst.testapp.sd


 fun currencyNameBySymbol(symbol: String): String?{
     return symbol
 }

 fun iso3166AlphaToName(isoAlpha: String): String? {
     return isoAlpha
 }

 fun languageCodeToName(isoAlpha: String): String?{
     return isoAlpha
 }

fun currencyNameByCode(code: Int): String? {
    val symbol = ISO4217.getInfoByCode(code)?.symbol ?: return null
    return currencyNameBySymbol(symbol)
}

fun countryCodeToName(countryCode: Int): String {
    val alpha = ISO3166.mapNumericToAlpha2(countryCode) ?: return "Localizer.localizeString(R.string.unknown_format, countryCode)"
    return iso3166AlphaToName(alpha) ?: "Localizer.localizeString(R.string.unknown_format, alpha)"
}
