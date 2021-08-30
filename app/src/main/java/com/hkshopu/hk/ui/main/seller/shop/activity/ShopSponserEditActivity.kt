package com.HKSHOPU.hk.ui.main.seller.shop.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ResourceIncome

import com.HKSHOPU.hk.databinding.ActivityShopincomeBinding
import com.HKSHOPU.hk.databinding.ActivitySponserBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.*
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.view.MotionEvent
import android.view.View.OnTouchListener

import android.widget.LinearLayout
import com.HKSHOPU.hk.ui.main.notification.activity.NotificationActivity


//import kotlinx.android.synthetic.main.activity_main.*

class ShopSponserEditActivity : BaseActivity() {
    private lateinit var binding: ActivitySponserBinding
    val tabList = listOf(
        R.string.sponser, R.string.sponser_tab2, R.string.sponser_tab3, R.string.sponser_tab4
    )

    val pagerFragments = listOf(
        SponserSellerFragment.newInstance(), SponserSeller2EditFragment.newInstance(), SponserSeller3EditFragment.newInstance(), SponserSeller4EditFragment.newInstance()
    )
    val userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var index:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySponserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        index = intent.getBundleExtra("bundle")!!.getString("index","").toString()
        Log.d("ShopSponserEditActivity", "index: ${index}")

        initVM()
        initFragment()
        initClick()
        getNotificationItemCount(userId)
    }

    private fun initVM() {
    }
    private fun initFragment() {

        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return pagerFragments[position]
            }
            override fun getItemCount(): Int {
                return tabList.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(tabList[position])
            tab.view.setClickable(false)
        }.attach()
        binding!!.mviewPager.setCurrentItem(index.toInt(),false)
        binding!!.mviewPager.isUserInputEnabled = false
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initClick() {
        binding.ivBack.setOnClickListener {
            val intent = Intent(this@ShopSponserEditActivity, ShopSponserActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding!!.ivNotifyClick.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
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