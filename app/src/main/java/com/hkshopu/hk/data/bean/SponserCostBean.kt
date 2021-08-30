package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class SponserCostBean {
    @SerializedName("id")
    var id: Int = 0;

    @SerializedName("title")
    var title: String= ""

    @SerializedName("price")
    var price: Int = 0;


}