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
import com.hkshopu.hk.databinding.ActivityUserinfoBinding
import com.hkshopu.hk.ui.main.activity.ShopmenuActivity
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import java.util.*


class UserIofoActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityUserinfoBinding
    private val VM = AuthVModel()
    private lateinit var settings: SharedPreferences
    var firstName: String = ""
    var lastName: String = ""
    var gender: String = ""
    var birth: String = ""
    var phone: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("DATA",0)
        initView()
        initVM()
        initClick()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    private fun initVM() {
        VM.registerLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.data.toString().equals("註冊成功!")) {
                        VM.verifycode(this)

                    } else {
                        val text1: String = it.data.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1,duration1)
                    }
                    finish()
                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })
        VM.verifycodeLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    if (it.data.toString().equals("已寄出驗證碼")) {
                        val intent = Intent(this, EmailVerifyActivity::class.java)
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
        if(settings.contains("firstname")) {
            firstName = settings.getString("firstname", "").toString()
            binding.editFirstName.setText(firstName)
            lastName = settings.getString("lastname", "").toString()
            binding.editlastName.setText(lastName)
            gender = settings.getString("gender", "").toString()
            if (gender.equals("男")) {
                binding.tvMale.setBackgroundResource(R.drawable.bg_userinfo_gender)
                binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
                binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            } else if (gender.equals("女")) {
                binding.tvFemale.setBackgroundResource(R.drawable.bg_userinfo_gender)
                binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
                binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            } else {
                binding.tvRainbow.setBackgroundResource(R.drawable.bg_userinfo_gender)
                binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
                binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
            }
            birth = settings.getString("birth", "").toString()
            binding.tvBirth.setText(birth)
            phone = settings.getString("phone", "").toString()
            binding.editmobile.setText(phone)
        }
        initEditText()
        initClick()

        KeyboardUtil.showKeyboard(binding.editFirstName)


    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {
            finish()
        }
        binding.tvMale.setOnClickListener {
            binding.tvMale.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            gender="男"
        }
        binding.tvFemale.setOnClickListener {
            binding.tvFemale.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_edit_login)
            gender="女"
        }
        binding.tvRainbow.setOnClickListener {
            binding.tvRainbow.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
            gender="其他"
        }
        binding.showDateBtn.setOnClickListener {
            ShowDatePick(it)
        }
        binding.tvNext.setOnClickListener {
            settings.edit()
                .putString("firstname", firstName)
                .putString("lastname", lastName)
                .putString("gender", gender)
                .putString("birth", birth)
                .putString("phone", phone)
                .apply()
            val intent = Intent(this, AddressEditActivity::class.java)
            startActivity(intent)
            finish()
        }

        settings = this.getSharedPreferences("DATA", 0)
        binding.tvSkip.setOnClickListener {
            val email = settings.getString("email", "")
            val password = settings.getString("password", "")
            val passwordconf = settings.getString("passwordconf", "")
            firstName = binding.editFirstName.text.toString()
            lastName = binding.editlastName.text.toString()
            birth = binding.tvBirth.text.toString()
            phone = binding.editmobile.text.toString()
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
            finish()
        }


    }

    private fun initEditText() {
        binding.editFirstName.addTextChangedListener(this)
        binding.editlastName.addTextChangedListener(this)
        binding.editmobile.addTextChangedListener(this)
    }
    fun ShowDatePick(view: View) {
        if (view.getId() === R.id.show_date_btn) {
            var calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]

            var dialog = DatePickerDialog(
                this, R.style.DateTimeDialogTheme,
                { datePicker, year, month, day ->
                    val month_actual = month + 1
                    binding.tvBirth.setText("$day/$month_actual/$year")
                }, mYear, mMonth, mDay
            )
            dialog.getDatePicker().setMaxDate(java.lang.System.currentTimeMillis())
            dialog.show()
        }

    }
}