package com.HKSHOPU.hk.ui.main.buyer.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.*

import com.HKSHOPU.hk.ui.user.activity.RetrieveEmailVerifyActivity
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.paypal.android.sdk.payments.*
import com.tencent.mmkv.MMKV

import java.util.*
import kotlin.concurrent.schedule

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerPWchange1Activity : BaseActivity() {

    private lateinit var binding: ActivityUserpwchange1Binding
    var password_old = MMKV.mmkvWithID("http").getString("Password", "")
    var password_now = ""
    var email = MMKV.mmkvWithID("http").getString("Email", "")
    private val VM = AuthVModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserpwchange1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initClick()
        initVM()
    }


    private fun initView() {
        binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
        binding.etPassword.doAfterTextChanged {
            password_now = binding.etPassword.text.toString()
        }

    }

    private fun initClick() {
        binding.layoutPwchange.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        binding.ivBack.setOnClickListener {

            finish()
        }
        binding.showPassBtn.setOnClickListener {
            ShowHidePass(it)
        }
        binding.tvGoOn.setOnClickListener {
            if (password_now.equals(password_old)) {
                val intent = Intent(this@BuyerPWchange1Activity, BuyerPWchange2Activity::class.java)
                startActivity(intent)
                finish()
            } else {
                val msg = "輸入值與舊密碼不一致"
                runOnUiThread{
                    toast(msg)
                }
            }

        }

        binding.goRetrieve.setOnClickListener {

            binding.goRetrieve.setTextColor(Color.parseColor("#8E8E93"))
            binding.goRetrieve.isEnabled = false
            Timer().schedule(60000) {
                binding.goRetrieve.setTextColor(Color.parseColor("#000000"))
                binding.goRetrieve.isEnabled = true
            }

            VM.verifycode(this, email!!)
        }

    }

    fun ShowHidePass(view: View) {
        if (view.getId() == R.id.show_pass_btn) {
            if (binding.etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeon)
                //Show Password
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())

            } else {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeoff)
                //Hide Password
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())

            }
        }
    }
    private fun initVM() {

        VM.verifycodeLiveData.observe(this, androidx.lifecycle.Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.ret_val.toString().equals("已寄出驗證碼!")) {

                        binding.goRetrieve.setTextColor(Color.parseColor("#48484A"))
                        Timer().schedule(60000) {
                            binding.goRetrieve.setTextColor(Color.parseColor("#1DBCCF"))
                        }

                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()
                        val intent = Intent(this, RetrieveEmailVerifyActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        val text1: String = it.ret_val.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1, duration1).show()
                    }
                }
            }
        })
    }
    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }


}