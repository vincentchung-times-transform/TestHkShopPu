package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopInfoBean {
    @SerializedName("id")
    var id: Int = 0;

    @SerializedName("user_id")
    var user_id: Int = 0;

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_pic")
    var shop_pic: String= ""

    @SerializedName("shop_description")
    var shop_description: String= ""

    @SerializedName("background_pic")
    var background_pic: String= ""

    @SerializedName("shop_phone")
    var shop_phone: String= ""

    @SerializedName("shop_email")
    var shop_email: String= ""
    @SerializedName("email_on")
    var email_on: String= ""

    @SerializedName("long_description")
    var long_description: String= ""

    @SerializedName("facebook_on")
    var facebook_on: String= ""

    @SerializedName("instagram_on")
    var instagram_on: String= ""

    @SerializedName("product_count")
    var product_count: Int = 0

    @SerializedName("rating")
    var rating: Int = 0

    @SerializedName("follower")
    var follower: Int = 0

    @SerializedName("income")
    var income: Int = 0

}