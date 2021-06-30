package com.HKSHOPU.hk.data.bean
import com.google.gson.annotations.SerializedName

class ShoppingCartShopItemNestedLayer {

    var shop_checked: Boolean = false

    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("shop_title")
    var shop_title: String = ""

    @SerializedName("shop_icon")
    var shop_icon: String = ""

    @SerializedName("productList")
    var productList : MutableList<ShoppingCartProductItemNestedLayer> = mutableListOf()

    var selected_addresss: SelectedBuyerAddressBean = SelectedBuyerAddressBean()

}
