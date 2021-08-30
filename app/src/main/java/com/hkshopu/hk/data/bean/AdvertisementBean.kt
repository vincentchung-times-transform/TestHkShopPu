package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class AdvertisementBean {
    @SerializedName("iv_ad_icon")
    var iv_ad_icon: String = ""

    @SerializedName("tv_ad_name")
    var tv_ad_name: String = ""

    @SerializedName("tv_ad_after_days_over")
    var tv_ad_after_days_over: Int = 0

    @SerializedName("tv_ad_keyword")
    var tv_ad_keyword: Int = 0

    @SerializedName("tv_ad_expenditure")
    var tv_ad_expenditure: Int = 0

    @SerializedName("ad_status")
    var ad_status: String = ""

    @SerializedName("layout_ad_statusBtn")
    var layout_ad_statusBtn: String = ""

    @SerializedName("iv_ad_status")
    var iv_ad_status: String = ""

    @SerializedName("tv_ad_status")
    var tv_ad_status: String = ""


}