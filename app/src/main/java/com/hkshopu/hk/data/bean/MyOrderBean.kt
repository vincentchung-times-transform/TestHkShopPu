package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class MyOrderBean {

    @SerializedName("status")
    var status: String = ""

    @SerializedName("waybill_number")
    var waybill_number: String = ""

    @SerializedName("shipment_info")
    var shipment_info: String = ""

    @SerializedName("phone")
    var phone: String = ""

    @SerializedName("name_in_address")
    var name_in_address: String = ""

    @SerializedName("full_address")
    var full_address: String = ""

    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("shop_title")
    var shop_title: String=""

    @SerializedName("shop_icon")
    var shop_icon: String= ""

    @SerializedName("subtotal")
    var subtotal: Int = 0

    @SerializedName("shipment_price")
    var shipment_price: Int = 0

    @SerializedName("bill")
    var bill: Int = 0

    @SerializedName("payment_desc")
    var payment_desc: String = ""

    @SerializedName("order_number")
    var order_number: String = ""

    @SerializedName("payment_at")
    var payment_at: String = " - "

    @SerializedName("actual_post_at")
    var actual_post_at: String = " - "

    @SerializedName("estimated_deliver_at")
    var estimated_deliver_at: String = " - "

    @SerializedName("actual_finished_at")
    var actual_finished_at: String = " - "

    @SerializedName("buyer_message_title")
    var buyer_message_title: String = ""

    @SerializedName("buyer_message_content")
    var buyer_message_content: String = ""


    @SerializedName("pay_time")
    var pay_time: String = " - "

    @SerializedName("shop_message_title")
    var shop_message_title: String = ""

    @SerializedName("shop_message_content")
    var shop_message_content: String = ""


}