package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopPreviewBean {
    @SerializedName("shop_id")
    var shop_id: String = ""
    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("long_description")
    var long_description: String= ""

    @SerializedName("background_pic")
    var background_pic: String= ""

    @SerializedName("average_of_shop_ratings")
    var average_of_shop_ratings: Double = 0.0

    @SerializedName("shop_rating_nums")
    var shop_rating_nums: Int = 0

    @SerializedName("product_nums_of_shop")
    var product_nums_of_shop: Int = 0

    @SerializedName("follower_nums_of_shop")
    var follower_nums_of_shop: Int = 0

    @SerializedName("sum_of_sales")
    var sum_of_sales: Int = 0

}