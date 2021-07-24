package com.HKSHOPU.hk.ui.onboard.login.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityNewPasswordBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.onboard.vm.AuthVModel
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding
    private val VM = AuthVModel()

    var user_id: String = ""
    var email: String = ""
    var password: String = ""
    var confirm_password = ""
    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = this.getSharedPreferences("DATA", 0)
        email = settings.getString("email", "").toString()

        initView()
        initVM()
    }

    private fun initVM() {
        VM.resetPasswordLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.ret_val.toString() == "密碼修改成功!")  {
                        MMKV.mmkvWithID("http")
                            .putString("Email",email)
                            .putString("Password",password)
                        Toast.makeText(this, "密碼修改成功!", Toast.LENGTH_SHORT ).show()

                        doLogin(email, password)
                    }else {
                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT ).show()
                    }
                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })
    }

    private fun initView() {
        binding.progressBarNewPassword.visibility = View.GONE
        binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
        initClick()
    }
    private fun initClick() {
        binding.titleBack.setOnClickListener {
            val intent = Intent(this, LoginPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogin.setOnClickListener {
            binding.progressBarNewPassword.visibility = View.VISIBLE
            binding.ivLoadingBackgroundNewPassword.visibility = View.VISIBLE

            password = binding.edtViewPasswordFirstInput.text.toString()
            confirm_password = binding.edtViewPasswordSecondInput.text.toString()

            val regex  = """^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\d]){1,})(?=(.*[\W]){1,})(?!.*\s).{8,}${'$'}""".toRegex()
            if(regex.matches(password)||regex.matches(confirm_password)){
                if(password != confirm_password){
                    Toast.makeText(this, "密碼不一致", Toast.LENGTH_SHORT).show()
                    binding.progressBarNewPassword.visibility = View.GONE
                    binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                }else{
                    VM.reset_password(this, email!!, password!!, confirm_password!!)
                }
            }else{
                Toast.makeText(this, "密碼格式錯誤", Toast.LENGTH_SHORT).show()
                binding.progressBarNewPassword.visibility = View.GONE
                binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
            }
        }
        binding.showPassBtn.setOnClickListener {
            ShowHidePass(it)
        }
        binding.showPassconfBtn.setOnClickListener {
            ShowHidePass(it)
        }
    }

    fun ShowHidePass(view: View) {
        if (view.getId() === R.id.show_pass_btn) {
            if (binding.edtViewPasswordFirstInput.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeon)
                //Show Password
                binding.edtViewPasswordFirstInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeoff)
                //Hide Password
                binding.edtViewPasswordFirstInput.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
        if (view.getId() === R.id.show_passconf_btn) {
            if (binding.edtViewPasswordSecondInput.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeon)
                //Show Password
                binding.edtViewPasswordSecondInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            } else {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeoff)
                //Hide Password
                binding.edtViewPasswordSecondInput.setTransformationMethod(PasswordTransformationMethod.getInstance())
            }
        }
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
                            binding.progressBarNewPassword.visibility = View.GONE
                            binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                        }

                    } else {

                        Log.d("doLogin", "ret_val: ${ret_val.toString()}")
                        runOnUiThread {
                            Toast.makeText(this@NewPasswordActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarNewPassword.visibility = View.GONE
                            binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                        }

                    }

                } catch (e: JSONException) {
                    Log.d("doLogin", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@NewPasswordActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarNewPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doLogin", "IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@NewPasswordActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarNewPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doLogin", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@NewPasswordActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    binding.progressBarNewPassword.visibility = View.GONE
                    binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
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
                                binding.progressBarNewPassword.visibility = View.GONE
                                binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                            }

                            val intent = Intent(this@NewPasswordActivity, ShopmenuActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            Log.d("doBackendUserIDValidation", "該使用者不存在!")
                            runOnUiThread {
                                Toast.makeText(this@NewPasswordActivity, "該使用者不存在!", Toast.LENGTH_SHORT).show()
                                binding.progressBarNewPassword.visibility = View.GONE
                                binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                            }

                            val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("doBackendUserIDValidation", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@NewPasswordActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarNewPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                    }

                    val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("doBackendUserIDValidation", "IOException: ${e.toString()}")
                    runOnUiThread {
                        Toast.makeText(this@NewPasswordActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarNewPassword.visibility = View.GONE
                        binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                    }

                    val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("doBackendUserIDValidation", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    Toast.makeText(this@NewPasswordActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    binding.progressBarNewPassword.visibility = View.GONE
                    binding.ivLoadingBackgroundNewPassword.visibility = View.GONE
                }
                val intent = Intent(this@NewPasswordActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }
        })
        web.doBackendUserIDValidation(url, user_id)
    }
}