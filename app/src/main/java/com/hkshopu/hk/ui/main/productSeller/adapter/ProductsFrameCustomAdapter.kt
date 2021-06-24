package com.HKSHOPU.hk.ui.main.productSeller.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.HKSHOPU.hk.R

class ProductsFrameCustomAdapter(private val context: Activity, private val imageIdList:  MutableList<Bitmap>)
    : BaseAdapter() {
    override fun getView(p: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.pics_list_item,null)
        val imageView = rowView.findViewById<ImageView>(R.id.imgView)
        imageView.setImageBitmap(imageIdList[p])
        return rowView
    }
    override fun getItem(p: Int): Any {
        return imageIdList.get(p)
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    override fun getCount(): Int {
        return imageIdList.size
    }
}
