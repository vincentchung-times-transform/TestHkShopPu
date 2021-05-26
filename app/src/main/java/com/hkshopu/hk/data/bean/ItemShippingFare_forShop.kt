package com.hkshopu.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemShippingFare_forShop(
    var id: Int, var shop_id : Int  , var shipment_desc : String = "", var onoff : String = "off") : Parcelable {
}