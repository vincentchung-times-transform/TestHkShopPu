package com.hkshopu.hk.ui.main.activity

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventPhoneShow
import com.hkshopu.hk.component.EventShopCatSelected
import com.hkshopu.hk.databinding.ActivityPhoneeditBinding
import com.hkshopu.hk.databinding.ActivityShippingfeeBinding
import com.hkshopu.hk.databinding.ActivityShopinfomodifyBinding
import com.hkshopu.hk.databinding.ActivityShopnameeditBinding

import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher


class PhoneEditActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityPhoneeditBinding

    private val VM = AuthVModel()
    var phone_country: String = ""
    var phone_number: String = ""
    var phone: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneeditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initClick()

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(p0: Editable?) {
        phone_number = binding.editShopphoneNumber.text.toString()
        phone_country = binding.editShopphoneCountry.diaL_CODE
        phone = phone_country + phone_number
    }

    private fun initView() {
        binding.editShopphoneNumber.addTextChangedListener(this)
        binding.editShopphoneCountry.addTextChangedListener(this)
        binding.layoutPhoneEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        val phoneShow: Boolean = MMKV.mmkvWithID("http").getBoolean("PhoneShow", false)
        if (phoneShow) {
            binding.switchview.openSwitcher()
        }

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

        binding.tvSave.setOnClickListener {

        }
        binding.switchview.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                MMKV.mmkvWithID("http").putBoolean("PhoneShow", isOpen)
                    .putString("phone", phone)
            }
        })


    }


}