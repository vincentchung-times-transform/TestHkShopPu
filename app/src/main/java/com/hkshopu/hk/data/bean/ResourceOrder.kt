package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.seller.order.adapter.CanceledOrderAdapter
import com.HKSHOPU.hk.ui.main.seller.order.fragment.OderCanceledForSalesFragment
import com.HKSHOPU.hk.ui.main.seller.order.fragment.OderCompletedForSalesFragment
import com.HKSHOPU.hk.ui.main.seller.order.fragment.ToBeReceivedForSalesFragment
import com.HKSHOPU.hk.ui.main.seller.order.fragment.ToBeShippedForSalesFragment


interface ResourceOrder {
    companion object {

        val tabList_saleslist= listOf(
            R.string.sales_tab1,R.string.sales_tab2, R.string.sales_tab3,R.string.sales_tab4
        )
        //        ,R.string.purchase_tab4
        val pagerFragments_saleslist = listOf(
            ToBeShippedForSalesFragment.newInstance(),
            ToBeReceivedForSalesFragment.newInstance(),
            OderCompletedForSalesFragment.newInstance(),
            OderCanceledForSalesFragment.newInstance()
        )

    }
}