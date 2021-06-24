package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class BankCodeBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("bank_code")
    var bank_code: String = ""

    @SerializedName("bank_name")
    var bank_name: String = ""

    @SerializedName("seq")
    var seq: Int = -999


}