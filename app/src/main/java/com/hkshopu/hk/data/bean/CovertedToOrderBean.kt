package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class CovertedToOrderBean {
    @SerializedName("payment_id")
    var payment_id: String = ""

    @SerializedName("user_address_id")
    var user_address_id: String = ""

    @SerializedName("productList")
    var productList: MutableList<ShoppingCartItemConvertedToOrder> = mutableListOf()

    @SerializedName("shop_id")
    var shop_id: String = ""


}