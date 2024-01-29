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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnWrite = findViewById(R.id.btnWrite)
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

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val lastReadId = prefs.getString(Preferences.PREF_LAST_READ_ID, "")
            val lastReadAt = prefs.getLong(Preferences.PREF_LAST_READ_AT, 0)

            // val customer_last_invoice_activity = prefs.getString(Preferences.PREFS_LAST_READ_ID,"). lastReadAT

            // Prevent reading the same card again right away.
            // This was especially a problem with FeliCa cards.

            if (ImmutableByteArray.getHexString(tagId) == lastReadId && GregorianCalendar.getInstance().timeInMillis - lastReadAt < 5000) {
                finish()
                return
            }

            ReadingTagTask.doRead(this, tag)
        } catch (ex: Exception) {
            Log.e("TAG", "onNewIntent: " +ex )
        }



       /* // Handle the NFC intent
        val action = intent.action
        when (action) {
           // NfcAdapter.ACTION_NDEF_DISCOVERED -> handleNdefDiscovered(intent)
          //  NfcAdapter.ACTION_TECH_DISCOVERED -> handleTechDiscovered(intent)
            NfcAdapter.ACTION_TAG_DISCOVERED -> handleTagDiscovered(intent)
            // Add more cases for other actions if needed
        }*/
    }

    fun bytesToString(byteArray: List<Byte>, charsetName: String): String {
        val byteArrayPrimitive = byteArray.toByteArray()
        val charset = java.nio.charset.Charset.forName(charsetName)
        return String(byteArrayPrimitive, charset)
    }

    

    private fun handleTagDiscovered(intent: Intent) {
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

     /*   val byteList = listOf(4, 1, 1, 1, 0, 24, 5, 4, 1, 1, 1, 4, 24, 5, 4, 74, 33, -46, 119, 97, -128, -71, 12, 24, 77, -128, 56, 24).map { it.toByte() }
        // Try different charsets
        val charsetsToTry = listOf("UTF-8", "ISO-8859-1", "US-ASCII", "UTF-16")

        for (charset in charsetsToTry) {
            try {
                val result = bytesToString(byteList, charset)
                println("$charset: $result")
            } catch (e: Exception) {
                println("$charset: Error decoding")
            }
        }*/

        AndroidIsoTransceiver(tag!!).use {
            it.connect()

            val d = DesfireCardReader.dumpTag(it)

        }





      /*  // Handle the discovered tag as needed
        Log.e("NFC", "Tag Discovered: ${tag.toString()}")

        // Example: Extract tag ID
        val tagId: ByteArray = tag?.id ?: byteArrayOf()
        Log.e("NFC", "Tag ID: ${tagId.toHexString()}")

        // Check for NfcA technology
        val technologies = tag?.techList
        if (technologies != null) {
            for (tech in technologies) {
                Log.e("TAG", "handleTagDiscovered: technologies $tech")
                when (tech) {
                    NfcA::class.java.name -> {
                        // Attempt to handle NfcA technology
                        val nfcA = NfcA.get(tag)
                        if (nfcA != null) {
                            handleNfcA(nfcA)
                            return
                        }
                    }
                    IsoDep::class.java.name -> {
                        // Attempt to handle IsoDep technology
                        val isoDep = IsoDep.get(tag)
                        if (isoDep != null) {
                            handleIsoDep(isoDep)
                            return
                        }
                    }
                    NdefFormatable::class.java.name -> {
                        // Attempt to handle NdefFormatable technology
                        val ndefFormatable = NdefFormatable.get(tag)
                        if (ndefFormatable != null) {
                            handleNdefFormatable(ndefFormatable)
                            return
                        }
                    }
                    // Add cases for other technologies as needed
                    else -> {
                        handleUnknownTechnology(tech)
                    }
                }
            }
        }*/

    }





    private fun handleIsoDep(isoDep: IsoDep) {
        // Example: Send an APDU command to retrieve data from the card
        val commandHeader = byteArrayOf(0x00.toByte(), 0xB0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x10.toByte())


        try {
            isoDep.connect()

            val selectCommand = byteArrayOf(0x00.toByte(), 0xB2.toByte(), 0x01.toByte(), 0x04.toByte(), 0x10.toByte())
            // Send the APDU command and receive the response
            val response = isoDep.transceive(selectCommand)

            // Convert the byte array to a human-readable string
            val responseString = String(response, Charsets.UTF_8)

            // Log the human-readable response
            Log.e("NFC", "IsoDep Response: $responseString")


            // Example 1: Read binary data from a specific file or block
            val readBinaryCommand = byteArrayOf(0x00.toByte(), 0xB0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x10.toByte())
            val response1 = isoDep.transceive(readBinaryCommand)

            // Example 2: Read record data from a specific file or block
            val readRecordCommand = byteArrayOf(0x00.toByte(), 0xB2.toByte(), 0x01.toByte(), 0x04.toByte(), 0x10.toByte())
            val response2 = isoDep.transceive(readRecordCommand)

            // Process the response data as needed
            Log.e("NFC", "Response 1: ${String(response1, Charsets.ISO_8859_1)}")
            Log.e("NFC", "Response 2: ${String(response2, Charsets.ISO_8859_1)}")






            // Example usage:
           /* val manufacturingDataHexString = byteArrayToHexString(r.manufacturingData)
            val application1File1DataString = byteArrayToUTF8String(r.applications[1]?.get(1) ?: byteArrayOf())
            val application1File2DataString = byteArrayToUTF8String(r.applications[1]?.get(2) ?: byteArrayOf())
            val application2File3DataString = byteArrayToUTF8String(r.applications[2]?.get(3) ?: byteArrayOf())

            Log.e("TAG", "Manufacturing Data Hex: $manufacturingDataHexString")
            Log.e("TAG", "Application 1 File 1 Data: $application1File1DataString")
            Log.e("TAG", "Application 1 File 2 Data: $application1File2DataString")
            Log.e("TAG", "Application 2 File 3 Data: $application2File3DataString")*/


           /* val desfireProtocol = DesfireProtocol(isoDep)

            // Authenticate and read card data
            if (desfireProtocol.authenticate()) {
                val desfireCard = desfireProtocol.readCardData()
                processDesfireCard(desfireCard)
            } else {
                Log.e("TAG", "Authentication failed")
            }*/


        } catch (e: Exception) {
            // Handle the exception
            Log.e("NFC", "IsoDep Error: ${e.message}")
        } finally {
            isoDep.close()
        }
    }

    fun byteArrayToHexString(byteArray: ByteArray): String {
        return byteArray.joinToString("") { "%02X".format(it) }
    }

    fun byteArrayToUTF8String(byteArray: ByteArray): String {
        return String(byteArray, Charsets.UTF_8)
    }

    private fun processDesfireCard(desfireCard: DesfireCard?) {
        desfireCard?.let {
            Log.d("TAG", "Manufacturing Data: ${it.manufacturingData.toHexString()}")
            val humanReadableText = hexStringToHumanReadableText(it.manufacturingData.toHexString())
            println("Manufacturing Data: $humanReadableText")


         /*   it.applications.forEach { (appId, desfireApp) ->
                Log.d("TAG", "Application ID: $appId")
                desfireApp.files.forEach { (fileId, desfireFile) ->
                    Log.d("TAG", "File ID: $fileId, Data: ${desfireFile.data?.toHexString()}")
                }
            }*/
        }
    }

    fun hexStringToHumanReadableText(hexString: String): String {
        val hexBytes = hexString.chunked(2) { it.toString() }
        val byteArray = ByteArray(hexBytes.size) { hexBytes[it].toInt(16).toByte() }
        return byteArray.joinToString("") { "%02X".format(it) }
    }


    // Function to authenticate with a key
    private fun authenticateWithKey(isoDep: IsoDep, key: ByteArray): Boolean {
        // Send DESFire authentication command using the specified key
        val authCommand = byteArrayOf(0x90.toByte(), 0x0A, 0x00, 0x00, 0x01, 0x00, 0x00)
        val commandWithKey = authCommand + key

        try {
            val response = isoDep.transceive(commandWithKey)
            return response.size == 2 && response[0] == 0x91.toByte() && response[1] == 0x00.toByte()
        } catch (e: Exception) {
            // Handle the exception
            return false
        }
    }

    // Function to read data after successful authentication
    private fun readData(isoDep: IsoDep): ByteArray? {
        // Example: Send a command to read data from a specific file or block
        val readCommand = byteArrayOf(0x30.toByte(), 0x04, 0x00, 0x00, 0x10.toByte())
        try {
            return isoDep.transceive(readCommand)
        } catch (e: Exception) {
            // Handle the exception
            Log.e("NFC", "Read Data Error: ${e.message}")
            return null
        }
    }

    private fun handleNfcA(nfcA: NfcA) {
        try {
            nfcA.connect()

            // Example: Read data from block 0 (adjust based on your card's memory structure)
            val blockNumber = 0
            val readCommand = byteArrayOf(0x30.toByte(), blockNumber.toByte())
            val response = nfcA.transceive(readCommand)

            // Process the response data as needed
            Log.d("NFC", "NfcA Response: ${response.toHexString()}")
        } catch (e: Exception) {
            // Handle the exception
            Log.e("NFC", "NfcA Error: ${e.message}")
        } finally {
            nfcA.close()
        }
    }

    private fun handleNdefFormatable(ndefFormatable: NdefFormatable) {
        try {
            ndefFormatable.connect()

            // Use NdefFormatable methods to format the tag
            // Example: ndefFormatable.format(ndefMessage)
        } catch (e: Exception) {
            // Handle the exception
            Log.e("NFC", "NdefFormatable Error: ${e.message}")
        } finally {
            ndefFormatable.close()
        }
    }

   /* private fun readNfcAData(tag: Tag) {
        val nfcA = NfcA.get(tag)
        nfcA?.let {
            try {
                it.connect()

                // Example: Read the UID (unique identifier) from the NfcA card
                val uid = it.tag.id.toHexString()
                Log.d("NFC", "NfcA UID: $uid")
            } catch (e: Exception) {
                // Handle the exception
                Log.e("NFC", "Error reading NfcA data: ${e.message}")
            } finally {
                it.close()
            }
        }
    }*/

    private fun handleUnknownTechnology(tech: String) {
        // Handle other technologies as needed
        Log.d("NFC", "Unknown technology: $tech")
    }

    // Utility function to convert byte array to hexadecimal string
    fun ByteArray.toHexString(): String {
        return joinToString("") { byte -> "%02X".format(byte) }
    }

  /*  private fun handleTechDiscovered(intent: Intent) {
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        // Check for IsoDep technology
        val technologies = tag?.techList
        if (technologies != null) {
            for (tech in technologies) {
                when (tech) {
                    IsoDep::class.java.name -> {
                        // IsoDep technology is supported
                        // Communicate with the card using IsoDep
                        handleIsoDep(tag)
                    }
                    // Add cases for other technologies as needed
                    else -> {
                        handleUnknownTechnology(tech)
                    }
                }
            }
        }
    }*/

   /* private fun handleIsoDep(tag: Tag) {
        val isoDep = IsoDep.get(tag)
        Log.e("TAG", "handleIsoDep: "+ isoDep )
        isoDep?.let {
            try {
                it.connect()

                // Example: Send an APDU command to retrieve data from the card
                val command = byteArrayOf(*//* Your APDU command bytes *//*)
                val response = it.transceive(command)

                // Process the response data as needed
                Log.d("NFC", "IsoDep Response: ${response.toHexString()}")
            } catch (e: Exception) {
                // Handle the exception
                Log.e("NFC", "IsoDep Error: ${e.message}")
            } finally {
                it.close()
            }
        }
    }*/

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
    }

    override fun updateProgressBar(progress: Int, max: Int) {
        Log.e("TAG", "updateProgressBar: $progress", )
    }

    override fun showCardType(cardInfo: CardInfo?) {
        Log.e("TAG", "showCardType: $cardInfo", )
       /* runOnUiThread {
            val i = findViewById<ImageView>(R.id.card_image)

            if (cardInfo != null) {
               *//* if (cardInfo.hasBitmap) {
                    i.setImageDrawable(DrawableUtils.getCardInfoDrawable(this, cardInfo))
                }*//*
                i.contentDescription = cardInfo.name
            } else {
                i.setImageResource(R.drawable.logo)
                i.contentDescription = "unknown_card"
            }
            i.invalidate()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val t = findViewById<TextView>(R.id.status_text)
                val man = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
                if (man != null && man.isEnabled) {
                    val e = AccessibilityEvent.obtain()
                    e.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                    e.text.add(t.text)
                    man.sendAccessibilityEvent(e)
                }
            }
        }*/
    }


}
