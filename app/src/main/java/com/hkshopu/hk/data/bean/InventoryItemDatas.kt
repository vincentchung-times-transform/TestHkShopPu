package com.hkshopu.hk.data.bean

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class InventoryItemDatas (var spec_first_: String, var spec_second : String, var price : Int, var quant: Int ) : Parcelable