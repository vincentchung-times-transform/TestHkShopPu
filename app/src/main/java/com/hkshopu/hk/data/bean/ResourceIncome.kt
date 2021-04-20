package com.hkshopu.hk.data.bean

import com.hkshopu.hk.R
import com.hkshopu.hk.ui.main.fragment.MyStoreFragment
import com.hkshopu.hk.ui.main.fragment.OrderDoneFragment
import com.hkshopu.hk.ui.main.fragment.OrderOngoingFragment
import com.hkshopu.hk.ui.main.fragment.ShopFunctionFragment

interface ResourceIncome {
    companion object {
        val tabList = listOf(
            R.string.income_tab1, R.string.income_tab2
        )

        val pagerFragments = listOf(
            OrderDoneFragment.newInstance(), OrderOngoingFragment.newInstance()
        )

    }
}