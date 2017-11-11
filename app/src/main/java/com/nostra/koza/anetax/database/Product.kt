package com.nostra.koza.anetax.database

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.io.Serializable

@DatabaseTable(tableName = "products")
data class Product(

        @DatabaseField(generatedId = true)
        val id: Int? = null,

        @DatabaseField
        val name: String,

        @DatabaseField(dataType = DataType.SERIALIZABLE)
        val barcode: Barcode?,

        @DatabaseField
        val taxRate: TaxRate

): Serializable {
    constructor() : this(null, "", Barcode("", "", 0), TaxRate.FIVE_PERCENT)
}