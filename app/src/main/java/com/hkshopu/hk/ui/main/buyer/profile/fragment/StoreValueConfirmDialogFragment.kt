package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventFinishLoadingShopmenu
import com.HKSHOPU.hk.component.EventStartLoadingShopmenu
import com.HKSHOPU.hk.data.bean.ShopListBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.payment.activity.FpsPayActivity
import com.HKSHOPU.hk.ui.main.sponsor.ToSponserEditActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Response
import org.jetbrains.anko.runOnUiThread
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class StoreValueConfirmDialogFragment(userId:String,sponsorLevelId: String): DialogFragment(), View.OnClickListener {


    var signal : Boolean = false
    var user_id = userId
    var sponsor_level_id = sponsorLevelId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        mEventBus = EventBus.getDefault();
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_ShrinkScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_fragment_storevalueconfirm, container, false)
        val inset = InsetDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.dialog_fragment_background
            ), 0
        )

        dialog!!.window!!.setBackgroundDrawable(inset)

        v.findViewById<ImageView>(R.id.btn_cancel).setOnClickListener(this)
        v.findViewById<ImageView>(R.id.btn_forward).setOnClickListener(this)

        return v
    }
    private fun do_JoinSponserBuyer(user_id: String, sponsor_level_id: String) {
        Log.d("do_JoinSponserBuyer", "user_id: ${user_id} ; sponsor_level_id: ${sponsor_level_id} ; ")
        val url = ApiConstants.API_HOST + "sponsor/join/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("Do_JoinSponser", "返回資料 resStr：" + resStr)
                    Log.d("Do_JoinSponser", "返回資料 ret_val：" + ret_val)
                    //        "order_id": "73306eab-fdbb-4bd6-8e7c-c77012a9de40",
                    //        "order_number": "SR2021082400003",
                    //        "order_amount": 78.0,
                    //        "order_stauts": "Pending Payment",
                    //        "sponsor_level_id": 4,
                    //        "sponsor_level_desc": "天王星"
                    if (status == 0) {
                        var data = json.getJSONObject("data")
                        var order_id = data.getString("order_id").toString()
                        var sponsor_level_desc = data.getString("sponsor_level_desc").toString()

                        var id_list = arrayListOf<String>()
                        id_list.add(order_id)
                        val gson = Gson()
                        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                        val jsonTutList: String = gson.toJson(id_list)
                        Log.d("jsonTutList_id_list", jsonTutList.toString())
                        val jsonTutListPretty: String = gsonPretty.toJson(id_list)
                        Log.d("jsonTutListPretty_id_list", jsonTutListPretty.toString())

                        val intent = Intent(requireActivity(), FpsPayActivity::class.java)
                        var bundle = Bundle()
                        bundle.putString("jsonTutList", jsonTutList.toString())
                        bundle.putString("orderNumber", "")
                        bundle.putString("mode", "sponsor")
                        intent.putExtra("bundle", bundle)
                        startActivity(intent)
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireActivity(), "${sponsor_level_desc}贊助商訂單已產生", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        activity!!.runOnUiThread {
                            Toast.makeText(
                                requireActivity(),
                                ret_val.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_JoinSponser", "JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_JoinSponser", "IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_JoinSponser", "onErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.Do_JoinSponserBuyer(
            url, user_id, sponsor_level_id
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_cancel -> dismiss()
            R.id.btn_forward -> {
                do_JoinSponserBuyer(user_id, sponsor_level_id)
            }
        }
    }

}