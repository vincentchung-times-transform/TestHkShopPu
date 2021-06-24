package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopLogisticBean {
    @SerializedName("id")
    var id: String= ""

    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("shipment_desc")
    var shipment_desc: String= ""

    @SerializedName("onoff")
    var onoff: String= ""

    fun getID(): String? {
        return id
    }

    fun setID(ID: String) {
        this.id = ID
    }
    fun getShopID(): String? {
        return shop_id
    }

    fun setShopID(Shop_id: String) {
        this.shop_id = Shop_id
    }

    fun getShipmentDesc(): String? {
        return shipment_desc
    }

    fun setShipmentDesc(Shipment_desc: String) {
        this.shipment_desc = Shipment_desc
    }

    fun getOnOff(): String? {
        return onoff
    }

    fun setOnOff(Onoff: String) {
        this.onoff = Onoff
    }
}