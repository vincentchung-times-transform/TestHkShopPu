package com.HKSHOPU.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShoppingCartItemIdBean(
    var shopping_cart_item_id: ArrayList<String>
    ) : Parcelable {
}