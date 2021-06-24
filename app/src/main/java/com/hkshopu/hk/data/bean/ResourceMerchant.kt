package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.shopProfile.fragment.*

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