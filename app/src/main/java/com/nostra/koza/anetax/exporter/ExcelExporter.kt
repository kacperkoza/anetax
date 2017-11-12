package com.nostra.koza.anetax.exporter

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.exporter.strategy.WriteStrategy
import jxl.Workbook
import jxl.WorkbookSettings
import java.io.File
import java.util.*

class ExcelExporter(
        val writeStrategy: WriteStrategy
) {

    fun export(directory: String, products: List<Product>, prices: List<PriceEntry>) {
        val file = File(directory)
//        val file = File("$directory${writeStrategy.getFileName()}")

        val settings = WorkbookSettings()
        settings.locale = Locale("pl", "PL")

        val writableWorkbook = Workbook.createWorkbook(file)
        writableWorkbook.createSheet("Ceny", 0)
        writableWorkbook.createSheet("Ceny", 1)

        val sheet = writableWorkbook.getSheet(0)
//
//        writeStrategy.write(sheet, products, prices)
        writableWorkbook.write()
        writableWorkbook.close()
    }
}