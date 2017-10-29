package com.nostra.koza.anetax.database

import com.j256.ormlite.dao.Dao

/**
 * Created by kacper.koza on 29/10/2017.
 */
class ProductDao(private val dao: Dao<Product, Int>) {

    fun add(product: Product): Product = dao.createIfNotExists(product)

    fun deleteById(productId: Int) = dao.deleteById(productId)

    fun findAll(): List<Product> = dao.queryForAll()

    fun findById(productId: Int) = dao.queryForId(productId)

    fun findByBarcodeOrName(query: String): List<Product> =
            findAll().filter { it.barcode.contains(query) || containsQueryIgnoringCase(it.name, query) }.distinct()

    private fun containsQueryIgnoringCase(name: String, phrase: String) = name.toLowerCase().contains(phrase.toLowerCase())

    fun update(product: Product) {
        dao.update(product)
    }

}