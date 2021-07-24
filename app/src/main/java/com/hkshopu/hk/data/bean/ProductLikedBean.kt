package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductLikedBean {

    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_description")
    var product_description: String= ""

    @SerializedName("product_price")
    var product_price: Int = 0

    @SerializedName("pic_path")
    var pic_path: String= ""


    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("min_price")
    var min_price: Int = 0

    @SerializedName("max_price")
    var max_price: Int = 0

    @SerializedName("liked")
    var liked: String = ""



}