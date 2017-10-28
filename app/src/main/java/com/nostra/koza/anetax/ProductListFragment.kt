package com.nostra.koza.anetax

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
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import kotlinx.android.synthetic.main.fragment_product_list.*


class ProductListFragment : Fragment() {

    @BindView(R.id.search_et) lateinit var searchEt: EditText
    @BindView(R.id.no_products_text) lateinit var noProductsText: TextView

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_product_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        productAdapter = ProductAdapter(context)
        productAdapter.registerDataSetObserver(object: DataSetObserver() {
            override fun onChanged() {
                noProductsText.visibility = if (productAdapter.isEmpty()) View.VISIBLE else View.GONE
            }
        })

        listView.setMenuCreator({ swipeMenu ->
            swipeMenu.addMenuItem(SwipeMenuItemBuilder.buildOpenItem(context))
            swipeMenu.addMenuItem(SwipeMenuItemBuilder.buildDeleteItem(context))
        })

        listView.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                1 -> {
                    val productId = productAdapter.getItem(position).id!!
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
        fragmentManager
                .beginTransaction()
                .replace(R.id.content, ProductDetailsFragment.newInstance(productAdapter.getItem(position)))
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
    private val products: List<Product> = productDao.findAll()
    private var filteredProducts: List<Product> = products

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.product_row, parent, false)

        val productName: TextView = rowView.findViewById(R.id.product_name)
        val productBarcode: TextView = rowView.findViewById(R.id.product_barcode)
        val productPrice: TextView = rowView.findViewById(R.id.product_price_margin)
        val addedAt: TextView = rowView.findViewById(R.id.last_price_date)

        val product = filteredProducts[position]
        productName.text = product.name
        productBarcode.text = product.barcode

        val mostRecentPrice = priceEntryDao.findByProductId(product.id!!).last()
        productPrice.text = "${formatPrice(mostRecentPrice.price)} zł"
        addedAt.text = formatDate(mostRecentPrice.date)

        return rowView
    }

    override fun getItemId(position: Int): Long = filteredProducts.get(position).id!!.toLong()

    override fun getItem(position: Int): Product = filteredProducts.get(position)

    override fun getCount(): Int = filteredProducts.size

    fun filterByText(text: String) {
        filteredProducts = products.filter { text.toLowerCase() in it.name || text in it.barcode }
        notifyDataSetChanged()
    }

    fun deleteProductAndPricesById(productId: Int): Boolean {
        productDao.deleteById(productId)
        priceEntryDao.deleteWhereProductId(productId)
        notifyDataSetChanged()
        return true
    }

}





