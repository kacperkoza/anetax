package com.nostra.koza.anetax.fragment

import android.content.Context
import android.database.DataSetObserver
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.SwipeMenuItemFactory
import com.nostra.koza.anetax.database.*
import com.nostra.koza.anetax.util.Keypad
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import kotlinx.android.synthetic.main.fragment_product_list.*


class ProductListFragment : Fragment() {

    @BindView(R.id.search_et) lateinit var searchEt: EditText
    @BindView(R.id.no_products_text) lateinit var noProductsText: TextView

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Keypad.hide(activity!!)
        productAdapter = ProductAdapter(context!!)
        productAdapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                noProductsText.visibility = if (productAdapter.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        listView.setMenuCreator({ swipeMenu ->
            swipeMenu.addMenuItem(SwipeMenuItemFactory.openItem(context!!))
            swipeMenu.addMenuItem(SwipeMenuItemFactory.deleteItem(context!!))
        })

        listView.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                1 -> {
                    val productId = (productAdapter.getItem(position) as Product).id!!
                    productAdapter.deleteProductAndPricesById(productId)
                }
                0 -> openProductDetailsFragment(position)
                else -> true
            }
        }
        listView.setOnItemClickListener { _, _, position, _ -> openProductDetailsFragment(position) }
        listView.adapter = productAdapter
        productAdapter.notifyDataSetChanged()
        addSearchToEditText()
    }

    private fun openProductDetailsFragment(position: Int): Boolean {
        fragmentManager!!
                .beginTransaction()
                .replace(R.id.content, ProductDetailsFragment.newInstance(productAdapter.getItem(position) as Product))
                .addToBackStack(null)
                .commit()
        return true
    }

    private fun addSearchToEditText() {
        searchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                productAdapter.filterByText(s.toString())
            }
        })
    }

}

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
        holder.barcode.text = product.barcode?.barcode ?: ""

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





