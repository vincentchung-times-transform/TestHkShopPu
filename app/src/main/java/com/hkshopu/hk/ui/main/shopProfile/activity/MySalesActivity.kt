package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.ActivityMysalesBinding

//import kotlinx.android.synthetic.main.activity_main.*

class MySalesActivity : BaseActivity() {
    private lateinit var binding: ActivityMysalesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMysalesBinding.inflate(layoutInflater)
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