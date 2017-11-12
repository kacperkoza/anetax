package com.nostra.koza.anetax

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.baoyz.swipemenulistview.SwipeMenuItem

/**
 * Created by kacper.koza on 28/10/2017.
 */
object SwipeMenuItemFactory {

    fun deleteItem(context: Context): SwipeMenuItem {
        val deleteItem = SwipeMenuItem(context)
        deleteItem.background = ColorDrawable(Color.RED)
        deleteItem.width = 180
        deleteItem.setIcon(R.drawable.ic_delete_white_24dp)
        return deleteItem
    }

    fun openItem(context: Context): SwipeMenuItem {
        val openItem = SwipeMenuItem(context)
        openItem.background = ColorDrawable(Color.GRAY)
        openItem.width = 180
        openItem.title = context.getString(R.string.open)
        openItem.titleSize = 18
        openItem.titleColor = Color.WHITE
        return openItem
    }

    fun sendItem(context: Context): SwipeMenuItem {
        val sendItem = SwipeMenuItem(context)
        sendItem.background = ColorDrawable(Color.DKGRAY)
        sendItem.width = 180
        sendItem.setIcon(R.drawable.ic_send_white_24dp)
        return sendItem
    }

}