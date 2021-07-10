package com.HKSHOPU.hk.ui.main.buyer.activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.paypal.android.sdk.payments.*
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerPWchange2Activity : BaseActivity() {

    private lateinit var binding: ActivityUserpwchange2Binding
    var password_old = MMKV.mmkvWithID("http").getString("Password", "")
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    val url = ApiConstants.API_HOST + "user_detail/update_detail/"
    var password_new =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserpwchange2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initClick()

    }

    private fun initView() {
        binding.etPassword.doAfterTextChanged {
            password_new = binding.etPassword.text.toString()
        }
    }

    private fun initClick() {
        binding.layoutPwchange.setOnClickListener {
            KeyboardUtil.hideKeyboard(it)
        }
        binding.ivBack.setOnClickListener {

            finish()
        }

        binding.showPassBtn.setOnClickListener {
            ShowHidePass(it)
        }

        binding.tvSave.setOnClickListener {
            val regex  = """^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\d]){1,})(?=(.*[\W]){1,})(?!.*\s).{8,}${'$'}""".toRegex()
            if(regex.matches(password_new)){
                doUpdatePassword(url,password_new)
            }else{
                Toast.makeText(this, "新密碼格式錯誤!", Toast.LENGTH_SHORT).show()
            }


        }

    }

    fun ShowHidePass(view: View) {
        if (view.getId() == R.id.show_pass_btn) {
            if (binding.etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeon)
                //Show Password
                binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance())

            } else {
                (view as ImageView).setImageResource(R.mipmap.ic_eyeoff)
                //Hide Password
                binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance())

            }
        }
    }


    private fun doUpdatePassword(url: String,passwordNew:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("UserPWchange2Activity", "返回資料 resStr：" + resStr)
                    Log.d("UserPWchange2Activity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        RxBus.getInstance().post(EventRefreshUserInfo())
                        runOnUiThread {
                            Toast.makeText(
                                this@BuyerPWchange2Activity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_UserInfoUpdate(url,userId,"","","","","","",passwordNew)
    }

    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }


}