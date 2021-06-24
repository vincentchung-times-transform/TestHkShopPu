package com.HKSHOPU.hk.net.net.imageloader

import com.HKSHOPU.hk.net.imageloader.ImageLoader
import com.HKSHOPU.hk.net.net.imageloader.glide.GlideImageLoader


/**
 * @Author: YangYang
 * @Date: 2017/12/25
 * @Version: 1.0.0
 * @Description:
 */
object ImageLoaderProvider {

    fun getImageLoader(): ImageLoader {
        return GlideImageLoader
    }
}