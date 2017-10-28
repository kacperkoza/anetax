package com.nostra.koza.anetax

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.baoyz.swipemenulistview.SwipeMenuItem

/**
 * Created by kacper.koza on 28/10/2017.
 */
object SwipeMenuItemBuilder {

    fun buildDeleteItem(context: Context): SwipeMenuItem {
        val deleteItem = SwipeMenuItem(context)
        deleteItem.background = ColorDrawable(Color.RED)
        deleteItem.width = 180
        deleteItem.setIcon(R.drawable.ic_delete_white_24dp)
        return deleteItem
    }

    fun buildOpenItem(context: Context): SwipeMenuItem {
        val openItem = SwipeMenuItem(context)
        openItem.background = ColorDrawable(Color.GRAY)
        openItem.width = 180
        openItem.title = context.getString(R.string.open)
        openItem.titleSize = 18
        openItem.titleColor = Color.WHITE
        return openItem
    }

}