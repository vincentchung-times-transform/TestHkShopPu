package com.HKSHOPU.hk.data.bean

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:返回数据实体的基类
 */
data class BaseResponse<T>(val status: Int,
                           val msg : String?,
                           var ret_val: T?)