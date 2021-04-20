package com.hkshopu.hk.ui.main.activity


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog

import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson


import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventAddShopSuccess
import com.hkshopu.hk.component.EventGetShopListSuccess
import com.hkshopu.hk.data.bean.ShopCategoryBean
import com.hkshopu.hk.databinding.ActivityMainBinding
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.net.WebListener
import com.hkshopu.hk.ui.main.fragment.*
import com.hkshopu.hk.ui.user.activity.LoginActivity
import com.hkshopu.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class ShopmenuActivity: BaseActivity(), ViewPager.OnPageChangeListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var manager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()
        initView()
        initClick()

    }
    private val fragments = mutableListOf<Fragment>()
    private fun initFragment() {
        manager = supportFragmentManager
        if (fragments.isNotEmpty())return
        val FirstFragment = FirstFragment.newInstance()
//        val FirstFragment = ShopInfoFragment.newInstance()
        val SecondFragment = SecondFragment.newInstance()
        val ShopListFragment = ShopListFragment.newInstance()
        fragments.add(FirstFragment)
        fragments.add(SecondFragment)
        fragments.add(ShopListFragment)
        binding.viewPager.adapter = object : FragmentPagerAdapter(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
            override fun getItem(position: Int) =  fragments[position]
            override fun getCount() = fragments.size
        }
        binding.viewPager.setPagingEnabled(false)
        binding.viewPager.addOnPageChangeListener(this)
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }

    fun initView() {
        binding.bottomNavigationViewLinear.setNavigationChangeListener { view, position ->
//            Log.d("ShopMenuActivity", "BottomView position：" + position)
            binding.viewPager.setCurrentItem(position, true);

        }

    }

    fun initClick(){


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }


}
