package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventLogout
import com.HKSHOPU.hk.databinding.*
import com.tencent.mmkv.MMKV

//import kotlinx.android.synthetic.main.activity_main.*

class AccountSetupActivity : BaseActivity() {
    private lateinit var binding: ActivityAccountSetUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSetUpBinding.inflate(layoutInflater)
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

        binding.layoutShopInfo.setOnClickListener {
            val intent = Intent(this, ShopInfoModifyActivity::class.java)
            startActivity(intent)
        }

        binding.layoutShopAddress.setOnClickListener {
            val intent = Intent(this, ShopAddressListActivity::class.java)
            startActivity(intent)
        }

        binding.layoutShopBankaccount.setOnClickListener {
            val intent = Intent(this, BankListActivity::class.java)
            startActivity(intent)
        }

        binding.ivChevronSetupNotify.setOnClickListener {

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