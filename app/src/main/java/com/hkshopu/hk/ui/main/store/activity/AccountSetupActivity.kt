package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.os.Bundle
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.databinding.*

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