package com.nostra.koza.anetax.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.RadioGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.nostra.koza.anetax.PriceCalculator
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.activity.BarcodeScanActivity
import com.nostra.koza.anetax.database.*
import com.nostra.koza.anetax.util.formatPrice
import com.nostra.koza.anetax.util.hide
import com.nostra.koza.anetax.util.shortToast

class AddProductFragment : Fragment() {

    @BindView(R.id.product_name_et) lateinit var productNameText: TextView
    @BindView(R.id.barcode_et) lateinit var barcodeText: TextView
    @BindView(R.id.tax_radio_group) lateinit var taxRadioGroup: RadioGroup
    @BindView(R.id.price_et) lateinit var priceText: TextView
    @BindView(R.id.margin_price_et) lateinit var marginPriceEt: TextView

    private lateinit var awesomeValidation: AwesomeValidation

    private lateinit var productDao: ProductDao
    private lateinit var priceEntryDao: PriceEntryDao

    private var scannedBarcode: Barcode? = null

    companion object {
        const val TAG = "AddProductFragment"
        const val ONLY_NUMBERS_PATTERN = "[0-9]{0,}"
        const val DECIMAL_FORMAT_PATTERN = "([0-9]+.[0-9]{1,2})|[0-9]+"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        ButterKnife.bind(this, view)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hide(activity!!)
        awesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        awesomeValidation.addValidation(activity, R.id.product_name_et, RegexTemplate.NOT_EMPTY, R.string.invalid_product_name)
        awesomeValidation.addValidation(activity, R.id.barcode_et, ONLY_NUMBERS_PATTERN, R.string.invalid_barcode)
        awesomeValidation.addValidation(activity, R.id.price_et, DECIMAL_FORMAT_PATTERN, R.string.invalid_price)
        awesomeValidation.addValidation(activity, R.id.margin_price_et, DECIMAL_FORMAT_PATTERN, R.string.invalid_price)

        val productDb = ProductDatabase(context!!)
        productDao = ProductDao(productDb.getDao(Product::class.java))
        priceEntryDao = PriceEntryDao(productDb.getDao(PriceEntry::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.scan_menu, menu)
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
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        if (requestCode == BarcodeScanActivity.SCAN_RESULT_CODE) {
            val scanResult = data.getSerializableExtra(BarcodeScanActivity.SCAN_RESULT_KEY) as Barcode
            Log.i(TAG, scanResult.toString())
            barcodeText.text = scanResult.barcodeText
            scannedBarcode = scanResult
        }
    }

    @OnClick(R.id.add_button)
    fun addNewProduct() {
        if (!awesomeValidation.validate()) return
        addProductAndPriceEntry()
        clearAndNotify()
    }

    private fun addProductAndPriceEntry() {
        val enteredBarcode = getBarcode()
        val product = productDao.add(
                Product(null,
                        getProductName(),
                        scannedBarcode ?: enteredBarcode,
                        getTaxRate()))
        priceEntryDao.add(
                PriceEntry(null,
                        product.id!!,
                        calculatePrice().copy(priceMargin = marginPriceEt.text.toString().toDouble())))
    }

    private fun clearAndNotify() {
        shortToast(context!!, getString(R.string.successfully_added_new_product))
        clearAddProductForm()
        hide(activity!!)
        productNameText.requestFocus()
    }

    private fun clearAddProductForm() {
        productNameText.text = ""
        barcodeText.text = ""
        barcodeText.text = ""
        priceText.text = ""
        marginPriceEt.text = ""
        taxRadioGroup.check(R.id.tax_five_percent)
    }

    @OnClick(R.id.tax_five_percent, R.id.tax_eight_percent, R.id.tax_twenty_three_percent)
    fun onTaxChanged() {
        setNewMarginPrice()
    }

    @OnTextChanged(R.id.price_et)
    fun onPriceChanged() {
        setNewMarginPrice()
    }

    private fun setNewMarginPrice() {
        if (priceText.text.isEmpty()) return
        marginPriceEt.text = formatPrice(calculatePrice().priceMargin)
    }

    private fun calculatePrice(): Price = PriceCalculator.calculateMarginPrice(getNetPrice(), getTaxRate())

    private fun getNetPrice() = priceText.text.toString().toDouble()

    private fun getBarcode() = if (!barcodeText.text.isEmpty()) Barcode(barcodeText.text.toString(), null, null) else null

    private fun getProductName() = productNameText.text.toString()

    private fun getTaxRate(): TaxRate = when (taxRadioGroup.checkedRadioButtonId) {
        R.id.tax_five_percent -> TaxRate.FIVE_PERCENT
        R.id.tax_eight_percent -> TaxRate.EIGHT_PERCENT
        R.id.tax_twenty_three_percent -> TaxRate.TWENTY_THREE_PERCENT
        else -> throw RuntimeException("Invalid tax rate!")
    }

    @OnClick(R.id.background_layout)
    fun hideKeypad() = hide(activity!!)

}
