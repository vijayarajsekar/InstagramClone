package com.insta.app

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.insta.app.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivitySignUpBinding
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /**
         * Go to Signin, if already account created
         */
        mBinding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        /**
         * Create new account, then navigate into MainActivity
         */
        mBinding.btnSignup.setOnClickListener {
            createAccount()
        }

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Sign Up")
        mProgressDialog.setMessage("Please wait, this may take a while...")
        mProgressDialog.setCanceledOnTouchOutside(false)

    }

    private fun createAccount() {
        val mFullname = mBinding.textSignupFullname.text.toString()
        val mUsername = mBinding.textSignupUsername.text.toString()
        val mEmailAddress = mBinding.textSignupEmail.text.toString()
        val mPassword = mBinding.textSignupPwd.text.toString()

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
            TextUtils.isEmpty(mPassword) -> Toast.makeText(
                this,
                "Password is required",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                mProgressDialog.show()

                /**
                 * Creating user with Firebase
                 */
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(mEmailAddress, mPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveuserInfo(mFullname, mUsername, mEmailAddress)
                        } else {
                            Toast.makeText(
                                this,
                                "Err -> $task.exception.toString()",
                                Toast.LENGTH_SHORT
                            ).show()
                            mAuth.signOut()
                            mProgressDialog.dismiss()
                        }
                    }
            }
        }
    }

    /**
     * , saving the userDetails, after created the user successfully
     */
    private fun saveuserInfo(fname: String, uname: String, email: String) {
        val mCurrenntUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val mUserDbRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("InstaUsers")

        val mCurrentUserMap = HashMap<String, Any>()
        mCurrentUserMap["uid"] = mCurrenntUserId
        mCurrentUserMap["fullname"] = fname.lowercase()
        mCurrentUserMap["username"] = uname.lowercase()
        mCurrentUserMap["email"] = email
        mCurrentUserMap["bio"] = "I am using Insta Clone App."
        mCurrentUserMap["image"] = "gs://instaclone-cc711.appspot.com/Default Images/profile.png"

        mUserDbRef.child(mCurrenntUserId).setValue(mCurrentUserMap).addOnCompleteListener { task ->

            mProgressDialog.dismiss()

            if (task.isSuccessful) {
                Toast.makeText(
                    this,
                    "Account has been created successfully",
                    Toast.LENGTH_SHORT
                ).show()

                FirebaseDatabase.getInstance().reference.child("Follow").child(mCurrenntUserId)
                    .child("Following").child(mCurrenntUserId)
                    .setValue(true)

                startActivity(
                    Intent(
                        this@SignUpActivity,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                Toast.makeText(
                    this,
                    "Err -> ${task.exception.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                FirebaseAuth.getInstance().signOut()
            }
        }
    }
}