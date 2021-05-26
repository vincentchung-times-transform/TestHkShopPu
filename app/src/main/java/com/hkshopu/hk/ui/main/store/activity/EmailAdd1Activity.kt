package com.hkshopu.hk.ui.main.store.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.core.widget.doAfterTextChanged

import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.databinding.*
import com.hkshopu.hk.ui.user.activity.RetrieveEmailVerifyActivity

import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher


class EmailAdd1Activity : BaseActivity(){
    private lateinit var binding: ActivityEmailadd1Binding

    private val VM = AuthVModel()
    var passwordCheck: String = ""
    var address_id:String = ""
    var email:String =""
    var email_on:String = ""
    val password = MMKV.mmkvWithID("http").getString("Password", "");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailadd1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        address_id = intent.getBundleExtra("bundle")!!.getString("address_id","")
        email = intent.getBundleExtra("bundle")!!.getString("email_old","")
        email_on = intent.getBundleExtra("bundle")!!.getString("email_on","")
        initView()
        initVM()
        initEditText()
        initClick()

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

        binding.etPassword.doAfterTextChanged {
            passwordCheck = binding.etPassword.text.toString()
        }

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
                var bundle = Bundle()
                bundle.putString("address_id",address_id)
                bundle.putString("email_old",email)
                bundle.putString("email_on", email_on)
                val intent = Intent(this, EmailAdd2Activity::class.java)
                intent.putExtra("bundle",bundle)
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