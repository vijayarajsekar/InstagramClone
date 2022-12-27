package com.insta.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.insta.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mTitle = mBinding.title

        val mNavView: BottomNavigationView = mBinding.navView
        mNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    mTitle.setText("Home")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_search -> {
                    mTitle.setText("Search")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_add_post -> {
                    mTitle.setText("Add Post")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_notification -> {
                    mTitle.setText("Favourite")
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    mTitle.setText("Profile")
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}