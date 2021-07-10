package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopRecommendHomeBean {
    @SerializedName("shop_id")
    var shop_id: String = "";

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("shop_average_ratings")
    var shop_average_ratings: Double = 0.0;

    @SerializedName("shop_followed")
    var shop_followed: String= ""

    @SerializedName("product_pics")
    lateinit var product_pics: List<String>

}