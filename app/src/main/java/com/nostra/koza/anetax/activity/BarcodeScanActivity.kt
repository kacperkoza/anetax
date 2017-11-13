package com.nostra.koza.anetax.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.zxing.Result
import com.nostra.koza.anetax.database.Barcode
import me.dm7.barcodescanner.zxing.ZXingScannerView

class BarcodeScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scanner: ZXingScannerView

    companion object {
        const val SCAN_RESULT_KEY = "scan_result"
        const val SCAN_RESULT_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanner = ZXingScannerView(this)
        setContentView(scanner)
    }

    override fun onResume() {
        super.onResume()
        scanner.setResultHandler(this)
        scanner.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner.stopCamera()
    }

    override fun handleResult(rawResult: Result) {
        val intent = Intent()
        intent.putExtra(SCAN_RESULT_KEY, Barcode(rawResult.text, rawResult.barcodeFormat.name, rawResult.barcodeFormat.ordinal))
        setResult(SCAN_RESULT_CODE, intent)
        finish()
    }

}

