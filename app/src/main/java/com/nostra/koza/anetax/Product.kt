package com.nostra.koza.anetax

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import org.joda.time.DateTime
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
        val taxRate: TaxRate,

        val entries: List<PriceEntry> = emptyList()
): Serializable {
    constructor() : this(null, "", "", 0.0, TaxRate.EIGHT_PERCENT)

    companion object {
        const val MARGIN_RATE = 0.25
    }
}

enum class TaxRate(val rate: Double) {
    FIVE_PERCENT(0.05),
    EIGHT_PERCENT(0.08),
    TWENTY_THREE_PERCENT(0.23)
}


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

class PriceEntryDao(private val dao: Dao<PriceEntry, Int>) {

    fun add(priceEntry: PriceEntry): PriceEntry = dao.createIfNotExists(priceEntry)

    fun findAll(): List<PriceEntry> = dao.queryForAll()

    fun deleteById(id: Int) = dao.deleteById(id)

    fun deleteWhereProductId(productId: Int) {
        val prices = dao.queryForFieldValues(mapOf<String, Any>(Pair<String, Any>("productId", productId)))
        prices.forEach {
            deleteById(it.id!!)
        }

    }

}

class ProductDao(private val dao: Dao<Product, Int>) {

    fun add(product: Product): Product = dao.createIfNotExists(product)

    fun deleteById(productId: Int) = dao.deleteById(productId)

    fun findAll(): List<Product> = dao.queryForAll()

    fun findById(productId: Int) = dao.queryForId(productId)

    fun findByBarcodeOrName(query: String): List<Product> =
            findAll().filter { it.barcode.contains(query) || containsQueryIgnoringCase(it.name, query) }.distinct()

    private fun containsQueryIgnoringCase(name: String, phrase: String) = name.toLowerCase().contains(phrase.toLowerCase())

}