package com.hkshopu.hk.ui.user.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.databinding.ActivityEmailverifyBinding
import com.hkshopu.hk.databinding.ActivityLoginBinding
import com.hkshopu.hk.ui.main.activity.ShopmenuActivity
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.extension.getResColor
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable
import java.util.*
import kotlin.concurrent.schedule


class EmailVerifyActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityEmailverifyBinding
    var number1: String = ""
    var number2: String = ""
    var number3: String = ""
    var number4: String = ""
    var validation: String = ""
    var email: String = ""
    private lateinit var settings: SharedPreferences
    private val VM = AuthVModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailverifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //local資料存取
        settings = this.getSharedPreferences("DATA", 0)
        email = settings.getString("email", "").toString()

        initView()
        initVM()
        initClick()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {
        VM.emailverifyLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.ret_val.toString().equals("驗證成功!")) {

                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT)

                        val intent = Intent(this, ShopmenuActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        val text1: String = it.ret_val.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1,duration1).show()
                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })
        VM.verifycodeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    if (it.ret_val.toString() == "已寄出驗證碼!") {
                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT).show()
                        Toast.makeText(this, "一分鐘後才能再寄送", Toast.LENGTH_SHORT).show()

                    }else {
                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT).show()
                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })

    }

    private fun initView() {
        binding.textViewEmail.text = email
        initEditText()
        initClick()
        KeyboardUtil.showKeyboard(binding.edtAuthenticate01)

    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {

            finish()
        }


        binding.btnAuthenticate.setOnClickListener {

            number1 = binding.edtAuthenticate01.text.toString()
            number2 = binding.edtAuthenticate02.text.toString()
            number3 = binding.edtAuthenticate03.text.toString()
            number4 = binding.edtAuthenticate04.text.toString()

            validation = number1 + number2 +number3 + number4

            binding.btnResend.setTextColor(Color.parseColor("#48484A"))
            binding.btnResend.isEnabled = false
            Timer().schedule(60000) {
                binding.btnResend.setTextColor(Color.parseColor("#1DBCCF"))
                binding.btnResend.isEnabled = true
            }


            VM.emailverify(this,email!!,validation)


        }
        binding.tvSkip.setOnClickListener {
            val intent = Intent(this, ShopmenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.termsOfService.setOnClickListener {

            val intent = Intent(this, TermsOfServiceActivity::class.java)
            startActivity(intent)

        }

        binding.btnResend.setOnClickListener {

            binding.btnResend.setTextColor(Color.parseColor("#48484A"))
            binding.btnResend.isEnabled = false
            Timer().schedule(60000) {
                binding.btnResend.setTextColor(Color.parseColor("#1DBCCF"))
                binding.btnResend.isEnabled = true
            }

            VM.verifycode(this, email!!)

        }


    }

    private fun initEditText() {
        binding.edtAuthenticate01.addTextChangedListener(this)
        binding.edtAuthenticate01.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
        binding.edtAuthenticate02.addTextChangedListener(this)
        binding.edtAuthenticate02.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
        binding.edtAuthenticate03.addTextChangedListener(this)
        binding.edtAuthenticate03.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
        binding.edtAuthenticate04.addTextChangedListener(this)
        binding.edtAuthenticate04.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))


        setNextFocus(binding.edtAuthenticate01,binding.edtAuthenticate02)
        setNextFocus(binding.edtAuthenticate02,binding.edtAuthenticate03)
        setNextFocus(binding.edtAuthenticate03,binding.edtAuthenticate04)
    }

    fun setNextFocus(nowEdit: EditText, nextEdit: EditText) {
        nowEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (nowEdit.getText().toString().length == 1) {
                    nextEdit.requestFocus()
                }

            }
        })
    }

}