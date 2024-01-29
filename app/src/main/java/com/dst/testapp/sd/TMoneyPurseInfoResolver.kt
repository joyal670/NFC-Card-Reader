/*
 * TMoneyPurseInfoResolver.kt
 *
 * Copyright 2019 Michael Farrell <micolous+git@gmail.com>
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
 *
 * References: https://github.com/micolous/metrodroid/wiki/T-Money
 */
package com.dst.testapp.sd


/**
 * [KSX6924PurseInfoResolver] singleton for T-Money.
 *
 * This contains mapping for IDs on a T-Money card.
 *
 * See https://github.com/micolous/metrodroid/wiki/T-Money for more information.
 */
object TMoneyPurseInfoResolver : KSX6924PurseInfoResolver() {
    override val issuers : Map<Int, String> = mapOf(
            // 0x00: reserved
            0x01 to "R.string.tmoney_issuer_kftci",
            // 0x02: A-CASH (에이캐시) (Also used by Snapper)
            0x03 to "R.string.tmoney_issuer_mybi",
            // 0x04: reserved
            // 0x05: V-Cash (브이캐시)
            0x06 to "R.string.tmoney_issuer_mondex",
            0x07 to "R.string.tmoney_issuer_kec",
            0x08 to "R.string.tmoney_issuer_kscc",
            0x09 to "R.string.tmoney_issuer_korail",
            // 0x0a: reserved
            0x0b to "R.string.tmoney_issuer_eb",
            0x0c to "R.string.tmoney_issuer_seoul_bus",
            0x0d to "R.string.tmoney_issuer_cardnet"
    )

    override val userCodes : Map<Int, String > = mapOf(
            0x01 to "R.string.tmoney_usercode_regular",
            0x02 to "R.string.tmoney_usercode_child",
            // TTAK.KO 12.0240 disagrees
            0x03 to "R.string.tmoney_usercode_youth",
            // TTAK.KO 12.0240 disagrees
            0x04 to "R.string.tmoney_usercode_senior",
            // TTAK.KO 12.0240 disagrees
            0x05 to "R.string.tmoney_usercode_disabled",
            // Only in TTAK.KO 12.0240
            0x0f to "R.string.tmoney_usercode_test",
            0x11 to "R.string.tmoney_usercode_bus",
            0x12 to "R.string.tmoney_usercode_lorry",
            0xff to "R.string.tmoney_usercode_inactive"
    )

    override val disRates : Map<Int, String > = mapOf(
            0x00 to "R.string.tmoney_disrate_none",
            0x10 to "R.string.tmoney_disrate_disabled_basic",
            0x11 to "R.string.tmoney_disrate_disabled_companion",
            // 0x12 - 0x1f: reserved
            0x20 to "R.string.tmoney_disrate_veteran_basic",
            0x21 to "R.string.tmoney_disrate_veteran_companion"
            // 0x22 - 0x2f: reserved
    )

    override val tCodes : Map<Int, String > = mapOf(
            0x00 to "R.string.none",
            0x01 to " R.string.tmoney_tcode_sk",
            0x02 to "R.string.tmoney_tcode_kt",
            0x03 to "R.string.tmoney_tcode_lg"
    )

    override val cCodes : Map<Int, String > = mapOf(
            0x00 to "R.string.none",
            0x01 to "R.string.tmoney_ccode_kb",
            0x02 to "R.string.tmoney_ccode_nonghyup",
            0x03 to "R.string.tmoney_ccode_lotte",
            0x04 to "R.string.tmoney_ccode_bc",
            0x05 to "R.string.tmoney_ccode_samsung",
            0x06 to "R.string.tmoney_ccode_shinhan",
            0x07 to "R.string.tmoney_ccode_citi",
            0x08 to "R.string.tmoney_ccode_exchange",
            0x09 to "R.string.tmoney_ccode_woori",
            0x0a to "R.string.tmoney_ccode_hana_sk",
            0x0b to "R.string.tmoney_ccode_hyundai"
    )
}
