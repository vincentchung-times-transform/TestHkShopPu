package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopDetailedProductForBuyerBean {

    @SerializedName("product_title")
    var product_title: String= ""

    @SerializedName("new_secondhand")
    var new_secondhand: String= ""

    @SerializedName("shop_id")
    var shop_id: String= ""

    @SerializedName("product_description")
    var product_description: String= ""

    @SerializedName("pic")
    var pic: MutableList<String> = mutableListOf()

    @SerializedName("liked_count")
    var liked_count: Int = 0

    @SerializedName("category")
    var category: String = ""

    @SerializedName("average_rating")
    var average_rating: Double = 3.25

    @SerializedName("min_price")
    var min_price: Int = 0

    @SerializedName("max_price")
    var max_price = 0

    @SerializedName("min_quantity")
    var min_quantity: Int = 0

    @SerializedName("max_quantity")
    var max_quantity: Int = 0

    @SerializedName("min_shipment")
    var min_shipment: Int = 0

    @SerializedName("max_shipment")
    var max_shipment: Int = 0

    @SerializedName("selling_count")
    var selling_count: Int = 0

    @SerializedName("liked")
    var liked: String = ""

    @SerializedName("product_spec_on")
    var product_spec_on: String = ""

    @SerializedName("longterm_stock_up")
    var longterm_stock_up: Int = 0

}