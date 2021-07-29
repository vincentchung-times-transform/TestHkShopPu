package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventLogout
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.ui.onboard.login.OnBoardActivity
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.tencent.mmkv.MMKV

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerAccountSetupActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyeraccountsetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyeraccountsetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initClick()

    }

    private fun initVM() {
    }

    private fun initClick() {

        binding.containerLogout.setOnClickListener {

            var settings_rememberEmail = this.getSharedPreferences("rememberEmail", 0)
            var settings_rememberPassword = this.getSharedPreferences("rememberPassword", 0)
            var settings_rememberMe = this.getSharedPreferences("rememberMe", 0)

            val editor_email : SharedPreferences.Editor = settings_rememberEmail.edit()
            editor_email.apply {
                putString("rememberEmail", "false")
            }.apply()

            val editor_password : SharedPreferences.Editor = settings_rememberPassword.edit()
            editor_password.apply {
                putString("rememberPassword", "false")
            }.apply()

            val editor : SharedPreferences.Editor = settings_rememberMe.edit()
            editor.apply {
                putString("rememberMe", "false")
            }.apply()

            MMKV.mmkvWithID("http")
                .putString("UserId", "")
                .putString("Email","")
                .putString("Password","")

            //google log out
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .build()
            var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut()


            //facebook log out
            LoginManager.getInstance().logOut()

            //Log out
            EventLogout()

            val intent = Intent(this, OnBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivNotify.setOnClickListener {
            val intent = Intent(this, ShopNotifyActivity::class.java)
            startActivity(intent)
        }

        binding.ivChevronSetupNotify.setOnClickListener {

        }

        binding.layoutPersonalInfo.setOnClickListener{
            val intent = Intent(this, BuyerInfoModifyActivity::class.java)
            this.startActivity(intent)
        }

        binding.layoutPayment.setOnClickListener {
            val intent = Intent(this, BuyerPayMethodActivity::class.java)
            startActivity(intent)
        }
        binding.ivChevronSetupLan.setOnClickListener {

        }
//
//        btn_Login.setOnClickListener {
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }

    }


}