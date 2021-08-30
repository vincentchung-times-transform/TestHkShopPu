package com.HKSHOPU.hk.ui.main.buyer.profile.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ResourceMerchant
import com.HKSHOPU.hk.databinding.FragmentPurchaselistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.notification.activity.NotificationActivity

import com.HKSHOPU.hk.ui.main.seller.shop.activity.ShopNotifyActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class PurchaseListFragment : Fragment(R.layout.fragment_purchaselist) {

    companion object {
        fun newInstance(): PurchaseListFragment {
            val args = Bundle()
            val fragment = PurchaseListFragment()
            fragment.arguments = args
            return fragment
        }
    }
    var page_id=""
    private var binding: FragmentPurchaselistBinding? = null
    private var fragmentPurchaselistBinding: FragmentPurchaselistBinding? = null
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        page_id = requireArguments().getString("page_id", "")
        binding = FragmentPurchaselistBinding.bind(view)
        fragmentPurchaselistBinding = binding

        getNotificationItemCount(userId)

        initView()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //go to previous fragemnt
                    //perform your fragment transaction here
                    //pass data as arguments
                    requireActivity().supportFragmentManager.beginTransaction().remove(this@PurchaseListFragment).commit()
                    return@OnKeyListener true
                }
            }
            false
        })
    }
    fun initView() {
        initClick()
        initVM()
        initEvent()
        initFragment()
        getNotificationItemCount(userId)
    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return ResourceMerchant.pagerFragments_purchaselist[position]
            }
            override fun getItemCount(): Int {
                return ResourceMerchant.tabList_purchaselist.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceMerchant.tabList_purchaselist[position])
        }.attach()
        binding!!.mviewPager.setUserInputEnabled(false);
        binding!!.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        if(page_id.isNotEmpty()) {
            requireActivity().runOnUiThread {
                binding!!.mviewPager.setCurrentItem(page_id.toInt(), false)
            }
        }
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    fun initVM() {
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

                        requireActivity().runOnUiThread {
//                            binding!!.tvNotifycount.text = notificationItemCount
                            if(notificationItemCount!!.equals("0")){
                                binding!!.tvNotifycount.visibility = View.GONE
                            }else{
                                binding!!.tvNotifycount.visibility = View.VISIBLE
                            }
                        }
                    }else{
                        activity!!.runOnUiThread {
//                            binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: JSONException: ${e.toString()}")
                    activity!!.runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: IOException: ${e.toString()}")
                    activity!!.runOnUiThread {
//                        binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getNotificationItemCount_errormessage", "GetNotificationItemCount: ErrorResponse: ${ErrorResponse.toString()}")
                activity!!.runOnUiThread {
//                    binding!!.imgViewLoadingBackgroundDetailedProductForBuyer.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }
    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventShopmenuToSpecificPage -> {
                        var index = it.index
                        binding!!.mviewPager.setCurrentItem(index, false)
                    }
                    is EventOrderCompelete ->{
                        binding!!.mviewPager.setCurrentItem(3, false)
                    }
                }
            }, {
                it.printStackTrace()
            })
    }
    fun initClick() {
        binding!!.ivBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this@PurchaseListFragment).commit()
        }
        binding!!.layoutNotify.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentPurchaselistBinding = null
        super.onDestroyView()
    }


}