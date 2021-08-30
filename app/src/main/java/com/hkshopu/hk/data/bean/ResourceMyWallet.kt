package com.HKSHOPU.hk.data.bean

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.wallet.fragment.StoredValueFinishedFragment
import com.HKSHOPU.hk.ui.main.wallet.fragment.StoredValueReviewingFragment
import com.HKSHOPU.hk.ui.main.wallet.fragment.TransactionHistoryFragment

interface ResourceMyWallet {
    companion object {
        val tabList_myWallet= listOf(
            R.string.stored_value_under_review, R.string.stored_value,  R.string.transaction_record)
        val pagerFragments_myWallet = listOf(
            StoredValueReviewingFragment.newInstance(), StoredValueFinishedFragment.newInstance(), TransactionHistoryFragment.newInstance()
        )
    }
}