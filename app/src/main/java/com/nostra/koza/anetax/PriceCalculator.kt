package com.nostra.koza.anetax

/**
 * Created by kacper.koza on 28/10/2017.
 */
object PriceCalculator {

    fun calculateMarginPrice(netPrice: Double, taxRate: TaxRate): Double {
        val priceWithVat = netPrice * (1 + taxRate.rate)
        return priceWithVat * (1 + Product.MARGIN_RATE)
    }
}