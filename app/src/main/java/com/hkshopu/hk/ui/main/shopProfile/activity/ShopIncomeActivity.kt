package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.data.bean.ResourceIncome

import com.HKSHOPU.hk.databinding.ActivityShopincomeBinding

//import kotlinx.android.synthetic.main.activity_main.*

class ShopIncomeActivity : BaseActivity() {
    private lateinit var binding: ActivityShopincomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopincomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVM()
        initFragment()
        initClick()

    }

    private fun initVM() {

    }
    private fun initFragment() {
        binding!!.mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceIncome.pagerFragments[position]
            }

            override fun getItemCount(): Int {
                return ResourceIncome.tabList.size
            }
        }
        TabLayoutMediator(binding!!.tabs, binding!!.mviewPager) { tab, position ->
            tab.text = getString(ResourceIncome.tabList[position])

        }.attach()
//        binding.setViewPager(binding.mviewPager, arrayOf(getString(R.string.product),getString(R.string.info)))
    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnReturn.setOnClickListener {
            finish()
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