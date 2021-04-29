package com.hkshopu.hk.ui.main.product.activity

import android.content.Intent
import android.os.Bundle

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.databinding.ActivityShippingfeeBinding
import com.hkshopu.hk.ui.main.store.activity.ShopmenuActivity

import com.hkshopu.hk.ui.user.vm.AuthVModel


class ShippingFeeActivity : BaseActivity() {
    private lateinit var binding: ActivityShippingfeeBinding

    private val VM = AuthVModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShippingfeeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initVM()
        initClick()

    }

    private fun initVM() {
//        VM.socialloginLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//                    if (url.isNotEmpty()) {
//                        toast("登录成功")
//
//                    }
//
//                    finish()
//                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
//            }
//        })
    }

    private fun initClick() {
        binding.titleBackShippingfee.setOnClickListener {

            finish()
        }

        binding.btnConfirmShipping.setOnClickListener {
            val intent = Intent(this, ShopmenuActivity::class.java)
            startActivity(intent)
        }

        binding.btnCancelShipping.setOnClickListener {
           finish()
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    }
}