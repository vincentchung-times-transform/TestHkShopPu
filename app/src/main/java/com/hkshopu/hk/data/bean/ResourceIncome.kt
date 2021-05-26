package com.hkshopu.hk.data.bean

import com.hkshopu.hk.R
import com.hkshopu.hk.ui.main.store.fragment.OrderDoneFragment
import com.hkshopu.hk.ui.main.store.fragment.OrderOngoingFragment

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