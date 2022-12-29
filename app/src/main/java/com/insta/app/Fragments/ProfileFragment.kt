package com.insta.app.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.insta.app.AccountSettingsActivity
import com.insta.app.Models.UserSearchModel
import com.insta.app.R
import com.insta.app.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var mRootView: FragmentProfileBinding
    private lateinit var mProfileId: String
    private lateinit var mFireBaseUser: FirebaseUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mRootView = FragmentProfileBinding.inflate(inflater, container, false)
        mRootView.btnEditProfile.setOnClickListener { it ->
            val getButtonText = mRootView.btnEditProfile.text

            when {
                getButtonText == "Edit profile" -> startActivity(
                    Intent(
                        context,
                        AccountSettingsActivity::class.java
                    )
                )

                getButtonText == "Follow" -> {
                    mFireBaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(mProfileId).setValue(true)
                    }

                    mFireBaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(mProfileId)
                            .child("Followers").child(it.toString()).setValue(true)
                    }
                }

                getButtonText == "Following" -> {
                    mFireBaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(mProfileId).removeValue()
                    }

                    mFireBaseUser?.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(mProfileId)
                            .child("Followers").child(it.toString()).removeValue()
                    }
                }

            }
        }
        mFireBaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        if (pref != null) {
            this.mProfileId = pref.getString("profileId", "none").toString()
        } else {
            this.mProfileId = mFireBaseUser.uid
        }

        if (mProfileId == mFireBaseUser.uid) {
            mRootView.btnEditProfile.text = "Edit profile"
        } else {
            checkFollowAndFollowingButton()
        }

        getFollowers()
        getFollowings()
        getUserInfo()

        return mRootView.root
    }

    private fun checkFollowAndFollowingButton() {

        val mFollowingRef = mFireBaseUser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                .child("Following")
        }

        if (mFollowingRef != null) {
            mFollowingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(mProfileId).exists()) {
                        mRootView.btnEditProfile?.text = "Following"
                    } else {
                        mRootView.btnEditProfile?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun getFollowers() {
        val mFollowersRef =
            FirebaseDatabase.getInstance().reference.child("Follow").child(mProfileId)
                .child("Followers")

        mFollowersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    mRootView.totalFollowers?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowings() {
        val mFollowersRef =
            FirebaseDatabase.getInstance().reference.child("Follow").child(mProfileId)
                .child("Following")

        mFollowersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    mRootView.totalFollowing?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getUserInfo() {
        val mUserInfo =
            FirebaseDatabase.getInstance().getReference().child("InstaUsers").child(mProfileId)
        mUserInfo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val user = snapshot.getValue<UserSearchModel>(UserSearchModel::class.java)
                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile)
                        .into(mRootView.imageProfile)

                    mRootView.textProfileFullname.text = user.fullname
                    mRootView.textProfileUname.text = user.username
                    mRootView.textProfileBio.text = user.bio
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    override fun onStop() {
        super.onStop()

        val mPref = context?.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)?.edit()
        mPref?.putString("profileId", mFireBaseUser.uid)?.apply()
    }

    override fun onPause() {
        super.onPause()
        val mPref = context?.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)?.edit()
        mPref?.putString("profileId", mFireBaseUser.uid)?.apply()
    }
}