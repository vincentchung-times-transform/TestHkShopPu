package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.databinding.ActivityAdvertisementBinding
import com.hkshopu.hk.databinding.ActivityHelpcenterBinding
import com.hkshopu.hk.databinding.ActivityShopattentionBinding

//import kotlinx.android.synthetic.main.activity_main.*

class HelpCenterActivity : BaseActivity() {
    private lateinit var binding: ActivityHelpcenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpcenterBinding.inflate(layoutInflater)
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