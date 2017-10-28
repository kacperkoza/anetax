package com.nostra.koza.anetax

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.shortToast
import kotlinx.android.synthetic.main.fragment_product_details.*


class ProductDetailsFragment : Fragment() {

    @BindView(R.id.product_name) lateinit var productName: TextView
    @BindView(R.id.product_barcode) lateinit var barcode: TextView

    private lateinit var priceListAdapter: PriceListAdapter
    private lateinit var product: Product

    companion object {
        const val PRODUCT_KEY = "priceList"
        const val TAG = "ProductDetailsFragment"

        fun newInstance(product: Product): ProductDetailsFragment {
            val bundle = Bundle()
            bundle.putSerializable(PRODUCT_KEY, product)
            val fragment = ProductDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_product_details, container, false)
        product = arguments.get(PRODUCT_KEY) as Product
        Log.i(TAG, product.toString())
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        productName.text = product.name
        barcode.text = product.barcode
        priceListAdapter = PriceListAdapter(context, product.id!!)
        listView.adapter = priceListAdapter
        listView.setMenuCreator({ menu -> menu.addMenuItem(SwipeMenuItemBuilder.buildDeleteItem(context)) })
        listView.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                0 -> {
                    priceListAdapter.removePriceElement(position)
                    true
                }
                else -> true
            }
        }
    }
}

class PriceListAdapter(val context: Context, id: Int) : BaseAdapter() {

    private val priceEntryDao: PriceEntryDao = PriceEntryDao(ProductDatabase(context).getDao(PriceEntry::class.java))
    private val priceEntries: List<PriceEntry> = priceEntryDao.findByProductId(id)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.price_row, parent, false)

        val price: TextView = rowView.findViewById(R.id.price)
        val date: TextView = rowView.findViewById(R.id.date)
        val priceEntry = priceEntries[position]
        price.text = formatPrice(priceEntry.price)
        date.text = formatDate(priceEntry.date)
        return rowView
    }

    override fun getItemId(position: Int): Long = priceEntries.get(position).id!!.toLong()

    override fun getItem(position: Int): Any = priceEntries.get(position)

    override fun getCount(): Int = priceEntries.size

    fun removePriceElement(position: Int): Boolean {
        if (isLastElement(position)) {
            toast(R.string.you_cant_delete_last_position)
            return false
        }
        if (isLastPrice()) {
            toast(R.string.you_cant_delete_last_price)
            return false
        }
        priceEntryDao.deleteById(getItemId(position).toInt())
        notifyDataSetChanged()
        return true
    }

    private fun isLastElement(position: Int): Boolean = priceEntries.size == position

    private fun isLastPrice(): Boolean = priceEntries.size == 1

    private fun toast(id: Int) = shortToast(context, context.getString(id))
}