package com.HKSHOPU.hk.ui.user.activity

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityRegisterBinding
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.HKSHOPU.hk.widget.view.disable
import com.HKSHOPU.hk.widget.view.enable


class RegisterActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityRegisterBinding
    private val VM = AuthVModel()
    var email: String = ""
    var gender: String = ""
    val genders = arrayListOf("Male", "Female")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        initVM()
        initClick()
    }

    override fun afterTextChanged(s: Editable?) {
        val username = binding.editUserName.text.toString()
        val email = binding.editEmailReg.text.toString()
        val password = binding.passwordReg.text.toString()
        val passwordcof = binding.passwordConf.text.toString()
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordcof.isEmpty()) {
            binding.btnSignUp.disable()
        } else {
            binding.btnSignUp.enable()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {
        VM.registerLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
//                    if (url.isNotEmpty()) {
//                        toast("登录成功")
//
//                    }

                    finish()
                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })
    }

    private fun initView() {
        binding.layoutRegister.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> KeyboardUtil.hideKeyboard(v)
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        binding.genderSpinner.adapter = adapter
        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                Log.d("RegisterActivity", "你選的是" + genders[pos])
                gender = genders[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        initEditText()
        initClick()
        binding.editUserName.requestFocus()
        KeyboardUtil.showKeyboard(binding.editUserName)
        binding.passwordReg.setFilters(arrayOf<InputFilter>(LengthFilter(16)))
        binding.editmobile1.setFilters(arrayOf<InputFilter>(LengthFilter(8)))

    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {

            finish()
        }
        binding.btnSignup.setOnClickListener {
            val account_name = binding.editUserName.text.toString()
            val email = binding.editEmailReg.text.toString()
            val password = binding.passwordReg.text.toString()
            val confirm_password = binding.passwordConf.text.toString()
            val first_name = binding.editFirstName.text.toString()
            val last_name = binding.editlastName.text.toString()
            val birthday = binding.editbirth.text.toString()
            val phone = binding.editmobile.text.toString() + binding.editmobile1.text.toString()
            val address = binding.editaddress.text.toString()
            if (confirm_password.equals(password)) {
                VM.register(
                    this,
                    account_name,
                    email,
                    password,
                    confirm_password,
                    first_name,
                    last_name,
                    gender,
                    birthday,
                    phone,
                    address,"","","","","",""
                )
            } else {
//                toast(R.string.please_confirm_password)
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, R.string.please_confirm_password, duration)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.show()
                binding.btnSignUp.disable()
            }

        }

    }

    private fun initEditText() {
        binding.editEmailReg.addTextChangedListener(this)
        binding.passwordReg.addTextChangedListener(this)
        binding.passwordConf.addTextChangedListener(this)
    }

}