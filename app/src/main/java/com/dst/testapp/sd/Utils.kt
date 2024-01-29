/*
 * Utils.kt
 *
 * Copyright 2011 Eric Butler <eric@codebutler.com>
 * Copyright 2015-2019 Michael Farrell <micolous+git@gmail.com>
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

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import java.io.IOException
import java.util.*
import android.content.pm.PackageManager.GET_META_DATA
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.Fragment
import com.dst.testapp.MetrodroidApplication
import com.dst.testapp.R

fun AlertDialog.Builder.safeShow() {
    try {
        this.show()
    } catch (unused: WindowManager.BadTokenException) {
        /* Ignore... happens if the activity was destroyed */
    }    
}

inline fun Activity.tryAndShowError(lambda: () -> Unit) {
    try {
        lambda()
    } catch (ex: Exception) {
        Utils.showError(this, ex)
    }
}

fun <I, O> ComponentActivity.registerForActivityResultIfOkAndShowError(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>) =
    registerForActivityResult(contract) { result ->
        tryAndShowError {
            callback.onActivityResult(result)
        }
    }

fun <I, O> Fragment.registerForActivityResultIfOkAndShowError(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>) =
    registerForActivityResult(contract) { result ->
        requireActivity().tryAndShowError {
            callback.onActivityResult(result)
        }
    }

object Utils {
    private const val TAG = "Utils"

    // Version:
    // Model
    // Manufacturer / brand:
    // OS:
    // NFC:




    private val versionString: String
        get() {
            val info = packageInfo
            return "${info.versionName} (${PackageInfoCompat.getLongVersionCode(info)})"
        }

    private val packageInfo: PackageInfo
        get() {
            try {
                val app = MetrodroidApplication.instance
                return app.packageManager.getPackageInfo(app.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }

        }

    /**
     * Tries to start the activity associated with [action].
     *
     * See [Intent] constructor for more details.
     *
     * @return `true` if the activity was started, `false` if the activity could not be found.
     */
    fun tryStartActivity(context: Context, action: String): Boolean =
        try {
            context.startActivity(Intent(action))
            true
        } catch (_: ActivityNotFoundException) {
            false
        }

    fun checkNfcEnabled(activity: Activity, adapter: NfcAdapter?) {
        if (adapter != null && adapter.isEnabled) {
            return
        }
        AlertDialog.Builder(activity)
                .setTitle(R.string.nfc_off_error)
                .setMessage(R.string.turn_on_nfc)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setNeutralButton(R.string.nfc_settings) { _, _ ->
                    NfcSettingsPreference.showNfcSettings(activity)
                }
                .safeShow()
    }

    fun showError(activity: Activity, ex: Exception) {
        Log.e(activity.javaClass.name, ex.message, ex)
        AlertDialog.Builder(activity)
                .setMessage(getErrorMessage(ex))
                .safeShow()
    }

    fun showErrorAndFinish(activity: Activity, ex: Exception?) {
        Log.e(activity.javaClass.name, getErrorMessage(ex))
        ex?.printStackTrace()

        AlertDialog.Builder(activity)
                .setMessage(getErrorMessage(ex))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ -> activity.finish() }
                .safeShow()
    }

    fun showErrorAndFinish(activity: Activity, @StringRes errorResource: Int) {
       // Log.e(activity.javaClass.name, errorResource)
        AlertDialog.Builder(activity)
                .setMessage(errorResource)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ -> activity.finish() }
                .safeShow()
    }


    fun copyTextToClipboard(context: Context, label: String, text: String) {
        val data = ClipData.newPlainText(label, text)

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        if (clipboard == null) {
            Log.w(TAG, "Unable to access ClipboardManager.")
            Toast.makeText(context, R.string.clipboard_error, Toast.LENGTH_SHORT).show()
            return
        }
        clipboard.setPrimaryClip(data)
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    fun weakLTR(input: String): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            return input
        val config = MetrodroidApplication.instance.resources.configuration
        if (config.layoutDirection != View.LAYOUT_DIRECTION_RTL)
            return input
        return "\u200E$input\u200E"
    }


    fun localeContext(base: Context, locale: Locale): Context {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Whatever, on such old devices we don't do language changes
            return base
        }

        var conf = base.resources.configuration
        conf = Configuration(conf)
        conf.setLocale(locale)
        return base.createConfigurationContext(conf)
    }

    fun languageToLocale(id: String): Locale {
        val lang = id.substringBefore('-')
        val region = id.substringAfter('-', "").removePrefix("r")
        return Locale(lang, region)
    }



    fun resetActivityTitle(a: Activity) {
        try {
            val info = a.packageManager.getActivityInfo(a.componentName, GET_META_DATA)
            if (info.labelRes != 0) {
                a.setTitle(info.labelRes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun loadMultiReuse(reuseView: View?, inflater: LayoutInflater, resource: Int,
                       root: ViewGroup?, attachToRoot: Boolean, tag: String = resource.toString(16)): View {
        if (reuseView != null && reuseView.tag == tag)
            return reuseView
        val v = inflater.inflate(resource, root, attachToRoot)
        v.tag = tag
        return v
    }
}
