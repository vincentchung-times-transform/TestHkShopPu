package com.hkshopu.hk.data.bean
data class ItemInvenFirstNestedLayer(
    var spec_desc_1: String,
    var spec_desc_2: String,
    var spec_dec_1_items: String,
    var mutableList_itemInvenSecondLayer : MutableList<ItemInvenSecondNestedLayer>
)