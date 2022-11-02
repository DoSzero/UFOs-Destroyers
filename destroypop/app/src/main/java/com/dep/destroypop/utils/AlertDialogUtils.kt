package com.dep.destroypop.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class AlertDialogUtils : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments ?: throw AssertionError()

        val builder = AlertDialog.Builder(activity)
            .setTitle(args.getString(TITLE_KEY))
            .setMessage(args.getString(MESSAGE_KEY))
            .setCancelable(false)

        builder.setPositiveButton(android.R.string.ok, null)
        return builder.create()
    }

    companion object {
        private const val TITLE_KEY = "title_key"
        private const val MESSAGE_KEY = "message_key"
    }
}