package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.shopProfile.fragment.OrderDoneFragment
import com.HKSHOPU.hk.ui.main.shopProfile.fragment.OrderOngoingFragment

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