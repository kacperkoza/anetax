package com.nostra.koza.anetax.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import butterknife.BindView
import butterknife.ButterKnife
import com.nostra.koza.anetax.R
import com.nostra.koza.anetax.fragment.AddProductFragment
import com.nostra.koza.anetax.fragment.ProductListFragment
import com.nostra.koza.anetax.util.shortToast

class MainActivity : AppCompatActivity() {

    @BindView(R.id.navigation) lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
                onSupportNavigateUp()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_list -> {
                beginFragmentTransaction(ProductListFragment())
                setActionBarTitle(R.string.title_product_list)
                onSupportNavigateUp()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_export -> {
                shortToast(this, "Not implemented yet...")
                onSupportNavigateUp()
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