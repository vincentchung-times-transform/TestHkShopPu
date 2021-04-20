package com.hkshopu.hk.Base.response.function

import com.hkshopu.hk.data.bean.BaseResponse
import com.hkshopu.hk.data.exception.RequestException
import io.reactivex.functions.Function

class SuccessFunc<T> : Function<BaseResponse<T>, T?> {
    override fun apply(t: BaseResponse<T>): T? {

//        if (t.status == 0 ||t.status < 0) {
            if (t.ret_val == null) {
                throw RequestException(RequestException.ERROR_REQUEST_SUCCESS_BUT_RETURN_NULL)
            }
            return t.ret_val
//        }


       /* if (t.code == 1009){
            throw ObjectException(t.data)
        }*/

        var msg = t.msg
        when (t.status) {
            1013,1015 -> msg = ""
        }
        throw RequestException(t.status, msg ?: "")
    }
}