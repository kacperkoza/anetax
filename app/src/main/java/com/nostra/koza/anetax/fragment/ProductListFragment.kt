package com.nostra.koza.anetax.fragment

import android.database.DataSetObserver
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnTextChanged
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.SwipeMenuItemFactory
import com.nostra.koza.anetax.adapter.productListAdapter
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.util.Keypad
import kotlinx.android.synthetic.main.fragment_product_list.*


class ProductListFragment : Fragment() {

    @BindView(R.id.no_products_text) lateinit var noProductsText: TextView

    private lateinit var productListAdapter: productListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Keypad.hide(activity!!)
        productListAdapter = productListAdapter(context!!)
        productListAdapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                noProductsText.visibility = if (productListAdapter.isEmpty()) View.VISIBLE else View.GONE
            }
        })
        listView.setMenuCreator({ swipeMenu ->
            swipeMenu.addMenuItem(SwipeMenuItemFactory.openItem(context!!))
            swipeMenu.addMenuItem(SwipeMenuItemFactory.deleteItem(context!!))
        })
        listView.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                1 -> {
                    productListAdapter.deleteProductAndPricesById(productListAdapter.getItemId(position).toInt())
                }
                0 -> openProductDetailsFragment(position)
                else -> true
            }
        }
        listView.setOnItemClickListener { _, _, position, _ -> openProductDetailsFragment(position) }
        listView.adapter = productListAdapter
        productListAdapter.notifyDataSetChanged()
    }

    private fun openProductDetailsFragment(position: Int): Boolean {
        fragmentManager!!
                .beginTransaction()
                .replace(R.id.content, ProductDetailsFragment.newInstance(productListAdapter.getItem(position) as Product))
                .addToBackStack(null)
                .commit()
        return true
    }

    @OnTextChanged(R.id.search_et, callback = OnTextChanged.Callback.TEXT_CHANGED)
    fun searchByText(s: CharSequence?) {
        productListAdapter.filterByText(s.toString())
    }

}