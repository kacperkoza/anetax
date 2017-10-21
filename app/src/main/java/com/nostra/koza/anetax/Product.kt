package com.nostra.koza.anetax

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.field.DatabaseField

/**
 * Created by kacper.koza on 19/10/2017.
 */

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
        val tax: Double
) {
    constructor() : this(null, "", "", 0.0, 0.0)

    val priceGross: Double = priceNet * tax
    val priceMargin = priceGross * MARGIN_RATE

    companion object {
        const val MARGIN_RATE = 0.25
    }
}

class ProductDao(private val dao: Dao<Product, Int>) {

    fun add(product: Product) = dao.create(product)

    fun delete(product: Product) = dao.delete(product)

    fun findAll(): List<Product> = dao.queryForAll()

    fun findByBarcode(barcode: String): List<Product> = findAll().filter { it.barcode == barcode }

    fun findByProductName(productName: String): List<Product> = findAll().filter { it.name.toLowerCase().contains(productName.toLowerCase()) }

}