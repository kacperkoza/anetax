package com.nostra.koza.anetax.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
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
        val holder: ViewHolder
        var view = convertView

        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.price_row, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        }
        val priceEntry = priceEntries[position]
        holder.priceNet.text= formatPrice(priceEntry.price.priceNet)
        holder.priceGross.text = formatPrice(priceEntry.price.priceGross)
        holder.priceMargin.text = formatPrice(priceEntry.price.priceMargin)
        holder.date.text = formatDate(priceEntry.date)

        return view!!
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

    class ViewHolder(val view: View) {
        @BindView(R.id.price_net_text) lateinit var priceNet: TextView
        @BindView(R.id.price_gross_text) lateinit var priceGross: TextView
        @BindView(R.id.price_margin_text) lateinit var priceMargin: TextView
        @BindView(R.id.date) lateinit var date: TextView

        init {
            ButterKnife.bind(this, view)
        }

    }

}