package com.HKSHOPU.hk.ui.main.payment.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.BankCodeBean
import com.HKSHOPU.hk.data.bean.FpsAccountBean
import com.HKSHOPU.hk.data.bean.FpsSettingBean
import com.HKSHOPU.hk.databinding.*
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.payment.fragment.FpsPayConfirmDialogFragment
import com.HKSHOPU.hk.widget.view.show
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.akexorcist.snaptimepicker.TimeRange
import com.akexorcist.snaptimepicker.TimeValue
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import okhttp3.Response

import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

//import kotlinx.android.synthetic.main.activity_main.*

class FpsPayAccountActivity : BaseActivity() {
    lateinit var manager: FragmentManager
    private lateinit var binding: ActivityFpspayAccountBinding
    private var spBank: SmartMaterialSpinner<String>? = null
    private var BankCodeList_Descs: MutableList<String> = mutableListOf()
    var BankCodeBeanList: MutableList<FpsAccountBean> = mutableListOf()
    var TransferAccount = ""
    var ContactType=""
    var TransferPhoneOrPhone = ""
    var TransferTime = ""
    var selectedFpsPayAccount_id = ""
    var jsonTutList_order = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFpspayAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bundle  = intent.getBundleExtra("bundle")
        jsonTutList_order = bundle!!.get("jsonTutList").toString()


        initView()
        initClick()
        getFpsAccount()
    }

    private fun initView() {

    }

    private fun initClick() {

        binding.ivBackClick.setOnClickListener {
            finish()
        }
        binding.showDateBtn.setOnClickListener {
            ShowDatePick(it)
        }
        binding.layoutTransfertime.setOnClickListener {
            SnapTimePickerDialog.Builder().apply {
                setThemeColor(R.color.turquoise)
                setTitle(R.string.title)
                setTitleColor(R.color.white)
                setNegativeButtonColor(R.color.dark_gray)
                setPositiveButtonColor(R.color.turquoise)
                setSelectableTimeRange(TimeRange(TimeValue(0, 1), TimeValue(23, 59)))
            }.build().apply {
                setListener { hour, minute -> onTimePicked(hour, minute) }
            }.show(supportFragmentManager, SnapTimePickerDialog.TAG)
        }
        manager = supportFragmentManager
        binding.tvSendTransferinfo.setOnClickListener {

            if (selectedFpsPayAccount_id.isNotEmpty() && binding.etTransferdate.text.toString().isNotEmpty() && binding.tvTransfertime.text.toString().isNotEmpty()) {
                TransferTime = binding.etTransferdate.text.toString()+" "+binding.tvTransfertime.text.toString()
                var calendar = Calendar.getInstance()
                var TransferTime_db=changeDateFormat_forDB(binding.etTransferdate.text.toString())+"T"+binding.tvTransfertime.text.toString()+":00"+"."+calendar.get(Calendar.MILLISECOND) / 10

                runOnUiThread {
                    FpsPayConfirmDialogFragment(TransferAccount, ContactType, TransferPhoneOrPhone, TransferTime, jsonTutList_order, selectedFpsPayAccount_id, TransferTime_db).show(
                        manager,
                        "MyCustomFragment"
                    )
                }
            }else{
                Toast.makeText(this, "請將資料填寫完畢", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun ShowDatePick(view: View) {

        if (view.getId() === R.id.show_date_btn) {
            var calendar = Calendar.getInstance()
            var mYear = calendar[Calendar.YEAR]
            var mMonth = calendar[Calendar.MONTH]
            var mDay = calendar[Calendar.DAY_OF_MONTH]
            calendar.add(Calendar.MONTH,1)
            var afterTwoMonthsinMilli=calendar.getTimeInMillis()

            var dialog = DatePickerDialog(
                this, R.style.DateTimeDialogTheme,
                { datePicker, year, month, day ->
                    val month_actual = month + 1

                    binding.etTransferdate.setText(changeDateFormat_forApp("$day/$month_actual/$year").toString())
                }, mYear, mMonth, mDay
            )
            dialog.getDatePicker().setMaxDate(afterTwoMonthsinMilli)
            dialog.show()
        }
    }
    fun changeDateFormat_forApp(item : String): String {
        val parser = SimpleDateFormat("dd/MM/yyyy")
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val output: String = formatter.format(parser.parse(item))

        return output
    }
    fun changeDateFormat_forDB(item : String): String {
        val parser = SimpleDateFormat("dd/MM/yyyy")
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val output: String = formatter.format(parser.parse(item))
        return output
    }


    private fun onTimePicked(selectedHour: Int, selectedMinute: Int) {
        val hour = selectedHour.toString().padStart(2, '0')
        val minute = selectedMinute.toString().padStart(2, '0')
        binding.tvTransfertime.text =
            String.format(getString(R.string.selected_time_format, hour, minute))
        binding.tvSendTransferinfo.textColor = getColor(R.color.white)
        binding.tvSendTransferinfo.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
    }

    private fun initSpinner() {
        runOnUiThread {

            for (i in 0 until BankCodeBeanList.size) {
                BankCodeList_Descs!!.add(
                    "${BankCodeBeanList.get(i).bank_code}${"\t"}${
                        BankCodeBeanList.get(
                            i
                        ).bank_name}${"\t"}${
                            BankCodeBeanList.get(
                                i
                            ).bank_account_name
                    }"
                )

            }

            spBank = findViewById(R.id.smartSpinner_bankAccount)

            spBank?.item = BankCodeList_Descs

            spBank?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Log.d("spBankSelectedItem", BankCodeBeanList!![position].bank_code)
                    TransferAccount = BankCodeBeanList!![position].bank_code + " "+BankCodeBeanList!![position].bank_name+"_"+BankCodeBeanList!![position].bank_account_name
                    if(BankCodeBeanList!![position].contact_type.equals("phone")) {
                        ContactType = "phone"
                        binding.tvTitleContactMethod.setText(getText(R.string.fpspay_phone))
                        var phoneNumber =
                            BankCodeBeanList!![position].phone_country_code + " " + BankCodeBeanList!![position].phone_number
                        binding.tvPhoneOrMailTransfer.text = phoneNumber
                        TransferPhoneOrPhone = phoneNumber
                    }else{
                        ContactType = "email"
                        binding.tvTitleContactMethod.setText(getText(R.string.fpspay_email))
                        var emailAddress =
                            BankCodeBeanList!![position].contact_email
                        binding.tvPhoneOrMailTransfer.text = emailAddress
                        TransferPhoneOrPhone = emailAddress
                    }

                    selectedFpsPayAccount_id = BankCodeBeanList!![position].id
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }

        }

    }

    private fun getFpsAccount() {
        var userId = MMKV.mmkvWithID("http").getString("UserId", "");
        var url = ApiConstants.API_HOST + "user/" + userId + "/fps_payment_account/"
        BankCodeBeanList.clear()
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    Log.d("FpsPayAccountActivity", "返回資料 resStr：" + resStr)
                    val status = json.get("status")
                    if (status == 0) {

                        val translations: JSONArray = json.getJSONArray("data")
                        for (i in 0 until translations.length()) {
                            val jsonObject: JSONObject = translations.getJSONObject(i)
                            val fpsAccountBean: FpsAccountBean =
                                Gson().fromJson(jsonObject.toString(), FpsAccountBean::class.java)
                            BankCodeBeanList.add(fpsAccountBean)
                        }

                        runOnUiThread {
                          initSpinner()

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
        web.Get_Data(url)
    }

    public override fun onDestroy() {
        // Stop service when done

        super.onDestroy()
    }


}