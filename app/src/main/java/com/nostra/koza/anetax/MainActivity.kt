package com.nostra.koza.anetax

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_add -> {
                beginFragmentTransaction()
                        .replace(R.id.content, AddProductFragment())
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_export -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun beginFragmentTransaction() = supportFragmentManager.beginTransaction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

}