package com.hkshopu.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemShippingFare_Filtered(
    var shipment_desc: String, var price : Int = 0, var onoff : String = "off", var shop_id : Int) : Parcelable {
}