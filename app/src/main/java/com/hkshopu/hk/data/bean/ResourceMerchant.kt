package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.buyer.profile.fragment.*
import com.HKSHOPU.hk.ui.main.shopProfile.fragment.*

interface ResourceMerchant {
    companion object {

        val tabList = listOf(
            R.string.merchants_tab1, R.string.merchants_tab2,R.string.merchants_tab3
        )

        val pagerFragments = listOf(
            MerchantsOndeckFragment.newInstance(), MerchantsSoldFragment.newInstance(), MerchantsNoPopFragment.newInstance()
        )

        val tabList_purchaselist = listOf(
            R.string.purchase_tab1,R.string.purchase_tab2, R.string.purchase_tab3,R.string.purchase_tab4,R.string.purchase_tab5
        )

        //        ,R.string.purchase_tab4
        val pagerFragments_purchaselist = listOf(
            BuyerPendingPaymentFragment.newInstance(),
            BuyerPendingDeliverFragment.newInstance(),
            BuyerPendingRecieveFragment.newInstance(),
            BuyerOrderCompleteFragment.newInstance(),
            BuyerOrderCancelFragment.newInstance()
        )

    }
}