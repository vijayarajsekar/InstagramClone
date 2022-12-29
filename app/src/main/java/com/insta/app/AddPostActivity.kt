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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.insta.app.databinding.ActivityAddPostBinding
import com.insta.imagecropper.CropImage

class AddPostActivity : AppCompatActivity() {
    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivityAddPostBinding

    private var mCurrentImageUrl = ""
    private var mImageUri: Uri? = null

    private var mPostStorageRef: StorageReference? = null
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mPostStorageRef = FirebaseStorage.getInstance().getReference().child("Post Pictures")
        CropImage.activity().setAspectRatio(2, 1).start(this@AddPostActivity)

        mBinding.postSaveImage.setOnClickListener {
            uploadImage()
        }

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Add post")
        mProgressDialog.setMessage("Please wait, we are uploading post...")
        mProgressDialog.setCanceledOnTouchOutside(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            val mResult = CropImage.getActivityResult(data)
            mImageUri = mResult.uri
            mBinding.postImageBanner.setImageURI(mImageUri)
        }
    }

    private fun uploadImage() {
        when {
            TextUtils.isEmpty(mImageUri.toString()) -> Toast.makeText(
                this,
                "Please select valid image",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                mProgressDialog.show()
                val fileRef =
                    mPostStorageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
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
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        mCurrentImageUrl = downloadUrl.toString()
                        val fireBaseDbRef =
                            FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = fireBaseDbRef.push().key

                        val mPostMap = HashMap<String, Any>()

                        mPostMap["post_id"] = postId!!
                        mPostMap["post_desc"] = mBinding.postDesc.text.toString()
                        mPostMap["post_user"] = FirebaseAuth.getInstance().currentUser!!.uid
                        mPostMap["post_image"] = mCurrentImageUrl

                        fireBaseDbRef.child(postId).updateChildren(mPostMap)

                        Toast.makeText(
                            this,
                            "Post has been uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this@AddPostActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                        mProgressDialog.dismiss()
                    } else {
                        mProgressDialog.dismiss()
                    }
                }
                )
            }
        }
    }
}