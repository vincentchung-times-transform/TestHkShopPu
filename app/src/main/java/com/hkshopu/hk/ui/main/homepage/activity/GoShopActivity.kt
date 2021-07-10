package com.HKSHOPU.hk.ui.main.homepage.activity

import android.content.Intent
import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.ui.main.homepage.fragment.*
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopNotifyActivity


//import kotlinx.android.synthetic.main.activity_main.*

class GoShopActivity : BaseActivity() {
    private lateinit var binding: ActivityShopcarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopcarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initClick()

    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBackClick.setOnClickListener {
            finish()
        }

        binding.ivNotifyClick.setOnClickListener {
//            val intent = Intent(this@GoShopActivity, FpsPayActivity::class.java)
            val intent = Intent(this@GoShopActivity, ShopNotifyActivity::class.java)
//            val intent = Intent(this@GoShopActivity, UserInfoModifyActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ivGoShop.setOnClickListener {
//            val intent = Intent(this@GoShopActivity, ShopmenuActivity::class.java)
//            startActivity(intent)
//            Log.d("GoShopActivity", "ivGoShop Clicked")
            finish()
        }

    }


}