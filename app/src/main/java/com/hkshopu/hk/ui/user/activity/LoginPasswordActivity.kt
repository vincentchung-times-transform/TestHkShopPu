package com.hkshopu.hk.ui.user.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.ActivityLoginPasswordBinding
import com.hkshopu.hk.net.ApiConstants
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.store.activity.ShopmenuActivity
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable
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

    var email: String = ""
    var password : String = ""
    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatasFromBundle()
        initView()
        initEditText()
        initClick()
        initVM()

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
        //Old Login Version
//        VM.loginLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//
//                    if (it.ret_val.toString() == "登入成功!") {
//                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT ).show()
//
//                        var settings_rememberPassword: SharedPreferences = this.getSharedPreferences("rememberPassword", 0)
//                        val editor : SharedPreferences.Editor = settings_rememberPassword.edit()
//                        editor.apply {
//                            putString("rememberPassword", "true")
//                        }.apply()
//
//                        val intent = Intent(this, ShopmenuActivity::class.java)
//                        startActivity(intent)
//
//                    }else {
//                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT ).show()
//                    }
//
//                }
////                Status.Start -> showLoading()
////                Status.Complete -> disLoading()
//            }
//        })

        VM.verifycodeLiveData.observe(this, Observer {
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
                        Toast.makeText(this, text1,duration1).show()
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

            binding.goRetrieve.setTextColor(Color.parseColor("#8E8E93"))
            binding.goRetrieve.isEnabled = false
            Timer().schedule(60000) {
                binding.goRetrieve.setTextColor(Color.parseColor("#000000"))
                binding.goRetrieve.isEnabled = true
            }

            VM.verifycode(this, email!!)
        }

        //hide showPassword eye and hidePassword eye show
        binding.showPassBtn.setOnClickListener {
            ShowHidePass(it)
        }

        binding.btnLogin.setOnClickListener {

            password = binding.edtPassword.text.toString()
            val url = ApiConstants.API_HOST+"/user/loginProcess/"
//            VM.login(this, getstring!!, password!!)

            doLogin(url, email!!, password!!)

//            VM.login(this, email!!, password!!)

        }

    }

    private fun initEditText() {

        binding.edtPassword.addTextChangedListener(this)

        binding.edtPassword.singleLine = true
        binding.edtPassword.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    email = binding.edtPassword.text.toString()

                    binding.edtPassword.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.edtPassword)

                    true
                }

                else -> false
            }
        }

    }



    fun ShowHidePass(view: View) {
        if (view.getId() === R.id.show_pass_btn) {
            if (binding.edtPassword.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
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
                    Log.d("LoginPasswordActivity", "返回資料 resStr：" + resStr)
                    Log.d("LoginPasswordActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    if (ret_val.equals("登入成功!")) {
                        var user_id: Int = json.getInt("user_id")

                        MMKV.mmkvWithID("http").putInt("UserId", user_id)
                            .putString("Email",email)
                            .putString("Password",password)


                        val intent = Intent(this@LoginPasswordActivity, ShopmenuActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@LoginPasswordActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
//                        initRecyclerView()


                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_Login(url, email, password)
    }
}