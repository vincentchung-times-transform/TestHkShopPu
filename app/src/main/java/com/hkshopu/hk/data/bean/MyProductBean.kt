package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class MyProductBean {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("min_price")
    var min_price: Int? = null

    @SerializedName("max_price")
    var max_price: Int? = null

    @SerializedName("pic_path")
    var pic_path: String= ""

}