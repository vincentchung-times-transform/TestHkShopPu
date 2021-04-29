package com.hkshopu.hk.ui.main.store.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.databinding.*

import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher


class EmailAdd2Activity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityEmailadd2Binding

    private val VM = AuthVModel()
    var getstring : String? = null
    var email: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailadd2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initClick()

    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(p0: Editable?) {
        email = binding.etAddEmail.text.toString()
    }
    private fun initView() {
        binding.layoutAddEmail.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        val emailShow:Boolean = MMKV.mmkvWithID("http").getBoolean("EmailShow",false)
        if(emailShow){
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
                MMKV.mmkvWithID("http").putBoolean("EmailShow", isOpen)
                    .putString("email",email)
            }
        })


    }



}