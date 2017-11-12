package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPriceForReport
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
        private const val VAT_COLUMN = "Stawka VAT"
        private const val PRICE_NET_COLUMN = "Netto"
        private const val PRICE_MARGIN_COLUMN = "Z marżą"
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

            val ordinalNumberCell = row.createCell(columnNumber++)
            ordinalNumberCell.setCellValue(HSSFRichTextString(ordinalNumber++.toString()))

            val nameCell = row.createCell(columnNumber++)
            nameCell.setCellValue(HSSFRichTextString(it.name))

            val barcodeCell = row.createCell(columnNumber++)
            if (it.barcode != null) barcodeCell.setCellValue(HSSFRichTextString(it.barcode.barcodeText))

            val taxCell = row.createCell(columnNumber++)
            taxCell.setCellValue(it.taxRate.rate * 100)

            val priceNetCell = row.createCell(columnNumber++)
            priceNetCell.setCellValue(lastPriceEntry.price.priceNet)

            val priceMarginCell = row.createCell(columnNumber++)
            priceMarginCell.setCellValue(formatPriceForReport(lastPriceEntry.price.priceMargin))

            val date = row.createCell(columnNumber++)
            date.setCellValue(formatDate(lastPriceEntry.date))

            columnNumber = 0
        }
    }

    private fun addTitleRow(sheet: HSSFSheet, rowNumber: Int) {
        val row = sheet.createRow(rowNumber)
        row.createCell(0).setCellValue(LP_COLUMN)
        row.createCell(1).setCellValue(PRODUCT_NAME_COLUMN)
        row.createCell(2).setCellValue(BARCODE_COLUMN)
        row.createCell(3).setCellValue(VAT_COLUMN)
        row.createCell(4).setCellValue(PRICE_NET_COLUMN)
        row.createCell(5).setCellValue(PRICE_MARGIN_COLUMN)
        row.createCell(6).setCellValue(DATE_COLUMN)
    }

    override fun getFileName(): String = "$FILE_NAME - ${getDateTimeNowFormatted()}$FILE_EXTENSION"

}
