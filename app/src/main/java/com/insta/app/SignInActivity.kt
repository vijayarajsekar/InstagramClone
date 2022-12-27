package com.insta.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        mBinding.btnSignUp.setOnClickListener({
            startActivity(Intent(this, SignUpActivity::class.java))
        })
    }
}