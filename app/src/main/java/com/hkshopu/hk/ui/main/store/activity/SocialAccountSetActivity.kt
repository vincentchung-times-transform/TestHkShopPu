package com.hkshopu.hk.ui.main.store.activity

import android.os.Bundle
import androidx.appcompat.app.AlertDialog

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.databinding.*

import com.zilchzz.library.widgets.EasySwitcher


class SocialAccountSetActivity : BaseActivity() {
    private lateinit var binding: ActivitySocialacntsetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySocialacntsetupBinding.inflate(layoutInflater)
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
        binding.ivBack.setOnClickListener {

            finish()
        }


        binding.switchviewFb.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if(isOpen){

                    AlertDialog.Builder(this@SocialAccountSetActivity)
                        .setTitle("「HKSHOPU」想要使用「facebook.com」登入")
                        .setMessage("這會讓App和網路分享關於您的資訊。")
                        .setPositiveButton("繼續"){
                            // 此為 Lambda 寫法
                                dialog, which ->
                        }
                        .setNegativeButton("取消"){
                                dialog, which -> dialog.cancel()
                            binding.switchviewFb.closeSwitcher()
                        }
                        .show()
                }
            }
        })

        binding.switchviewIg.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if(isOpen){

//                    AlertDialog.Builder(this@SocialAccountSetActivity)
//                        .setTitle("「HKSHOPU」想要使用「facebook.com」登入")
//                        .setMessage("這會讓App和網路分享關於您的資訊。")
//                        .setPositiveButton("繼續"){
//                            // 此為 Lambda 寫法
//                                dialog, which ->
//                        }
//                        .setNegativeButton("取消"){
//                                dialog, which -> dialog.cancel()
//                            binding.switchviewFb.closeSwitcher()
//                        }
//                        .show()
                }
            }
        })

    }



}