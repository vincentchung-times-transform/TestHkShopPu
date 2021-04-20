package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopInfoBean {
    @SerializedName("id")
    var id: Int = 0;

    @SerializedName("user_id")
    var user_id: Int = 0;

    @SerializedName("shop_category_id")
    var shop_category_id: String = ""

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_pic")
    var shop_pic: String= ""

    @SerializedName("shop_description")
    var shop_description: String= ""


}