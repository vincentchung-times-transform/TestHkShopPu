package com.hkshopu.hk.data.bean

import com.hkshopu.hk.R
import com.hkshopu.hk.ui.main.store.fragment.*

interface ResourceMerchant {
    companion object {

        val tabList = listOf(
            R.string.merchants_tab1, R.string.merchants_tab2,R.string.merchants_tab3
        )

        val pagerFragments = listOf(
            MerchantsOndeckFragment.newInstance(), MerchantsSoldFragment.newInstance(), MerchantsNoPopFragment.newInstance()
        )

    }
}