package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class MyProductBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_price")
    var product_price: Int = 0

    @SerializedName("quantity")
    var quantity: Int = 0

    @SerializedName("min_price")
    var min_price: Int? = 0

    @SerializedName("max_price")
    var max_price: Int? = 0

    @SerializedName("pic_path")
    var pic_path: String= ""

    @SerializedName("product_status")
    var product_status: String = ""

}