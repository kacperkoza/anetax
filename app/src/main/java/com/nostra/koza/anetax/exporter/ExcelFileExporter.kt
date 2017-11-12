package com.nostra.koza.anetax.exporter

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.exporter.strategy.WriteStrategy
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File

class ExcelFileExporter(
        val strategy: WriteStrategy
) {

    fun export(path: String, products: List<Product>, prices: List<PriceEntry>) {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Produkty")
        strategy.writeExcelSheet(sheet,
                products.sortedBy { it.name },
                prices)
        workbook.write(File("$path${strategy.getFileName()}"))
        workbook.close()
    }

}