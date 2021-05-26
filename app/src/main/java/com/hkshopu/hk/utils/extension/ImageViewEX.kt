package com.hkshopu.hk.utils.extension

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.hkshopu.hk.R
import com.hkshopu.hk.net.net.imageloader.ImageLoaderProvider


/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */


/**
 * 加载图片
 */
fun ImageView.load(url: String?) {
    url?.let {
        var imgUrl = it
        if (!it.startsWith("http://")) {
//            imgUrl = "${CommonDataProvider.instance.getImgDomain()}$it"
        }
        ImageLoaderProvider.getImageLoader().loadImage(this, imgUrl.trim())
    }
}

/**
 * 加载图片有占位图和加载错误图
 */
fun ImageView.load(url: String?, loadingResId: Int, errorResId: Int) {
  //  this.setImageResource(loadingResId)
    url?.let {
        var imgUrl = it
        if (!it.startsWith("http://")) {
//            imgUrl = "${CommonDataProvider.instance.getImgDomain() + "/"}$it"
        }
        ImageLoaderProvider.getImageLoader().loadImage(this, imgUrl.trim(), loadingResId, errorResId)
    }
}

/**
 *
 */
fun ImageView.loadNovelCover(url: String?) {
    // Log.d("loadNovelCover", "url:$url")
    this.load(url, R.mipmap.no_image, R.mipmap.no_image)
}


fun ImageView.loadBlurCover(url:String?){
    url?.let {
        var imgUrl = it
        if (!it.startsWith("http://")) {
//            imgUrl = "${CommonDataProvider.instance.getImgDomain() + "/"}$it"
        }
        ImageLoaderProvider.getImageLoader().loadBlurImage(this, imgUrl.trim(),25 , 8)
    }
}


fun ImageView.loadBlurCoverBitmap(bitmap:Bitmap){
        ImageLoaderProvider.getImageLoader().loadBlurImageBitmap(this, bitmap)
}

fun ImageView.loadCoverBitmap(bitmap:Bitmap){
    ImageLoaderProvider.getImageLoader().loadBlurImageBitmap(this, bitmap)
}

fun ImageView.loadImageScale(url:String){
    ImageLoaderProvider.getImageLoader().loadImageScale(this, url)
}

fun ImageView.loadImageScale(url:String, viewGroup: View/*, position:Int,textView: TextView*/){
    ImageLoaderProvider.getImageLoader().loadImageScale(this,url,viewGroup/*,position,textView*/)
}
fun ImageView.loadImageScale(url:String,viewGroup: FrameLayout){
    ImageLoaderProvider.getImageLoader().loadImageScale(this,url,viewGroup)
}



