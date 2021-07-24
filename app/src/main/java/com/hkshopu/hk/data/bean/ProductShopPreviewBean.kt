package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductShopPreviewBean {
    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("product_category_id")
    var product_category_id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_price")
    var product_price: Int = 0

    @SerializedName("quantity")
    var quantity: Int = 0

    @SerializedName("product_description")
    var product_description: String = ""

    @SerializedName("shipping_fee")
    var shipping_fee: Int = 0

    @SerializedName("weight")
    var weight: Int = 0

    @SerializedName("longterm_stock_up")
    var longterm_stock_up: Int = 0

    @SerializedName("new_secondhand")
    var new_secondhand: String= ""

    @SerializedName("length")
    var length: Int = 0
    @SerializedName("width")
    var width: Int = 0

    @SerializedName("height")
    var height: Int = 0

    @SerializedName("like")
    var like: Int = 0

    @SerializedName("seen")
    var seen: Int = 0

    @SerializedName("sold_quantity")
    var sold_quantity: Int = 0

    @SerializedName("pic_path")
    var pic_path: String= ""

    @SerializedName("product_status")
    var product_status: String = ""

    @SerializedName("product_spec_on")
    var product_spec_on: String = ""

    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("shop_title")
    var shop_title: String = ""

    @SerializedName("liked")
    var liked: String = ""

    @SerializedName("min_price")
    var min_price: Int? = 0

    @SerializedName("max_price")
    var max_price: Int? = 0

    @SerializedName("min_quantity")
    var min_quantity: Int? = 0

    @SerializedName("max_quantity")
    var max_quantity: Int? = 0

    @SerializedName("sum_quantity")
    var sum_quantity: Int? = 0

}