package com.insta.app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.insta.app.Models.UserSearchModel
import com.insta.app.databinding.ActivityAccountSettingsBinding
import com.squareup.picasso.Picasso

class AccountSettingsActivity : AppCompatActivity() {
    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivityAccountSettingsBinding
    private lateinit var mFireBaseUser: FirebaseUser
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mFireBaseUser = FirebaseAuth.getInstance().currentUser!!

        mBinding.btnLogoutAccount.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(
                    this@AccountSettingsActivity,
                    SignInActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        mBinding.imageOkay.setOnClickListener {
            if (checker == "clicked") {

            } else {
                updateUserInfoOnly()
            }
        }

        mBinding.imageClose.setOnClickListener {

        }

        getUserInfo()
    }

    private fun getUserInfo() {
        val mUserInfo =
            FirebaseDatabase.getInstance().getReference().child("InstaUsers")
                .child(mFireBaseUser.uid)
        mUserInfo.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val user = snapshot.getValue<UserSearchModel>(UserSearchModel::class.java)
                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile)
                        .into(mBinding.profileImageAccount)

                    mBinding.editBioFullname?.setText(user.fullname)
                    mBinding.editBioUsername?.setText(user.username)
                    mBinding.editBio?.setText(user.bio)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun updateUserInfoOnly() {
        val mFullname = mBinding.editBioFullname.text.toString()
        val mUsername = mBinding.editBioUsername.text.toString()
        val mEmailAddress = mBinding.editBio.text.toString()

        /**
         * Validating user inputs
         */
        when {
            TextUtils.isEmpty(mFullname) -> Toast.makeText(
                this,
                "Fullname is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(mUsername) -> Toast.makeText(
                this,
                "Username is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(mEmailAddress) -> Toast.makeText(
                this,
                "Email Id is required",
                Toast.LENGTH_SHORT
            ).show()

            else -> {

                val mUserRef = FirebaseDatabase.getInstance().getReference().child("InstaUsers")

                val mCurrentUserMap = HashMap<String, Any>()

                mCurrentUserMap["fullname"] = mFullname.lowercase()
                mCurrentUserMap["username"] = mUsername.lowercase()
                mCurrentUserMap["bio"] = mEmailAddress.lowercase()

                mUserRef.child(mFireBaseUser.uid).updateChildren(mCurrentUserMap)

                Toast.makeText(
                    this,
                    "Account has been updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this@AccountSettingsActivity,
                        MainActivity::class.java
                    )
                )

                finish()
            }
        }
    }
}