package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductInfoBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("product_category_id")
    var product_category_id: String = ""

    @SerializedName("product_sub_category_id")
    var product_sub_category_id: String = ""


    @SerializedName("product_title")
    var product_title: String= ""

    @SerializedName("quantity")
    var quantity: Int = 0

    @SerializedName("product_description")
    var product_description: String= ""

    @SerializedName("product_price")
    var product_price: Int= 0

    @SerializedName("new_secondhand")
    var new_secondhand: String= ""

    @SerializedName("shipping_fee")
    var shipping_fee: Int = 0

    @SerializedName("created_at")
    var created_at: String= ""

    @SerializedName("updated_at")
    var updated_at: String= ""

    @SerializedName("weight")
    var weight: Int = 0

    @SerializedName("longterm_stock_up")
    var longterm_stock_up: Int = 0


    @SerializedName("length")
    var length: Int = 0

    @SerializedName("width")
    var width: Int= 0

    @SerializedName("height")
    var height: Int = 0

    @SerializedName("like")
    var like: Int = 0

    @SerializedName("seen")
    var seen: Int = 0

    @SerializedName("sold_quantity")
    var sold_quantity: Int = 0


    @SerializedName("product_spec_on")
    var product_spec_on: String = ""

    @SerializedName("c_product_category")
    var c_product_category: String = ""

    @SerializedName("c_sub_product_category")
    var c_sub_product_category: String = ""

    @SerializedName("price")
    var price  : MutableList<Int> = mutableListOf()

    @SerializedName("spec_desc_1")
    var spec_desc_1 :  MutableList<String> = mutableListOf()

    @SerializedName("spec_desc_2")
    var spec_desc_2 :  MutableList<String> = mutableListOf()

    @SerializedName("spec_dec_1_items")
    var spec_dec_1_items :  MutableList<String> = mutableListOf()

    @SerializedName("spec_dec_2_items")
    var spec_dec_2_items :  MutableList<String> = mutableListOf()

    @SerializedName("spec_quantity")
    var spec_quantity :  MutableList<Int> = mutableListOf()

    @SerializedName("pic_path")
    var pic_path:  MutableList<String> = mutableListOf()

    @SerializedName("min_price")
    var min_price:  Int = 0

    @SerializedName("max_price")
    var max_price:  Int = 0


    @SerializedName("min_quantity")
    var min_quantity:  Int = 0

    @SerializedName("max_quantity")
    var max_quantity:  Int = 0

    @SerializedName("shipment_min_price")
    var shipment_min_price:  Int = 0

    @SerializedName("shipment_max_price")
    var shipment_max_price:  Int = 0

    @SerializedName("product_shipment_list")
    lateinit var product_shipment_list:  MutableList<ItemShippingFare_forGet>

    @SerializedName("product_status")
    var product_status:  String = ""

    @SerializedName("sum_quantity")
    var sum_quantity:  String = ""

}