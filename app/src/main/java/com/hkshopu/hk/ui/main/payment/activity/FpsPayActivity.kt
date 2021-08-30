package com.HKSHOPU.hk.ui.main.payment.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.component.EventGenerateAddValueOeder
import com.HKSHOPU.hk.component.EventGenerateOeder
import com.HKSHOPU.hk.data.bean.FpsSettingBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

//import kotlinx.android.synthetic.main.activity_main.*

class FpsPayActivity : BaseActivity() {

    private lateinit var binding: ActivityFpspayBinding
    var jsonTutList = ""
    var order_number_for_add_value = ""
    var mode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFpspayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var bundle  = intent.getBundleExtra("bundle")
        jsonTutList = bundle!!.get("jsonTutList").toString()
        order_number_for_add_value = bundle!!.get("orderNumber").toString()
        mode = bundle!!.get("mode").toString()
        Log.d("FpsPayActivity_jsonTutList","jsonTutList: ${jsonTutList.toString()}")

        getFpsSetting()
        initVM()
        initClick()
    }

    private fun initVM() {

    }

    private fun initClick() {

        binding.ivBackClick.setOnClickListener {

            finish()
        }
        binding.ivGotransfer.setOnClickListener {
            val intent = Intent()
            var bundle = Bundle()
            bundle.putString("jsonTutList", jsonTutList)
            bundle.putString("orderNumber", order_number_for_add_value)
            bundle.putString("mode", mode.toString())
            intent.putExtra("bundle", bundle)
            intent.setClass(this, FpsPayAccountActivity::class.java)
            startActivity(intent)
        }

        binding.ivCopyText.setOnClickListener {
            copyText()
        }

    }
    fun copyText() {
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?;
        myClip = ClipData.newPlainText("text", binding.tvPhone.text.toString());
        myClipboard?.setPrimaryClip(myClip);

        Toast.makeText(this, "複製電話成功", Toast.LENGTH_SHORT).show();
    }
    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }
    private fun getFpsSetting() {
        var url = ApiConstants.API_HOST + "payment/fps/fps_setting/"
        val list = ArrayList<FpsSettingBean>()
        list.clear()
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("FpsPayActivity", "返回資料 resStr：" + resStr)
                    Log.d("FpsPayActivity", "返回資料 ret_val：" + json.get("ret_val"))

                    val status = json.get("status")
                    if (status == 0) {

                        val translations: JSONArray = json.getJSONArray("data")

                        if(translations.length()>0){
                            for (i in 0 until translations.length()) {
                                val jsonObject: JSONObject = translations.getJSONObject(i)
                                val fpsSettingBean: FpsSettingBean =
                                    Gson().fromJson(jsonObject.toString(), FpsSettingBean::class.java)
                                list.add(fpsSettingBean)
                            }
                            runOnUiThread {
                                binding.tvCompanyName.text = list[0].company_name
                                binding.tvPhoneCode.text = list[0].phone_country_code
                                binding.tvPhone.text = list[0].phone_number

                            }
                        }

                    }
                } catch (e: JSONException) {
                    Log.d("errorMessage", "JSONException: ${e.toString()}")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("errorMessage", "IOException: ${e.toString()}")
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("errorMessage", "ErrorResponse${ErrorResponse.toString()}")
            }
        })
        web.Get_Data(url)
    }

}