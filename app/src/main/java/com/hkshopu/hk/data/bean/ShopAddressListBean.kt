package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopAddressListBean {
    @SerializedName("id")
    var id: String= ""

    @SerializedName("shop_id")
    var shop_id: Int= 0

    @SerializedName("name")
    var name: String= ""

    @SerializedName("country_code")
    var country_code: String= ""

    @SerializedName("phone")
    var phone: String= ""

    @SerializedName("is_phone_show")
    var is_phone_show: String= ""

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
    @SerializedName("is_address_show")
    var is_address_show: String= ""
    @SerializedName("is_default")
    var is_default: String= ""


}