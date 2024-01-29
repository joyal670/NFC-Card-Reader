/*
 * ReadingTagActivity.kt
 *
 * Copyright 2011 Eric Butler <eric@codebutler.com>
 * Copyright 2018 Michael Farrell <micolous+git@gmail.com>
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

package com.dst.testapp

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ReadingTagActivity : AppCompatActivity() {


     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_tag)
         Log.e("TAG", "onCreate: activity_reading_tag", )

        resolveIntent(intent)
    }

     override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        resolveIntent(intent)
    }

    private fun resolveIntent(intent: Intent) {
        try {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)!!
            val tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)!!
            Log.e("TAG", "resolveIntent: "+ tag +"  **  " + tagId )

/*
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val lastReadId = prefs.getString(Preferences.PREF_LAST_READ_ID, "")
            val lastReadAt = prefs.getLong(Preferences.PREF_LAST_READ_AT, 0)
*/

           // val customer_last_invoice_activity = prefs.getString(Preferences.PREFS_LAST_READ_ID,"). lastReadAT

            // Prevent reading the same card again right away.
            // This was especially a problem with FeliCa cards.

           /* if (ImmutableByteArray.getHexString(tagId) == lastReadId && GregorianCalendar.getInstance().timeInMillis - lastReadAt < 5000) {
                finish()
                return
            }*/

           // ReadingTagTask.doRead(this, tag)
        } catch (ex: Exception) {
            Log.e("TAG", "resolveIntent: "+ ex.message )
        }
    }

}
