package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.user.activity.LoginActivity
import com.HKSHOPU.hk.ui.user.activity.RetrieveEmailVerifyActivity

import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.concurrent.schedule


class EmailAddBeforeIdentifyingActivity : BaseActivity(){
    private lateinit var binding: ActivityEmailAddBeforeIdentifyingBinding

    var passwordCheck: String = ""
    var address_id:String = ""

    var user_id: String = ""
    var email:String =""
    var email_on:String = ""
    var login_email = ""

    private val VM = AuthVModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailAddBeforeIdentifyingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarAddEmail.visibility = View.GONE
        binding.ivLoadingBackgroundAddEmail.visibility = View.GONE

        login_email = MMKV.mmkvWithID("http").getString("Email", "").toString()


        address_id = intent.getBundleExtra("bundle")!!.getString("address_id","").toString()
        email = intent.getBundleExtra("bundle")!!.getString("email_old","")
        email_on = intent.getBundleExtra("bundle")!!.getString("email_on","")
        initView()
        initEditText()
        initClick()
        initVM()

    }

    private fun initVM() {

        VM.verifycodeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    if (it.ret_val.toString().equals("已寄出驗證碼!")) {

                        binding.progressBarAddEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundAddEmail.visibility = View.GONE

                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()
                        Log.d("verifycodeLiveData", "ret_val: ${it.ret_val.toString()}")

                        //傳送email address給Retrieve Page
                        var bundle = Bundle()
                        bundle.putString("email", email)
                        intent.putExtra("bundle", bundle)
                        val intent = Intent(this, RetrieveEmailVerifyActivity::class.java)
                        startActivity(intent)

                    } else {

                        binding.progressBarAddEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundAddEmail.visibility = View.GONE

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
        binding.layoutEmailAdd.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }

    }

    private fun initEditText() {

        binding.etPassword.doAfterTextChanged {
            passwordCheck = binding.etPassword.text.toString()
        }

    }
    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.showPassword.setOnClickListener {
            binding.showPassword.visibility = View.INVISIBLE
            binding.hidePassword.visibility = View.VISIBLE
            binding.etPassword.transformationMethod= PasswordTransformationMethod.getInstance()
        }

        //hide hidePassword eye and showPassword eye show
        binding.hidePassword.setOnClickListener {
            binding.hidePassword.visibility = View.INVISIBLE
            binding.showPassword.visibility = View.VISIBLE
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }

        binding.tvGoOn.setOnClickListener {

            binding.progressBarAddEmail.visibility = View.VISIBLE
            binding.ivLoadingBackgroundAddEmail.visibility = View.VISIBLE
            doLogin(login_email, passwordCheck)

        }
        binding.goRetrieve.setOnClickListener {

            binding.progressBarAddEmail.visibility = View.VISIBLE
            binding.ivLoadingBackgroundAddEmail.visibility = View.VISIBLE

            binding.goRetrieve.setTextColor(Color.parseColor("#8E8E93"))
            binding.goRetrieve.isEnabled = false

            Timer().schedule(60000) {
                binding.goRetrieve.setTextColor(Color.parseColor("#000000"))
                binding.goRetrieve.isEnabled = true
            }

            VM.verifycode(this, login_email!!)

        }
        binding.switchview.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if(isOpen){

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

                        var bundle = Bundle()
                        bundle.putString("address_id",address_id)
                        bundle.putString("email_old",email)
                        bundle.putString("email_on", email_on)
                        val intent = Intent(this@EmailAddBeforeIdentifyingActivity, EmailAddAfterIdentifyingActivity::class.java)
                        intent.putExtra("bundle",bundle)

                        runOnUiThread {
                            Log.d("doLogin", "ret_val: ${ret_val.toString()}")
                            binding.progressBarAddEmail.visibility = View.GONE
                            binding.ivLoadingBackgroundAddEmail.visibility = View.GONE
                        }

                        startActivity(intent)
                        finish()

                    } else {

                        runOnUiThread {
                            Toast.makeText(this@EmailAddBeforeIdentifyingActivity, "密碼錯誤或不存在", Toast.LENGTH_SHORT).show()
                            Log.d("doLogin", "ret_val: ${ret_val.toString()}")
                            binding.progressBarAddEmail.visibility = View.GONE
                            binding.ivLoadingBackgroundAddEmail.visibility = View.GONE
                        }
                    }
//                        initRecyclerView()


                } catch (e: JSONException) {

                    runOnUiThread {
                        Toast.makeText(this@EmailAddBeforeIdentifyingActivity, "網路異常請重新操作", Toast.LENGTH_SHORT).show()
                        Log.d("doLogin", "JSONException: ${e.toString()}")
                        binding.progressBarAddEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundAddEmail.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()

                    runOnUiThread {
                        Toast.makeText(this@EmailAddBeforeIdentifyingActivity, "網路異常請重新操作", Toast.LENGTH_SHORT).show()
                        Log.d("doLogin", "IOException: ${e.toString()}")
                        binding.progressBarAddEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundAddEmail.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

                runOnUiThread {
                    Toast.makeText(this@EmailAddBeforeIdentifyingActivity, "網路異常請重新操作", Toast.LENGTH_SHORT).show()
                    Log.d("doLogin", "ErrorResponse: ${ErrorResponse.toString()}")
                    binding.progressBarAddEmail.visibility = View.GONE
                    binding.ivLoadingBackgroundAddEmail.visibility = View.GONE
                }

            }
        })
        web.Do_Login(url, email, password)
    }

}