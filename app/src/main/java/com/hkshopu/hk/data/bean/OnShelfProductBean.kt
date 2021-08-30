package com.HKSHOPU.hk.data.bean

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

class OnShelfProductBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("product_title")
    var product_title: String = ""

    @SerializedName("pic_path")
    var pic_path: ArrayList<String> = arrayListOf()

    var pics_bitmap: ArrayList<Bitmap> = arrayListOf()

    @SerializedName("min_price")
    var min_price: Int = 0

    @SerializedName("max_price")
    var max_price: Int = 0

    var checked: Boolean = false
}