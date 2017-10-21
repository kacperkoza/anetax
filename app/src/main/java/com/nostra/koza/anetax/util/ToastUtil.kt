package com.nostra.koza.anetax.util

import android.content.Context
import android.widget.Toast

/**
 * Created by kacper.koza on 21/10/2017.
 */
fun toast(context: Context, msg: String) = makeToast(context, msg, Toast.LENGTH_SHORT)

fun longToast(context: Context, msg: String) = makeToast(context, msg, Toast.LENGTH_LONG)

private fun makeToast(context: Context, msg: String, length: Int) = Toast.makeText(context, msg, length).show()