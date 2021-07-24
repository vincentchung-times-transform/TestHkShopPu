package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class StoreFollowBean {

    @SerializedName("shop_id")
    var shop_id: String= "";

    @SerializedName("shop_pic")
    var shop_pic: ArrayList<String> = arrayListOf()

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("rating")
    var rating: Double = 0.0;

    @SerializedName("follow_count")
    var follow_count: Int = 0

}