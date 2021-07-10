package com.HKSHOPU.hk.ui.main.homepage.activity


import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible

import com.google.gson.Gson

import com.HKSHOPU.hk.Base.BaseActivity

import com.HKSHOPU.hk.data.bean.*

import com.HKSHOPU.hk.databinding.ActivityShopbriefUserBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.activity.ShopNotifyActivity
import com.HKSHOPU.hk.utils.extension.load


import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException


class ShopBriefActivity : BaseActivity() {
    private lateinit var binding: ActivityShopbriefUserBinding

    var shopId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopbriefUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initVM()
        initClick()
        shopId = intent.getBundleExtra("bundle")!!.getInt("shopId", 0)
        var url =
            ApiConstants.API_HOST + "shop/" + shopId + "/get_simple_info_of_specific_shop_for_buyer/"
        getShopBrief(url)


    }

    private fun initView() {
        binding.progressBarShopBrief.isVisible = true

    }

    private fun initVM() {
//        VM.socialloginLiveData.observe(this, Observer {
//            when (it?.status) {
//                Status.Success -> {
//                    if (url.isNotEmpty()) {
//                        toast("登录成功")
//
//                    }
//
//                    finish()
//                }
//                Status.Start -> showLoading()
//                Status.Complete -> disLoading()
//            }
//        })
    }

    private fun initClick() {

        binding.titleBackShopbrief.setOnClickListener {

            finish()
        }

        binding.ivNotifyClick.setOnClickListener {
            val intent = Intent(this@ShopBriefActivity, ShopNotifyActivity::class.java)
            startActivity(intent)
        }


    }

    private fun getShopBrief(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {

                    val infolist = ArrayList<ShopBriefUserBean>()
                    infolist.clear()
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("ShopBriefActivity", "返回資料 resStr：" + resStr)
                    Log.d("ShopBriefActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val jsonObject: JSONObject = json.getJSONObject("data")
                        val shopBriefUserBean: ShopBriefUserBean =
                            Gson().fromJson(jsonObject.toString(), ShopBriefUserBean::class.java)
                        infolist.add(shopBriefUserBean)

                        runOnUiThread {
                            binding.tvShopbriefName.text = infolist[0].shop_title
                            binding.ivShopbriefPic.load(infolist[0].shop_icon)
                            binding.ivShopimage.load(infolist[0].background_pic)
                            binding.tvShopbrief.setText(infolist[0].long_description)

                            if (infolist[0].shop_email.length > 0) {
                                binding.tvShopbriefContact.visibility = View.VISIBLE
                                binding.ivShopbriefEmail.visibility = View.VISIBLE
                                binding.tvShopbriefEmail.visibility = View.VISIBLE
                                binding.tvShopbriefEmail.text = infolist[0].shop_email
                            }


                            if (infolist[0].address_phone.length > 0) {

                                binding.tvShopbriefContact.visibility = View.VISIBLE
                                binding.ivShopbriefContact1.visibility = View.VISIBLE
                                binding.tvShopbriefPhone.text = infolist[0].address_phone
                                binding.tvShopbriefPhone.visibility = View.VISIBLE
                            }

                            val shopaddress: JSONObject = jsonObject.getJSONObject("shop_address")
                            if (shopaddress.length() > 0) {
                                val country = shopaddress.getString("country_code")
                                val area = shopaddress.getString("area")
                                val district = shopaddress.getString("district")
                                val road = shopaddress.getString("road")
                                val other = shopaddress.getString("other")
                                val number = shopaddress.getString("number")
                                val floor = shopaddress.getString("floor")
                                val room = shopaddress.getString("room")

                                val address_brief =
                                    area + district + road + other + number + floor + room
                                binding.ivShopbriefAddress.visibility = View.VISIBLE
                                binding.tvShopbriefAddress.visibility = View.VISIBLE
                                binding.tvShopbriefAddress.text = address_brief

                            }


                            binding.tvShopbriefContact.visibility = View.VISIBLE
                            binding.progressBarShopBrief.isVisible = false
                        }
                    } else {

                        runOnUiThread {

                            Toast.makeText(
                                this@ShopBriefActivity,
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
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
        web.Get_Data(url)
    }
}