package com.nostra.koza.anetax.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import butterknife.*
import com.afollestad.materialdialogs.MaterialDialog
import com.nostra.koza.anetax.DialogFactory
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.SwipeMenuItemFactory
import com.nostra.koza.anetax.activity.BarcodeScanActivity
import com.nostra.koza.anetax.adapter.PriceListAdapter
import com.nostra.koza.anetax.database.Barcode
import com.nostra.koza.anetax.database.Product
import com.nostra.koza.anetax.database.ProductDao
import com.nostra.koza.anetax.database.ProductDatabase
import com.nostra.koza.anetax.util.Keypad
import com.nostra.koza.anetax.util.shortToast
import kotlinx.android.synthetic.main.fragment_product_details.*


class ProductDetailsFragment : Fragment() {

    @BindView(R.id.product_name) lateinit var productName: TextView
    @BindView(R.id.product_barcode) lateinit var barcode: TextView

    private lateinit var productDao: ProductDao
    private lateinit var product: Product
    private lateinit var priceListAdapter: PriceListAdapter
    private lateinit var dialogFactory: DialogFactory

    companion object {
        const val PRODUCT_KEY = "priceList"
        private val DECIMAL_NUMBER_REGEX = Regex("([0-9]+.[0-9]{1,2})|[0-9]+")
        private val BARCODE_REGEX = Regex("[0-9]{0,}")

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
        ButterKnife.bind(this, view)
        setHasOptionsMenu(true)
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
        barcode.text = product.writeBarcodeToString()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.scan -> {
                scanProduct()
            }
        }
        return true
    }

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
        editBarcodeDialog(Barcode("", "", 0))
        return true
    }

    private fun editBarcodeDialog(newBarcode: Barcode) {
        dialogFactory.editBarcode(newBarcode.barcodeText, MaterialDialog.InputCallback { dialog, input ->
            if (!input.matches(BARCODE_REGEX)) {
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
        dialogFactory.newPrice(MaterialDialog.InputCallback { dialog, input ->
            if (!input.matches(DECIMAL_NUMBER_REGEX)) {
                shortToast(context!!, R.string.invalid_price)
                dialog.dismiss()
            } else {
                priceListAdapter.addNewPrice(input.toString())
            }
        })
    }

    @OnItemLongClick(R.id.listView)
    fun editPrice(position: Int): Boolean {
        dialogFactory.newPrice(MaterialDialog.InputCallback { dialog, input ->
            if (!input.matches(DECIMAL_NUMBER_REGEX)) {
                shortToast(context!!, R.string.invalid_price)
                dialog.dismiss()
            } else {
                val oldPriceEntry = priceListAdapter.getItem(position)
                val modifiedPrice = oldPriceEntry.price.copy(priceMargin = input.toString().toDouble())
                priceListAdapter.editPrice(oldPriceEntry.copy(price = modifiedPrice))
            }
        })
        return false
    }

    private fun updateProduct(modifiedProduct: Product) {
        productDao.update(modifiedProduct)
        product = productDao.findById(product.id!!)
        fillViews()
    }
}