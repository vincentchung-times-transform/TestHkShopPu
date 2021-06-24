package com.HKSHOPU.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemShippingFare_forGet(
    var shipment_desc: String, var price : Int  , var btn_delete: Int, var onoff : String = "off") : Parcelable {
}