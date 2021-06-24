package com.HKSHOPU.hk.ui.user.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityNewPasswordBinding
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.tencent.mmkv.MMKV

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding
    private val VM = AuthVModel()

    var email: String = ""
    var password: String = ""
    var confirm_password = ""
    private lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //local資料存取
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
                        val intent = Intent(this, ShopmenuActivity::class.java)
                        startActivity(intent)
                        finish()

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

        initEditText()
        initClick()

    }


    private fun initClick() {
        binding.titleBack.setOnClickListener {

            val intent = Intent(this, LoginPasswordActivity::class.java)
            startActivity(intent)

            finish()

        }



        binding.btnLogin.setOnClickListener {

            password = binding.edtViewPasswordFirstInput.text.toString()
            confirm_password = binding.edtViewPasswordSecondInput.text.toString()

            val regex  = """^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\d]){1,})(?=(.*[\W]){1,})(?!.*\s).{8,}${'$'}""".toRegex()
            if(regex.matches(password)||regex.matches(confirm_password)){
                if(password != confirm_password){
                    Toast.makeText(this, "密碼不一致", Toast.LENGTH_SHORT).show()
                }else{
                    VM.reset_password(this, email!!, password!!, confirm_password!!)

                }

            }else{
                Toast.makeText(this, "密碼格式錯誤", Toast.LENGTH_SHORT).show()
            }

        }

        binding.showPassBtn.setOnClickListener {
            ShowHidePass(it)
        }
        binding.showPassconfBtn.setOnClickListener {
            ShowHidePass(it)
        }

    }

    private fun initEditText() {

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

}