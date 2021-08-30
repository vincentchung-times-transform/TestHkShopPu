package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class AdvertisementDetailedInfoBean {
    //product
    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_pic")
    var product_pic: String = ""

    @SerializedName("min_price")
    var min_price: String = ""

    @SerializedName("max_price")
    var max_price: String = ""

    //shop
    @SerializedName("shop_rate")
    var shop_rate: String = ""

    @SerializedName("rate_count")
    var rate_count: String = ""

    @SerializedName("start_datetime")
    var start_datetime: String = ""

    @SerializedName("end_datetime")
    var end_datetime: String = ""

    @SerializedName("budget_amount")
    var budget_amount: String = ""

    @SerializedName("budget_type")
    var budget_type: String = ""

    @SerializedName("ad_period_type")
    var ad_period_type: String = ""

    @SerializedName("ad_img")
    var ad_img: String = ""

    @SerializedName("shop_title")
    var shop_title: String = ""

    @SerializedName("shop_icon")
    var shop_icon: String = ""

    @SerializedName("keyword_list")
    var keyword_list: ArrayList<ItemKeywordAd> = arrayListOf()

    @SerializedName("bid")
    var bid: String = ""
}

