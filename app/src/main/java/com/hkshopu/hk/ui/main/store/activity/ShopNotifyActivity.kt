package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.databinding.ActivityAdvertisementBinding
import com.hkshopu.hk.databinding.ActivityShopattentionBinding
import com.hkshopu.hk.databinding.ActivityShopnotifyBinding
import com.hkshopu.hk.databinding.ActivityShoppreviewBinding

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