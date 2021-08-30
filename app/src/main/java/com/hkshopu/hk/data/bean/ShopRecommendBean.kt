package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopRecommendBean {
    @SerializedName("user_id")
    var user_id: String= ""

    @SerializedName("shop_id")
    var shop_id: String= "";

    @SerializedName("seq")
    var seq: Int = 0;

    @SerializedName("pic_path_1")
    var pic_path_1: String= ""

    @SerializedName("pic_path_2")
    var pic_path_2: String= ""

    @SerializedName("pic_path_3")
    var pic_path_3: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_name")
    var shop_name: String= ""

    @SerializedName("rating")
    var rating: Double = 0.0;

    @SerializedName("followed")
    var followed: String= ""

    @SerializedName("follower_count")
    var follower_count: Int = 0

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