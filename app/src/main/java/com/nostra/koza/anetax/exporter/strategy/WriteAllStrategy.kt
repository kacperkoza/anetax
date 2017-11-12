package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook

class WriteAllStrategy : WriteStrategy {
    override fun getFileName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun write(workbook: WritableSheet, products: List<Product>, prices: List<PriceEntry>) {
        var column = 0
        var row = 0
        var lp = 0
        products.forEach {
            val filtered = prices.find { p -> p.productId == it.id }




        }

    }
}