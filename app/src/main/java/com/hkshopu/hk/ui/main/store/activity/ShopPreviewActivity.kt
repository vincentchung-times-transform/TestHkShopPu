package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.databinding.ActivityAdvertisementBinding
import com.hkshopu.hk.databinding.ActivityShopattentionBinding
import com.hkshopu.hk.databinding.ActivityShoppreviewBinding

//import kotlinx.android.synthetic.main.activity_main.*

class ShopPreviewActivity : BaseActivity() {
    private lateinit var binding: ActivityShoppreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppreviewBinding.inflate(layoutInflater)
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

    }


}