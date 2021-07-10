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

class SearchStoreFragment : Fragment() {

    companion object {
        fun newInstance(): SearchStoreFragment {
            val args = Bundle()
            val fragment = SearchStoreFragment()
            fragment.arguments = args
            return fragment
        }
    }
    lateinit var tabs : TabLayout
    lateinit var mviewPager : ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_search_store, container, false)
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
                return ResourceSearch.pagerFragments_Store[position]
            }

            override fun getItemCount(): Int {
                return ResourceSearch.tabList_store.size
            }
        }
        TabLayoutMediator(tabs, mviewPager) { tabs, position ->
            tabs.text = getString(ResourceSearch.tabList_store[position])

        }.attach()
        mviewPager.isSaveEnabled = false
    }

}