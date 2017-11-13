package com.nostra.koza.anetax.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Created by kacper.koza on 28/10/2017.
 */
fun hide(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}