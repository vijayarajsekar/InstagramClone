package com.insta.app

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.insta.app.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivitySignInBinding
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        mBinding.btnLogin.setOnClickListener {
            loginUser()
        }

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Sign in")
        mProgressDialog.setMessage("Please wait, this may take a while...")
        mProgressDialog.setCanceledOnTouchOutside(false)

    }

    override fun onStart() {
        super.onStart()

        /**
         * Validating user, if already Logged in
         */
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(
                Intent(
                    this@SignInActivity,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun loginUser() {
        val mEmailAddress = mBinding.textLoginEmail.text.toString()
        val mPassword = mBinding.textLoginPwd.text.toString()

        /**
         * Validating user inputs
         */
        when {
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
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(mEmailAddress, mPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mProgressDialog.dismiss()
                            startActivity(
                                Intent(
                                    this@SignInActivity,
                                    MainActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
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
}