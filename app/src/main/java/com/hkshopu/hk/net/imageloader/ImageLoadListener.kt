package com.hkshopu.hk.net.imageloader

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


import java.net.URLDecoder

class ImageLoadListener(private val imageView : ImageView) : RequestListener<Bitmap> {
    override fun onLoadFailed(e: GlideException?, model: Any?,
        target: Target<Bitmap>?,
        isFirstResource: Boolean
    ): Boolean {

        return false
    }

    override fun onResourceReady(
        resource: Bitmap?,
        model: Any?,
        target: Target<Bitmap>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        return false;
    }
}