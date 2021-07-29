package com.HKSHOPU.hk.data.bean

import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.ui.main.homepage.fragment.*

interface ResourceNotification {
    companion object {
        val tabList_notification = arrayListOf<String>()
        val pagerFragments_notification = arrayListOf<Fragment>(
            SearchProductFragment.newInstance()
        )
    }
}