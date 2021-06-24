package com.HKSHOPU.hk.ui.user.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityLoginBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopmenuActivity
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class LoginActivity : BaseActivity(), TextWatcher {
    lateinit var callbackManager: CallbackManager
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 900

    var user_id = ""
    var email: String = ""
    private lateinit var settings: SharedPreferences

    lateinit var settings_rememberMe: SharedPreferences
    lateinit var settings_rememberEmail: SharedPreferences


    var to: Int = 0
    private val VM = AuthVModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarLoginEmail.visibility = View.GONE
        binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE

        callbackManager = CallbackManager.Factory.create()

        //local資料存取
        settings = this.getSharedPreferences("DATA",0)
        settings_rememberMe = this.getSharedPreferences("rememberMe", 0)
        settings_rememberEmail = this.getSharedPreferences("rememberEmail", 0)


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

        VM.emailcheckLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {

                    binding.progressBarLoginEmail.visibility = View.GONE
                    binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE

                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        if (it.ret_val!!.equals("該電子郵件已存在!")) {

                            settings.edit()
                                .putString("email", email)
                                .apply()

                            val intent = Intent(this, LoginPasswordActivity::class.java)
                            startActivity(intent)

                        }else{
                            Toast.makeText(this, "電子郵件不存在", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(this, "電子郵件格式錯誤", Toast.LENGTH_SHORT).show()
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

            binding.progressBarLoginEmail.visibility = View.VISIBLE
            binding.ivLoadingBackgroundLoginEmail.visibility = View.VISIBLE

            email = binding.editEmail.text.toString()
            val editor : SharedPreferences.Editor = settings_rememberEmail.edit()
            editor.apply {
                putString("rememberEmail", "true")
            }.apply()

            VM.emailCheck(this,email)

        }

        binding.checkBoxStayLogin.setOnClickListener {


            if (binding.checkBoxStayLogin.isChecked()) {

                val editor : SharedPreferences.Editor = settings_rememberMe.edit()
                editor.apply {
                    putString("rememberMe", "true")
                }.apply()

            }else{

                val editor : SharedPreferences.Editor = settings_rememberMe.edit()
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

                                    // method_1.判斷用戶是否登入過
                                    if (Profile.getCurrentProfile() != null) {
                                        val profile: Profile = Profile.getCurrentProfile()
                                        // 取得用戶大頭照
                                        val userPhoto: Uri = profile.getProfilePictureUri(300, 300)
                                        val id: String = profile.getId()
                                        val name: String = profile.getName()
                                        Log.d("OnBoardActivity", "Facebook userPhoto: $userPhoto")
                                        Log.d("OnBoardActivity", "Facebook id: $id")
                                        Log.d("OnBoardActivity", "Facebook name: $name")
                                    }

                                    // method_2.判斷用戶是否登入過
                                    /*if (AccessToken.getCurrentAccessToken() != null) {
                                        Log.d(TAG, "Facebook getApplicationId: " + AccessToken.getCurrentAccessToken().getApplicationId());
                                        Log.d(TAG, "Facebook getUserId: " + AccessToken.getCurrentAccessToken().getUserId());
                                        Log.d(TAG, "Facebook getExpires: " + AccessToken.getCurrentAccessToken().getExpires());
                                        Log.d(TAG, "Facebook getLastRefresh: " + AccessToken.getCurrentAccessToken().getLastRefresh());
                                        Log.d(TAG, "Facebook getToken: " + AccessToken.getCurrentAccessToken().getToken());
                                        Log.d(TAG, "Facebook getSource: " + AccessToken.getCurrentAccessToken().getSource());
                                    }*/
                                    runOnUiThread {
                                        binding.progressBarLoginEmail.visibility = View.VISIBLE
                                        binding.ivLoadingBackgroundLoginEmail.visibility = View.VISIBLE
                                    }

                                    // Application code
                                    val id = response.jsonObject.getString("id")
                                    val email = response.jsonObject.getString("email")
                                    doSocialLogin(email,id,"","")                                              } catch (e: Exception) {
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

                runOnUiThread {
                    binding.progressBarLoginEmail.visibility = View.VISIBLE
                    binding.ivLoadingBackgroundLoginEmail.visibility = View.VISIBLE
                }

                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val email = account.email.toString()
                val id = account.id.toString()

                doSocialLogin(email,"",id,"")

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("OnBoardActivity", "Google sign in failed", e)
                runOnUiThread {
                    binding.progressBarLoginEmail.visibility = View.GONE
                    binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                }

            }
        }
    }


    private fun doSocialLogin(email: String, facebook_account: String, google_account: String, apple_account: String) {
        var url = ApiConstants.API_PATH+"user/socialLoginProcess/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var ret_val: Any = ""
                var status: Any = 999
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("OnBoardActivity", "返回資料 resStr：" + resStr)
                    Log.d("OnBoardActivity", "返回資料 ret_val：" + json.get("ret_val"))

                    ret_val = json.get("ret_val")
                    status = json.get("status")

                    if (status != 0) {

                        user_id= json.getString("user_id")

                        Log.d("doSocialLogin", "ret_val: ${ret_val.toString()}")
                        doBackendUserIDValidation(user_id, email)

                        doInsertAuditLog(user_id,
                            "第三方登入/doSocialLogin()",
                            "email: ${email.toString()} ; " +
                                    "facebook_account: ${facebook_account} ; " +
                                    "google_account : ${google_account} ; " +
                                    "apple_account : ${apple_account} ; ",
                            json.get("ret_val").toString()
                        )


                    } else {

                        user_id = json.getString("user_id")

                        doInsertAuditLog(user_id,
                            "第三方登入/doSocialLogin()",
                            "email: ${email.toString()} ; " +
                                    "facebook_account: ${facebook_account} ; " +
                                    "google_account : ${google_account} ; " +
                                    "apple_account : ${apple_account} ; ",
                            json.get("ret_val").toString()
                        )

                        runOnUiThread {

                            Toast.makeText(this@LoginActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            Log.d("doSocialLogin", "ret_val: ${ret_val.toString()}")

                            binding.progressBarLoginEmail.visibility = View.GONE
                            binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE

                        }

                        val intent = Intent(this@LoginActivity, BuildAccountActivity::class.java)
                        startActivity(intent)
                        finish()


                    }



                } catch (e: JSONException) {


                    doInsertAuditLog(user_id,
                        "第三方登入/doSocialLogin()",
                        "email: ${email.toString()} ; " +
                                "facebook_account: ${facebook_account} ; " +
                                "google_account : ${google_account} ; " +
                                "apple_account : ${apple_account} ; ",
                        e.toString()
                    )
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        Log.d("doSocialLogin", "JSONException: ${e.toString()}")
                        binding.progressBarLoginEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    e.printStackTrace()

                    doInsertAuditLog(user_id,
                        "第三方登入/doSocialLogin()",
                        "email: ${email.toString()} ; " +
                                "facebook_account: ${facebook_account} ; " +
                                "google_account : ${google_account} ; " +
                                "apple_account : ${apple_account} ; ",
                        e.toString()
                    )
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        Log.d("doSocialLogin", "IOException: ${e.toString()}")
                        binding.progressBarLoginEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {


                doInsertAuditLog(user_id,
                    "第三方登入/doSocialLogin()",
                    "email: ${email.toString()} ; " +
                            "facebook_account: ${facebook_account} ; " +
                            "google_account : ${google_account} ; " +
                            "apple_account : ${apple_account} ; ",
                    ErrorResponse.toString()
                )
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    Log.d("doBackendUserIDValidation", "ErrorResponse: ${ErrorResponse.toString()}")
                    binding.progressBarLoginEmail.visibility = View.GONE
                    binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                }
            }
        })
        web.Do_SocialLogin(url, email,facebook_account,google_account, apple_account)
    }


    private fun doBackendUserIDValidation(user_id: String, email: String) {

        var url = ApiConstants.API_PATH+"user/user_id_validation/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("doBackendUserIDValidation", "返回資料 resStr：" + resStr)
//                    Log.d("doInsertAuditLog", "返回資料 ret_val：" + json.get("ret_val"))

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        if (ret_val.equals("該使用者存在!")){


                            MMKV.mmkvWithID("http").putString("UserId", user_id)
                                .putString("Email", email)

                            Log.d("doBackendUserIDValidation", "該使用者存在!")
                            runOnUiThread {
                                binding.progressBarLoginEmail.visibility = View.GONE
                                binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                            }

                            val intent = Intent(this@LoginActivity, ShopmenuActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "該使用者不存在!", Toast.LENGTH_SHORT).show()
                                Log.d("doBackendUserIDValidation", "該使用者不存在!")
                                binding.progressBarLoginEmail.visibility = View.GONE
                                binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                            }
                        }

                    }

                } catch (e: JSONException) {

                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        Log.d("doBackendUserIDValidation", "JSONException: ${e.toString()}")
                        binding.progressBarLoginEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    e.printStackTrace()

                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        Log.d("doBackendUserIDValidation", "IOException: ${e.toString()}")
                        binding.progressBarLoginEmail.visibility = View.GONE
                        binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                    }

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    Log.d("doBackendUserIDValidation", "ErrorResponse: ${ErrorResponse.toString()}")
                    binding.progressBarLoginEmail.visibility = View.GONE
                    binding.ivLoadingBackgroundLoginEmail.visibility = View.GONE
                }

            }
        })
        web.doBackendUserIDValidation(url, user_id)
    }

    private fun doInsertAuditLog(user_id: String, action: String, parameter_in: String, parameter_out: String) {

        var url = ApiConstants.API_PATH+"user/${user_id}/auditLog/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("doInsertAuditLog", "返回資料 resStr：" + resStr)
//                    Log.d("doInsertAuditLog", "返回資料 ret_val：" + json.get("ret_val"))

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        if (ret_val.equals("新增成功")){
                            Log.d("doInsertAuditLog", "訊息狀態：訊息已送出!!")
                        }else{
                            Log.d("doInsertAuditLog", "訊息狀態：訊息尚未送出~")
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
        web.InsertAuditLog(url, action,parameter_in,parameter_out)
    }


}