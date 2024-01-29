package com.dst.testapp

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.nfc.Tag
import android.nfc.TagLostException
import android.util.Log
import androidx.preference.PreferenceManager
import com.dst.testapp.sd.CardProvider
import com.dst.testapp.sd.CardReader
import com.dst.testapp.sd.CardSerializer
import com.dst.testapp.sd.CardsTableColumns
import com.dst.testapp.sd.Preferences
import com.dst.testapp.sd.UnsupportedTagException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.GregorianCalendar

internal class ReadingTagTask private constructor(
        private val readingTagActivity: MainActivity,
        private val tag: Tag): CoroutineScope  {
    override val coroutineContext = Job()

    private fun doInBackground(): Pair<Uri?,Boolean> {
        val card = CardReader.dumpTag(tag, readingTagActivity)

        readingTagActivity.updateStatusText("saving card")

        val cardXml = CardSerializer.toPersist(card)

        if (card.isPartialRead) {
            Log.e(TAG, "Partial card read.")
        } else {
            Log.e(TAG, "Finished dumping card.")
        }
        for (line in cardXml.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {

            Log.e(TAG, "Persist: $line")
        }

        val tagIdString = card.tagId.toHexString()

        val values = ContentValues()
        values.put(CardsTableColumns.TYPE, card.cardType.toString())
        values.put(CardsTableColumns.TAG_SERIAL, (tagIdString).toString())
        values.put(CardsTableColumns.DATA, cardXml)
        values.put(CardsTableColumns.SCANNED_AT, card.scannedAt.timeInMillis)
        values.put(CardsTableColumns.LABEL, card.label)

        val uri = readingTagActivity.contentResolver.insert(CardProvider.CONTENT_URI_CARD, values)

        val prefs = PreferenceManager.getDefaultSharedPreferences(readingTagActivity).edit()
        prefs.putString(Preferences.PREF_LAST_READ_ID, tagIdString)
        prefs.putLong(Preferences.PREF_LAST_READ_AT, GregorianCalendar.getInstance().timeInMillis)
        prefs.apply()

        Log.e(TAG, "doInBackground:  ${Pair(uri, card.isPartialRead)} ")

        return Pair(uri, card.isPartialRead)
    }

    private fun showCard(cardUri: Uri?) {
        Log.e(TAG, "showCard: $cardUri")
        readingTagActivity.updateStatusText("Read successfully")
        if (cardUri == null)
            return

        try {
            val intent = Intent(Intent.ACTION_VIEW, cardUri)
            intent.putExtra(CardInfoActivity.SPEAK_BALANCE_EXTRA, true)
            readingTagActivity.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "showCard: $e" )
        }
        // readingTagActivity.finish()
    }

    fun onPostExecute(cardUri: Uri?, exception: Exception?, isPartialRead: Boolean) {
        if (isPartialRead) {
            AlertDialog.Builder(readingTagActivity)
                    .setTitle(R.string.card_partial_read_title)
                    .setMessage(R.string.card_partial_read_desc)
                    .setCancelable(false)
                    .setPositiveButton(R.string.show_partial_data) { _, _ -> showCard(cardUri) }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> readingTagActivity.finish() }
                    .show()
            return
        }

        if (exception == null && cardUri != null) {
            showCard(cardUri)
            return
        }

        when (exception) {
            is TagLostException -> {
                // Tag was lost. Just drop out silently.
            }
            is UnsupportedTagException -> AlertDialog.Builder(readingTagActivity)
                    .setTitle(R.string.unsupported_tag)
                    .setMessage(exception.dialogMessage)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { _, _ -> readingTagActivity.finish() }
                    .show()
            else -> Log.e(TAG, "onPostExecute: $exception")
        }
    }

    companion object {
        private val TAG = ReadingTagTask::class.java.simpleName

        fun doRead(readingTagActivity: MainActivity, tag: Tag) {
            readingTagActivity.updateStatusText("Reading Card")
            ReadingTagTask(readingTagActivity, tag).start()
        }
    }

    private fun start() {
        launch {
            var res: Pair<Uri?, Boolean>? = null
            var ex: Exception? = null
            try {
                res = doInBackground()
            } catch (e: Exception) {
                ex = e
            }

            readingTagActivity.runOnUiThread { onPostExecute(res?.first, ex, res?.second ?: false) }
        }
    }
}
