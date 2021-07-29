package com.HKSHOPU.hk.data.bean

import com.google.gson.annotations.SerializedName

class ProductDetailedPagerForBuyer_RecommendedShopInfoBean {
    @SerializedName("shop")
    var shop: ProductDetailedPageForBuyer_RecommendedShopBean = ProductDetailedPageForBuyer_RecommendedShopBean()

    @SerializedName("products")
    var products: ArrayList<ProductDetailedPageForBuyer_RecommendedProductsBean> = arrayListOf()


}