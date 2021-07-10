package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName


class BuyerAddressListBean {
    @SerializedName("id")
    var id: String= ""

    @SerializedName("name")
    var name: String= ""

    @SerializedName("phone")
    var phone: String= ""

    @SerializedName("address")
    var address: String= ""


    @SerializedName("is_default")
    var is_default: String= ""


}