package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class NotificationMessageBean {

    @SerializedName("notitfication_id")
    var notitfication_id: String = ""

    @SerializedName("order_id")
    var order_id: String = ""

    @SerializedName("notification_title")
    var notification_title: String = ""

    @SerializedName("notification_content")
    var notification_content: String = ""

    @SerializedName("clicked")
    var clicked: String = ""

    @SerializedName("created_at")
    var created_at: String = ""

    @SerializedName("product_pic")
    var product_pic: String = ""

    @SerializedName("order_status")
    var order_status: String = ""

}