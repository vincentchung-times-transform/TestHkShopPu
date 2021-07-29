package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class NotificationMessageBean {

    @SerializedName("id")
    var id: String = ""

    @SerializedName("iv_product_icon")
    var iv_product_icon: String = ""

    @SerializedName("tv_orderer_name")
    var tv_orderer_name: String = ""

    @SerializedName("tv_message_content")
    var tv_message_content: String = ""

}