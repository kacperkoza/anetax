package com.nostra.koza.anetax.exporter.strategy

import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.getDateTimeNowFormatted
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFRow
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
        var firstRowWritten = false

        addTitleRow(sheet, rowNumber++)

        products.forEach { product ->
            run {
                val productPrices = prices.filter { p -> p.productId == product.id }

                productPrices.forEach { p ->
                    val row = sheet.createRow(rowNumber++)

                    if (!firstRowWritten) {
                        val ordinalCell = row.createCell(columnNumber++)
                        ordinalCell.setCellValue(HSSFRichTextString(ordinalNumber++.toString()))
                        columnNumber = writeProductInfo(row, columnNumber, product)
                        firstRowWritten = true
                    }
                    columnNumber = writePrices(row, columnNumber, p)
                    columnNumber = 4
                }
                columnNumber = 0
                firstRowWritten = false
            }
        }
    }

    private fun writeProductInfo(row: HSSFRow, columnNumber: Int, product: Product): Int {
        var cn = columnNumber

        val nameCell = row.createCell(cn++)
        nameCell.setCellValue(HSSFRichTextString(product.name))

        val barcodeCell = row.createCell(cn++)
        if (product.barcode != null) barcodeCell.setCellValue(HSSFRichTextString(product.barcode.barcodeText))

        val taxCell = row.createCell(cn++)
        taxCell.setCellValue(product.taxRate.rate * 100)

        return cn
    }

    private fun writePrices(row: HSSFRow, columnNumber: Int, priceEntry: PriceEntry): Int {
        var cn = columnNumber
        val priceNetCell = row.createCell(cn++)
        priceNetCell.setCellValue(formatPrice(priceEntry.price.priceNet))

        val priceGrossCell = row.createCell(cn++)
        priceGrossCell.setCellValue(formatPrice(priceEntry.price.priceGross))

        val priceMarginCell = row.createCell(cn++)
        priceMarginCell.setCellValue(formatPrice(priceEntry.price.priceMargin))

        val dateCell = row.createCell(cn++)
        dateCell.setCellValue(formatDate(priceEntry.date))
        return cn
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