package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductSearchBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("user_id")
    var user_id: String = ""

    @SerializedName("page_id")
    var page_id: Int = 0

    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("seq")
    var seq: Int = 0

    @SerializedName("pic_path")
    var pic_path: String= ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("shop_title")
    var shop_title: String = ""

    @SerializedName("liked")
    var liked: String = ""

    @SerializedName("min_price")
    var min_price: Int? = 0

    @SerializedName("max_price")
    var max_price: Int? = 0

}