package com.hkshopu.hk.ui.user.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.Base.response.Status
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.ActivityLoginBinding
import com.hkshopu.hk.ui.main.activity.ShopmenuActivity
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable
import org.jetbrains.anko.singleLine
import java.util.*


class LoginActivity : BaseActivity(), TextWatcher {
    lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 900

    var email: String = ""
    private lateinit var settings: SharedPreferences

    lateinit var settings_rememberMe: SharedPreferences
    lateinit var settings_rememberEmail: SharedPreferences
    lateinit var settings_rememberPassword: SharedPreferences
    var rememberMeOrNot = ""
    var rememberEmailOrNot = ""
    var rememberPasswordOrNot = ""

    var to: Int = 0
    private val VM = AuthVModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callbackManager = CallbackManager.Factory.create()

        //local資料存取
        settings = this.getSharedPreferences("DATA",0)
        settings_rememberMe = this.getSharedPreferences("rememberMe", 0)
        settings_rememberEmail = this.getSharedPreferences("rememberEmail", 0)
        settings_rememberPassword = this.getSharedPreferences("rememberPassword", 0)
        rememberMeOrNot = settings_rememberMe.getString("rememberMe", "").toString()
        rememberEmailOrNot = settings_rememberEmail.getString("rememberEmail", "").toString()
        rememberPasswordOrNot = settings_rememberPassword.getString("rememberPassword", "").toString()

        if ( rememberMeOrNot == "true" && rememberEmailOrNot == "true" && rememberPasswordOrNot == "true") {

            val intent = Intent(this, ShopmenuActivity::class.java)
            startActivity(intent)

        }

        //google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestId()
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        initView()
        initClick()
        initEditText()
        initVM()

    }

    //settings of textWatcher
    override fun afterTextChanged(s: Editable?) {

        if (s.toString().isEmpty()) {
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
        VM.loginLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    if (it.ret_val.toString() == "密碼錯誤!") {

                        val editor : SharedPreferences.Editor = settings_rememberEmail.edit()
                        editor.apply {
                            putString("rememberEmail", "true")
                        }.apply()


                        settings.edit().apply {
                            putString("email", email)
                        }.apply()

                        val intent = Intent(this, LoginPasswordActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, it.ret_val.toString(), Toast.LENGTH_SHORT ).show()
                    }

                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
            }
        })

        VM.socialloginLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
//                    Log.d("OnBoardActivity", "Sign-In Result" + it.data)
                    if (it.ret_val.toString().isNotEmpty()) {
                        val intent = Intent(this, ShopmenuActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        val intent = Intent(this, BuildAccountActivity::class.java)
                        startActivity(intent)
                        finish()
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
        if (email.isNotEmpty()) {
            binding.editEmail.setText(email)
//            binding.password1.requestFocus()
//            KeyboardUtil.showKeyboard(binding.password1)

        }

        //hide hidePassword eye and showPassword eye (default)
//        binding.hidePassword.visibility = View.INVISIBLE

    }

    private fun initClick() {
        binding.titleBack.setOnClickListener {

            finish()
        }
        binding.btnNextStep.setOnClickListener {

            email = binding.editEmail.text.toString()
//            val password = binding.password1.text.toString()
            VM.login(this, email, "checkfortheemail")

        }

        binding.checkBoxStayLogin.setOnClickListener {
            if (binding.checkBoxStayLogin.isChecked()) {
                val sharedPreferences : SharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply {
                    putString("rememberMe", "true")
                }.apply()

            }else{
                val sharedPreferences : SharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply {
                    putString("rememberMe", "false")
                }.apply()
            }
        }


        binding.btnGoogleLogin.setOnClickListener {
            GoogleSignIn()
        }

        binding.btnFacebookLogin.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList("public_profile", "email")
            )
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        val request =
                            GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                                Log.d("OnBoardActivity", response.toString())
                                try {
                                    // Application code
                                    val id = response.jsonObject.getString("id")
                                    val email = response.jsonObject.getString("email")
                                    VM.sociallogin(this@LoginActivity, email, id, "", "")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email,gender,birthday")
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        Log.d("OnBoardActivity", "Facebook onCancel.")

                    }

                    override fun onError(error: FacebookException) {
                        Log.d("OnBoardActivity", "Facebook onError.")

                    }
                })

        }

    }
    private fun initEditText() {
        binding.editEmail.addTextChangedListener(this)
//        binding.password1.addTextChangedListener(this)

        binding.editEmail.singleLine = true
        binding.editEmail.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    email = binding.editEmail.text.toString()

                    binding.editEmail.clearFocus()
                    KeyboardUtil.hideKeyboard(binding.editEmail)

                    true
                }

                else -> false
            }
        }
    }

    private fun GoogleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val email = account.email.toString()
                val id = account.id.toString()

                VM.sociallogin(this, email, "", id, "")
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("OnBoardActivity", "Google sign in failed", e)
                // ...
            }
        }
    }


}