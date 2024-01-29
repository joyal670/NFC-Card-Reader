/*
 * TransitName.kt
 *
 * Copyright (C) 2019 Google
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


class TransitName(
    val englishFull: String?,
    val englishShort: String?,
    val localFull: String?,
    val localShort: String?,
    val localLanguagesList: List<String>,
    val ttsHintLanguage: String
) {
    private fun useEnglishName(): Boolean {
        val locale = Preferences.language
        return !localLanguagesList.contains(locale)
    }

    fun selectBestName(isShort: Boolean):  String? {
        val hasEnglishFull = englishFull != null && englishFull.isNotEmpty()
        val hasEnglishShort = englishShort != null && englishShort.isNotEmpty()

        val english: String? = when {
            hasEnglishFull && !hasEnglishShort -> englishFull
            !hasEnglishFull && hasEnglishShort -> englishShort
            isShort -> englishShort
            else -> englishFull
        }

        val hasLocalFull = localFull != null && localFull.isNotEmpty()
        val hasLocalShort = localShort != null && localShort.isNotEmpty()

        val local: String? = when {
            hasLocalFull && !hasLocalShort -> localFull
            !hasLocalFull && hasLocalShort -> localShort
            isShort -> localShort
            else -> localFull
        }

        if (showBoth() && english != null && english.isNotEmpty()
                && local != null && local.isNotEmpty()
        ) {
            if (english == local)
                return  "String.language(local, ttsHintLanguage)"
            return if (useEnglishName())  "String.english(english)" + " (" +  "String.language(local, ttsHintLanguage)" + ")" else  "String.language(local, ttsHintLanguage) "+ " (" +  "String.english(english)" + ")"
        }
        if (useEnglishName() && english != null && english.isNotEmpty()) {
            return  "String.english(english)"
        }

        return if (local != null && local.isNotEmpty()) {
            // Local preferred, or English not available
            " String.language(local, ttsHintLanguage)"
        } else if (english != null) {
            // Local unavailable, use English
            " String.english(english)"
        } else
            null
    }

    companion object {
        private fun showBoth(): Boolean = Preferences.showBothLocalAndEnglish
    }
}
