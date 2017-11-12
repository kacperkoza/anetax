package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import jxl.write.WritableSheet

interface WriteStrategy {

    fun write(workbook: WritableSheet, products: List<Product>, prices: List<PriceEntry>)

    fun getFileName(): String

}