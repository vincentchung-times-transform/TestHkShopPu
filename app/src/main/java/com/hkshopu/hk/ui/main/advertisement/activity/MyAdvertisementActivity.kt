package com.HKSHOPU.hk.ui.main.advertisement.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.databinding.ActivityAdvertisementBinding
import com.HKSHOPU.hk.databinding.ActivityMyAdvertisementBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.BufferUnderflowException

//import kotlinx.android.synthetic.main.activity_main.*

class MyAdvertisementActivity : BaseActivity() {
    private lateinit var binding: ActivityMyAdvertisementBinding
    val userId = MMKV.mmkvWithID("http")!!.getString("UserId", "")
    var shop_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAdvertisementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shop_id = intent.getBundleExtra("bundle")!!.getString("shopId").toString()


        if (userId != null) {
            getNotificationItemCount(userId)
        }
        initVM()
        initClick()

    }

    private fun initVM() {

    }

    private fun initClick() {
        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(this, ShopNotifyActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnReturn.setOnClickListener {
            finish()
        }
        binding.btnKnowMore.setOnClickListener {
            val url = "http://www.hkshopu.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        binding.layoutKeywordAdvertisement.setOnClickListener {
            val intent = Intent(this, KeywordAdvertisementActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId", shop_id)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutRecommendedAdvertisement.setOnClickListener {
            val intent = Intent(this, RecommendAdvertisementActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId", shop_id)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutMarketAdvertisement.setOnClickListener {
            val intent = Intent(this, MarketAdvertisementActivity::class.java)
            var bundle = Bundle()
            bundle.putString("shopId", shop_id)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

    }

    private fun  getNotificationItemCount (user_id: String) {
        val url = ApiConstants.API_HOST+"user_detail/${user_id}/notification_count/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var notificationItemCount : String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getNotificationItemCount", "返回資料 resStr：" + resStr)
                    Log.d("getNotificationItemCount", "返回資料 ret_val：" + ret_val)
                    if (status == 0) {
                        val jsonArray: JSONArray = json.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            notificationItemCount = jsonArray.get(i).toString()
                        }
                        Log.d(
                            "getNotificationItemCount",
                            "返回資料 jsonArray：" + notificationItemCount
                        )

                        runOnUiThread {
//                            binding!!.tvNotifycount.text = notificationItemCount
                            if(notificationItemCount!!.equals("0")){
                                binding!!.tvNotifycount.visibility = View.GONE
                            }else{
                                binding!!.tvNotifycount.visibility = View.VISIBLE
                            }
                        }
                    }else{
                        runOnUiThread {
//                            binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: JSONException: ${e.toString()}")
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: IOException: ${e.toString()}")
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
//                    binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

}