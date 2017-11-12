package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.util.formatDate
import jxl.demo.Write
import jxl.write.*
import jxl.write.Number

class WriteLastPriceStrategy : WriteStrategy {

    companion object {
        const val FILE_NAME = "Ceny"
    }

    override fun write(workbook: WritableSheet, products: List<Product>, prices: List<PriceEntry>) {
        var column = 0
        var row = 0
        var lp = 1

        products.forEach {
            val filtered = prices.filter { p -> p.productId == it.id }

            val labelLp = Label(column++, row, lp++.toString())
            val name = Label(column++, row, it.name)
            val barcode: Label? = if (it.barcode == null) {
                null
            } else {
                Label(column++, row, it.barcode.barcodeText)
            }
            val lastPrice = filtered.last()
            val lastPriceNumber = Number(column++, row, lastPrice.price.priceMargin)
            val lastDate = DateTime(column++, row, lastPrice.date.toDate())

            workbook.addCell(labelLp)
            workbook.addCell(name)
            if (barcode != null) workbook.addCell(barcode)
            workbook.addCell(lastPriceNumber)
            workbook.addCell(lastDate)
            row++
            column = 0
        }
        prices
    }

    override fun getFileName() = "${FILE_NAME}_${org.joda.time.DateTime.now()}"
//            = "/nazwa"


}