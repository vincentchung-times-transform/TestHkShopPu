package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShoppingCartItemConvertedToOrder {
    @SerializedName("shopping_cart_item_id")
    var shopping_cart_item_id: String = ""

    @SerializedName("shopping_cart_quantity")
    var shopping_cart_quantity: Int = 0

    @SerializedName("product_shipment_id")
    var product_shipment_id: String = ""
}