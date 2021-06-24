package com.HKSHOPU.hk.ui.user.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityAddresseditBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class AddressEditActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityAddresseditBinding
    private val VM = AuthVModel()
    private lateinit var settings: SharedPreferences
    var email : String? = ""
    var region:String =""
    var district:String =""
    var street_name:String =""
    var street_no:String =""
    var floor:String =""
    var room:String =""
    var address:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddresseditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
        binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE

        settings = getSharedPreferences("DATA",0)

        initView()
        initVM()
        initClick()
    }

    override fun afterTextChanged(s: Editable?) {
        region = binding.editCountry.text.toString()
        district = binding.editAdmin.text.toString()
        street_name = binding.editthoroughfare.text.toString()
        street_no = binding.editfeature.text.toString()
        address = binding.editsubaddress.text.toString()
        floor = binding.editfloor.text.toString()
        room = binding.editroom.text.toString()

        if (region.isEmpty() || district.isEmpty() || street_name.isEmpty()) {
            binding.btnNextStep.isEnabled = false
            binding.btnNextStep.setImageResource(R.mipmap.next_step_inable)
        } else {
            binding.btnNextStep.isEnabled = true
            binding.btnNextStep.setImageResource(R.mipmap.next_step)

        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {
        VM.registerLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.ret_val.toString().equals("註冊成功!")) {

                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT).show()

                        email = settings.getString("email", "")

                        VM.verifycode(this, email!!)

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
                    if (it.ret_val.toString().equals("已寄出驗證碼!")) {

                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_LONG).show()

                        val intent = Intent(this, EmailVerifyActivity::class.java)
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

        //imgViewNextStep預設不能按
        binding.btnNextStep.isEnabled = false

        initEditText()
        initClick()

    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {

            finish()
        }

        settings = this.getSharedPreferences("DATA", 0)

        binding.btnNextStep.setOnClickListener {
            val email = settings.getString("email", "")
            val password = settings.getString("password", "")
            val passwordconf = settings.getString("passwordconf", "")
            val firstName = settings.getString("firstName", "")
            val lastName = settings.getString("lastName", "")
            val birth = settings.getString("birth", "")
            val phone = settings.getString("phone", "")
            val gender = settings.getString("gender", "")

//            VM.register(
//                this,
//                "",
//                email!!,
//                password!!,
//                passwordconf!!,
//                firstName!!,
//                lastName!!,
//                gender!!,
//                birth!!,
//                phone!!,
//                address!!,region!!,district!!,street_name!!,street_no!!,floor!!, room!!
//            )
            doRegister(
                "",
                email!!,
                password!!,
                passwordconf!!,
                firstName!!,
                lastName!!,
                gender!!,
                birth!!,
                phone!!,
                address!!,region!!,district!!,street_name!!,street_no!!,floor!!, room!!
            )

        }


        binding.tvSkip.setOnClickListener {
            val email = settings.getString("email", "")
            val password = settings.getString("password", "")
            val passwordconf = settings.getString("passwordconf", "")
            val firstName = settings.getString("firstName", "")
            val lastName = settings.getString("lastName", "")
            val birth = settings.getString("birth", "")
            val phone = settings.getString("phone", "")
            val gender = settings.getString("gender", "")

            val country = binding.editCountry.text.toString()
            val admin = binding.editAdmin.text.toString()
            val street = binding.editthoroughfare.text.toString()
            val door = binding.editfeature.text.toString()
            val subaddress = binding.editsubaddress.text.toString()
            val floor = binding.editfloor.text.toString()
            val room = binding.editroom.text.toString()
            var address = country+admin+street+door+subaddress+floor+room
            doRegister("",
                email!!,
                password!!,
                passwordconf!!,
                firstName!!,
                lastName!!,
                gender!!,
                birth!!,
                phone!!,
                "","","","","","","")
//            VM.register(
//                this,
//                "",
//                email!!,
//                password!!,
//                passwordconf!!,
//                firstName!!,
//                lastName!!,
//                gender!!,
//                birth!!,
//                phone!!,
//                "","","","","","",""
//            )
        }
    }

    private fun doRegister(account_name: String, email: String, password: String,confirm_password:String,first_name:String,last_name:String,gender:String,birthday:String,phone:String,address:String,region:String,district:String,street_name:String,street_no:String,floor:String,room:String) {
        val url = ApiConstants.API_HOST+"/user/registerProcess/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    runOnUiThread {
                        binding.progressBarBuildAccountAddressEditing.visibility = View.VISIBLE
                        binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.VISIBLE
                    }
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddressEditActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddressEditActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status  = json.get("status")
                    if (status == 0) {
                        var user_id: String= json.getString("user_id")
                        MMKV.mmkvWithID("http").getString("UserId", user_id)

                        doVerifyCode(email)

                        runOnUiThread {
                            Toast.makeText(this@AddressEditActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
//                            VM.verifycode(this@AddressEditActivity, email)

                        }


                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddressEditActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
//                        initRecyclerView()


                } catch (e: JSONException) {


                    runOnUiThread {
                        binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
                        binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    e.printStackTrace()

                    runOnUiThread {
                        binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
                        binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE
                    }

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

                runOnUiThread {
                    binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
                    binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE
                }
            }
        })
        web.Do_Register(url, "",email, password,confirm_password,first_name,last_name,gender,birthday,phone,address,region,district,street_name,street_no,floor,room)
    }


    private fun doVerifyCode(email: String) {
        val url = ApiConstants.API_HOST+"user/generateAndSendValidationCodeProcess/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("AddressEditActivity", "返回資料 resStr：" + resStr)
                    Log.d("AddressEditActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status  = json.get("status")
                    if (status == 0) {

                        runOnUiThread {
                            Toast.makeText(this@AddressEditActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }

                        val intent = Intent(this@AddressEditActivity, EmailVerifyActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@AddressEditActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
//                        initRecyclerView()

                    runOnUiThread {
                        binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
                        binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE
                    }

                } catch (e: JSONException) {

                    runOnUiThread {
                        binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
                        binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    e.printStackTrace()


                    runOnUiThread {
                        binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
                        binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

                runOnUiThread {
                    binding.progressBarBuildAccountAddressEditing.visibility = View.GONE
                    binding.ivLoadingBackgroundBuildAccountAddressEditing.visibility = View.GONE
                }

            }
        })
        web.Do_verifyCode(url, email)
    }

    private fun initEditText() {
        binding.editCountry.addTextChangedListener(this)
        binding.editAdmin.addTextChangedListener(this)
        binding.editthoroughfare.addTextChangedListener(this)
        binding.editfeature.addTextChangedListener(this)
        binding.editsubaddress.addTextChangedListener(this)
        binding.editfloor.addTextChangedListener(this)
        binding.editroom.addTextChangedListener(this)
    }

}