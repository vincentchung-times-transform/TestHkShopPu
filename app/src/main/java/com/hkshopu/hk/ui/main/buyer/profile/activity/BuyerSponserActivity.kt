package com.HKSHOPU.hk.ui.main.buyer.profile.activity

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
import com.HKSHOPU.hk.data.bean.SponserCostBean

import com.HKSHOPU.hk.databinding.ActivityShopincomeBinding
import com.HKSHOPU.hk.databinding.ActivitySponserBinding
import com.HKSHOPU.hk.databinding.ActivitySponserBuyerBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.*
import com.HKSHOPU.hk.ui.main.notification.activity.NotificationActivity
import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopSponserEditActivity
import com.HKSHOPU.hk.ui.main.seller.shop.fragment.*
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerSponserActivity : BaseActivity() {
    private lateinit var binding: ActivitySponserBuyerBinding
    val tabList = listOf(
        R.string.sponser, R.string.sponser_buyer_tab2, R.string.sponser_buyer_tab3, R.string.sponser_buyer_tab4
    )

    val pagerFragments = listOf(
        SponserBuyerFragment.newInstance(), SponserBuyer2Fragment.newInstance(), SponserBuyer3Fragment.newInstance(), SponserBuyer4Fragment.newInstance()
    )

    val userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var sponserId = MMKV.mmkvWithID("http").getString("SponserBuyerId", "").toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySponserBuyerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initFragment()
        initClick()
        initEvent()
        getNotificationItemCount(userId)
        getSponserCost()
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
            var tabName = getString(tabList[position])
            tab.view.setOnClickListener {
                ToEditPage_checkbytab(tabName)
            }
        }.attach()
        binding!!.mviewPager.isUserInputEnabled = false
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding!!.ivNotifyClick.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventChangeSponserBuyerTab -> {
                        var index = it.index
                        ToEditPage_checkbytab(index)
                    }
                    is EventChangeSponserBuyerTabPosition -> {
                        var index = it.index
                        binding!!.mviewPager.setCurrentItem(index,false)
                    }
                }
            }, {
                it.printStackTrace()
            })
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
    private fun getSponserCost() {
        val url = ApiConstants.API_HOST + "sponsor/sponsor_buyer/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                CommonVariable.costlist.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getSponserCost", "返回資料 resStr：" + resStr)
                    Log.d("getSponserCost", "返回資料 ret_val：" + ret_val)
                    if (status == 0) {
                        val jsonArray: JSONArray = json.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val sponserCostBean: SponserCostBean =
                                Gson().fromJson(jsonObject.toString(), SponserCostBean::class.java)
                            CommonVariable.costlist.add(sponserCostBean)
                        }

                        runOnUiThread {

                        }
                    } else {
                        runOnUiThread {
//                            binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d(
                        "getSponserCost_errormessage",
                        "GetSponserCost: JSONException: ${e.toString()}"
                    )
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d(
                        "getSponserCost_errormessage",
                        "GetSponserCost: IOException: ${e.toString()}"
                    )
                    runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d(
                    "getSponserCost_errormessage",
                    "GetSponserCost: IOException: ${ErrorResponse.toString()}"
                )
                runOnUiThread {
//                    binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun ToEditPage_checkbytab(tabtext:String) {
        Log.d("ToEditPage_checkbytab", "sponserId: ${sponserId}")
        Log.d("ToEditPage_checkbytab", "tabtext: ${tabtext}")
        if (sponserId in tabtext){
            when(tabtext){
                "冥王星/天王星贊助商"->{
                    val intent = Intent(this@BuyerSponserActivity, BuyerSponserEditActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("index","1")
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
                "金星贊助商"->{
                    val intent = Intent(this@BuyerSponserActivity, BuyerSponserEditActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("index","2")
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
                "水星贊助商"->{
                    val intent = Intent(this@BuyerSponserActivity, BuyerSponserEditActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("index","3")
                    intent.putExtra("bundle",bundle)
                    startActivity(intent)
                }
            }
        }
    }
}