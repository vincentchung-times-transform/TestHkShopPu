package com.hkshopu.hk.ui.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.databinding.ActivityLoginPasswordBinding
import com.hkshopu.hk.databinding.ActivityNewPasswordBinding
import com.hkshopu.hk.ui.user.vm.AuthVModel

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding
    private val VM = AuthVModel()

    var getstring : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initVM()
        initIntent()
    }


    private fun initIntent() {
        //取得LoginPage傳來的email address
        getstring = intent.getBundleExtra("bundle")?.getString("email")
    }


    private fun initVM() {

        VM.resetPasswordLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    Toast.makeText(this, it.data.toString(), Toast.LENGTH_SHORT ).show()

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })


    }


    private fun initView() {

        initEditText()
        initClick()

    }


    private fun initClick() {

        var password = binding.edtViewPasswordFirstInput.text.toString()
        var confirm_password = binding.edtViewPasswordSecondInput.text.toString()

        binding.btnLogin.setOnClickListener {
            VM.reset_password(this, getstring!!, password!!, confirm_password!!)
        }


    }

    private fun initEditText() {



    }

}