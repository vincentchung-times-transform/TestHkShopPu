package com.HKSHOPU.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemShippingFare_Certained(
    var shipment_desc: String, var price : String = "", var onoff : String = "off", var shop_id : String = "") : Parcelable {
}