package com.HKSHOPU.hk.ui.user.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityLoginPasswordBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.HKSHOPU.hk.widget.view.disable
import com.HKSHOPU.hk.widget.view.enable
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule

class LoginPasswordActivity : BaseActivity(), TextWatcher {

    private lateinit var binding: ActivityLoginPasswordBinding
    private val VM = AuthVModel()

    var user_id: String = ""
    var email: String = ""
    var password : String = ""
    private lateinit var settings: SharedPreferences
    lateinit var settings_rememberPassword: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarLoginPassword.visibility = View.GONE
        binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE


        initDatasFromBundle()
        initView()
        initVM()

        binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())
    }

    //settings of textWatcher
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


    fun initDatasFromBundle(){

        //local資料存取
        settings = this.getSharedPreferences("DATA", 0)
        email = settings.getString("email", "").toString()

    }

    private fun initVM() {

        VM.verifycodeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    if (it.ret_val.toString().equals("已寄出驗證碼!")) {

                        binding.progressBarLoginPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE

                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()
                        Log.d("verifycodeLiveData", "ret_val: ${it.ret_val.toString()}")

                        val intent = Intent(this, RetrieveEmailVerifyActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        binding.progressBarLoginPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE

                        val text1: String = it.ret_val.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1,duration1).show()
                        Log.d("verifycodeLiveData", "ret_val: ${text1}")

                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })
    }

    private fun initView() {

        binding.txtViewLoginEmail.setText(email!!)
        binding.titleBack.setOnClickListener {

            finish()
        }

        initEditText()
        initClick()

    }

    private fun initClick() {

        binding.goRetrieve.setOnClickListener {

            binding.progressBarLoginPassword.visibility = View.VISIBLE
            binding.ivLoadingBackgroundLoginPassword.visibility = View.VISIBLE

            binding.goRetrieve.setTextColor(Color.parseColor("#8E8E93"))
            binding.goRetrieve.isEnabled = false

            Timer().schedule(60000) {
                binding.goRetrieve.setTextColor(Color.parseColor("#000000"))
                binding.goRetrieve.isEnabled = true
            }

            VM.verifycode(this, email!!)

        }

        //hide showPassword eye and hidePassword eye show
        binding.showPassBtnLogin.setOnClickListener {
            ShowHidePass(it)
        }

        binding.btnLogin.setOnClickListener {

            binding.progressBarLoginPassword.visibility = View.VISIBLE
            binding.ivLoadingBackgroundLoginPassword.visibility = View.VISIBLE

            password = binding.edtPassword.text.toString()

            val url = ApiConstants.API_HOST+"/user/loginProcess/"
            doLogin(url, email!!, password!!)

        }

    }

    private fun initEditText() {

        binding.edtPassword.addTextChangedListener(this)
        binding.edtPassword.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(16)))


        binding.edtPassword.singleLine = true
        binding.edtPassword.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    password = binding.edtPassword.text.toString()

                    binding.edtPassword.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.edtPassword)

                    true
                }

                else -> false
            }
        }

    }



    fun ShowHidePass(view: View) {
        if (view.getId() == R.id.show_pass_btn_login) {
            if (binding.edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeon)
                //Show Password
                binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())

            } else {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeoff)
                //Hide Password
                binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())

            }
        }
    }

    private fun doLogin(url: String, email: String, password: String) {

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

                        runOnUiThread {
                            Log.d("doLogin", "ret_val: ${ret_val.toString()}")
                            binding.progressBarLoginPassword.visibility = View.GONE
                            binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                        }

                    } else {

                        runOnUiThread {
                            Toast.makeText(this@LoginPasswordActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            Log.d("doLogin", "ret_val: ${ret_val.toString()}")
                            binding.progressBarLoginPassword.visibility = View.GONE
                            binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                        }
                    }
//                        initRecyclerView()


                } catch (e: JSONException) {

                    runOnUiThread {
                        Toast.makeText(this@LoginPasswordActivity, "網路異常請重新嘗試登入", Toast.LENGTH_SHORT).show()
                        Log.d("doLogin", "JSONException: ${e.toString()}")
                        binding.progressBarLoginPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                    runOnUiThread {
                        Toast.makeText(this@LoginPasswordActivity, "網路異常請重新嘗試登入", Toast.LENGTH_SHORT).show()
                        Log.d("doLogin", "IOException: ${e.toString()}")
                        binding.progressBarLoginPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

                runOnUiThread {
                    Toast.makeText(this@LoginPasswordActivity, "網路異常請重新嘗試登入", Toast.LENGTH_SHORT).show()
                    Log.d("doLogin", "ErrorResponse: ${ErrorResponse.toString()}")
                    binding.progressBarLoginPassword.visibility = View.GONE
                    binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
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

                    Log.d("doBackendUserIDValidation", "返回資料 resStr：" + resStr)
//                    Log.d("doInsertAuditLog", "返回資料 ret_val：" + json.get("ret_val"))

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        if (ret_val.equals("該使用者存在!")){

                            MMKV.mmkvWithID("http").putString("UserId", user_id)
                                .putString("Email",email)
                                .putString("Password",password)

                            settings_rememberPassword = getSharedPreferences("rememberPassword", 0)
                            val editor : SharedPreferences.Editor = settings_rememberPassword.edit()
                            editor.apply {
                                putString("rememberPassword", "true")
                            }.apply()

                            Log.d("doBackendUserIDValidation", "該使用者存在!")
                            runOnUiThread {
                                binding.progressBarLoginPassword.visibility = View.GONE
                                binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                            }

                            val intent = Intent(this@LoginPasswordActivity, ShopmenuActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{

                            runOnUiThread {
                                binding.progressBarLoginPassword.visibility = View.GONE
                                binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE

                                Toast.makeText(this@LoginPasswordActivity, "該使用者不存在!", Toast.LENGTH_SHORT).show()
                                Log.d("doBackendUserIDValidation", "該使用者不存在!")

                            }

                            val intent = Intent(this@LoginPasswordActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                        }

                    }

                } catch (e: JSONException) {

                    runOnUiThread {

                        Toast.makeText(this@LoginPasswordActivity, "網路異常請重新嘗試登入", Toast.LENGTH_SHORT).show()
                        Log.d("doBackendUserIDValidation", "JSONException: ${e.toString()}")
                        binding.progressBarLoginPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                    }

                    val intent = Intent(this@LoginPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (e: IOException) {
                    e.printStackTrace()

                    runOnUiThread {
                        Toast.makeText(this@LoginPasswordActivity, "網路異常請重新嘗試登入", Toast.LENGTH_SHORT).show()
                        Log.d("doBackendUserIDValidation", "IOException: ${e.toString()}")
                        binding.progressBarLoginPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                    }
                    val intent = Intent(this@LoginPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@LoginPasswordActivity, "網路異常請重新嘗試登入", Toast.LENGTH_SHORT).show()
                    Log.d("doBackendUserIDValidation", "ErrorResponse: ${ErrorResponse.toString()}")
                    binding.progressBarLoginPassword.visibility = View.GONE
                    binding.ivLoadingBackgroundLoginPassword.visibility = View.GONE
                }
                val intent = Intent(this@LoginPasswordActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }
        })
        web.doBackendUserIDValidation(url, user_id)
    }

}