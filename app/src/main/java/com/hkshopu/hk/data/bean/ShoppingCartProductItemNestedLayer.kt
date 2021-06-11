package com.hkshopu.hk.data.bean
data class ShoppingCartProductItemNestedLayer(
    var product_icon_url: String,
    var product_name: String,
    var product_fist_spec_name: String,
    var product_fist_spec_item: String,
    var product_second_spec_name: String,
    var product_second_spec_item: String,
    var product_odered_quauntity: Int,
    var product_sum_price: Int,
    var logistcs_list: MutableList<ItemShippingFare_Filtered>,
    var selected_logistic_name: String,
    var selected_logistic_price: Int
)