package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import org.apache.poi.hssf.usermodel.HSSFSheet

interface WriteStrategy {

    fun writeExcelSheet(sheet: HSSFSheet, products: List<Product>, prices: List<PriceEntry>)

    fun getFileName(): String

}