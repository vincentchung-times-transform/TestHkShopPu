package com.hkshopu.hk.data.bean
data class ShoppingCartShopItemNestedLayer(
    var shop_checked: Boolean,
    var shop_icon_url: String,
    var shop_name: String,
    var mutableList_shoppingCartProductItem : MutableList<ShoppingCartProductItemNestedLayer>
)