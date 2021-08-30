package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class AddValueWalletBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("balance")
    var balance: Int = 0

    @SerializedName("is_active")
    var is_active: Boolean = false

    @SerializedName("created_at")
    var created_at: String = ""

    @SerializedName("updated_at")
    var updated_at: String = ""


}