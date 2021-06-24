package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductDetailedPageForBuyer_RecommendedProductsBean {

    //  Similar Products Columns
    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("product_category_id")
    var product_category_id: String = ""

    @SerializedName("product_title")
    var product_title: String= ""

    @SerializedName("product_description")
    var product_description: String= ""

    @SerializedName("product_price")
    var product_price: Int = 0

    @SerializedName("product_status")
    var product_status: String = ""

    @SerializedName("pic_path")
    var pic_path: String = ""

    @SerializedName("product_spec_on")
    var product_spec_on: String = ""

    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("shop_title")
    var shop_title: String = ""

    @SerializedName("like")
    var like: String = ""

    @SerializedName("price")
    var price: MutableList<Int> = mutableListOf()

    @SerializedName("min_price")
    var min_price: Int = 0

    @SerializedName("max_price")
    var max_price: Int = 0

    @SerializedName("min_quantity")
    var min_quantity: Int = 0

    @SerializedName("max_quantity")
    var max_quantity: Int = 0

    @SerializedName("sum_quantity")
    var sum_quantity: Int = 0

    //  Same Shop Products Columns

    @SerializedName("shop_icon")
    var shop_icon: String = ""

    @SerializedName("liked")
    var liked: String = ""

    @SerializedName("rating")
    var rating: Double = 0.0

    @SerializedName("follow_count")
    var follow_count: Int = 0

    @SerializedName("shop_rating")
    var shop_rating: Double = 0.0

    @SerializedName("followed")
    var followed: String = ""



}