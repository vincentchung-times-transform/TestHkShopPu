package com.hkshopu.hk.net

import com.hkshopu.hk.data.bean.ShopCategoryBean
import java.util.*


/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
class ApiConstants private constructor() {

    companion object {
        //请求地址
        //  var API_HOST = BuildConfig.API_HOST
        //  var API_HOST = "http://47.75.63.143/"
        //  var API_HOST = "http://47.52.26.64:8080/"
        var API_HOST = "https://hkshopu.df.r.appspot.com/"
        //        var API_HOST = "https://hkshopu-20700.df.r.appspot.com/"
        var IMG_HOST = "https://hkshopu.df.r.appspot.com"
        //        var API_HOST = ""


        const val OSS_PATH = "https://cartoon202007d.oss-cn-hongkong.aliyuncs.com/api.json.txt"
//          const val OSS_PATH2 = "https://cartoon202007d.oss-cn-hongkong.aliyuncs.com/api.json.txt"
//        const val OSS_PATH3 = "https://cartoon202008d.oss-cn-hongkong.aliyuncs.com/api.json.txt"

        const val PAY_CALLBACK = "?redirect="

        const val API_PATH = "https://hkshopu.df.r.appspot.com/"

    }
}