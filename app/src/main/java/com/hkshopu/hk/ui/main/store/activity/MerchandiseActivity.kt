package com.hkshopu.hk.ui.main.store.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.BoardingObjBean
import com.hkshopu.hk.databinding.ActivityMerchandiseBinding

class MerchandiseActivity : BaseActivity() {

    private lateinit var binding : ActivityMerchandiseBinding
//    lateinit var points: ArrayList<ImageVsiew> //指示器圖片
    val list = ArrayList<BoardingObjBean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMerchandiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initViewPager()

    }


//    private fun initViewPager() {
//        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//
//            }
//
//            override fun onPageSelected(position: Int) {
//
//                for (i in 0 until points.size) {
//                    val params = points[position].layoutParams
//                    params.width = 96
//                    params.height = 36
//                    points[position].layoutParams = params
//                    points[position].setImageResource(R.drawable.banner_radius)
//
//                    if (position != i) {
//                        val params1 = points[i].layoutParams
//                        params1.width = 36
//                        params1.height = 36
//                        points[i].layoutParams = params1
//                        points[i].setImageResource(R.drawable.banner_radius_unselect)
//
//                    }
//                }
//
//            }
//
//        })
//
//        binding.pager.adapter = MerchandiseActivity.ImageAdapter(list)
//
//        initPoints()
//
//    }

//    private fun initPoints() {
//        points = arrayListOf()
//        for (i in 0 until list.size) {
//            val point = ImageView(this)
//            point.setPadding(10, 10, 10, 10)
//            point.scaleType = ImageView.ScaleType.FIT_XY
//
//            if (i == 0) {
//                point.setImageResource(R.drawable.banner_radius)
//                point.layoutParams = ViewGroup.LayoutParams(96, 36)
//            } else {
//                point.setImageResource(R.drawable.banner_radius_unselect)
//                point.layoutParams = ViewGroup.LayoutParams(36, 36)
//            }
//
//            binding.indicator.addView(point)
//            points.add(point)
//        }
//    }

    private class ImageAdapter internal constructor(
        arrayList: ArrayList<BoardingObjBean>
    ) : PagerAdapter() {
        private val arrayList: ArrayList<BoardingObjBean>

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                container.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.boarding_view, null)
            val boardingObj: BoardingObjBean = arrayList[position]
            val imageView = view.findViewById<View>(R.id.image_view) as ImageView
            imageView.setImageResource(boardingObj.imageResId)

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