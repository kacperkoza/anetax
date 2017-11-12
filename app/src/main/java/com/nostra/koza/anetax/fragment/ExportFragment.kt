package com.nostra.koza.anetax.fragment

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.database.*
import com.nostra.koza.anetax.exporter.ExcelExporter
import com.nostra.koza.anetax.exporter.strategy.WriteLastPriceStrategy
import com.nostra.koza.anetax.util.Keypad
import java.io.File
import android.widget.Toast
import com.nostra.koza.anetax.activity.MainActivity
import android.os.Environment.getExternalStorageDirectory
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.record.aggregates.RowRecordsAggregate.createRow
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.FileOutputStream
import java.io.IOException


class ExportFragment : Fragment() {

    private lateinit var exporter: ExcelExporter
    private lateinit var productDao: ProductDao
    private lateinit var priceEntryDao: PriceEntryDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_export, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val db = ProductDatabase(context!!)
        priceEntryDao = PriceEntryDao(db.getDao(PriceEntry::class.java))
        productDao = ProductDao(db.getDao(Product::class.java))
        Keypad.hide(activity!!)
    }

    @OnClick(R.id.export_all_btn)
    fun exportAll() {
        Log.i("", "")
    }

    @OnClick(R.id.export_for_employees_btn)
    fun exportForEmployees() {
//        exporter = ExcelExporter(WriteLastPriceStrategy())
//        val f = "${context!!.filesDir}/file.xls"
//        exporter.export(f, productDao.findAll(), priceEntryDao.findAll())
//        Log.i("after save", File("/storage/sdcard/").list().joinToString { "$it, " })
//
//        val file = context!!.filesDir.listFiles().last()
//        if (!file.exists()) {
//            file.mkdir()
//        }
//        val path: Uri = Uri.fromFile(file)
//        val p = FileProvider.getUriForFile(context!!, "com.nostra.koza.anetax", file)
        val workbook = HSSFWorkbook()
        val firstSheet = workbook.createSheet("Sheet No 1")
        val secondSheet = workbook.createSheet("Sheet No 2")
        val rowA = firstSheet.createRow(0)
        val cellA = rowA.createCell(0)
        cellA.setCellValue(HSSFRichTextString("Sheet One"))
        val rowB = secondSheet.createRow(0)
        val cellB = rowB.createCell(0)
        cellB.setCellValue(HSSFRichTextString("Sheet two"))
        var fos: FileOutputStream? = null


        val str_path = context!!.filesDir.absolutePath + "/"
        var file = File(str_path, "pliczek" + ".xls")
        fos = FileOutputStream(file)
        workbook.write(fos)
        fos.flush()
        fos.close()
//            Toast.makeText(this@MainActivity, "Excel Sheet Generated", Toast.LENGTH_SHORT).show()


        val p = FileProvider.getUriForFile(context!!, "com.nostra.koza.anetax", file)

        sendEmail(p)
    }

    fun sendEmail(file: Uri) {
//        val Root = Environment.getExternalStorageDirectory()
//
//        val path = Root.absolutePath + "/plik2.csv"
        val emailIntent = Intent(Intent.ACTION_SEND)
        // set the type to 'email'
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("kkoza11@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, file)
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        startActivity(emailIntent)
    }

    private fun getFileDirectory(): String {
        val directory = context!!.filesDir.path
        Log.i("Directory", directory)
        Log.i("roo", File(".").list().joinToString { "$it, " })
        return directory
    }

}

class Provider : ContentProvider() {
    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(uri: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}