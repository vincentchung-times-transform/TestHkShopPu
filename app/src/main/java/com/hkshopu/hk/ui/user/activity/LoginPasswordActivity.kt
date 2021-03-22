package com.hkshopu.hk.ui.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.databinding.ActivityLoginBinding
import com.hkshopu.hk.databinding.ActivityLoginPasswordBinding
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable

class LoginPasswordActivity : BaseActivity(), TextWatcher {

    private lateinit var binding: ActivityLoginPasswordBinding
    private val VM = AuthVModel()

    var getstring : String? = null
    var password : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)



        InitIntent()
        initView()
        initVM()
        initClick()
    }

    private fun initVM() {
        VM.loginLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    Toast.makeText(this, it.data.toString(), Toast.LENGTH_SHORT ).show()


                    if (it.data.toString() == "登入成功!") {

                    }else if (it.data.toString() == "電子郵件或密碼未填寫!") {

                    }else if (it.data.toString() == "電子郵件錯誤!") {

                    }else if (it.data.toString() == "密碼錯誤!") {


                    }else {

                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })


    }

    private fun InitIntent() {

        //取得LoginPage傳來的email address
        getstring = intent.getBundleExtra("bundle")?.getString("email")

    }
    private fun initView() {

        binding.txtViewLoginEmail.setText(getstring!!)

        initEditText()
        initClick()

    }

    private fun initClick() {

        binding.goRetrieve.setOnClickListener {


            //傳送email address給Retrieve Page
            var bundle = Bundle()
            bundle.putString("email", getstring)

            val intent = Intent(this, Retrieve::class.java)
            intent.putExtra("bundle", bundle)

            startActivity(intent)
        }

        //hide showPassword eye and hidePassword eye show
        binding.showPassword.setOnClickListener {
            binding.showPassword.visibility = View.INVISIBLE
            binding.hidePassword.visibility = View.VISIBLE
            binding.edtPassword.transformationMethod= PasswordTransformationMethod.getInstance()
        }

        //hide hidePassword eye and showPassword eye show
        binding.hidePassword.setOnClickListener {
            binding.hidePassword.visibility = View.INVISIBLE
            binding.showPassword.visibility = View.VISIBLE
            binding.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }
        binding.btnLogin.setOnClickListener {


            password = binding.edtPassword.text.toString()
//            val password = binding.password1.text.toString()

            VM.login(this, getstring!!, password!!)

        }

    }

    private fun initEditText() {

//        binding.editEmail.addTextChangedListener(this)
        binding.edtPassword.addTextChangedListener(this)

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit


    override fun afterTextChanged(s: Editable?) {
//        val email = binding.editEmail.text.toString()
        password = binding.edtPassword.text.toString()
        if (password!!.isEmpty()) {
            binding.btnLogin.disable()
        } else {
            binding.btnLogin.enable()
        }
    }
}