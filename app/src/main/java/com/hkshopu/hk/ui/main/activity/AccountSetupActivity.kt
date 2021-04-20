package com.hkshopu.hk.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ResourceMerchant
import com.hkshopu.hk.data.bean.ResourceStore
import com.hkshopu.hk.databinding.*
import com.hkshopu.hk.ui.user.activity.LoginActivity
import com.hkshopu.hk.ui.user.activity.RegisterActivity
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.ui.user.vm.ShopVModel

//import kotlinx.android.synthetic.main.activity_main.*

class AccountSetupActivity : BaseActivity() {
    private lateinit var binding: ActivityAccountsetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initClick()

    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivChevronShopInfo.setOnClickListener {
            val intent = Intent(this, ShopInfoModifyActivity::class.java)
            startActivity(intent)
        }
        binding.ivChevronShopAddress.setOnClickListener {

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