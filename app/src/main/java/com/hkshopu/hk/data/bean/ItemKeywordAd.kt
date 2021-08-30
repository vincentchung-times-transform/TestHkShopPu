package com.HKSHOPU.hk.data.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


 class ItemKeywordAd
{
    @SerializedName("shop_id")
    var shop_id: String = ""

    @SerializedName("product_id")
    var product_id: String = ""

    @SerializedName("keyword")
    var keyword: String = ""

    @SerializedName("bid")
    var bid: String = ""

//    @SerializedName("search_volume")
//    var search_volume: String = "..."
//
//    @SerializedName("higher_bidding_ad")
//    var higher_bidding_ad: String = "..."
//
//    @SerializedName("recommended_bid")
//    var recommended_bid: String = "..."
}