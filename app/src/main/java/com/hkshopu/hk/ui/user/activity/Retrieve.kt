package com.hkshopu.hk.ui.user.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.databinding.ActivityRetrieveBinding
import com.hkshopu.hk.ui.user.fragmentdialog.BottomSheeFragment
import com.hkshopu.hk.ui.user.vm.AuthVModel



class Retrieve : BaseActivity() {

    private lateinit var binding: ActivityRetrieveBinding
    private val VM = AuthVModel()

    var getstring: String? = null
    var authentication_code: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetrieveBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initIntent()
        initView()
        initVM()
        initClick()


    }

    private fun initIntent() {
        //取得LoginPage傳來的email address
        getstring = intent.getBundleExtra("bundle")?.getString("email")
    }

    private fun initVM() {

        VM.generateAndSendVerificationCodeData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    Toast.makeText(this, it.data.toString(), Toast.LENGTH_SHORT).show()

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })

        VM.authenticationCodeData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    Toast.makeText(this, it.data.toString(), Toast.LENGTH_SHORT).show()

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })


    }

    private fun initView() {

        //email
        binding.textViewEmail.setText(getstring!!)

        //notify
//        NotificationDialogFragment().show(supportFragmentManager, "MyCustomFragment")

        initClick()
        initEditText()


    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {
            finish()
        }

        binding.btnResend.setOnClickListener {


            VM.generate_and_send_verification_code(this)

        }

        binding.btnAuthenticate.setOnClickListener {

            VM.authenticate_email(this, getstring!!, authentication_code!!)


            //傳送email address給Retrieve Page
            var bundle = Bundle()
            bundle.putString("email", getstring)

            val intent = Intent(this, NewPasswordActivity::class.java)
            intent.putExtra("bundle", bundle)

            startActivity(intent)
        }


        binding.termsOfService.setOnClickListener {

            val intent = Intent(this, TermsOfServiceActivity::class.java)
            startActivity(intent)

        }


    }

    private fun initEditText() {
        authentication_code =
            binding.edtAuthenticate01.text.toString() + binding.edtAuthenticate02.text.toString() + binding.edtAuthenticate03.text.toString() + binding.edtAuthenticate04.text.toString()



        setNextFocus(binding.edtAuthenticate01,binding.edtAuthenticate02)
        setNextFocus(binding.edtAuthenticate02,binding.edtAuthenticate03)
        setNextFocus(binding.edtAuthenticate03,binding.edtAuthenticate04)

    }

    fun setNextFocus(nowEdit: EditText, nextEdit: EditText) {
        nowEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (nowEdit.getText().toString().length == 1) {
                    nextEdit.requestFocus()
                }

            }
        })
    }



}