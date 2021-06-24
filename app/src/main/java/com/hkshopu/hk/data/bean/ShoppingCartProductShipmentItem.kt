package com.HKSHOPU.hk.data.bean
import com.google.gson.annotations.SerializedName

class ShoppingCartProductShipmentItem {

    @SerializedName("shipment_id")
    var shipment_id: String=""

    @SerializedName("shipment_desc")
    var shipment_desc: String=""

    @SerializedName("shipment_price")
    var shipment_price: Int = 0
}

