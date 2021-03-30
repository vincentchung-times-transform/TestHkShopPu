package com.hkshopu.hk.ui.main.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.ActivityAddshopBinding
import com.hkshopu.hk.databinding.ActivityShopcategoryBinding
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.fragment.ShopInfoFragment
import com.hkshopu.hk.ui.user.vm.AuthVModel
import com.hkshopu.hk.ui.user.vm.ShopVModel
import com.hkshopu.hk.widget.view.KeyboardUtil
import com.hkshopu.hk.widget.view.disable
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopCategoryActivity : BaseActivity() {
    private lateinit var binding: ActivityShopcategoryBinding

    private val VM = ShopVModel()
    var url = "https://hkshopu-20700.df.r.appspot.com/shop_category/index/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopcategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initVM()
        initEditText()
        initClick()
        getShopCategory(url)
    }

    private fun initVM() {

    }

    private fun initView() {

    }

    private fun initClick() {
        binding.titleBackShopcategory.setOnClickListener {

            finish()
        }


    }


    private fun initEditText() {
//        binding.etShopname.addTextChangedListener(this)
//        password1.addTextChangedListener(this)
    }

    private fun getShopCategory(url:String){
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
//                    val json = JSONObject(resStr)
                    Log.d("ShopCategoryActivity", "返回資料：" + resStr.toString())
//                    Log.d("RechargeActivity", "返回值：" + rtnCode)

//                    Log.d("ComicReadActivity", "返回值：" + imgUrl)
                    runOnUiThread {



                    }
                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }

}