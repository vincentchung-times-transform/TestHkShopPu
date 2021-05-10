package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopBankAccountBean {
    @SerializedName("id")
    var id: String= ""

    @SerializedName("shop_id")
    var shop_id: Int = 0;

    @SerializedName("code")
    var code: String= ""

    @SerializedName("name")
    var name: String= ""

    @SerializedName("account")
    var account: String= ""

    @SerializedName("account_name")
    var account_name: String= ""
}