package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class CartItemQuantityBean {
    @SerializedName("shopping_cart_item_id")
    var shopping_cart_item_id: String = ""

    @SerializedName("new_quantity")
    var new_quantity: Int = 0

}