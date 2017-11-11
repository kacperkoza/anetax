package com.nostra.koza.anetax.fragment

import android.content.Context
import android.content.Intent
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
import butterknife.OnClick
import butterknife.OnLongClick
import com.afollestad.materialdialogs.MaterialDialog
import com.nostra.koza.anetax.*
import com.nostra.koza.anetax.activity.BarcodeScanActivity
import com.nostra.koza.anetax.database.*
import com.nostra.koza.anetax.util.Keypad
import com.nostra.koza.anetax.util.formatDate
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.shortToast
import kotlinx.android.synthetic.main.fragment_product_details.*


class ProductDetailsFragment : Fragment() {

    @BindView(R.id.product_name) lateinit var productName: TextView
    @BindView(R.id.product_barcode) lateinit var barcode: TextView

    private lateinit var priceListAdapter: PriceListAdapter
    private lateinit var product: Product

    private lateinit var productDao: ProductDao

    private lateinit var dialogFactory: DialogFactory

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)
        product = arguments!!.get(PRODUCT_KEY) as Product
        Log.i(TAG, product.toString())
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Keypad.hide(activity!!)
        dialogFactory = DialogFactory(context!!)
        productDao = ProductDao(ProductDatabase(context!!).getDao(Product::class.java))
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        priceListAdapter = PriceListAdapter(context!!, product)
        listView.adapter = priceListAdapter
        listView.setMenuCreator({ menu -> menu.addMenuItem(SwipeMenuItemFactory.deleteItem(context!!)) })
        listView.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                0 -> {
                    priceListAdapter.removePriceElement(position)
                    true
                }
                else -> true
            }
        }
        fillViews()
    }

    private fun fillViews() {
        productName.text = product.name
        barcode.text = product.barcode?.barcode ?: ""
    }

    @OnClick(R.id.add_fab)
    fun scanProduct() {
        startActivityForResult(Intent(activity, BarcodeScanActivity::class.java), BarcodeScanActivity.SCAN_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (BarcodeScanActivity.SCAN_RESULT_CODE != requestCode) return
        data ?: return
        val result: Barcode = data.getSerializableExtra(BarcodeScanActivity.SCAN_RESULT_KEY) as Barcode
        editBarcodeDialog(result)
    }

    @OnLongClick(R.id.product_barcode)
    fun editBarcode(): Boolean {
        editBarcodeDialog(Barcode("","",0))
        return true
    }

    private fun editBarcodeDialog(newBarcode: Barcode) {
        dialogFactory.editBarcode(newBarcode.barcode, MaterialDialog.InputCallback { dialog, input ->
            if (!input.matches(Regex("[0-9]{0,}"))) {
                shortToast(context!!, R.string.invalid_barcode)
                dialog.dismiss()
            } else {
                updateProduct(product.copy(barcode = newBarcode))
            }
        })
    }

    @OnLongClick(R.id.product_name)
    fun editProductName(): Boolean {
        dialogFactory.editProductName(MaterialDialog.InputCallback { dialog, input ->
            if (input.isEmpty()) {
                shortToast(context!!, R.string.invalid_product_name)
                dialog.dismiss()
            } else {
                updateProduct(product.copy(name = input.toString()))
            }
        })
        return true
    }

    @OnClick(R.id.add_price_button)
    fun addPrice() {
        dialogFactory.newPrice(MaterialDialog.InputCallback {dialog, input ->
            if (!input.matches(Regex("([0-9]+.[0-9]{1,2})|[0-9]+"))) {
                shortToast(context!!, R.string.invalid_price)
                dialog.dismiss()
            } else {
                priceListAdapter.addNewPrice(input.toString())
            }
        })
    }

    private fun updateProduct(modifiedProduct: Product) {
        productDao.update(modifiedProduct)
        product = productDao.findById(product.id!!)
        fillViews()
    }
}

class PriceListAdapter(val context: Context, val product: Product) : BaseAdapter() {

    private val priceEntryDao: PriceEntryDao = PriceEntryDao(ProductDatabase(context).getDao(PriceEntry::class.java))
    private var priceEntries: List<PriceEntry> = priceEntryDao.findByProductId(product.id!!)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.price_row, parent, false)

        val price: TextView = rowView.findViewById(R.id.price)
        val date: TextView = rowView.findViewById(R.id.date)
        val priceEntry = priceEntries[position]
        price.text = formatPrice(priceEntry.price.priceMargin)
        date.text = formatDate(priceEntry.date)
        return rowView
    }

    override fun getItemId(position: Int): Long = priceEntries.get(position).id!!.toLong()

    override fun getItem(position: Int): Any = priceEntries.get(position)

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
}