package com.HKSHOPU.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemShippingFare_forShop(
    var id: String, var shop_id : String  , var shipment_desc : String = "", var onoff : String = "off") : Parcelable {
}