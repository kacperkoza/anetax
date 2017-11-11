package com.nostra.koza.anetax

import android.content.Context
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog

/**
 * Created by kacper.koza on 29/10/2017.
 */
class DialogFactory(val context: Context) {

    fun editBarcode(barcode: String, callback: MaterialDialog.InputCallback) {
        MaterialDialog.Builder(context)
                .title(R.string.scanned_code)
                .content(R.string.is_code_proper)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(context.getString(R.string.barcode), barcode, callback)
                .negativeText(R.string.cancel)
                .show()
    }

    fun editProductName(callback: MaterialDialog.InputCallback) {
        MaterialDialog.Builder(context)
                .title(R.string.product_name)
                .content(R.string.enter_new_product_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(context.getString(R.string.product_name), "", callback)
                .negativeText(R.string.cancel)
                .show()
    }

    fun newPrice(callback: MaterialDialog.InputCallback) {
        MaterialDialog.Builder(context)
                .title(R.string.new_price)
                .content(R.string.enter_new_price)
                .inputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input("", "", callback)
                .negativeText(R.string.cancel)
                .show()
    }

    fun editMarginPrice(callback: MaterialDialog.InputCallback) {
        MaterialDialog.Builder(context)
                .title(R.string.new_margin_price)
                .content(R.string.enter_new_price)
                .inputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input("", "", callback)
                .negativeText(R.string.cancel)
                .show()
    }

}