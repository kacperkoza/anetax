package com.nostra.koza.anetax

import com.nostra.koza.anetax.database.Price
import com.nostra.koza.anetax.database.TaxRate

/**
 * Created by kacper.koza on 28/10/2017.
 */
object PriceCalculator {

    const val MARGIN_RATE = 0.25

    fun calculateMarginPrice(netPrice: Double, taxRate: TaxRate): Price {
        val grossPrice = netPrice * (1 + taxRate.rate)
        val marginPrice =  grossPrice * (1 + MARGIN_RATE)
        return Price(netPrice, grossPrice, marginPrice)
    }

}