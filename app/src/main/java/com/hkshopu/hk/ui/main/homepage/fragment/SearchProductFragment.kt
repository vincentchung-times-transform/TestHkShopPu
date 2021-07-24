package com.HKSHOPU.hk.ui.main.homepage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ResourceSearch


import org.jetbrains.anko.find

class SearchProductFragment : Fragment() {

    companion object {
        fun newInstance(): SearchProductFragment {
            val args = Bundle()
            val fragment = SearchProductFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var tabs :TabLayout
    lateinit var mviewPager :ViewPager2

    var userId = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_search_product, container, false)

        tabs = v.find<TabLayout>(R.id.tabs)
        mviewPager = v.find<ViewPager2>(R.id.mviewPager)

        initView()
        initFragment()
        return v
    }

    private fun initView(){

    }
    private fun initFragment() {
                mviewPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return ResourceSearch.pagerFragments_Product[position]
            }

            override fun getItemCount(): Int {
                return ResourceSearch.tabList.size
            }
        }
        TabLayoutMediator(tabs, mviewPager) { tabs, position ->
            tabs.text = getString(ResourceSearch.tabList[position])

        }.attach()
        mviewPager.isSaveEnabled = false
    }

}