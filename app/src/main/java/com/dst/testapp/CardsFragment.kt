/*
 * CardsFragment.kt
 *
 * Copyright 2012-2014 Eric Butler <eric@codebutler.com>
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

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.*
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.dst.testapp.sd.BetterAsyncTask
import com.dst.testapp.sd.CardDBHelper
import com.dst.testapp.sd.CardMultiImporter
import com.dst.testapp.sd.CardProvider
import com.dst.testapp.sd.CardType
import com.dst.testapp.sd.CardsTableColumns
import com.dst.testapp.sd.MetroTimeZone
import com.dst.testapp.sd.Preferences
import com.dst.testapp.sd.TimestampFormatter
import com.dst.testapp.sd.TimestampFull
import com.dst.testapp.sd.TransitIdentity
import com.dst.testapp.sd.TripObfuscator
import com.dst.testapp.sd.Utils
import com.dst.testapp.sd.XmlOrJsonCardFormat
import com.dst.testapp.sd.getErrorMessage
import org.jetbrains.annotations.NonNls
import java.io.InputStream
import java.lang.ref.WeakReference

class CardsFragment : ExpandableListFragment()  {



    private val mLoaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
            return CursorLoader(requireActivity(), CardProvider.CONTENT_URI_CARD,
                    CardDBHelper.PROJECTION,
                    null, null,
                    "${CardsTableColumns.SCANNED_AT} DESC, ${CardsTableColumns._ID} DESC")
        }

        override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor?) {
            if (cursor == null)
                return
            val scans = mutableMapOf<CardId, MutableList<Scan>>()
            val cards = ArrayList<CardId>()
            val reverseCards = mutableMapOf<CardId, Int>()
            cursor.moveToPosition(-1)
            while (cursor.moveToNext()) {
                val type = cursor.getInt(cursor
                        .getColumnIndexOrThrow(CardsTableColumns.TYPE))
                val serial = cursor.getString(cursor
                        .getColumnIndexOrThrow(CardsTableColumns.TAG_SERIAL))
                val id = CardId(type, serial)
                if (!scans.containsKey(id)) {
                    scans[id] = ArrayList()
                    cards.add(id)
                    reverseCards[id] = cards.size - 1
                }
                scans[id]!!.add(Scan(cursor))
            }

            Log.d(TAG, "creating adapter " + cards.size)
            listAdapter = CardsAdapter(requireActivity(), scans, cards, reverseCards)
            setListShown(true)
            setEmptyText(getString(R.string.no_scanned_cards))
        }

        override fun onLoaderReset(cursorLoader: Loader<Cursor>) {}
    }

    private class Scan(cursor: Cursor) {
        val mScannedAt: Long = cursor.getLong(cursor.getColumnIndexOrThrow(CardsTableColumns.SCANNED_AT))
        val mLabel: String? = cursor.getString(cursor.getColumnIndexOrThrow(CardsTableColumns.LABEL))
        val mType: Int = cursor.getInt(cursor.getColumnIndexOrThrow(CardsTableColumns.TYPE))
        val mSerial: String = cursor.getString(cursor.getColumnIndexOrThrow(CardsTableColumns.TAG_SERIAL))
        val mData: String = cursor.getString(cursor.getColumnIndexOrThrow(CardsTableColumns.DATA))
        val mTransitIdentity: TransitIdentity? by lazy {
            try {
                XmlOrJsonCardFormat.parseString(mData)?.parseTransitIdentity()
            } catch (ex: Exception) {
                Log.e("TAG", ": error ${ex.message}", )
                val error = String.format("Error: %s", getErrorMessage(ex))
                TransitIdentity(error, null)
            }
        }
        val mId: Int = cursor.getInt(cursor.getColumnIndexOrThrow(CardsTableColumns._ID))


    }

    private data class CardId (val type: Int, val serial: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerForContextMenu(listView!!)
        if (listAdapter == null) {
            LoaderManager.getInstance(this).initLoader(0, null, mLoaderCallbacks).startLoading()
        }
    }

    override fun onListChildClick(parent: ExpandableListView, v: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {

        Log.d(TAG, "Clicked $id $groupPosition $childPosition")
        val uri = ContentUris.withAppendedId(CardProvider.CONTENT_URI_CARD, id)
        val intent = Intent(activity, CardInfoActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        intent.data = uri
        startActivity(intent)
        return true
    }


    class UserCancelledException : Exception()


    private fun updateListView() {
        (requireView().findViewById<ExpandableListView>(android.R.id.list).expandableListAdapter as CardsAdapter).notifyDataSetChanged()
    }

    private class CardsAdapter(ctxt: Context,
                               private val mScans: Map<CardId, List<Scan>>,
                               private val mCards: List<CardId>,
                               private val mReverseCards: Map<CardId, Int>) : BaseExpandableListAdapter() {

        private var filteredCards: List<CardId>? = null

        private val effectiveCards: List<CardId>
            get() = filteredCards ?: mCards

        init {
            Log.d(TAG, "Cards adapter " + effectiveCards.size)
        }

        private val mLayoutInflater: LayoutInflater = LayoutInflater.from(ctxt)



        override fun getGroupCount(): Int {
            Log.d(TAG, "getGroupCount " + effectiveCards.size)
            return effectiveCards.size
        }

        override fun getChildrenCount(i: Int): Int = mScans.getValue(effectiveCards[i]).size

        override fun getGroup(i: Int): Any? {
            Log.d(TAG, "getGroup $i")
            return mScans[effectiveCards[i]]
        }

        override fun getChild(parent: Int, child: Int): Any = mScans.getValue(effectiveCards[parent])[child]

        override fun getGroupId(i: Int): Long = (mReverseCards[effectiveCards[i]] ?: i).toLong() + 0x1000000

        override fun getChildId(parent: Int, child: Int): Long = mScans.getValue(effectiveCards[parent])[child].mId.toLong()

        override fun hasStableIds(): Boolean = true

        override fun getGroupView(group: Int, isExpanded: Boolean, convertViewReuse: View?, parent: ViewGroup): View {
            val convertView = convertViewReuse ?: mLayoutInflater.inflate(R.layout.card_name_header,
                        parent, false)
            val scan = mScans.getValue(effectiveCards[group])[0]
            val type = scan.mType
            val serial = scan.mSerial
            val label = scan.mLabel

            val identity = scan.mTransitIdentity

            val textView1 = convertView!!.findViewById<TextView>(android.R.id.text1)
            val textView2 = convertView.findViewById<TextView>(android.R.id.text2)

            if (identity != null) {
                textView1.text = identity.name
                when {
                    label?.isEmpty() == false -> {
                        // This is used for imported cards from mfcdump_to_farebotxml.py
                        // Used for development and testing. We should always show this.
                        textView2.text = label
                    }
                    Preferences.hideCardNumbers -> {
                        textView2.text = ""
                        textView2.visibility = View.GONE
                        // User doesn't want to show any card numbers.
                    }
                    else -> {
                        // User wants to show card numbers (default).
                        textView2.text = Utils.weakLTR(identity.serialNumber ?: serial)
                    }
                }
            } else

            {
                textView1.setText(R.string.unknown_card)
                if (Preferences.hideCardNumbers) {
                    textView2.text = CardType.parseValue(type).toString()
                } else {
                    @SuppressLint("SetTextI18n")
                    textView2.text = "${CardType.parseValue(type)} - $serial"
                }
            }

            return convertView
        }

        override fun getChildView(parent: Int, child: Int, isLast: Boolean, convertViewReuse: View?, viewGroup: ViewGroup): View {
            val convertView = convertViewReuse ?: mLayoutInflater.inflate(R.layout.card_scan_item,
                        viewGroup, false)
            val scan = mScans.getValue(effectiveCards[parent])[child]
            val scannedAt = TripObfuscator.maybeObfuscateTS(TimestampFull(scan.mScannedAt, MetroTimeZone.LOCAL))

            val textView1 = convertView.findViewById<TextView>(android.R.id.text1)
            textView1.text = "Scanned at ${TimestampFormatter.timeFormat(scannedAt)}---------${TimestampFormatter.dateFormat(scannedAt)}"

            return convertView
        }

        override fun isChildSelectable(i: Int, i1: Int): Boolean = true
    }

    companion object {
        private const val TAG = "CardsFragment"
        @NonNls
        private const val STD_EXPORT_FILENAME = "Metrodroid-Export.zip"

        private fun onCardsImported(ctx: Context, uriCount: Int, firstUri: Uri?) {
            Toast.makeText(ctx, "R.plurals.cards_imported, $uriCount, $uriCount", Toast.LENGTH_SHORT).show()
            if (uriCount == 1 && firstUri != null) {
                ctx.startActivity(Intent(Intent.ACTION_VIEW, firstUri))
            }
        }
    }
}
