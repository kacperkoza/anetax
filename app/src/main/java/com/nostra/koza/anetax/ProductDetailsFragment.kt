package com.nostra.koza.anetax

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.shortToast
import kotlinx.android.synthetic.main.fragment_product_details.*


class ProductDetailsFragment : Fragment() {

    @BindView(R.id.product_name) lateinit var productName: TextView
    @BindView(R.id.product_barcode) lateinit var barcode: TextView

    private lateinit var priceList: MutableList<PriceEntry>
    private lateinit var priceListAdapter: PriceListAdapter
    private lateinit var priceEntryDao: PriceEntryDao
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
        priceList = product.entries.toMutableList()
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        priceEntryDao = PriceEntryDao(ProductDatabase(context).getDao(PriceEntry::class.java))
        productName.text = product.name
        barcode.text = product.barcode
        priceListAdapter = PriceListAdapter(context, product.entries)
        listView.adapter = priceListAdapter
        val swipeMenuCreator = SwipeMenuCreator({ menu ->
            menu.addMenuItem(buildDeleteSwipeItem())
        })
        listView.setMenuCreator(swipeMenuCreator)
        listView.setOnMenuItemClickListener { position, menu, index ->
            when (index) {
                0 -> deletePriceAtPosition(position)
                else -> true
            }
        }
    }

    private fun deletePriceAtPosition(position: Int): Boolean {
        if (position == priceList.size) {
            shortToast(context, "Nie można usunac ostatniej pozycji")
            return true
        }
        if (priceList.size == 1) {
            shortToast(context, "Nie można usunac ostatniej ceny")
            return true
        }
        val price = priceList.removeAt(position)
        priceEntryDao.deleteById(price.id!!)
        priceListAdapter = PriceListAdapter(context, priceList)
        return true
    }

    private fun buildDeleteSwipeItem(): SwipeMenuItem {
        val deleteItem = SwipeMenuItem(context)
        deleteItem.background = ColorDrawable(Color.RED)
        deleteItem.width = 180
        deleteItem.setIcon(R.drawable.ic_delete_white_24dp)
        return deleteItem
    }


}

class PriceListAdapter(val context: Context, val priceEntries: List<PriceEntry>) : BaseAdapter() {
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

}