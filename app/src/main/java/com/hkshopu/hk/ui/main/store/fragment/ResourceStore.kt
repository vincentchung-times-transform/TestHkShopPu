package com.hkshopu.hk.ui.main.store.fragment

import com.hkshopu.hk.R

interface ResourceStore {
    companion object {
        val tabList = listOf(
            R.string.tab1, R.string.tab2
        )
        val tabIconList = listOf(R.mipmap.ic_shopfunction,R.mipmap.ic_mystore_1)
        val pagerFragments = listOf(
            ShopFunctionFragment.newInstance(), MyStoreFragment.newInstance())
    }
}