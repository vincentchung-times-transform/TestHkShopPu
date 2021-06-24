package com.HKSHOPU.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class InventoryItemDatas (var spec_desc_1: String, var spec_desc_2 : String, var spec_dec_1_items : String, var spec_dec_2_items : String, var price : Int, var quantity: Int ) : Parcelable

