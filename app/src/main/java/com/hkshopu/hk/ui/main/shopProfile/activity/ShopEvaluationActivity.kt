package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.ActivityShopevaluationBinding

//import kotlinx.android.synthetic.main.activity_main.*

class ShopEvaluationActivity : BaseActivity() {
    private lateinit var binding: ActivityShopevaluationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopevaluationBinding.inflate(layoutInflater)
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