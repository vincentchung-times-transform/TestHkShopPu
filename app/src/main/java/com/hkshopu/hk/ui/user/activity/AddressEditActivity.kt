package com.hkshopu.hk.ui.user.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.ActivityAddresseditBinding
import com.hkshopu.hk.databinding.ActivityUserinfoBinding
import com.hkshopu.hk.ui.main.activity.ShopmenuActivity
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import java.util.*


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

        if (region.isEmpty() || district.isEmpty() || street_name.isEmpty() || street_no.isEmpty()) {
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

            VM.register(
                this,
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
            VM.register(
                this,
                "",
                email!!,
                password!!,
                passwordconf!!,
                firstName!!,
                lastName!!,
                gender!!,
                birth!!,
                phone!!,
                "","","","","","",""
            )
        }
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