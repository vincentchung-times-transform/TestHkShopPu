package com.HKSHOPU.hk.ui.main.wallet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.AddValueHistoryBean
import com.HKSHOPU.hk.data.bean.AddValueWalletBean
import com.HKSHOPU.hk.data.bean.ResourceMyWallet

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.notification.activity.NotificationActivity
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


//import kotlinx.android.synthetic.main.activity_main.*

class MyWalletActivity : BaseActivity() {
    private lateinit var binding: ActivityMyWalletBinding
    val userId = MMKV.mmkvWithID("http")!!.getString("UserId", "")
    var shopId = ""
    var walletId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle = intent.getBundleExtra("bundle")
        shopId = bundle!!.getString("shopId").toString()
        Log.d("MyWalletActivity", "shopId: ${shopId}")

        if (userId != null) {
            getNotificationItemCount(userId)
        }
        getWalletShopRead(shopId)

        initVM()
        initView()

    }

    private fun initVM() {

    }

    private fun initView() {
        binding.tvSmallValue.setText("158")
        binding.tvMediumValue.setText("398")
        binding.tvBigValue.setText("788")

//        var notificationFragment = NotificationFragment.newInstance()
//        ResourceNotification.tabList_notification.add("")
//        ResourceNotification.pagerFragments_notification.add(notificationFragment)

        initClick()
    }

    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                var fragment = ResourceMyWallet.pagerFragments_myWallet[position]
                var args = Bundle()
                args.putString("walletId", walletId.toString())
                fragment.arguments = args
                return fragment
            }

            override fun getItemCount(): Int {
                return ResourceMyWallet.pagerFragments_myWallet.size
            }

        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceMyWallet.tabList_myWallet.get(position))
        }.attach()

        binding!!.mviewPager.isSaveEnabled = false

//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initClick() {

        binding!!.ivNotify.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.layoutSmallValue.setOnClickListener {
            val intent = Intent(this, AddValueActivity::class.java)
            var bundle  = Bundle()
            bundle.putString("addValue", "159")
            bundle.putString("shopId", shopId)
            bundle.putString("walletId", walletId)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutMediumValue.setOnClickListener {
            val intent = Intent(this, AddValueActivity::class.java)
            var bundle  = Bundle()
            bundle.putString("addValue", "398")
            bundle.putString("shopId", shopId)
            bundle.putString("walletId", walletId)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
        binding.layoutBigValue.setOnClickListener {
            val intent = Intent(this, AddValueActivity::class.java)
            var bundle  = Bundle()
            bundle.putString("addValue", "788")
            bundle.putString("shopId", shopId)
            bundle.putString("walletId", walletId)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }

    }

    @JvmName("getUserId1")
    fun getUserId(): String? {
        return userId
    }

    fun getWalletShopRead(
        shop_id: String
    )
    {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        Log.d("getWalletCreate", "shop_id: ${shop_id}")
        val url = ApiConstants.API_SWAGGER+"wallet/shop/${shop_id}"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var code: Int = 0
                try {
                    resStr = response.body()!!.string()
                    code = response.code()

                    val jsonObject = JSONObject(resStr)
                    Log.d("getWalletCreate", "返回資料 resStr：" + resStr)
                    Log.d("getWalletCreate", "返回資料 code：" + code.toString())

                    if (code == 200) {

                        val addValueWalletBean: AddValueWalletBean =
                            Gson().fromJson(jsonObject.toString(), AddValueWalletBean::class.java)
                        Log.d("getWalletCreate", "解析資料 addValueBean：" + addValueWalletBean.toString())

                        walletId = addValueWalletBean.id
                        shopId = addValueWalletBean.shop_id

                        runOnUiThread {
                            binding.tvStoredValue.setText("$${addValueWalletBean.balance.toString()}")
                            initFragment()

                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MyWalletActivity, "Wallet Get Failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                } catch (e: JSONException) {
                    com.paypal.pyplcheckout.sca.runOnUiThread {
                        Toast.makeText(this@MyWalletActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletCreate", "JSONException: ${e.toString()}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    com.paypal.pyplcheckout.sca.runOnUiThread {
                        Toast.makeText(this@MyWalletActivity, "網路異常", Toast.LENGTH_SHORT).show()
                        Log.d("getWalletCreate", "IOException: ${e.toString()}")
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                com.paypal.pyplcheckout.sca.runOnUiThread {
                    Toast.makeText(this@MyWalletActivity, "網路異常", Toast.LENGTH_SHORT).show()
                    Log.d("getWalletCreate", "ErrorResponse: ${ErrorResponse.toString()}")
                }
            }
        })
        web.Get_Data(
            url
        )
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