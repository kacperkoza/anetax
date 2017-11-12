package com.nostra.koza.anetax.util

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Created by kacper.koza on 25/10/2017.
 */
fun formatDate(date: DateTime): String = date.toString("dd-MM-yyyy")


val dtfOut = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm")

fun getDateTimeNowFormatted(): String = dtfOut.print(DateTime.now())