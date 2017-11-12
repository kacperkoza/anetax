package com.nostra.koza.anetax.util

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by kacper.koza on 25/10/2017.
 */

fun formatPrice(price: Double) = String.format("%.2f", price)


fun formatPriceForReport(price: Double): String {
    val df = DecimalFormat("#.#")
    df.setRoundingMode(RoundingMode.CEILING)
    return df.format(price)
}
