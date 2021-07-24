package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class UserInfoBean {
    @SerializedName("user_id")
    var user_id: String= ""

    @SerializedName("name")
    var name: String= ""

    @SerializedName("gender")
    var gender: String= ""

    @SerializedName("birthday")
    var birthday: String= ""

    @SerializedName("phone")
    var phone: String= ""

    @SerializedName("email")
    var email: String= ""

    @SerializedName("pic")
    var pic: String= ""

}