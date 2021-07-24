package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class SalerSaleListBean {
    @SerializedName("order_id")
    var order_id: String = ""

    @SerializedName("order_number")
    var order_number: String = ""

    @SerializedName("product_pic")
    var product_pic: String = ""

    @SerializedName("count")
    var count: String = ""

    @SerializedName("sub_total")
    var sub_total: String = ""

    @SerializedName("buyer_id")
    var buyer_id: String = ""

    @SerializedName("buyer_name")
    var buyer_name: String = ""

    @SerializedName("buyer_pic")
    var buyer_pic: String = ""

    @SerializedName("shipment_info")
    var shipment_info: String = ""

    @SerializedName("shipment_desc")
    var shipment_desc: String = ""
}