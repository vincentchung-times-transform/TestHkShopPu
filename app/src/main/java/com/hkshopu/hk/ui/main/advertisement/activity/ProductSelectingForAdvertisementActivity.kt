package com.HKSHOPU.hk.ui.main.advertisement.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventCreateBtnStatusInspecting
import com.HKSHOPU.hk.component.EventKeywordProductSelected
import com.HKSHOPU.hk.data.bean.OnShelfProductBean
import com.HKSHOPU.hk.data.bean.TopProductBean
import com.HKSHOPU.hk.databinding.ActivityAdvertisementBinding
import com.HKSHOPU.hk.databinding.ActivityMyAdvertisementBinding
import com.HKSHOPU.hk.databinding.ActivityProductSelectingForAdBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.homepage.adapter.TopProductAdapter
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.OnShelfProductAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

//import kotlinx.android.synthetic.main.activity_main.*

class ProductSelectingForAdvertisementActivity : BaseActivity() {
    private lateinit var binding: ActivityProductSelectingForAdBinding

    private val adapter = OnShelfProductAdapter(this)
    var shop_id = ""
    var selectedProductId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductSelectingForAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shop_id = intent.getBundleExtra("bundle")!!.getString("shopId").toString()

        initView()
        initVM()
        initEvent()
    }

    fun initView(){
        getOnShelfProducts(shop_id)
        initClick()
    }

    private fun initVM() {

    }

    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            if(selectedProductId.isNotEmpty()){
                RxBus.getInstance().post(EventKeywordProductSelected(selectedProductId))
                finish()
            }else{
                Toast.makeText(this, "請選取需廣告之商品", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecyclerView(){
        val layoutManager = GridLayoutManager(this,2)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
    }

    private fun getOnShelfProducts(shop_id:String) {
        Log.d("getOnShelfProducts", "shop_id: ${shop_id.toString()}")
        binding.progressBar.visibility = View.VISIBLE
        binding.imgViewLoadingBackground.visibility = View.VISIBLE

        val url = ApiConstants.API_HOST+"user/adProductList/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<OnShelfProductBean>()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getOnShelfProducts", "返回資料 resStr：${resStr.toString()}")
                    Log.d("getOnShelfProducts", "返回資料 ret_val：${ret_val.toString()}")

                    if (status == 0) {

                        val jsonArray: JSONArray = json.getJSONArray("data")
                        Log.d("getOnShelfProducts", "返回資料 jsonArray：" + jsonArray.toString())

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            val onShelfProductBean: OnShelfProductBean =
                                Gson().fromJson(jsonObject.toString(), OnShelfProductBean::class.java)

                            //important: 因adapter無法即時生成context，getBitmapFromURL則無法運作，故會導致系統crash，因此務必在setAdapterData前完成Bitmap轉換。
                            for(j in 0..onShelfProductBean.pic_path.size-1){
                                onShelfProductBean.pics_bitmap.add(getBitmapFromURL(onShelfProductBean.pic_path.get(j))!!)
                            }

                            list.add(onShelfProductBean)
                        }


                    }

                    if(list.size > 0){
                        runOnUiThread {
                            adapter.setData(list)
                            initRecyclerView()
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                            binding.imgViewLoadingBackground.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("errormessage", "getOnShelfProducts: JSONException：" + e.toString())
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errormessage", "getOnShelfProducts: IOException：" + e.toString())
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        binding.imgViewLoadingBackground.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errormessage", "getOnShelfProducts: ErrorResponse：" + ErrorResponse.toString())
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.imgViewLoadingBackground.visibility = View.GONE
                }
            }
        })
        web.Do_GetOnShelfProducts(url,shop_id)
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventCreateBtnStatusInspecting -> {
                        selectedProductId = adapter.getSelectedProductId()
                        Log.d("EventCreateBtnStatusInspecting", "selectedProductId: ${selectedProductId.toString()}")
                        if(selectedProductId.isNotEmpty()){
                            binding.btnConfirm.background = getDrawable(com.HKSHOPU.hk.R.drawable.customborder_40dp_hkcolor)
                        }else{
                            binding.btnConfirm.background = getDrawable(com.HKSHOPU.hk.R.drawable.customborder_40dp_gray_8e8e93)
                        }
                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true


            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


}