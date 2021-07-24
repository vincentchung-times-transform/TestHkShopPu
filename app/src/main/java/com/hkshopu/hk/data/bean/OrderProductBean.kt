package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class OrderProductBean {
    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_spec_id")
    var product_spec_id: String = ""

    @SerializedName("spec_desc_1")
    var spec_desc_1: String = ""

    @SerializedName("spec_desc_2")
    var spec_desc_2: String = ""

    @SerializedName("spec_dec_1_items")
    var spec_dec_1_items: String=""

    @SerializedName("spec_dec_2_items")
    var spec_dec_2_items: String= ""

    @SerializedName("quantity")
    var quantity: Int = 0

    @SerializedName("product_pic")
    var product_pic: String = ""

    @SerializedName("price")
    var price: Int = 0

}