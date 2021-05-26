package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopAddressBriefBean {

    @SerializedName("country_code")
    var country_code: String= ""

    @SerializedName("area")
    var area: String= ""

    @SerializedName("district")
    var district: String= ""
    @SerializedName("road")
    var road: String= ""
    @SerializedName("number")
    var number: String= ""
    @SerializedName("other")
    var other: String= ""
    @SerializedName("floor")
    var floor: String= ""
    @SerializedName("room")
    var room: String= ""

}