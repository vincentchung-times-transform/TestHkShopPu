package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopInfoBean {
    @SerializedName("id")
    var id: Int = 0;

    @SerializedName("user_id")
    var user_id: Int = 0;

    @SerializedName("shop_title")
    var shop_title: String= ""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("shop_pic")
    var shop_pic: String= ""

    @SerializedName("shop_description")
    var shop_description: String= ""

    @SerializedName("bank_code")
    var bank_code: String= ""

    @SerializedName("bank_name")
    var bank_name: String= ""

    @SerializedName("bank_account")
    var bank_account: String= ""

    @SerializedName("bank_account_name")
    var bank_account_name: String= ""

    @SerializedName("address_name")
    var address_name: String= ""

    @SerializedName("address_country_code")
    var address_country_code: String= ""

    @SerializedName("address_phone")
    var address_phone: String= ""

    @SerializedName("address_is_phone_show")
    var address_is_phone_show: String= ""

    @SerializedName("address_area")
    var address_area: String= ""

    @SerializedName("address_district")
    var address_district: String= ""

    @SerializedName("address_road")
    var address_road: String= ""

    @SerializedName("address_number")
    var address_number: String= ""

    @SerializedName("address_other")
    var address_other: String= ""

    @SerializedName("address_floor")
    var address_floor: String= ""

    @SerializedName("address_room")
    var address_room: String= ""

    @SerializedName("product_count")
    var product_count: Int = 0

    @SerializedName("rating")
    var rating: Int = 0

    @SerializedName("follower")
    var follower: Int = 0

    @SerializedName("income")
    var income: Int = 0

}