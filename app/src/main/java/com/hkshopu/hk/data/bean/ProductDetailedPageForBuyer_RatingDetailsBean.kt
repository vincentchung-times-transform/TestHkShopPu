package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductDetailedPageForBuyer_RatingDetailsBean {

    @SerializedName("user_name")
    var user_name: String = ""

    @SerializedName("rating")
    var rating: Double = 0.0

    @SerializedName("comment")
    var comment: String= ""

}