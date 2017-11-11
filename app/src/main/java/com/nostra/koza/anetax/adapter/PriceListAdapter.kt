package com.nostra.koza.anetax.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.nostra.koza.anetax.PriceCalculator
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.database.PriceEntry
import com.nostra.koza.anetax.database.PriceEntryDao
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.database.ProductDatabase
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.shortToast

class PriceListAdapter(val context: Context, val product: Product) : BaseAdapter() {

    private val priceEntryDao: PriceEntryDao = PriceEntryDao(ProductDatabase(context).getDao(PriceEntry::class.java))
    private var priceEntries: List<PriceEntry> = priceEntryDao.findByProductId(product.id!!)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.price_row, parent, false)

        val priceNet: TextView = rowView.findViewById(R.id.price_net_text)
        val priceGross: TextView = rowView.findViewById(R.id.price_gross_text)
        val priceMargin: TextView = rowView.findViewById(R.id.price_margin_text)
        val date: TextView = rowView.findViewById(R.id.date)
        val priceEntry = priceEntries[position]
        priceNet.text = formatPrice(priceEntry.price.priceNet)
        priceGross.text = formatPrice(priceEntry.price.priceGross)
        priceMargin.text = formatPrice(priceEntry.price.priceMargin)
        date.text = formatDate(priceEntry.date)
        return rowView
    }

    override fun getItemId(position: Int): Long = priceEntries.get(position).id!!.toLong()

    override fun getItem(position: Int): PriceEntry = priceEntries.get(position)

    override fun getCount(): Int = priceEntries.size

    fun removePriceElement(position: Int): Boolean {
        if (isLastPrice()) {
            toast(R.string.you_cant_delete_last_price)
            return false
        }
        if (isLastElement(position)) {
            toast(R.string.you_cant_delete_last_position)
            return false
        }
        priceEntryDao.deleteById(getItemId(position).toInt())
        priceEntries = priceEntryDao.findByProductId(product.id!!)
        notifyDataSetChanged()
        return true
    }

    private fun isLastElement(position: Int): Boolean = priceEntries.size == position + 1

    private fun isLastPrice(): Boolean = priceEntries.size == 1

    private fun toast(id: Int) = shortToast(context, context.getString(id))

    fun addNewPrice(newPrice: String) {
        priceEntryDao.add(
                PriceEntry(null, product.id!!, PriceCalculator.calculateMarginPrice(newPrice.toDouble(), product.taxRate))
        )
        priceEntries = priceEntryDao.findByProductId(product.id)
        notifyDataSetChanged()
    }

    fun editPrice(modifiedPrice: PriceEntry) {
        priceEntryDao.update(modifiedPrice)
        priceEntries = priceEntryDao.findByProductId(product.id!!)
        notifyDataSetChanged()
    }
}