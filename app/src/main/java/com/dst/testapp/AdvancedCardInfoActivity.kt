/*
 * AdvancedCardInfoActivity.kt
 *
 * Copyright (C) 2011 Eric Butler
 * Copyright 2015-2018 Michael Farrell <micolous+git@gmail.com>
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

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dst.testapp.sd.Card
import com.dst.testapp.sd.CardSerializer
import com.dst.testapp.sd.Preferences
import com.dst.testapp.sd.TimestampFormatter
import com.dst.testapp.sd.TripObfuscator
import com.dst.testapp.sd.UnauthorizedException
import com.dst.testapp.sd.UnsupportedCardException
import com.dst.testapp.sd.getErrorMessage
import com.dst.testapp.sd.registerForActivityResultIfOkAndShowError


class AdvancedCardInfoActivity : AppCompatActivity() {

    private var mCard: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_card_info)

        val card = CardSerializer.fromPersist(intent.getStringExtra(EXTRA_CARD)!!)
        mCard = card

        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val tabsAdapter = TabPagerAdapter(this, viewPager)

        if (intent.hasExtra(EXTRA_ERROR)) {
            when (val error = intent.getSerializableExtra(EXTRA_ERROR) as Exception) {
                is UnsupportedCardException -> findViewById<View>(R.id.unknown_card).visibility = View.VISIBLE
                is UnauthorizedException -> {
                    findViewById<View>(R.id.unauthorized_card).visibility = View.VISIBLE
                    findViewById<View>(R.id.load_keys).setOnClickListener {
                        AlertDialog.Builder(this@AdvancedCardInfoActivity)
                                .setMessage(R.string.add_key_directions)
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                    }
                }
                else -> {
                    findViewById<View>(R.id.error).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.error_text).text = getErrorMessage(error)
                }
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Preferences.hideCardNumbers) {
            supportActionBar?.title = card.cardType.toString()
        } else {
            supportActionBar?.title = card.cardType.toString() + " " + card.tagId.toHexString()
        }

        var scannedAt = card.scannedAt
        if (card.scannedAt.timeInMillis > 0) {
            scannedAt = TripObfuscator.maybeObfuscateTS(scannedAt)
            val date = TimestampFormatter.dateFormat(scannedAt)
            val time = TimestampFormatter.timeFormat(scannedAt)
            supportActionBar?.subtitle = "Scanned at $time on $date"
        }

        if (card.manufacturingInfo != null) {
            tabsAdapter.addTab("Manufacturing info", ::CardHWDetailFragment,
                    intent.extras)
        }

        if (card.rawData != null) {
            tabsAdapter.addTab("Raw Data", ::CardRawDataFragment,
                    intent.extras)
        }
    }





    private val requestSaveFileLauncher = registerForActivityResultIfOkAndShowError(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri: Uri? = result.data?.data
        Log.d(TAG, "REQUEST_SAVE_FILE")
        val os = contentResolver.openOutputStream(uri!!)!!
        val json = CardSerializer.toJsonString(mCard!!)
        os.write(json.encodeToByteArray())
        os.close()
        Toast.makeText(this, R.string.saved_xml_custom, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_CARD = "au.id.micolous.farebot.EXTRA_CARD"
        const val EXTRA_ERROR = "au.id.micolous.farebot.EXTRA_ERROR"
        private val TAG = AdvancedCardInfoActivity::class.java.name
    }
}
