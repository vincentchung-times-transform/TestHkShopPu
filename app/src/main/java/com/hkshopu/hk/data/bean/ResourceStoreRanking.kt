package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.homepage.fragment.*

interface ResourceStoreRanking {
    companion object {
        val tabList = listOf(
            R.string.tab_ranking1, R.string.tab_ranking2,R.string.tab_ranking3
        )

        val pagerFragments = listOf(
            StoreRankingAllFragment.newInstance(), StoreRankingLatestFragment.newInstance(), StoreRankingTopFragment.newInstance()
        )

        val tabList_reserve = listOf(
            R.string.tab_ranking4,R.string.tab_ranking5
        )
        val pagerFragments_reserve = listOf(
            StoreRankingCheapFragment.newInstance(), StoreRankingExpensiveFragment.newInstance()
        )
    }
}