package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.ActivityShopnotifyBinding

//import kotlinx.android.synthetic.main.activity_main.*

class ShopNotifyActivity : BaseActivity() {
    private lateinit var binding: ActivityShopnotifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopnotifyBinding.inflate(layoutInflater)
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
        binding.btnReturn.setOnClickListener {
            finish()
        }

    }


}