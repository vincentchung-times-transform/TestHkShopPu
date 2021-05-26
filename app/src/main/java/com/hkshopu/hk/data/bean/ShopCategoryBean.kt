package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopCategoryBean {
    @SerializedName("id")
    var id: Int = 0;

    @SerializedName("c_shop_category")
    var c_shop_category: String = ""

    @SerializedName("e_shop_category")
    var e_shop_category: String= ""

    @SerializedName("unselected_shop_category_icon")
    var unselected_shop_category_icon: String= ""

    @SerializedName("selected_shop_category_icon")
    var selected_shop_category_icon: String= ""

    @SerializedName("shop_category_background_color")
    var shop_category_background_color: String= ""

    var isSelect: Boolean = false

}