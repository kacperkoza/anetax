package com.nostra.koza.anetax

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import kotlinx.android.synthetic.main.fragment_product_list.*


class ProductListFragment : Fragment() {

    @BindView(R.id.search_et) lateinit var searchEt: EditText
    @BindView(R.id.no_products_text) lateinit var noProductsText: TextView

    private lateinit var productDao: ProductDao
    private lateinit var priceEntryDao: PriceEntryDao
    private lateinit var productList: MutableList<Product>

    private lateinit var products: List<Product>
    private lateinit var prices: List<PriceEntry>

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_product_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val productDb = ProductDatabase(context)
        productDao = ProductDao(productDb.getDao(Product::class.java))
        priceEntryDao = PriceEntryDao(productDb.getDao(PriceEntry::class.java))
        products = productDao.findAll()
        prices = priceEntryDao.findAll()
        productList = joinProductWithPrices()
        productAdapter = ProductAdapter(context, productList)
        listView.adapter = productAdapter
        val swipeMenuCreator = SwipeMenuCreator({ swipeMenu ->
            val openItem = buildOpenSwipeItem()
            swipeMenu.addMenuItem(openItem)

            val deleteItem = buildDeleteSwipeItem()
            swipeMenu.addMenuItem(deleteItem)
        })
        listView.setMenuCreator(swipeMenuCreator)
        listView.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                1 -> {
                    deleteItemAtPosition(position)
                }
                0 -> {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.content, ProductDetailsFragment.newInstance(productList[position]))
                            .addToBackStack(null)
                            .commit()
                    true
                }
                else -> true
            }
        }
        addSearchToEditText()
        setMessageIfListEmpty()
    }

    private fun deleteItemAtPosition(position: Int): Boolean {
        val productId = productList[position].id
        productList.removeAt(position)
        productDao.deleteById(productId!!)
        productAdapter = ProductAdapter(context, productList)
        listView.adapter = productAdapter
        setMessageIfListEmpty()
        return true
    }

    private fun joinProductWithPrices(): MutableList<Product> {
        return products
                .map { Product(it.id, it.name, it.barcode, it.priceNet, it.taxRate, prices.filter { p -> p.productId == it.id }) }
                .toMutableList()
    }

    private fun buildDeleteSwipeItem(): SwipeMenuItem {
        val deleteItem = SwipeMenuItem(context)
        deleteItem.background = ColorDrawable(Color.RED)
        deleteItem.width = 180
        deleteItem.setIcon(R.drawable.ic_delete_white_24dp)
        return deleteItem
    }

    private fun buildOpenSwipeItem(): SwipeMenuItem {
        val openItem = SwipeMenuItem(context)
        openItem.background = ColorDrawable(Color.GRAY)
        openItem.width = 180
        openItem.title = getString(R.string.open)
        openItem.titleSize = 18
        openItem.titleColor = Color.WHITE
        return openItem
    }

    private fun addSearchToEditText() {
        searchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterByText(s.toString())
            }

            private fun filterByText(text: String) {
                products = productDao.findByBarcodeOrName(text)
                listView.adapter = ProductAdapter(context, joinProductWithPrices())
                setMessageIfListEmpty()
            }
        })
    }

    private fun setMessageIfListEmpty() {
        noProductsText.visibility = if (productList.isEmpty()) View.VISIBLE else View.GONE
    }

}

class ProductAdapter(val context: Context, val products: List<Product>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.product_row, parent, false)

        val productName: TextView = rowView.findViewById(R.id.product_name)
        val productBarcode: TextView = rowView.findViewById(R.id.product_barcode)
        val productPrice: TextView = rowView.findViewById(R.id.product_price_margin)
        val addedAt: TextView = rowView.findViewById(R.id.last_price_date)

        val product = products[position]
        productName.text = product.name
        productBarcode.text = product.barcode
        val mostRecentPrice = product.entries.last()
        productPrice.text = "${formatPrice(mostRecentPrice.price)} zł"
        addedAt.text = formatDate(mostRecentPrice.date)
        return rowView
    }

    override fun getItemId(position: Int): Long = products.get(position).id!!.toLong()

    override fun getItem(position: Int): Any = products.get(position)

    override fun getCount(): Int = products.size

}





