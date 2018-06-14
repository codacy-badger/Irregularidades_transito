package com.joaoeudes7.multas.extraComponents.ProgressDialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.joaoeudes7.multas.R
import kotlinx.android.synthetic.main.progress_dialog.view.*

/**
 * This is my solution for problem ProgressDialog deprecated in API 26 (Android 8 - Oreo)
 * @exception "This class was deprecated in API level 26. ProgressDialog is a modal dialog, which prevents the user from interacting with the app. Instead of using this class, you should use a progress indicator like ProgressBar, which can be embedded in your app's UI. Alternatively, you can use a notification to inform the user of the task's progress"
 *
 * @author "João Eudes Lima" <joaoeudes7@gmail.com>
 *
 * The xml used is:
 * Go to res/layout/progress_dialog.xml or [R.layout.progress_dialog]
 *
 * Parameters:
 *  @param context
 *  @param msg
 */

@SuppressLint("InflateParams")
class ProgressDialog(context: Context, msg: String = "Carregando...", cancelable: Boolean = false) {

    private val alertDialog: AlertDialog

    init {
        val dialogBuilder = AlertDialog.Builder(context)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.progress_dialog, null)

        // Set Message
        dialogView.titleDialog.text = msg

        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(cancelable)

        alertDialog = dialogBuilder.create()
    }

    fun show(): ProgressDialog {
        alertDialog.show()
        return this
    }

    fun dismiss() {
        alertDialog.dismiss()
    }
}