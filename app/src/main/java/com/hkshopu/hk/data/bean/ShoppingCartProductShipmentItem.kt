package com.HKSHOPU.hk.data.bean
import com.google.gson.annotations.SerializedName

class ShoppingCartProductShipmentItem {

    @SerializedName("product_shipment_id")
    var product_shipment_id: String=""

    @SerializedName("shipment_desc")
    var shipment_desc: String=""

    @SerializedName("shipment_price")
    var shipment_price: Int = 0
}

