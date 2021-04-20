package com.hkshopu.hk.data.bean

import com.hkshopu.hk.R
import com.hkshopu.hk.ui.main.fragment.MyStoreFragment
import com.hkshopu.hk.ui.main.fragment.ShopFunctionFragment

interface ResourceStore {
    companion object {
        val tabList = listOf(
            R.string.tab1, R.string.tab2
        )

        val pagerFragments = listOf(
            ShopFunctionFragment.newInstance(), MyStoreFragment.newInstance()
        )

    }
}