package com.hkshopu.hk.component

import com.hkshopu.hk.data.bean.ShopAddressBean
import com.hkshopu.hk.data.bean.ShopBankAccountBean
import com.hkshopu.hk.data.bean.ShopCategoryBean
import java.util.*
import kotlin.collections.ArrayList


/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
class CommonVariable private constructor() {

    companion object {

        //Shop Info
        val list = ArrayList<ShopCategoryBean>()
        var ShopCategory = TreeMap<String,ShopCategoryBean>()
        var bankaccountlist = ArrayList<ShopBankAccountBean>()
        var addresslist = ArrayList<ShopAddressBean>()

        //Product Info
        var product_price_list = ArrayList<Int>()
        var product_spec_desc_1_list = ArrayList<String>()
        var product_spec_desc_2_list = ArrayList<String>()
        var product_spec_dec_1_items_list = ArrayList<String>()
        var product_spec_dec_2_items_list = ArrayList<String>()
        var product_pic_path_list = ArrayList<String>()



    }
}