package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductChildCategoryBean {

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("product_category_id")
    var product_category_id: Int = 0

    @SerializedName("c_product_sub_category")
    var c_product_sub_category: String = ""

    @SerializedName("e_product_sub_category")
    var e_product_sub_category: String= ""

    @SerializedName("unselected_product_sub_category_icon")
    var unselected_product_sub_category_icon: String= ""

    @SerializedName("selected_product_sub_category_icon")
    var selected_product_sub_category_icon: String= ""

    @SerializedName("product_sub_category_background_color")
    var product_sub_category_background_color: String= ""

    @SerializedName("product_sub_category_seq")
    var product_sub_category_seq: String= ""

    @SerializedName("is_delete")
    var is_delete: String= ""


    @SerializedName("created_at")
    var created_at: String= ""

    @SerializedName("updated_at")
    var updated_at: String= ""


    var isSelect: Boolean = false

}