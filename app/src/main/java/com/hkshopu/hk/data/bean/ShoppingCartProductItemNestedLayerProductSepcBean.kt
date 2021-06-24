package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShoppingCartProductItemNestedLayerProductSepcBean {

    @SerializedName("shopping_cart_item_id")
    var shopping_cart_item_id: String = ""

    @SerializedName("shopping_cart_quantity")
    var shopping_cart_quantity: Int = 0

    @SerializedName("spec_desc_1")
    var spec_desc_1: String = ""

    @SerializedName("spec_desc_2")
    var spec_desc_2: String = ""

    @SerializedName("spec_dec_1_items")
    var spec_dec_1_items: String = ""

    @SerializedName("spec_dec_2_items")
    var spec_dec_2_items: String = ""

    @SerializedName("spec_price")
    var spec_price: Int = 0

    @SerializedName("spec_quantity")
    var spec_quantity: Int =0

}
