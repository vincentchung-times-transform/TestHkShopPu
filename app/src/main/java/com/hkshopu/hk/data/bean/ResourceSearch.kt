package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.homepage.fragment.*

interface ResourceSearch {
    companion object {
        val tabList_search = listOf(
            R.string.search_tab1, R.string.search_tab2
        )

        val pagerFragments_Search = listOf(
            SearchProductFragment.newInstance(),SearchStoreFragment.newInstance()
        )

        val tabList = listOf(
            R.string.tab_ranking1, R.string.tab_ranking2,R.string.tab_ranking3,R.string.tab_ranking4,R.string.tab_ranking5
        )

        val tabList_store = listOf(
            R.string.tab_ranking1, R.string.tab_ranking2,R.string.tab_ranking3
        )

        val pagerFragments_Product = listOf(
            RankingAllSearchFragment.newInstance(),RankingLatestSearchFragment.newInstance(),RankingTopSaleSearchFragment.newInstance(),
            RankingCheapSearchFragment.newInstance(),RankingExpensiveSearchFragment.newInstance()
        )

        val pagerFragments_Store = listOf(
            StoreSearchAllFragment.newInstance(),StoreSearchLatestFragment.newInstance(),StoreSearchTopFragment.newInstance()
        )

    }
}