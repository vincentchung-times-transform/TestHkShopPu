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
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import java.text.SimpleDateFormat
import java.util.*


class UserIofoActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityUserinfoBinding
    private val VM = AuthVModel()
    private lateinit var settings: SharedPreferences
    var email: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var gender: String = "其他"
    var birth: String = ""
    var phone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settings = getSharedPreferences("DATA", 0)


        initView()
        initVM()
        initClick()
    }

    override fun afterTextChanged(s: Editable?) {

        firstName = binding.editFirstName.text.toString()
        lastName = binding.editlastName.text.toString()
        birth = binding.edtViewBirth.text.toString()
        phone = binding.editmobile.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()||birth.isEmpty()) {
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

                        email = settings.getString("email", "").toString()
                        VM.verifycode(this, email!!)

                    } else {
                        val text1: String = it.ret_val.toString() //設定顯示的訊息
                        val duration1 = Toast.LENGTH_SHORT //設定訊息停留長短
                        Toast.makeText(this, text1, duration1).show()
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
                        Toast.makeText(this, text1, duration1).show()
                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })
    }

    private fun initView() {
        //btnNextStep預設不能案
        binding.btnNextStep.isEnabled = false


        initEditText()
        initClick()

        KeyboardUtil.showKeyboard(binding.editFirstName)


    }

    private fun initClick() {

        //預設性別為其他
        binding.tvRainbow.setBackgroundResource(R.drawable.bg_userinfo_gender)
        binding.tvFemale.setBackgroundResource(R.drawable.bg_edit_login)
        binding.tvMale.setBackgroundResource(R.drawable.bg_edit_login)
        gender="其他"

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
        binding.btnNextStep.setOnClickListener {
            var bithForDB = changeDateFormat_forDB(birth)

            settings.edit()
                .putString("firstName", firstName)
                .putString("lastName", lastName)
                .putString("gender ", gender)
                .putString("birth", bithForDB)
                .putString("phone", phone)
                .apply()

            val intent = Intent(this, AddressEditActivity::class.java)
            startActivity(intent)
        }


        binding.tvSkip.setOnClickListener {

            settings = this.getSharedPreferences("DATA", 0)
            val email = settings.getString("email", "")
            val password = settings.getString("password", "")
            val passwordconf = settings.getString("passwordconf", "")

            VM.register(
                this,
                "",
                email!!,
                password!!,
                passwordconf!!,
                "",
                "",
                "",
                "",
                "",
                "", "", "", "", "", "", ""
            )
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

                    binding.edtViewBirth.setText(changeDateFormat_forApp("$month_actual/$day/$year").toString())
                }, mYear, mMonth, mDay
            )
            dialog.getDatePicker().setMaxDate(java.lang.System.currentTimeMillis())
            dialog.show()
        }

    }

    fun changeDateFormat_forDB(item : String): String {
        val parser = SimpleDateFormat("dd/MM/yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val output: String = formatter.format(parser.parse(item))

        return output
    }

    fun changeDateFormat_forApp(item : String): String {
        val parser = SimpleDateFormat("dd/MM/yyyy")
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val output: String = formatter.format(parser.parse(item))

        return output
    }





}