package com.nostra.koza.anetax.database

import java.io.Serializable

data class ScanResult(
        val barcode: String,
        val barcodeName: String,
        val barcodeOrdinal: Int
) : Serializable