package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopListBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_pic")
    var shop_pic: String= ""

    @SerializedName("product_count")
    var product_count: String= ""

    @SerializedName("rating")
    var rating: String= ""

    @SerializedName("follower")
    var follower: String= ""

    @SerializedName("income")
    var income: String= ""


}