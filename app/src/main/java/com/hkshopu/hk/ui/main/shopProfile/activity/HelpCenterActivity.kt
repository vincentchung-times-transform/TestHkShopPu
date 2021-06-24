package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.ActivityHelpcenterBinding

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