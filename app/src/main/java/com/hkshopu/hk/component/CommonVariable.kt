package com.hkshopu.hk.component

import com.hkshopu.hk.data.bean.ShopAddressBean
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.data.bean.ShopCategoryBean
import java.util.*


/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
class CommonVariable private constructor() {

    companion object {

        val list = ArrayList<ShopCategoryBean>()
        var ShopCategory = TreeMap<String,ShopCategoryBean>()
        var bankaccountlist = ArrayList<ShopBankAccountBean>()
        var addresslist = ArrayList<ShopAddressBean>()

    }
}