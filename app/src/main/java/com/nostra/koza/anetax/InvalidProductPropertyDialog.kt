package com.nostra.koza.anetax

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

/**
 * Created by kacper.koza on 19/10/2017.
 */
class InvalidProductPropertyDialog : DialogFragment() {

    companion object {
        const val KEY_MESSAGE = "message"

        fun newInstance(message: String): InvalidProductPropertyDialog {
            val fragment = InvalidProductPropertyDialog()
            val bundle = Bundle()
            bundle.putString(KEY_MESSAGE, message)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments.get(KEY_MESSAGE)

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message as CharSequence)
                .setPositiveButton("Ok", { dialog, id -> dialog.dismiss()})
        return builder.create()
    }
}