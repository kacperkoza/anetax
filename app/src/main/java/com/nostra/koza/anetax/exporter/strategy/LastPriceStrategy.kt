package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.getDateTimeNowFormatted
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFSheet


class LastPriceStrategy : WriteStrategy {
    companion object {
        private const val FILE_NAME = "Ostatnie ceny"
        private const val FILE_EXTENSION = ".xls"

        private const val LP_COLUMN = "Lp."
        private const val PRODUCT_NAME_COLUMN = "Nazwa produktu"
        private const val BARCODE_COLUMN = "Kod kreskowy"
        private const val PRICE_COLUMNN = "Cena"
        private const val DATE_COLUMN = "Data"
    }

    override fun writeExcelSheet(sheet: HSSFSheet, products: List<Product>, prices: List<PriceEntry>) {
        var columnNumber = 0
        var rowNumber = 0
        var ordinalNumber = 1
        addTitleRow(sheet, rowNumber++)

        products.forEach {
            val priceByProductId = prices.filter { p -> p.productId == it.id }
            val lastPriceEntry: PriceEntry = priceByProductId.last()

            val row = sheet.createRow(rowNumber++)

            val lpCell = row.createCell(columnNumber++)
            val nameCell = row.createCell(columnNumber++)
            val barcodeCell = row.createCell(columnNumber++)
            val lastPriceCell = row.createCell(columnNumber++)
            val lastDateCell = row.createCell(columnNumber++)

            lpCell.setCellValue(HSSFRichTextString(ordinalNumber++.toString()))
            nameCell.setCellValue(HSSFRichTextString(it.name))
            if (it.barcode != null) barcodeCell.setCellValue(HSSFRichTextString(it.barcode.barcodeText))
            lastPriceCell.setCellValue(formatPrice(lastPriceEntry.price.priceMargin))
            lastDateCell.setCellValue(formatDate(lastPriceEntry.date))

            columnNumber = 0
        }
    }

    private fun addTitleRow(sheet: HSSFSheet, row: Int) {
        val row = sheet.createRow(row)
        row.createCell(0).setCellValue(LP_COLUMN)
        row.createCell(1).setCellValue(PRODUCT_NAME_COLUMN)
        row.createCell(2).setCellValue(BARCODE_COLUMN)
        row.createCell(3).setCellValue(PRICE_COLUMNN)
        row.createCell(4).setCellValue(DATE_COLUMN)
    }

    override fun getFileName(): String = "$FILE_NAME - ${getDateTimeNowFormatted()}$FILE_EXTENSION"

}
