package com.hkshopu.hk.ui.user.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
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
        settings = this.getSharedPreferences("DATA", 0)
        email = settings.getString("email", "").toString()
        initView()
        initVM()
        initClick()
    }

    override fun afterTextChanged(s: Editable?) {
        number1 = binding.editFirstNumber.text.toString()
        number2 = binding.edit2ndNumber.text.toString()
        number3 = binding.edit3rdNumber.text.toString()
        number4 = binding.edit4thNumber.text.toString()

        validation = number1 + number2 +number3 + number4
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {
        VM.emailverifyLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.data.toString().equals("驗證成功!")) {
                        val intent = Intent(this, ShopmenuActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        val text1: String = it.data.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1,duration1)
                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })
    }

    private fun initView() {
        binding.tvEmailTo.text = email
        initEditText()
        initClick()
        KeyboardUtil.showKeyboard(binding.editFirstNumber)

    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {

            finish()
        }


        binding.tvVerify.setOnClickListener {
           binding.tvResend.setTextColor(Color.parseColor("#48484A"))
            Timer().schedule(60000) {
                binding.tvResend.setTextColor(Color.parseColor("#1DBCCF"))
            }

            VM.emailverify(this,email!!,validation)
        }
        binding.tvSkip.setOnClickListener {
            val intent = Intent(this, ShopmenuActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun initEditText() {
        binding.editFirstNumber.addTextChangedListener(this)
        binding.editFirstNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
        binding.edit2ndNumber.addTextChangedListener(this)
        binding.edit2ndNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
        binding.edit3rdNumber.addTextChangedListener(this)
        binding.edit3rdNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
        binding.edit4thNumber.addTextChangedListener(this)
        binding.edit4thNumber.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
    }

}