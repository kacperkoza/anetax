package com.nostra.koza.anetax


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
import com.nostra.koza.anetax.util.toast


class AddProductFragment : Fragment() {

    @BindView(R.id.product_name_et) lateinit var productNameText: TextView
    @BindView(R.id.barcode_et) lateinit var barcodeText: TextView
    @BindView(R.id.tax_radio_group) lateinit var taxRadioGroup: RadioGroup
    @BindView(R.id.price_et) lateinit var priceText: TextView

    private lateinit var awesomeValidation: AwesomeValidation

    private lateinit var productDatabase: ProductDatabase
    private lateinit var productDao: ProductDao

    companion object {
        const val DIGITS_IN_BARCODE = 10
        const val TAX_FIVE_PERCENT = 0.05
        const val TAX_EIGHT_PERCENT = 0.08
        const val TAX_TWENTY_THREE_PERCENT = 0.23
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
        awesomeValidation.addValidation(activity, R.id.barcode_et, "[0-9]{$DIGITS_IN_BARCODE}", R.string.invalid_barcode)
        awesomeValidation.addValidation(activity, R.id.price_et, "([0-9]+.[0-9]{1,2})|[0-9]+", R.string.invalid_price)
        productDatabase = ProductDatabase(context)
        productDao = ProductDao(productDatabase.getDao(Product::class.java))
    }

    @OnClick(R.id.add_button)
    fun addNewProduct() {
        Log.i("OnClick", "Adding new product")
        val product = if (awesomeValidation.validate()) {
            Product(
                    null,
                    productNameText.text.toString(),
                    barcodeText.text.toString(),
                    priceText.text.toString().toDouble(),
                    when (taxRadioGroup.checkedRadioButtonId) {
                        R.id.tax_five_percent -> TAX_FIVE_PERCENT
                        R.id.tax_eight_percent -> TAX_EIGHT_PERCENT
                        R.id.tax_twenty_three_percent -> TAX_TWENTY_THREE_PERCENT
                        else -> throw RuntimeException("Invalid tax rate!")
                    })
        } else {
            return
        }
        Log.i("OnClick", "Adding new product = $product")
        productDao.add(product)
        toast(context, getString(R.string.successfully_added_new_product))
        Log.i("OnClick", "Product added, list = ${productDao.findAll()}")
        clearForm()
    }

    private fun clearForm() {
        productNameText.text = ""
        barcodeText.text = ""
        barcodeText.text = ""
        priceText.text = ""
        taxRadioGroup.check(R.id.tax_five_percent)
    }

}