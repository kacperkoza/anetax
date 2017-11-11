package com.nostra.koza.anetax.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.database.*
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice

class ProductAdapter(val context: Context) : BaseAdapter() {

    private val productDao = ProductDao(ProductDatabase(context).getDao(Product::class.java))
    private val priceEntryDao = PriceEntryDao(ProductDatabase(context).getDao(PriceEntry::class.java))
    private var filteredProducts: List<Product> = productDao.findAll()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var view = convertView

        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.product_row, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        }

        val product = getItem(position) as Product
        holder.productName.text = product.name
        holder.barcode.text = product.barcode?.barcodeText ?: ""

        val mostRecentPrice = priceEntryDao.findByProductId(product.id!!).last()
        holder.price.text = "${formatPrice(mostRecentPrice.price.priceMargin)} zł"
        holder.date.text = formatDate(mostRecentPrice.date)

        return view!!
    }

    override fun getItemId(position: Int): Long = this.filteredProducts.get(position).id!!.toLong()

    override fun getItem(position: Int): Any = this.filteredProducts.get(position)

    override fun getCount(): Int = this.filteredProducts.size

    fun filterByText(text: String) {
        filteredProducts = productDao.findByBarcodeOrName(text)
        notifyDataSetChanged()
    }

    fun deleteProductAndPricesById(productId: Int): Boolean {
        productDao.deleteById(productId)
        priceEntryDao.deleteWhereProductId(productId)
        filteredProducts = filteredProducts.filter { it.id != productId }
        notifyDataSetChanged()
        return true
    }

    class ViewHolder(val view: View) {
        @BindView(R.id.product_name) lateinit var productName: TextView
        @BindView(R.id.product_barcode) lateinit var barcode: TextView
        @BindView(R.id.product_price_margin) lateinit var price: TextView
        @BindView(R.id.last_price_date) lateinit var date: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }

}