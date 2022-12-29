package com.insta.app

import android.content.Intent
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
                    moveToFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_search -> {
                    moveToFragment(SearchFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_add_post -> {
                    it.isChecked = false
                    startActivity(
                        Intent(
                            this@MainActivity,
                            AddPostActivity::class.java
                        )
                    )
                }
                R.id.navigation_notification -> {
                    moveToFragment(NotificationsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    moveToFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        /**
         * Setting Default page as HomeScreenFragment
         */
        moveToFragment(HomeFragment())
    }

    /**
     * Moving into relevant Fragment
     */
    private fun moveToFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, fragment)
        fragmentTransaction.commit()
    }
}