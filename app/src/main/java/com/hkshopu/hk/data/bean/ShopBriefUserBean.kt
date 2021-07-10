package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopBriefUserBean {
    @SerializedName("shop_id")
    var shop_id: Int = 0

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("background_pic")
    var background_pic: String= ""

    @SerializedName("address_phone")
    var address_phone: String= ""

    @SerializedName("shop_email")
    var shop_email: String= ""

    @SerializedName("long_description")
    var long_description: String= ""

}