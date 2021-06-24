package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class DetailedProductSpecificationBean {

    @SerializedName("id")
    var id: MutableList<MutableList<String>> = mutableListOf()

    @SerializedName("spec_desc_1")
    var spec_desc_1: String = ""

    @SerializedName("spec_desc_2")
    var spec_desc_2: String = ""

    @SerializedName("spec_dec_1_items")
    var spec_dec_1_items: MutableList<String> = mutableListOf()

    @SerializedName("spec_dec_2_items")
    var spec_dec_2_items: MutableList<String> = mutableListOf()

    @SerializedName("price")
    var price: MutableList<MutableList<Int>> = mutableListOf()

    @SerializedName("quantity")
    var quantity: MutableList<MutableList<Int>> = mutableListOf()

    var seleted_status : Boolean = false

}