package com.HKSHOPU.hk.data.bean
import com.google.gson.annotations.SerializedName

class ShoppingCartProductItemNestedLayer {

    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("product_pic")
    var product_pic: String = ""

    @SerializedName("shipmentList")
    var shipmentList: MutableList<ShoppingCartProductShipmentItem> = mutableListOf()

    @SerializedName("product_spec")
    var product_spec: MutableList<ShoppingCartProductItemNestedLayerProductSepcBean> = mutableListOf()

}