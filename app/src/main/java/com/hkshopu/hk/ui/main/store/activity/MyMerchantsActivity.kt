package com.hkshopu.hk.ui.main.store.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.data.bean.ResourceMerchant
import com.hkshopu.hk.databinding.ActivityMymechantsBinding

//import kotlinx.android.synthetic.main.activity_main.*

class MyMerchantsActivity : BaseActivity() {
    private lateinit var binding: ActivityMymechantsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMymechantsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initFragment()
        initVM()
        initClick()

    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceMerchant.pagerFragments[position]
            }

            override fun getItemCount(): Int {
                return ResourceMerchant.tabList.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceMerchant.tabList[position])

        }.attach()
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }
    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvAddproduct.setOnClickListener {

//            val intent = Intent(this, RegisterActivity::class.java)
//            startActivity(intent)
        }
//
//        btn_Login.setOnClickListener {
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        btn_Skip.setOnClickListener {
//            val intent = Intent(this, ShopmenuActivity::class.java)
//            startActivity(intent)
//        }

    }


}