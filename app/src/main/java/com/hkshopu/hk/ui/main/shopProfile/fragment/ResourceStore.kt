package com.HKSHOPU.hk.ui.main.shopProfile.fragment

import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R

interface ResourceStore {
    companion object {
        val tabList = listOf(
            R.string.tab1, R.string.tab2
        )
        val tabIconList = listOf(R.mipmap.ic_shopfunction,R.mipmap.ic_mystore_1)
        val pagerFragments = mutableListOf<Fragment>(
            ShopFunctionFragment.newInstance(), MyStoreFragment.newInstance())
    }
}