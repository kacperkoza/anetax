package com.nostra.koza.anetax.database

import java.io.Serializable

data class Price(
        val priceNet: Double,
        val priceGross: Double,
        val priceMargin: Double
) : Serializable