package com.HKSHOPU.hk.ui.main.seller.notification.activity

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
import com.HKSHOPU.hk.component.EventToProductSearch
import com.HKSHOPU.hk.component.EventToShopSearch
import com.HKSHOPU.hk.data.bean.ResourceNotification
import com.HKSHOPU.hk.data.bean.ResourceSearch

import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.PurchaseListFragment
import com.HKSHOPU.hk.ui.main.seller.notification.fragment.NotificationFragment
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.KeyboardUtil
import com.tencent.mmkv.MMKV


//import kotlinx.android.synthetic.main.activity_main.*

class NotificationActivity : BaseActivity() {
    private lateinit var binding: ActivityNotificationBinding
    val userId = MMKV.mmkvWithID("http")!!.getString("UserId", "");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initView()
        initFragment()
        initClick()

    }

    private fun initVM() {

    }

    private fun initView() {
        var notificationFragment = NotificationFragment.newInstance()
        ResourceNotification.tabList_notification.add("")
        ResourceNotification.pagerFragments_notification.add(notificationFragment)
    }

    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceNotification.pagerFragments_notification[position]
            }

            override fun getItemCount(): Int {
                return ResourceSearch.tabList_search.size
            }

        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = ResourceNotification.tabList_notification[position].toString()
        }.attach()

        binding!!.mviewPager.isSaveEnabled = false

//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    @JvmName("getUserId1")
    fun getUserId(): String? {
        return userId
    }

}