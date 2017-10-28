package com.nostra.koza.anetax

import java.io.Serializable

/**
 * Created by kacper.koza on 25/10/2017.
 */
data class ScanResult(
        val barcode: String,
        val barcodeName: String,
        val barcodeOrdinal: Int
) : Serializable