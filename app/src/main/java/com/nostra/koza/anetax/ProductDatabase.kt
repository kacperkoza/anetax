package com.nostra.koza.anetax

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

/**
 * Created by kacper.koza on 21/10/2017.
 */
class ProductDatabase(context: Context) : OrmLiteSqliteOpenHelper(context, "product.db", null, 1) {


    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {
        TableUtils.createTableIfNotExists(connectionSource, Product::class.java)
        TableUtils.createTableIfNotExists(connectionSource, PriceEntry::class.java)
    }

    override fun onUpgrade(database: SQLiteDatabase?, connectionSource: ConnectionSource?, oldVersion: Int, newVersion: Int) {
        TableUtils.dropTable<Product, Any>(connectionSource, Product::class.java, true)
        TableUtils.dropTable<PriceEntry, Any>(connectionSource, PriceEntry::class.java, true)
        onCreate(database, connectionSource)
    }

}