package com.nostra.koza.anetax.database

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import org.joda.time.DateTime
import java.io.Serializable

@DatabaseTable(tableName = "prices")
data class PriceEntry(
        @DatabaseField(generatedId = true)
        val id: Int?,

        @DatabaseField
        val productId: Int,

        @DatabaseField(dataType = DataType.SERIALIZABLE)
        val price: Price,

        @DatabaseField
        val date: DateTime = DateTime()
) {
    constructor(): this(null, 0, Price(0.0, 0.0, 0.0), DateTime.now())
}

data class Price(
        val priceNet: Double,
        val priceGross: Double,
        val priceMargin: Double
) : Serializable



