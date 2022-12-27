package com.insta.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.insta.app.databinding.ActivitySignInBinding
import com.insta.app.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSignIn.setOnClickListener({
            startActivity(Intent(this, SignInActivity::class.java))
        })
    }
}