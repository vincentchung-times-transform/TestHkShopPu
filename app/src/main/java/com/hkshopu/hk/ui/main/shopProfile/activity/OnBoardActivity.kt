package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.Base.response.Status
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BoardingObjBean
import com.HKSHOPU.hk.data.bean.VersionNameBean
import com.HKSHOPU.hk.databinding.ActivityOnboardBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.fragment.UpdateNotifyDialogFragment
import com.HKSHOPU.hk.ui.main.productBuyer.activity.ProductDetailedPageBuyerViewActivity
import com.HKSHOPU.hk.ui.user.activity.BuildAccountActivity
import com.HKSHOPU.hk.ui.user.activity.LoginActivity
import com.HKSHOPU.hk.ui.user.vm.AuthVModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class OnBoardActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityOnboardBinding

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 900;
    lateinit var callbackManager: CallbackManager
    private val VM = AuthVModel()
    lateinit var points: ArrayList<ImageView> //指示器圖片
    val list = ArrayList<BoardingObjBean>()

    lateinit var sharedPreferences_rememberMe: SharedPreferences
    lateinit var sharedPreferences_rememberEmail: SharedPreferences
    lateinit var sharedPreferences_rememberPassword: SharedPreferences
    var rememberMeOrNot = ""
    var rememberEmailOrNot = ""
    var rememberPasswordOrNot = ""

    var versionNameBean: VersionNameBean = VersionNameBean()


    private fun setBoardingData() {

        var boardingObj = BoardingObjBean(R.mipmap.online_shopping, R.mipmap.online_shopping)
        list.add(boardingObj)
        var boardingObj1 = BoardingObjBean(R.mipmap.online_shopping1, R.mipmap.online_shopping1)
        list.add(boardingObj1)
        var boardingObj2 = BoardingObjBean(R.mipmap.online_shopping2, R.mipmap.online_shopping2)
        list.add(boardingObj2)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doGetLatestAppVersion("Android")

        var height = 0
        var width = getScreenWidth(this)

        MMKV.mmkvWithID("phone_size").putInt("height",height)
        MMKV.mmkvWithID("phone_size").putInt("width",width)

        runOnUiThread {
            binding.progressBarOnBoard.visibility = View.GONE
            binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
        }

        sharedPreferences_rememberMe = this.getSharedPreferences("rememberMe", 0)
        sharedPreferences_rememberEmail = this.getSharedPreferences("rememberEmail", 0)
        sharedPreferences_rememberPassword = this.getSharedPreferences("rememberPassword", 0)
        rememberMeOrNot = sharedPreferences_rememberMe.getString("rememberMe", "").toString()
        rememberEmailOrNot = sharedPreferences_rememberEmail.getString("rememberEmail", "").toString()
        rememberPasswordOrNot = sharedPreferences_rememberPassword.getString("rememberPassword", "").toString()

        if ( rememberMeOrNot == "true" && rememberEmailOrNot == "true" && rememberPasswordOrNot == "true") {

            val intent = Intent(this, ShopmenuActivity::class.java)
            startActivity(intent)

        }


        callbackManager = CallbackManager.Factory.create()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestId()
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setBoardingData()
        initViewPager()

        initVM()
        initClick()


    }

    override fun onPause() {
        super.onPause()

    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()

    }

    private fun initViewPager() {
        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

            }

        })

        binding.pager.adapter = ImageAdapter(list)
        binding.pager.addOnPageChangeListener(this)
        initPoints()

    }

    private fun initPoints() {
        points = arrayListOf()
        for (i in 0 until list.size) {
            val point = ImageView(this)
            point.setPadding(10, 10, 10, 10)
            point.scaleType = ImageView.ScaleType.FIT_XY

            if (i == 0) {
                point.setImageResource(R.drawable.banner_radius)
                point.layoutParams = ViewGroup.LayoutParams(96, 36)
            } else {
                point.setImageResource(R.drawable.banner_radius_unselect)
                point.layoutParams = ViewGroup.LayoutParams(36, 36)
            }

            binding.indicator.addView(point)
            points.add(point)
        }
    }

    private fun initVM() {
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

    private fun initClick() {
        binding.btnFb.setOnClickListener {

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
                                        val id: String = profile.getId().toString()
                                        val name: String = profile.getName().toString()
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
                                        binding.progressBarOnBoard.visibility = View.VISIBLE
                                        binding.ivLoadingBackgroundOnBoard.visibility = View.VISIBLE
                                    }
                                    // Application code
                                    val id: String = response.jsonObject.getString("id")
                                    val email: String = response.jsonObject.getString("email")
//                                    VM.sociallogin(this@OnBoardActivity, email, id, "", "")
                                    doSocialLogin(email,id,"","")
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

        binding.btnGoogle.setOnClickListener {


            GoogleSignIn()


        }

        binding.btnSignup.setOnClickListener {

            val intent = Intent(this, BuildAccountActivity::class.java)
            startActivity(intent)

        }

        binding.tvLogin.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        binding.btnSkip.setOnClickListener {
            var mmkv = MMKV.mmkvWithID("http")
            mmkv.clearAll()

            val intent = Intent(this, ShopmenuActivity::class.java)
            startActivity(intent)

//            val intent = Intent(this, ProductDetailedPageBuyerViewActivity::class.java)
//            startActivity(intent)

//            val intent = Intent(this, EmailVerifyActivity::class.java)
//            startActivity(intent)
        }

    }


    private class ImageAdapter internal constructor(
        arrayList: ArrayList<BoardingObjBean>,
    ) : PagerAdapter() {
        private val arrayList: ArrayList<BoardingObjBean>

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                container.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.boarding_view, null)
            val boardingObj: BoardingObjBean = arrayList[position]
            val imageView = view.findViewById<View>(R.id.image_view) as ImageView
            imageView.setImageResource(boardingObj.imageResId)

            if (position == 0) {
                imageView.scaleType = ImageView.ScaleType.FIT_XY

            } else {
                imageView.scaleType = ImageView.ScaleType.FIT_XY

            }

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return arrayList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        init {
            this.arrayList = arrayList
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View?)
        }

    }

    private fun GoogleSignIn() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun doSocialLogin(email: String, facebook_account: String, google_account: String, apple_account: String) {
        var url = ApiConstants.API_PATH+"user/socialLoginProcess/"
        var user_id: String = ""
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var ret_val: Any = ""
                var status: Any = 999
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("doSocialLogin", "返回資料 resStr：" + resStr)
                    Log.d("doSocialLogin", "返回資料 ret_val：" + json.get("ret_val"))

                    ret_val = json.get("ret_val")
                    status = json.get("status")

                    if (status != 0) {
                        user_id= json.getString("user_id")

                        MMKV.mmkvWithID("http").putString("UserId", user_id).putString("Email",email)

                        doInsertAuditLog(user_id,
                            "第三方登入/doSocialLogin()",
                            "email: ${email.toString()} ; " +
                                    "facebook_account: ${facebook_account} ; " +
                                    "google_account : ${google_account} ; " +
                                    "apple_account : ${apple_account} ; ",
                            json.get("ret_val").toString()
                        )

                        doBackendUserIDValidation(user_id.toString())

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

                            val intent = Intent(this@OnBoardActivity, BuildAccountActivity::class.java)

                            startActivity(intent)
                            finish()
                            Toast.makeText(this@OnBoardActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()

                        }


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
                        binding.progressBarOnBoard.visibility = View.GONE
                        binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                    }
                    runOnUiThread {
                        Toast.makeText(this@OnBoardActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarOnBoard.visibility = View.GONE
                        binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
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
                        Toast.makeText(this@OnBoardActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                        binding.progressBarOnBoard.visibility = View.GONE
                        binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
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
                    Toast.makeText(this@OnBoardActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    binding.progressBarOnBoard.visibility = View.GONE
                    binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                }
            }
        })
        web.Do_SocialLogin(url, email,facebook_account,google_account, apple_account)
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

    private fun doBackendUserIDValidation(user_id: String) {

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

                            Log.d("doBackendUserIDValidation", "該使用者存在!")
                            runOnUiThread {
                                binding.progressBarOnBoard.visibility = View.GONE
                                binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                            }

                            val intent = Intent(this@OnBoardActivity, ShopmenuActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            Toast.makeText(this@OnBoardActivity, "該使用者不存在!", Toast.LENGTH_SHORT).show()
                            Log.d("doBackendUserIDValidation", "該使用者不存在!")
                            runOnUiThread {
                                binding.progressBarOnBoard.visibility = View.GONE
                                binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                            }
                        }

                    }

                } catch (e: JSONException) {

                    Toast.makeText(this@OnBoardActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    Log.d("doBackendUserIDValidation", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarOnBoard.visibility = View.GONE
                        binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                    }


                } catch (e: IOException) {
                    e.printStackTrace()

                    Toast.makeText(this@OnBoardActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                    Log.d("doBackendUserIDValidation", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarOnBoard.visibility = View.GONE
                        binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                    }

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

                Toast.makeText(this@OnBoardActivity, "網路異常請重新登入", Toast.LENGTH_SHORT).show()
                Log.d("doBackendUserIDValidation", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarOnBoard.visibility = View.GONE
                    binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                }

            }
        })
        web.doBackendUserIDValidation(url, user_id)
    }

    private fun doGetLatestAppVersion(app_type: String) {

        var url = ApiConstants.API_PATH+"app_version/get_latest_app_version_number/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)

                    Log.d("doGetLatestAppVersion", "返回資料 resStr：" + resStr)
//                    Log.d("doInsertAuditLog", "返回資料 ret_val：" + json.get("ret_val"))

                    val ret_val = json.get("ret_val")
                    val status = json.get("status")

                    if (status == 0) {

                        if (ret_val.equals( "取得最新 App 版本編號成功!")){
                            Log.d("doGetLatestAppVersion", "取得最新 App 版本編號成功!")

                            val jsonObject: JSONObject = json.getJSONObject("data")
                            versionNameBean = Gson().fromJson(
                                jsonObject.toString(),
                                versionNameBean::class.java
                            )

                            val pInfo: PackageInfo =
                                this@OnBoardActivity.getPackageManager().getPackageInfo(this@OnBoardActivity.getPackageName(), 0)
                            val verName = pInfo.versionName

                            Log.d("doGetLatestAppVersion", "返回資料 latest_version: " + versionNameBean.version_number.toString())
                            Log.d("doGetLatestAppVersion", "返回資料 device_version： " + verName.toString())


                            if(versionNameBean.version_number == verName.toString()){
                                Log.d("doGetLatestAppVersion", "版本檢測: 目前版本為最新版本")
                            }else{
                                Log.d("doGetLatestAppVersion", "版本檢測: 有最新版本可進行更新")
                                runOnUiThread {

                                    UpdateNotifyDialogFragment().show(supportFragmentManager, "UpdateNotifyDialogFragment")
                                }
                            }

                        }else{
                            Log.d("doBackendUserIDValidation", "取得最新 App 版本編號失敗!")
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
        web.doGetLatestAppVersion(url, app_type)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                runOnUiThread {
                    binding.progressBarOnBoard.visibility = View.VISIBLE
                    binding.ivLoadingBackgroundOnBoard.visibility = View.VISIBLE
                }
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val email = account.email.toString()
                val id = account.id.toString()
//                VM.sociallogin(this, email, "", id, "")
                doSocialLogin(email,"",id,"")

            } catch (e: ApiException) {

                // Google Sign In failed, update UI appropriately
                    Log.d("OnBoardActivityRequestCode", requestCode.toString())
                    Log.d("OnBoardActivity", "Google sign in failed", e)

                runOnUiThread {
                    binding.progressBarOnBoard.visibility = View.GONE
                    binding.ivLoadingBackgroundOnBoard.visibility = View.GONE
                }

            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {


    }

    override fun onPageSelected(position: Int) {
        if (position == 0) {
            binding.titleBanner.text = "開店好簡單 什麼都賣等你來"
            binding.tv2.text = "不論你是買家、商家或是創業家，一鍵上手"
        } else if (position == 1) {

            binding.titleBanner.text = "我的店鋪 隨時隨地不NG"
            binding.tv2.text = "無時無刻管理你的店鋪，不再受限地區與時差"
        } else {

            binding.titleBanner.text = "簡單明瞭 你我都是行銷高手"
            binding.tv2.text = "導引頁面清楚，「店匯」帶你輕鬆搞定所有設定"

        }
        for (i in 0 until points.size) {
            val params = points[position].layoutParams
            params.width = 96
            params.height = 36
            points[position].layoutParams = params
            points[position].setImageResource(R.drawable.banner_radius)

            if (position != i) {
                val params1 = points[i].layoutParams
                params1.width = 36
                params1.height = 36
                points[i].layoutParams = params1
                points[i].setImageResource(R.drawable.banner_radius_unselect)

            }
        }

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }


//
//    screenSizeInDp.apply {
//        // screen width in dp
//        textView.append("\n\nWidth : $x dp")
//
//        // screen height in dp
//        textView.append("\nHeight : $y dp")
//    }
//}
//}
//
//
//// extension property to get display metrics instance
//val Activity.displayMetrics: DisplayMetrics
//    get() {
//        // display metrics is a structure describing general information
//        // about a display, such as its size, density, and font scaling
//        val displayMetrics = DisplayMetrics()
//
//        if (Build.VERSION.SDK_INT >= 30){
//            display?.apply {
//                getRealMetrics(displayMetrics)
//            }
//        }else{
//            // getMetrics() method was deprecated in api level 30
//            windowManager.defaultDisplay.getMetrics(displayMetrics)
//        }
//
//        return displayMetrics
//    }
//
//
//// extension property to get screen width and height in dp
//val Activity.screenSizeInDp: Point
//    get() {
//        val point = Point()
//        displayMetrics.apply {
//            // screen width in dp
//            point.x = (widthPixels / density).roundToInt()
//
//            // screen height in dp
//            point.y = (heightPixels / density).roundToInt()
//        }
//
//        return point
//    }

}