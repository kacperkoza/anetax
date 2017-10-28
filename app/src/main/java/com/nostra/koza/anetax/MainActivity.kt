package com.nostra.koza.anetax

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.nostra.koza.anetax.util.shortToast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        beginFragmentTransaction(AddProductFragment())
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return true
    }

    private val mOnNavigationItemSelectedListener = OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_add -> {
                beginFragmentTransaction(AddProductFragment())
                setActionBarTitle(R.string.title_add_product)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                beginFragmentTransaction(ProductListFragment())
                setActionBarTitle(R.string.title_product_list)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_export -> {
                shortToast(this, "Not implemented yet...")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun beginFragmentTransaction(fragment: Fragment) =
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit()

    private fun setActionBarTitle(id: Int) = supportActionBar?.setTitle(getString(id))

}