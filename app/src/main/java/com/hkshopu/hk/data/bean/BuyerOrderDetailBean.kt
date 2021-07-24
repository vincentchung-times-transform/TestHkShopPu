package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class BuyerOrderDetailBean {
    @SerializedName("order_id")
    var order_id: String= ""

    @SerializedName("order_number")
    var order_number: String= ""

    @SerializedName("shop_id")
    var shop_id: String= ""

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String = ""

    @SerializedName("product_pic")
    var product_pic: String = ""

    @SerializedName("count")
    var count: Int = 0

    @SerializedName("sub_total")
    var sub_total: Double = 0.0

}