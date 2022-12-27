package com.insta.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.insta.app.Fragments.HomeFragment
import com.insta.app.Fragments.NotificationsFragment
import com.insta.app.Fragments.ProfileFragment
import com.insta.app.Fragments.SearchFragment
import com.insta.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivityMainBinding
    internal var mSelectedFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /**
         * Initialize and Setting BottomNavigationView fragments
         */
        val mNavView: BottomNavigationView = mBinding.navView
        mNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    mSelectedFragment = HomeFragment()
                }
                R.id.navigation_search -> {
                    mSelectedFragment = SearchFragment()
                }
                R.id.navigation_add_post -> {
                    mSelectedFragment = NotificationsFragment()
                }
                R.id.navigation_notification -> {
                    mSelectedFragment = NotificationsFragment()
                }
                R.id.navigation_profile -> {
                    mSelectedFragment = ProfileFragment()
                }
            }

            /**
             * Setting selected fragment as per the click
             */
            if (mSelectedFragment != null)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, mSelectedFragment!!).commit()

            false
        }

        /**
         * Setting Default page as HomeScreenFragment
         */
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, HomeFragment()).commit()
    }
}