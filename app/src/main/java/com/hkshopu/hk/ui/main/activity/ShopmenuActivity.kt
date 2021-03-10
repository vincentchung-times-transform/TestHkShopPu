package com.hkshopu.hk.ui.main.activity


import android.os.Bundle

import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager


import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.databinding.ActivityMainBinding
import com.hkshopu.hk.ui.main.fragment.FirstFragment
import com.hkshopu.hk.ui.main.fragment.SecondFragment
import com.hkshopu.hk.ui.main.fragment.ShopInfoFragment


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
        val SecondFragment = SecondFragment.newInstance()
        val ShopInfoFragment = ShopInfoFragment.newInstance()
        fragments.add(FirstFragment)
        fragments.add(SecondFragment)
        fragments.add(ShopInfoFragment)
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
            binding.viewPager.setCurrentItem(position, true);
        }
        manager = supportFragmentManager
//        manager.beginTransaction().add(R.id.main, Fragment_main()).commit()

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
