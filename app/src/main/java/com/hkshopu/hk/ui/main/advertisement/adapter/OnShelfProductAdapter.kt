package com.HKSHOPU.hk.ui.main.shopProfile.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.OnShelfProductBean
import com.HKSHOPU.hk.data.bean.ProductImagesObjBean
import com.HKSHOPU.hk.ui.main.advertisement.activity.ProductSelectingForAdvertisementActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.widget.view.click
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV


import org.jetbrains.anko.find
import java.util.*
import android.graphics.BitmapFactory
import android.util.Log
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventCreateBtnStatusInspecting
import com.HKSHOPU.hk.component.EventRefreshShoppingCartItemCount
import com.HKSHOPU.hk.utils.rxjava.RxBus
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class OnShelfProductAdapter(var activity: ProductSelectingForAdvertisementActivity) : RecyclerView.Adapter<OnShelfProductAdapter.ShopInfoLinearHolder>(){
    private var mData: ArrayList<OnShelfProductBean> = ArrayList()
    var selectedProductId = ""
    var lastPosition = -1

    fun setData(list : ArrayList<OnShelfProductBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopInfoLinearHolder {
        val v = parent.context.inflate(R.layout.item_on_shelf_product,parent,false)

        return ShopInfoLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    fun removeAt(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ShopInfoLinearHolder, position: Int) {
        val item = mData.get(position)
        holder.bindShop(item)
    }

    @JvmName("getSelectedProductId1")
    fun getSelectedProductId(): String {
        return this.selectedProductId
    }

    inner class ShopInfoLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val container = itemView.find<RelativeLayout>(R.id.layout_new_product)
        val checkBox = itemView.find<CheckBox>(R.id.checkBox)
        val tv_productName = itemView.find<TextView>(R.id.tv_productName)
        val tv_price = itemView.find<TextView>(R.id.tv_price)
        val cardView_products_pics_pager = itemView.find<CardView>(R.id.cardView_products_pics_pager)
        val product_pics_pager = itemView.find<ViewPager>(R.id.product_pics_pager)
        val product_pics_indicator = itemView.find<LinearLayout>(R.id.product_pics_indicator)
        var arrayList_pics = arrayListOf<Bitmap>()
        val list = ArrayList<ProductImagesObjBean>()
        lateinit var points: ArrayList<ImageView> //指示器圖片


        fun bindShop(bean : OnShelfProductBean){

            if(bean.checked){
                checkBox.isChecked = true
            }else{
                checkBox.isChecked = false
            }

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){

                    if(!lastPosition.equals(-1)){
                        mData.get(lastPosition).checked = false
                        notifyItemChanged(lastPosition)
                    }

                    selectedProductId = bean.id
                    bean.checked = true
                    RxBus.getInstance().post(EventCreateBtnStatusInspecting())

                    Log.d("position", "lastPosition: ${lastPosition}")
                    lastPosition = adapterPosition

                }else{
                    if(lastPosition.equals(adapterPosition)){
                        selectedProductId = ""
                        bean.checked = false
                        RxBus.getInstance().post(EventCreateBtnStatusInspecting())
                    }
                }

            }

            arrayList_pics.clear()
            list.clear()
            product_pics_indicator.removeAllViews()

            for(i in 0..bean.pic_path.size-1){
                arrayList_pics.add(bean.pics_bitmap.get(i))
            }
            for ( i in 0..arrayList_pics.size-1){
                list.add(ProductImagesObjBean(arrayList_pics.get(i), arrayList_pics.get(i)))
            }
            initViewPager()

            tv_productName.setText(bean.product_title.toString())
            if(bean.min_price != bean.max_price){
                tv_price.text = "${bean.min_price.toString()}-${bean.max_price.toString()}"
            }else{
                tv_price.text = bean.min_price.toString()
            }

            val height = MMKV.mmkvWithID("phone_size").getInt("height",0)
            val width =  MMKV.mmkvWithID("phone_size").getInt("width",0)

            if(width.equals(1080)){

                val params_container: ViewGroup.LayoutParams = container.getLayoutParams()
                var width_scaling =  (width*168)/375

                params_container.width = width_scaling
                container.setLayoutParams(params_container)

                val params_layout_product: ViewGroup.LayoutParams = cardView_products_pics_pager.getLayoutParams()
                params_layout_product.width = width_scaling
                params_layout_product.height = (width_scaling*18)/21
                cardView_products_pics_pager.setLayoutParams(params_layout_product)
            }


        }


        private fun initViewPager() {

            val height = MMKV.mmkvWithID("phone_size").getInt("height",0)
            val width =  MMKV.mmkvWithID("phone_size").getInt("width",0)

            if(width.equals(1080)){

                val params_layout_product: ViewGroup.LayoutParams = product_pics_pager.getLayoutParams()
                var width_scaling =  (width*345)/375
                params_layout_product.width = width_scaling
                params_layout_product.height = width_scaling
                runOnUiThread {
                    product_pics_pager.setLayoutParams(params_layout_product)
                }

            }

            runOnUiThread {
                product_pics_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                    override fun onPageScrollStateChanged(state: Int) {
                    }

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {

                    }

                    override fun onPageSelected(position: Int) {

                        for (i in 0 until points.size) {
                            val params = points[position].layoutParams
                            params.width = 96
                            params.height = 36
                            points[position].layoutParams = params
                            points[position].setImageResource(R.drawable.selected_points_products)

                            if (position != i) {
                                val params1 = points[i].layoutParams
                                params1.width = 36
                                params1.height = 36
                                points[i].layoutParams = params1
                                points[i].setImageResource(R.drawable.unselected_points_products)

                            }
                        }
                    }

                })

                product_pics_pager.adapter = ImageAdapter(list)
            }

            initPoints()
        }

        private fun initPoints() {
            points = arrayListOf()

            for (i in 0 until list.size) {
                val point = ImageView(itemView.context)
                point.setPadding(10, 10, 10, 10)
                point.scaleType = ImageView.ScaleType.FIT_XY

                if (i == 0) {
                    point.setImageResource(R.drawable.selected_points_products)
                    point.layoutParams = ViewGroup.LayoutParams(96, 36)
                } else {
                    point.setImageResource(R.drawable.unselected_points_products)
                    point.layoutParams = ViewGroup.LayoutParams(36, 36)
                }

                runOnUiThread {
                    product_pics_indicator.addView(point)
                }

                points.add(point)
            }
        }

    }

    private class ImageAdapter internal constructor(
        arrayList: ArrayList<ProductImagesObjBean>
    ) : PagerAdapter() {
        private val arrayList: ArrayList<ProductImagesObjBean>

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                container.context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.boarding_view, null)
            val ImagesObj: ProductImagesObjBean = arrayList[position]
            val imageView = view.findViewById<View>(R.id.image_view) as ImageView
            imageView.setImageBitmap(ImagesObj.front_pic)

            if (position == 0) {
                imageView.scaleType = ImageView.ScaleType.FIT_XY

            } else {
                imageView.scaleType = ImageView.ScaleType.FIT_XY
            }

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return arrayList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        init {
            this.arrayList = arrayList
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View?)
        }

    }

}

