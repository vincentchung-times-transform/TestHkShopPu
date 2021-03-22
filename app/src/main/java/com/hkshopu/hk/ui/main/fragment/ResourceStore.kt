package com.hkshopu.hk.ui.main.fragment

import com.hkshopu.hk.R

interface ResourceStore {
    companion object {
        val tabList = listOf(
            R.string.tab1, R.string.tab2
        )
        val pagerFragments = listOf(
            ShopFunctionFragment.newInstance(), MyStoreFragment.newInstance())
    }
}