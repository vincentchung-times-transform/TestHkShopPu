package com.HKSHOPU.hk.ui.onboard.registeration.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.databinding.ActivityEmailverifyBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.onboard.login.activity.LoginActivity
import com.HKSHOPU.hk.ui.onboard.vm.AuthVModel
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule


class EmailVerifyActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityEmailverifyBinding
    var number1: String = ""
    var number2: String = ""
    var number3: String = ""
    var number4: String = ""
    var validation: String = ""
    var user_id: String = ""
    var email: String = ""
    var password: String = ""
    private lateinit var settings: SharedPreferences
    private val VM = AuthVModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailverifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = this.getSharedPreferences("DATA", 0)
        email = settings.getString("email", "").toString()
        password = settings.getString("password", "").toString()

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
                        binding.progressBarEmailVerify.visibility = View.GONE
                        binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE

                        doLogin(email, password)
                    } else {
                        val text1: String = it.ret_val.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1,duration1).show()
                        binding.progressBarEmailVerify.visibility = View.GONE
                        binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
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
        binding.progressBarEmailVerify.visibility = View.GONE
        binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
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
            binding.progressBarEmailVerify.visibility = View.VISIBLE
            binding.ivLoadingBackgroundEmailVerify.visibility = View.VISIBLE

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

    private fun doLogin(email: String, password: String) {
        val url = ApiConstants.API_HOST+"/user/loginProcess/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("doLogin", "返回資料 resStr：" + resStr)
                    Log.d("doLogin", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("登入成功!")) {
                        user_id = json.getString("user_id")

                        doBackendUserIDValidation(user_id)

                        Log.d("doLogin", "ret_val: ${ret_val.toString()}")
                        runOnUiThread {
                            binding.progressBarEmailVerify.visibility = View.GONE
                            binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                        }

                    } else {

                        Log.d("doLogin", "ret_val: ${ret_val.toString()}")
                        runOnUiThread {
                            Toast.makeText(this@EmailVerifyActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarEmailVerify.visibility = View.GONE
                            binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                        }

                    }

                } catch (e: JSONException) {
                    Log.d("doLogin", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@EmailVerifyActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarEmailVerify.visibility = View.GONE
                        binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doLogin", "IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@EmailVerifyActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarEmailVerify.visibility = View.GONE
                        binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doLogin", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@EmailVerifyActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    binding.progressBarEmailVerify.visibility = View.GONE
                    binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                }
            }
        })
        web.Do_Login(url, email, password)
    }
    private fun doBackendUserIDValidation(user_id: String) {

        var url = ApiConstants.API_PATH+"user/user_id_validation/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    Log.d("doBackendUserIDValidation", "返回資料 resStr：" + resStr)
                    Log.d("doInsertAuditLog", "返回資料 ret_val：" + json.get("ret_val"))

                    if (status == 0) {
                        if (ret_val.equals("該使用者存在!")){

                            MMKV.mmkvWithID("http").putString("UserId", user_id)
                                .putString("Email",email)

                            Log.d("doBackendUserIDValidation", "該使用者存在!")
                            runOnUiThread {
                                binding.progressBarEmailVerify.visibility = View.GONE
                                binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                            }

                            val intent = Intent(this@EmailVerifyActivity, ShopmenuActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            Log.d("doBackendUserIDValidation", "該使用者不存在!")
                            runOnUiThread {
                                Toast.makeText(this@EmailVerifyActivity, "該使用者不存在!", Toast.LENGTH_SHORT).show()
                                binding.progressBarEmailVerify.visibility = View.GONE
                                binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                            }

                            val intent = Intent(this@EmailVerifyActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doBackendUserIDValidation", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@EmailVerifyActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarEmailVerify.visibility = View.GONE
                        binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                    }

                    val intent = Intent(this@EmailVerifyActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doBackendUserIDValidation", "IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@EmailVerifyActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarEmailVerify.visibility = View.GONE
                        binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                    }

                    val intent = Intent(this@EmailVerifyActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doBackendUserIDValidation", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@EmailVerifyActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    binding.progressBarEmailVerify.visibility = View.GONE
                    binding.ivLoadingBackgroundEmailVerify.visibility = View.GONE
                }
                val intent = Intent(this@EmailVerifyActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }
        })
        web.doBackendUserIDValidation(url, user_id)
    }
}