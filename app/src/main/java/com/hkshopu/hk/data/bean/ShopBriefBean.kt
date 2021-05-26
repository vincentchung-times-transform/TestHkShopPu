package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopBriefBean {
    @SerializedName("shop_name")
    var shop_name: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("background_pic")
    var background_pic: String= ""

    @SerializedName("shop_email")
    var shop_email: String= ""

    @SerializedName("phone")
    var phone: String= ""

    @SerializedName("long_description")
    var long_description: String= ""

}