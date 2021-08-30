package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class BuyerProfileBean {
    @SerializedName("user_id")
    var user_id: String= ""

    @SerializedName("name")
    var name: String= ""

    @SerializedName("pic")
    var pic: String= ""

    @SerializedName("user_rating")
    var user_rating: Int = 0

    //sponsor
    @SerializedName("identity")
    var identity: String = ""

    @SerializedName("background_is_show")
    var background_is_show: String = ""

    @SerializedName("badge_is_show")
    var badge_is_show: String = ""

    @SerializedName("frame_is_show")
    var frame_is_show: String = ""

}