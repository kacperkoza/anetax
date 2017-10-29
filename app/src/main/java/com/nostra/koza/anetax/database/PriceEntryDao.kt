package com.nostra.koza.anetax.database

import com.j256.ormlite.dao.Dao

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

    fun findByProductId(productId: Int): List<PriceEntry> = findAll().filter { it.productId == productId }

}