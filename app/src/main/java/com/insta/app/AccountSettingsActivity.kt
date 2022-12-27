package com.insta.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.insta.app.databinding.ActivityAccountSettingsBinding
import com.insta.app.databinding.ActivitySignInBinding

class AccountSettingsActivity : AppCompatActivity() {
    /**
     * Variable Declaration
     */
    private lateinit var mBinding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnLogoutAccount.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(
                    this@AccountSettingsActivity,
                    SignInActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}