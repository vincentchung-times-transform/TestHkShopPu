package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class KeywordAdBean {
    @SerializedName("ad_header_id")
    var ad_header_id: String = ""

    @SerializedName("ad_detail_id")
    var ad_detail_id: String = ""

    @SerializedName("start_datetime")
    var start_datetime: String = ""

    @SerializedName("end_datetime")
    var end_datetime: String = ""

    @SerializedName("budget_amount")
    var budget_amount: String = ""

    @SerializedName("keyword_count")
    var keyword_count: String = ""

    @SerializedName("count_down")
    var count_down: String = ""

    @SerializedName("budget_type")
    var budget_type: String = ""

    @SerializedName("ad_period_type")
    var ad_period_type: String = ""

    @SerializedName("status")
    var status: String = ""

    //product
    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_pic")
    var product_pic: String = ""

    //shop
    @SerializedName("shop_title")
    var shop_title: String = ""

    @SerializedName("shop_icon")
    var shop_icon: String = ""
}