/*
 * MainActivity.kt
 *
 * Copyright 2011-2015 Eric Butler <eric@codebutler.com>
 * Copyright 2015-2019 Michael Farrell <micolous+git@gmail.com>
 * Copyright 2018-2019 Google
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


import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.dst.testapp.sd.AndroidIsoTransceiver
import com.dst.testapp.sd.CardInfo
import com.dst.testapp.sd.DesfireCard
import com.dst.testapp.sd.DesfireCardReader
import com.dst.testapp.sd.ImmutableByteArray
import com.dst.testapp.sd.Preferences
import com.dst.testapp.sd.TagReaderFeedbackInterface
import java.util.GregorianCalendar


class MainActivity : AppCompatActivity(), TagReaderFeedbackInterface {

    private var mNfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null
    private val mTechLists = arrayOf(
        arrayOf(IsoDep::class.java.name),
        arrayOf(MifareClassic::class.java.name),
        arrayOf(MifareUltralight::class.java.name),
        arrayOf(NfcA::class.java.name),
        arrayOf(NfcF::class.java.name),
        arrayOf(NfcV::class.java.name)
    )
    private lateinit var btnWrite: Button
    private lateinit var tvScan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnWrite = findViewById(R.id.btnWrite)
        tvScan = findViewById(R.id.tvScan)
        btnWrite.setOnClickListener {
            startActivity(Intent(this, CardsActivity::class.java))
        }


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (mNfcAdapter != null) {
            mPendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0
            )
        } else {
            Log.e("TAG", "NFC is not supported on this device")
            // Handle accordingly
        }


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)


        try {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)!!
            val tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)!!

          /*  val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val lastReadId = prefs.getString(Preferences.PREF_LAST_READ_ID, "")
            val lastReadAt = prefs.getLong(Preferences.PREF_LAST_READ_AT, 0)

            // val customer_last_invoice_activity = prefs.getString(Preferences.PREFS_LAST_READ_ID,"). lastReadAT

            // Prevent reading the same card again right away.
            // This was especially a problem with FeliCa cards.

            if (ImmutableByteArray.getHexString(tagId) == lastReadId && GregorianCalendar.getInstance().timeInMillis - lastReadAt < 5000) {
                finish()
                return
            }*/

            ReadingTagTask.doRead(this, tag)
        } catch (ex: Exception) {
            Log.e("TAG", "onNewIntent: " +ex )
        }

    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter?.enableForegroundDispatch(this, mPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.disableForegroundDispatch(this)
    }

    override fun updateStatusText(msg: String) {
        Log.e("TAG", "updateStatusText: $msg", )
        runOnUiThread {
            tvScan.text = msg
        }

    }

    override fun updateProgressBar(progress: Int, max: Int) {
        Log.e("TAG", "updateProgressBar: $progress", )
    }

    override fun showCardType(cardInfo: CardInfo?) {
        Log.e("TAG", "showCardType: $cardInfo", )

    }


}
