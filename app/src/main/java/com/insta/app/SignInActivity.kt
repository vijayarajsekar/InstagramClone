package com.insta.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.insta.app.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        /**
         * Validating user, if already Logged in
         */
        if(FirebaseAuth.getInstance().currentUser!= null) {
            startActivity(
                Intent(
                    this@SignInActivity,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}