package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.databinding.*
import com.hkshopu.hk.ui.user.activity.RetrieveEmailVerifyActivity

import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher


class EmailAdd1Activity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityEmailadd1Binding

    private val VM = AuthVModel()
    var passwordCheck: String = ""
    val email = MMKV.mmkvWithID("http").getString("Email", "");
    val password = MMKV.mmkvWithID("http").getString("Password", "");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailadd1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initEditText()
        initClick()

    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(p0: Editable?) {
        passwordCheck = binding.etPassword.text.toString()

    }
    private fun initView() {
        binding.layoutEmailAdd.setOnClickListener {
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
    private fun initEditText() {

//        binding.editEmail.addTextChangedListener(this)
        binding.etPassword.addTextChangedListener(this)

    }
    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.showPassword.setOnClickListener {
            binding.showPassword.visibility = View.INVISIBLE
            binding.hidePassword.visibility = View.VISIBLE
            binding.etPassword.transformationMethod= PasswordTransformationMethod.getInstance()
        }

        //hide hidePassword eye and showPassword eye show
        binding.hidePassword.setOnClickListener {
            binding.hidePassword.visibility = View.INVISIBLE
            binding.showPassword.visibility = View.VISIBLE
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }

        binding.tvGoOn.setOnClickListener {
            Log.d("EmailAdd1Activity", "PassWordCheck：" + passwordCheck)
            if(passwordCheck.equals(password)) {
                val intent = Intent(this, EmailAdd2Activity::class.java)
                startActivity(intent)
                finish()
            }else{
                val msg = "請輸入正確密碼"
                runOnUiThread{
                    toast(msg)
                }

            }
        }
        binding.goRetrieve.setOnClickListener {
            //傳送email address給Retrieve Page
            var bundle = Bundle()
            bundle.putString("email", email)

            val intent = Intent(this, RetrieveEmailVerifyActivity::class.java)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
            finish()
        }
        binding.switchview.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if(isOpen){

                }
            }
        })

    }



}