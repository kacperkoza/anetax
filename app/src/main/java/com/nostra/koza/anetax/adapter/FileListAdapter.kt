package com.nostra.koza.anetax.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.util.shortToast
import java.io.File


class FileListAdapter(private val context: Context) : BaseAdapter() {

    private var files: List<File> = getReversedFileList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var view = convertView

        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.file_row, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        }
        holder.fileName.text = files[position].name
        return view!!
    }

    override fun getItemId(position: Int): Long = files.get(position).hashCode().toLong()

    override fun getItem(position: Int): Any = this.files.get(position)

    override fun getCount(): Int = this.files.size

    fun deleteFile(position: Int) {
        if (files[position].delete()) {
            shortToast(context, R.string.succesfully_deleted_file)
            files = getReversedFileList()
            notifyDataSetChanged()
        } else {
            shortToast(context, R.string.delete_file_problem)
        }
    }

    private fun getReversedFileList(): List<File> = context.filesDir.listFiles().toList().reversed()

    fun refresh() {
        files = getReversedFileList()
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) {
        @BindView(R.id.file_name) lateinit var fileName: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }
}

