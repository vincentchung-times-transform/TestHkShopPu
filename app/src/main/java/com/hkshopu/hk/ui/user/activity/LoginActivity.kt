package com.hkshopu.hk.ui.user.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.databinding.ActivityLoginBinding
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable



class LoginActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 900
    var email: String = ""

    private val VM = AuthVModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //remember me
        val sharedPreferences : SharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE)
        val checkRememberMe : String? = sharedPreferences.getString("rememberMe", "")
        if (checkRememberMe == "true") {
            //transfer to next page
        }


        initEditText()
        initView()
        initClick()
        initVM()

    }

    override fun afterTextChanged(s: Editable?) {
        email = binding.editEmail.text.toString()
//        val password = binding.password1.text.toString()
        if (email.isEmpty()) {
            binding.btnNextStep.isEnabled = false

        } else {
            binding.btnNextStep.isEnabled = true
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {
        VM.loginLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                   Toast.makeText(this, it.data.toString(), Toast.LENGTH_SHORT ).show()

                    var bundle = Bundle()
                        bundle.putString("email", email)

                        val intent = Intent(this, LoginPasswordActivity::class.java)
                        intent.putExtra("bundle", bundle)

                        startActivity(intent)

                    if (it.data.toString() == "登入成功!") {

                    }else if (it.data.toString() == "電子郵件或密碼未填寫!") {

                    }else if (it.data.toString() == "電子郵件錯誤!") {

                    }else if (it.data.toString() == "密碼錯誤!") {

//                        var bundle = Bundle()
//                        bundle.putString("email", email)
//
//                        val intent = Intent(this, LoginPasswordActivity::class.java)
//                        intent.putExtra("bundle", bundle)
//
//                        startActivity(intent)


                    }else {

                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })


    }

    private fun initView() {

        initClick()
        if (email.isNotEmpty()) {
            binding.editEmail.setText(email)
//            binding.password1.requestFocus()
//            KeyboardUtil.showKeyboard(binding.password1)

        }

        //hide hidePassword eye and showPassword eye (default)
//        binding.hidePassword.visibility = View.INVISIBLE

    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {

            finish()
        }
        binding.btnNextStep.setOnClickListener {

            email = binding.editEmail.text.toString()
//            val password = binding.password1.text.toString()
            VM.login(this, email, "checkfortheemail")

        }

        binding.checkBoxStayLogin.setOnClickListener {
            if (binding.checkBoxStayLogin.isChecked()) {
                val sharedPreferences : SharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply {
                    putString("rememberMe", "true")
                }.apply()

            }else{
                val sharedPreferences : SharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply {
                    putString("rememberMe", "false")
                }.apply()
            }
        }


        binding.btnGoogleLogin.setOnClickListener {
                GoogleSignIn()
        }



    }

    private fun initEditText() {
        binding.editEmail.addTextChangedListener(this)
//        binding.password1.addTextChangedListener(this)
    }

    private fun GoogleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

}