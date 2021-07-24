package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class BuyerPaymentBean {
    @SerializedName("id")
    var id: String = ""

    @SerializedName("payment_type")
    var payment_type: String = ""

    @SerializedName("bank_code")
    var bank_code: String = ""

    @SerializedName("bank_name")
    var bank_name: String= ""

    @SerializedName("bank_account_name")
    var bank_account_name: String= ""

    @SerializedName("contact_type")
    var contact_type: String= ""


    @SerializedName("phone_country_code")
    var phone_country_code:  String = ""

    @SerializedName("phone_number")
    var phone_number:  String = ""

    @SerializedName("contact_email")
    var contact_email:  String = ""

    @SerializedName("is_default")
    var is_default: String= ""

}