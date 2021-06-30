package com.HKSHOPU.hk.ui.main.shopProfile.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventCheckLogisticsEnableBtnOrNot
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityLogisticlistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.LogisticsListAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class LogisticListActivity : BaseActivity() {
    private lateinit var binding: ActivityLogisticlistBinding

    private val adapter = LogisticsListAdapter()
    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
    var url = ApiConstants.API_HOST + "/shop/" + shopId + "/shipmentSettings/get/"
    var isUpdate:Boolean = false
    var  list: ArrayList<ShopLogisticBean> = ArrayList()
    private var mData: ArrayList<ShopLogisticBean> = ArrayList()
    var btn_enable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogisticlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initEvent()
        initView()
        initVM()
        initClick()
        getShopLogisticsList(url)

    }

    private fun initView() {
//        adapter.cancelClick = {
//            cancelurl =  ApiConstants.API_HOST +"shop/bankAccount/"+it+"/"
//        }

    }

    private fun initVM() {

    }

    @SuppressLint("CheckResult")
    fun initEvent() {

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventCheckLogisticsEnableBtnOrNot->{

                        var boolean = it.boolean

                        if(boolean){
                            btn_enable = true
                        }else{
                            btn_enable = false
                        }


                    }
                }
            }, {
                it.printStackTrace()
            })

    }

    private fun getShopLogisticsList(url: String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""

                list.clear()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("LogisticListActivity", "返回資料 resStr：" + resStr)
                    Log.d("LogisticListActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val shopLogisticBean: ShopLogisticBean =
                                Gson().fromJson(jsonObject.toString(), ShopLogisticBean::class.java)
                            list.add(shopLogisticBean)
                        }
                        if(list.size == 0){
                            binding.tvLogisticSave.isClickable = false
                        }else {
                            binding.tvLogisticSave.isClickable = true
                            adapter.setData(list)
                            runOnUiThread {
                                binding.recyclerview.adapter = adapter

                            }
                        }


                    }
//                        initRecyclerView()

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Get_Data(url)
    }

    private fun initClick() {

        binding.tvLogisticSave.setOnClickListener {

            if(btn_enable){
                mData = adapter.get_shipping_method_datas()
                doShopLogistisSetup(mData)
            }else{
                Toast.makeText(this, "請先完成編輯動作", Toast.LENGTH_SHORT).show()
            }

        }
        binding.ivBack.setOnClickListener {
            if(isUpdate) {
                finish()
            }else{
                AlertDialog.Builder(this , R.style.AlertDialogTheme)
                            .setTitle("")
                            .setMessage("您尚未儲存變更，確定要離開？")
                            .setPositiveButton("捨棄"){
                                // 此為 Lambda 寫法
                                    dialog, which ->finish()
                            }
                            .setNegativeButton("取消"){ dialog, which -> dialog.cancel()

                            }
                            .show()
            }
        }

        binding.tvEdit.setOnClickListener {



            if(binding.tvEdit.text.equals("編輯")){
                btn_enable = false

                binding.tvEdit.text = "完成"
                binding.tvEdit.textColor = Color.parseColor("#1DBCCF")
                adapter.updateData(true)

            }else{
                btn_enable = true

                binding.tvEdit.text = "編輯"
                binding.tvEdit.textColor = Color.parseColor("#8E8E93")
                adapter.updateData(false)


            }
        }

    }
    private fun doShopLogistisSetup(mData: ArrayList<ShopLogisticBean>) {
        var url = ApiConstants.API_HOST + "/shop/" + shopId + "/shipmentSettings/set/"
        Log.d("LogisticList", "返回資料 Url：" + url)
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("LogisticList", "返回資料 resStr：" + resStr)

                    val ret_val = json.get("ret_val")
                    Log.d("LogisticList", "返回資料 ret_val：" + ret_val)
                    val status = json.get("status")
                    if (status == 0) {
                        runOnUiThread {
                            isUpdate = true
                            Toast.makeText(this@LogisticListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {

                            Toast.makeText(this@LogisticListActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: JSONException) {

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {

            }
        })
        web.Do_ShopLogisticSetup(url, mData)
    }

}