package com.nostra.koza.anetax.fragment

import android.content.Intent
import android.database.DataSetObserver
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.view.*
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.SwipeMenuItemFactory
import com.nostra.koza.anetax.adapter.FileListAdapter
import com.nostra.koza.anetax.database.*
import com.nostra.koza.anetax.exporter.ExcelFileExporter
import com.nostra.koza.anetax.exporter.strategy.AllPriceStrategy
import com.nostra.koza.anetax.exporter.strategy.WriteStrategy
import com.nostra.koza.anetax.exporter.strategy.LastPriceStrategy
import com.nostra.koza.anetax.util.Keypad
import com.nostra.koza.anetax.util.shortToast
import kotlinx.android.synthetic.main.fragment_export.*
import java.io.File


class ExportFragment : Fragment() {

    @BindView(R.id.no_files_text) lateinit var noFilesTv: TextView

    private lateinit var productDao: ProductDao
    private lateinit var priceEntryDao: PriceEntryDao

    private lateinit var fileListAdapter: FileListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_export, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        val db = ProductDatabase(context!!)
        priceEntryDao = PriceEntryDao(db.getDao(PriceEntry::class.java))
        productDao = ProductDao(db.getDao(Product::class.java))
        fileListAdapter = FileListAdapter(context!!)
        fileListAdapter.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                noFilesTv.visibility = if (fileListAdapter.isEmpty()) View.VISIBLE else View.GONE
            }
        })
        listView.adapter = fileListAdapter
        listView.setMenuCreator({ swipeMenu ->
            swipeMenu.addMenuItem(SwipeMenuItemFactory.sendItem(context!!))
            swipeMenu.addMenuItem(SwipeMenuItemFactory.deleteItem(context!!))
        })
        listView.setOnMenuItemClickListener { position, _, index ->
            when (index) {
                1 -> {
                    fileListAdapter.deleteFile(position)
                    true
                }
                0 -> {
                    val file = fileListAdapter.getItem(position) as File
                    sendEmailWithAttachment(file)
                    true
                }
                else -> true
            }
        }
        fileListAdapter.refresh()
        Keypad.hide(activity!!)
    }

    private fun sendEmailWithAttachment(file: File) {
        val uri = FileProvider.getUriForFile(context!!, "com.nostra.koza.anetax", file)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "vnd.android.cursor.dir/email"
        val to = arrayOf("kkoza11@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, file.nameWithoutExtension)
        startActivity(emailIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.export_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.export -> {
                createFileWithLastPrice()
                createFileWithAllPrices()
            }
        }
        return true
    }

    private fun createFileWithLastPrice() {
        exportToFile(LastPriceStrategy())
     }

    private fun createFileWithAllPrices() {
        exportToFile(AllPriceStrategy())
    }

    private fun exportToFile(strategy: WriteStrategy) {
        val exporter = ExcelFileExporter(strategy)
        exporter.export(
                getPath(),
                productDao.findAll(),
                priceEntryDao.findAll())
        shortToast(context!!, R.string.successful_export)
        fileListAdapter.refresh()
    }

    private fun getPath() = context!!.filesDir.absolutePath + "/"

}