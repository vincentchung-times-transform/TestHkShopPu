package com.hkshopu.hk.data.bean

import com.google.gson.annotations.SerializedName

class ShopLogisticBean {
    @SerializedName("id")
    var id: Int= 0

    @SerializedName("shop_id")
    var shop_id: Int = 0

    @SerializedName("shipment_desc")
    var shipment_desc: String= ""

    @SerializedName("onoff")
    var onoff: String= ""

    fun getID(): Int? {
        return id
    }

    fun setID(ID: Int) {
        this.id = ID
    }
    fun getShopID(): Int? {
        return shop_id
    }

    fun setShopID(Shop_id: Int) {
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