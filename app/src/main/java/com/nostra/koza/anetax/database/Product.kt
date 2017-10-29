package com.nostra.koza.anetax.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.io.Serializable

@DatabaseTable(tableName = "products")
data class Product(

        @DatabaseField(generatedId = true)
        val id: Int? = null,

        @DatabaseField
        val name: String,

        @DatabaseField
        val barcode: String,

        @DatabaseField
        val priceNet: Double,

        @DatabaseField
        val taxRate: TaxRate
): Serializable {
    constructor() : this(null, "", "", 0.0, TaxRate.EIGHT_PERCENT)

    companion object {
        const val MARGIN_RATE = 0.25
    }
}