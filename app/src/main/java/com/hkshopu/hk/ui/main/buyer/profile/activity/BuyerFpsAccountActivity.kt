package com.HKSHOPU.hk.ui.main.buyer.profile.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventRefreshFpsAccountList
import com.HKSHOPU.hk.data.bean.BuyerPaymentBean
import com.HKSHOPU.hk.databinding.ActivityBuyerfpsaccountlistBinding
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.buyer.profile.adapter.BuyerBankAccount_ListAdapter
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

//import kotlinx.android.synthetic.main.activity_main.*

class BuyerFpsAccountActivity : BaseActivity() {
    private lateinit var binding: ActivityBuyerfpsaccountlistBinding
    private val adapter = BuyerBankAccount_ListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerfpsaccountlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarBuyerFpsAccountList.visibility = View.VISIBLE
        binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.VISIBLE

        initVM()
        initEvent()
        initClick()
        getAccountList()
    }

    private fun initVM() {

    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventRefreshFpsAccountList -> {
                        getAccountList()
                    }

                }
            }, {
                it.printStackTrace()
            })
    }
    private fun initClick() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvEdit.setOnClickListener {
            if(binding.tvEdit.text.equals("編輯")){
                binding.tvEdit.text = "完成"
                binding.tvEdit.textColor = Color.parseColor("#1DBCCF")
                adapter.updateData(true)
            }else{
                binding.tvEdit.text = "編輯"
                binding.tvEdit.textColor = Color.parseColor("#8E8E93")
                adapter.updateData(false)
            }
        }
        adapter.cancelClick = {
            binding.progressBarBuyerFpsAccountList.visibility = View.VISIBLE
            binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.VISIBLE
            doAccountDel(it)
        }
        binding.tvAddbankaccount.setOnClickListener {
            val intent = Intent(this@BuyerFpsAccountActivity, BuyerAddFpsAccountActivity::class.java)
            startActivity(intent)
        }
        adapter.intentClick = {
            val intent = Intent(this@BuyerFpsAccountActivity, BuyerFpsAccountPresetActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding!!.recyclerview.layoutManager = layoutManager
        binding!!.recyclerview.adapter = adapter
    }

    private fun getAccountList() {
        var userId = MMKV.mmkvWithID("http").getString("UserId", "")
        var url = ApiConstants.API_HOST + "user/" + userId + "/paymentAccount/"
        val list = ArrayList<BuyerPaymentBean>()
        list.clear()
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BuyerFpsAccountActivity", "返回資料 resStr：" + resStr)
                    Log.d("BuyerFpsAccountActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val buyerPaymentBean: BuyerPaymentBean =
                                Gson().fromJson(jsonObject.toString(), BuyerPaymentBean::class.java)
                            list.add(buyerPaymentBean)
                        }
                        runOnUiThread {
                            adapter.setData(list)
                            initRecyclerView()
                            binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                        }
                    }else{
                        runOnUiThread {
                            binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("getAccountList_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getAccountList_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getAccountList_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                }
            }
        })
        web.Get_Data(url)
    }

    private fun doAccountDel(id: String) {

        var url = ApiConstants.API_HOST +"user/paymentAccount/"+id+"/"

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("BuyerFpsAccountActivity", "返回資料 resStr：" + resStr)
                    Log.d("BuyerFpsAccountActivity", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        runOnUiThread {
                            Toast.makeText(this@BuyerFpsAccountActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@BuyerFpsAccountActivity, ret_val.toString(), Toast.LENGTH_SHORT).show()
                            binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                            binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                        }
                    }

                } catch (e: JSONException) {
                    Log.d("getAccountList_errorMessage", "JSONException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getAccountList_errorMessage", "IOException: ${e.toString()}")
                    runOnUiThread {
                        binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                        binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getAccountList_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")
                runOnUiThread {
                    binding.progressBarBuyerFpsAccountList.visibility = View.GONE
                    binding.imgViewLoadingBackgroundBuyerFpsAccountList.visibility = View.GONE
                }
            }
        })
        web.Delete_Data(url)
    }

}