package com.insta.app

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.insta.app.Models.UserSearchModel
import com.insta.app.databinding.ActivityAccountSettingsBinding
import com.insta.imagecropper.CropImage
import com.squareup.picasso.Picasso

class AccountSettingsActivity : AppCompatActivity() {
    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivityAccountSettingsBinding
    private lateinit var mFireBaseUser: FirebaseUser
    private var checker = ""

    private var mCurrentImageUrl = ""
    private var mImageUri: Uri? = null

    private var mProfileStorageRef: StorageReference? = null

    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mFireBaseUser = FirebaseAuth.getInstance().currentUser!!
        mProfileStorageRef = FirebaseStorage.getInstance().getReference().child("ProfilePics")

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
                uploadImageAndInfo()
            } else {
                updateUserInfoOnly()
            }
        }

        mBinding.textChangeImage.setOnClickListener {
            checker = "clicked"
            CropImage.activity().setAspectRatio(1, 1).start(this@AccountSettingsActivity)
        }

        getUserInfo()

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Account Settings")
        mProgressDialog.setMessage("Please wait, we are updating your profile...")
        mProgressDialog.setCanceledOnTouchOutside(false)
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

    private fun uploadImageAndInfo() {

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

            TextUtils.isEmpty(mImageUri.toString()) -> Toast.makeText(
                this,
                "Please select valid image",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                mProgressDialog.show()
                val fileRef = mProfileStorageRef!!.child(mFireBaseUser!!.uid + ".jpg")
                val uploadImageTask: StorageTask<*>
                uploadImageTask = fileRef.putFile(mImageUri!!)

                uploadImageTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            mProgressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (
                    OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result
                            mCurrentImageUrl = downloadUrl.toString()
                            val fireBaseDbRef =
                                FirebaseDatabase.getInstance().reference.child("InstaUsers")

                            val mCurrentUserMap = HashMap<String, Any>()

                            mCurrentUserMap["fullname"] = mFullname.lowercase()
                            mCurrentUserMap["username"] = mUsername.lowercase()
                            mCurrentUserMap["bio"] = mEmailAddress.lowercase()
                            mCurrentUserMap["image"] = mCurrentImageUrl

                            fireBaseDbRef.child(mFireBaseUser.uid).updateChildren(mCurrentUserMap)

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
                        } else {
                            mProgressDialog.dismiss()
                        }
                    }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {

            val mResult = CropImage.getActivityResult(data)
            mImageUri = mResult.uri
            mBinding.profileImageAccount.setImageURI(mImageUri)
        }
    }


}