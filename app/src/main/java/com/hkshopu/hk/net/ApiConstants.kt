package com.hkshopu.hk.net


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
        var API_HOST = "https://hkshopu-20700.df.r.appspot.com/"
        var IMG_HOST = "https://hkshopu-20700.df.r.appspot.com"
//        var API_HOST = ""

        var fdsfsdfsdf = "0123456789abcdef"
        const val API_VERSION = "2/"
        const val API_PAY_VERSION = "3/"

        const val OSS_PATH = "https://cartoon202007d.oss-cn-hongkong.aliyuncs.com/api.json.txt"
//          const val OSS_PATH2 = "https://cartoon202007d.oss-cn-hongkong.aliyuncs.com/api.json.txt"
//        const val OSS_PATH3 = "https://cartoon202008d.oss-cn-hongkong.aliyuncs.com/api.json.txt"

        const val PAY_CALLBACK = "?redirect="
        var PAY_CALLBACK_PATH = ""
        var APP_DOWNLOAD_PATH = ""
        var CS_NUMBER = ""
        var CS_ROUTE = ""
        var APP_ID : String? = null
        var HOTFIX : Int = 0

        const val API_PATH = "https://hkshopu-20700.df.r.appspot.com/"

    }
}