package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductDetailedPageForBuyer_RecommendedShopBean {

    //  Same Shop Products Columns

    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("shop_title")
    var shop_title: String = ""

    @SerializedName("shop_icon")
    var shop_icon: String = ""

    @SerializedName("liked")
    var liked: String = ""

    @SerializedName("rating")
    var rating: Double = 0.0

    @SerializedName("follow_count")
    var follow_count: Int = 0

    @SerializedName("followed")
    var followed: String = ""

    @SerializedName("shop_rating")
    var shop_rating: Double = 0.0


    //sponsor
    @SerializedName("identity")
    var identity: String = ""

    @SerializedName("background_is_show")
    var background_is_show: String = ""

    @SerializedName("badge_is_show")
    var badge_is_show: String = ""

    @SerializedName("frame_is_show")
    var frame_is_show: String = ""


}