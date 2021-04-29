package com.hkshopu.hk.ui.main.store.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.databinding.ActivityShopnameeditBinding

import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil


class ShopNameEditActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityShopnameeditBinding

    private val VM = AuthVModel()
    var shopName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopnameeditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initClick()

    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(p0: Editable?) {
        shopName = binding.etShopnameedit.text.toString()
    }
    private fun initView() {
        binding.layoutShopnameEdit.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
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

    }



}