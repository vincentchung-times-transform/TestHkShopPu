package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopBankAccountBean {
    @SerializedName("id")
    var id: String= ""

    @SerializedName("shop_id")
    var shop_id: String= ""

    @SerializedName("code")
    var code: String= ""

    @SerializedName("name")
    var name: String= ""

    @SerializedName("account")
    var account: String= ""

    @SerializedName("account_name")
    var account_name: String= ""

    @SerializedName("is_default")
    var is_default: String= ""
}