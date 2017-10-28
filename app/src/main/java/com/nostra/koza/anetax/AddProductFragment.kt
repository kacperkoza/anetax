package com.nostra.koza.anetax


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.nostra.koza.anetax.util.shortToast

class AddProductFragment : Fragment() {

    @BindView(R.id.product_name_et) lateinit var productNameText: TextView
    @BindView(R.id.barcode_et) lateinit var barcodeText: TextView
    @BindView(R.id.tax_radio_group) lateinit var taxRadioGroup: RadioGroup
    @BindView(R.id.price_et) lateinit var priceText: TextView

    private lateinit var awesomeValidation: AwesomeValidation

    private lateinit var productDao: ProductDao
    private lateinit var priceEntryDao: PriceEntryDao

    companion object {
        const val TAG = "AddProductFragment"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_add_product, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        awesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        awesomeValidation.addValidation(activity, R.id.product_name_et, RegexTemplate.NOT_EMPTY, R.string.invalid_product_name)
        awesomeValidation.addValidation(activity, R.id.barcode_et, "[0-9]+", R.string.invalid_barcode)
        awesomeValidation.addValidation(activity, R.id.price_et, "([0-9]+.[0-9]{1,2})|[0-9]+", R.string.invalid_price)
        val productDb = ProductDatabase(context)
        productDao = ProductDao(productDb.getDao(Product::class.java))
        priceEntryDao = PriceEntryDao(productDb.getDao(PriceEntry::class.java))
    }

    @OnClick(R.id.add_button)
    fun addNewProduct() {
        if (!awesomeValidation.validate()) return

        val taxRate = getTaxRate()
        val priceNet = priceText.text.toString().toDouble()
        val product = productDao.add(Product(null,
                productNameText.text.toString(),
                barcodeText.text.toString(),
                priceNet,
                taxRate))

        priceEntryDao.add(PriceEntry(null, product.id!!, PriceCalculator.calculateMarginPrice(priceNet, taxRate)))
        shortToast(context, getString(R.string.successfully_added_new_product))
        Log.i("OnClick", "Product added, list = ${productDao.findAll()}")
        Log.i("OnClick", "Product prices, list = ${priceEntryDao.findAll()}")
        clearAddProductForm()
    }

    private fun getTaxRate(): TaxRate = when (taxRadioGroup.checkedRadioButtonId) {
        R.id.tax_five_percent -> TaxRate.FIVE_PERCENT
        R.id.tax_eight_percent -> TaxRate.EIGHT_PERCENT
        R.id.tax_twenty_three_percent -> TaxRate.TWENTY_THREE_PERCENT
        else -> throw RuntimeException("Invalid tax rate!")
    }

    @OnClick(R.id.add_fab)
    fun scanProduct() {
        startActivityForResult(Intent(activity, BarcodeScanActivity::class.java), BarcodeScanActivity.SCAN_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        if (requestCode == BarcodeScanActivity.SCAN_RESULT_CODE) {
            val scanResult = data.getSerializableExtra(BarcodeScanActivity.SCAN_RESULT_KEY) as ScanResult
            Log.i(TAG, scanResult.toString())
            barcodeText.text = scanResult.barcode
        }
    }

    private fun clearAddProductForm() {
        productNameText.text = ""
        barcodeText.text = ""
        barcodeText.text = ""
        priceText.text = ""
        taxRadioGroup.check(R.id.tax_five_percent)
    }

}
