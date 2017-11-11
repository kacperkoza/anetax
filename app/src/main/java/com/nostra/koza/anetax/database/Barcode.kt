package com.nostra.koza.anetax.database

import java.io.Serializable

data class Barcode(
        val barcodeText: String,
        val barcodeName: String?,
        val barcodeOrdinal: Int?
) : Serializable