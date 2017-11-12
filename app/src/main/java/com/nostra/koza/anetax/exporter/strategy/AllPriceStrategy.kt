package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.getDateTimeNowFormatted
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFSheet

class AllPriceStrategy : WriteStrategy {

    companion object {
        private const val FILE_NAME = "Całość danych"
        private const val FILE_EXTENSION = ".xls"

        private const val LP_COLUMN = "Lp."
        private const val PRODUCT_NAME_COLUMN = "Nazwa produktu"
        private const val BARCODE_COLUMN = "Kod kreskowy"
        private const val VAT_COLUMN = "Stawka VAT"
        private const val PRICE_NET_COLUMNN = "Netto"
        private const val PRICE_GROSS_COLUMN = "Brutto"
        private const val PRICE_MARGIN_COLUMN = "Marża"
        private const val DATE_COLUMN = "Data"
    }

    override fun writeExcelSheet(sheet: HSSFSheet, products: List<Product>, prices: List<PriceEntry>) {
        var columnNumber = 0
        var rowNumber = 0
        var ordinalNumber = 1
        addTitleRow(sheet, rowNumber++)
        var counter = 0
        products.forEach { product ->
            run {
                val priceByProductId = prices.filter { p -> p.productId == product.id }

                priceByProductId.forEach { p ->
                    val row = sheet.createRow(rowNumber++)
                    if (counter++ == 0) {
                        val ordinalCell = row.createCell(columnNumber++)
                        ordinalCell.setCellValue(HSSFRichTextString(ordinalNumber++.toString()))

                        val nameCell = row.createCell(columnNumber++)
                        nameCell.setCellValue(HSSFRichTextString(product.name))

                        val barcodeCell = row.createCell(columnNumber++)
                        if (product.barcode != null) barcodeCell.setCellValue(HSSFRichTextString(product.barcode.barcodeText))

                        val taxCell = row.createCell(columnNumber++)
                        taxCell.setCellValue(product.taxRate.rate * 100)
                    }

                    val priceNetCell = row.createCell(columnNumber++)
                    priceNetCell.setCellValue(formatPrice(p.price.priceNet))

                    val priceGrossCell = row.createCell(columnNumber++)
                    priceGrossCell.setCellValue(formatPrice(p.price.priceGross))

                    val priceMarginCell = row.createCell(columnNumber++)
                    priceMarginCell.setCellValue(formatPrice(p.price.priceMargin))

                    val dateCell = row.createCell(columnNumber++)
                    dateCell.setCellValue(formatDate(p.date))

                    columnNumber = 4
                }
                columnNumber = 0
                counter = 0
            }
        }
    }

    private fun addTitleRow(sheet: HSSFSheet, rowNumber: Int) {
        val row = sheet.createRow(rowNumber)
        row.createCell(0).setCellValue(LP_COLUMN)
        row.createCell(1).setCellValue(PRODUCT_NAME_COLUMN)
        row.createCell(2).setCellValue(BARCODE_COLUMN)
        row.createCell(3).setCellValue(VAT_COLUMN)
        row.createCell(4).setCellValue(PRICE_NET_COLUMNN)
        row.createCell(5).setCellValue(PRICE_GROSS_COLUMN)
        row.createCell(6).setCellValue(PRICE_MARGIN_COLUMN)
        row.createCell(7).setCellValue(DATE_COLUMN)
    }

    override fun getFileName(): String = "$FILE_NAME - ${getDateTimeNowFormatted()}$FILE_EXTENSION"

}