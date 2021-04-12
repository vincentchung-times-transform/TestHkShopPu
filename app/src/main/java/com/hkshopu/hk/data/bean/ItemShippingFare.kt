package com.hkshopu.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemShippingFare(
    var ship_method_name: String, var ship_method_fare : Int  , var btn_delete: Int, var is_checked : Boolean = false
) : Parcelable {
}