package com.HKSHOPU.hk.ui.main.notification.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventToProductSearch
import com.HKSHOPU.hk.component.EventToShopSearch
import com.HKSHOPU.hk.data.bean.*

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.PurchaseListFragment
import com.HKSHOPU.hk.ui.main.notification.fragment.NotificationFragment
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList


//import kotlinx.android.synthetic.main.activity_main.*

class NotificationActivity : BaseActivity() {
    private lateinit var binding: ActivityNotificationBinding
    val userId = MMKV.mmkvWithID("http")!!.getString("UserId", "");
    var url_forNotification_Id = ApiConstants.API_HOST + "user_detail/"+userId+"/notification_identity/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVM()
        initClick()
        getNotification_Identity(url_forNotification_Id)
    }

    private fun initVM() {

    }

    private fun initView() {

    }

    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceNotification.pagerFragments_notification[position]
            }

            override fun getItemCount(): Int {
                return ResourceNotification.tabList_notification.size
            }

        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = ResourceNotification.tabList_notification[position]
        }.attach()

        binding!!.mviewPager.isSaveEnabled = false

//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun getNotification_Identity(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list_idTotal = ArrayList<String>()
                    list_idTotal.clear()
                val list = ArrayList<ShopIdBean>()
                    list.clear()
                ResourceNotification.tabList_notification.clear()
                ResourceNotification.pagerFragments_notification.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getNotification_Id", "返回資料 resStr：" + resStr)
                    Log.d("getNotification_Id", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val jsonObject = json.getJSONObject("data")
                        val user_Id = jsonObject.get("user_id")
                        val user_name = jsonObject.get("user_name")
                        list_idTotal.add(user_Id.toString())
                        val translations: JSONArray = jsonObject.getJSONArray("shop_list")
                        Log.d("getNotification_Id", "返回資料 List：" + translations.toString())
                        for (i in 0 until translations.length()) {
                            val jsonObject_shop: JSONObject = translations.getJSONObject(i)
                            val shopIdBean: ShopIdBean =
                                Gson().fromJson(jsonObject_shop.toString(), ShopIdBean::class.java)
                            list.add(shopIdBean)
                            list_idTotal.add(shopIdBean.shop_id)
                        }

                        ResourceNotification.tabList_notification.add(user_name.toString())
                        if(list.isNotEmpty()){
                            for (i in 0 until list.size) {
                                ResourceNotification.tabList_notification.add(list[i].shop_title)
                                runOnUiThread {
                                    initView()
                                }
                            }
                        }
                        for (j in 0 until list_idTotal.size) {
                            runOnUiThread {
                                var notificationFragment = NotificationFragment.newInstance()
                                notificationFragment.setMyTag(list_idTotal[j])
                                ResourceNotification.pagerFragments_notification.add(notificationFragment)
                            }
                        }
                        runOnUiThread {
                            initFragment()
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getShopCategory_errorMessage", "JSONException: ${e.toString()}")

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getShopCategory_errorMessage", "IOException: ${e.toString()}")
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getShopCategory_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
            }
        })
        web.Get_Data(url)
    }

    @JvmName("getUserId1")
    fun getUserId(): String? {
        return userId
    }

}