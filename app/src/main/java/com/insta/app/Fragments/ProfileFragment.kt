package com.insta.app.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.insta.app.AccountSettingsActivity
import com.insta.app.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    lateinit var mRootView: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mRootView = FragmentProfileBinding.inflate(inflater, container, false)
        mRootView.btnEditProfile.setOnClickListener({
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        })

        return mRootView.root
    }

}