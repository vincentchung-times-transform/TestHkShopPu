package com.HKSHOPU.hk.ui.main.buyer.activity


import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged

import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventRefreshUserInfo
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.rxjava.RxBus

import com.paypal.android.sdk.payments.*
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


//import kotlinx.android.synthetic.main.activity_main.*

class BirthDayChangeActivity : BaseActivity() {

    private lateinit var binding: ActivityBirthdaychangeBinding
    var userId = MMKV.mmkvWithID("http").getString("UserId", "");
    val url = ApiConstants.API_HOST + "user_detail/update_detail/"
    var birth: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirthdaychangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initClick()

    }

    private fun initView() {
        binding.etViewBirth.doAfterTextChanged {
            birth = binding.etViewBirth.text.toString()
        }

    }

    private fun initClick() {

        binding.ivBack.setOnClickListener {

            finish()
        }
        binding.showDateBtn.setOnClickListener {
            ShowDatePick(it)
        }
        binding.tvSave.setOnClickListener {
            var birthForupdate = changeDateFormat_forApp(birth)
            doUpdateBirth(url,birthForupdate)
        }

    }

    fun ShowDatePick(view: View) {
        if (view.getId() === R.id.show_date_btn) {
            var calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]

            var dialog = DatePickerDialog(
                this, R.style.DateTimeDialogTheme,
                { datePicker, year, month, day ->
                    val month_actual = month + 1

                    binding.etViewBirth.setText("$day/$month_actual/$year")
                }, mYear, mMonth, mDay
            )
            dialog.getDatePicker().setMaxDate(java.lang.System.currentTimeMillis())
            dialog.show()
        }

    }

    fun changeDateFormat_forApp(item : String): String {
        val inputPattern = SimpleDateFormat("dd/MM/yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val output: String = formatter.format(inputPattern.parse(item))

        return output
    }

    private fun doUpdateBirth(url: String,birthday:String) {

        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("BirthDayChangeActivity", "返回資料 resStr：" + resStr)
                    Log.d("BirthDayChangeActivity", "返回資料 ret_val：" + json.get("ret_val"))
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    if (status == 0) {

                        RxBus.getInstance().post(EventRefreshUserInfo())
                        runOnUiThread {
                            Toast.makeText(
                                this@BirthDayChangeActivity, ret_val.toString(), Toast.LENGTH_SHORT
                            ).show()
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
        web.Do_UserInfoUpdate(url,userId,"","",birthday,"","","","")
    }

    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }


}