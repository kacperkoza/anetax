package com.nostra.koza.anetax.database

import com.j256.ormlite.field.DatabaseField
import org.joda.time.DateTime

data class PriceEntry(
        @DatabaseField(generatedId = true)
        val id: Int?,

        @DatabaseField
        val productId: Int,

        @DatabaseField
        val price: Double,

        @DatabaseField
        val date: DateTime = DateTime()
) {
    constructor(): this(null, 0, 0.0, DateTime.now())
}



