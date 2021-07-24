package com.HKSHOPU.hk.ui.main.seller.shop.activity

import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.ActivityShoppreviewCommingsoonBinding

//import kotlinx.android.synthetic.main.activity_main.*

class ShopPreviewCommingSoonActivity : BaseActivity() {
    private lateinit var binding: ActivityShoppreviewCommingsoonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppreviewCommingsoonBinding.inflate(layoutInflater)
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