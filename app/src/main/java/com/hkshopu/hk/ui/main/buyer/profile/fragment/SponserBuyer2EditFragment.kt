package com.HKSHOPU.hk.ui.main.buyer.profile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.data.bean.BuyerProfileBean
import com.HKSHOPU.hk.data.bean.ShopInfoBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.extension.load
import com.google.gson.Gson
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SponserBuyer2EditFragment : Fragment() {
    companion object {
        fun newInstance(): SponserBuyer2EditFragment {
            val args = Bundle()
            val fragment = SponserBuyer2EditFragment()
            fragment.arguments = args
            return fragment
        }
    }
    var userId = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var sponsor_level_id = CommonVariable.costlist.get(0).id
    var background_is_show: String = "Y"
    var frame_is_show: String = "Y"
    lateinit var tv_pluto_cost : TextView
    lateinit var tv_uranus_cost : TextView
    lateinit var tv_pluto : TextView
    lateinit var tv_uranus : TextView
    lateinit var sw_bgColor : EasySwitcher
    lateinit var sw_headSticker : EasySwitcher
    lateinit var preView1: ImageView
    lateinit var preView2: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_sponser_buyer_pluto_edit, container, false)
        tv_pluto_cost = v.findViewById<TextView>(R.id.tv_pluto_cost)
        tv_uranus_cost = v.findViewById<TextView>(R.id.tv_uranus_cost)
        preView1 = v.findViewById<ImageView>(R.id.iv_1)
        preView2 = v.findViewById<ImageView>(R.id.iv_2)
        tv_pluto = v.findViewById<TextView>(R.id.tv_pluto)
        tv_pluto.setOnClickListener {
            tv_pluto.setBackgroundResource(R.drawable.customborder_onboard_8dp)
            tv_pluto.textColor = R.color.black
            tv_uranus.setBackgroundResource(R.drawable.bg_edit_login)
            tv_uranus.textColor = R.color.dark_gray
            sponsor_level_id = CommonVariable.costlist.get(0).id
            sw_bgColor.openSwitcher()
            sw_headSticker.openSwitcher()
            background_is_show = "Y"
            frame_is_show = "Y"
            preView1.setImageResource(R.mipmap.plutoall)
            preView2.setImageResource(R.mipmap.plutoall2)
        }
        tv_uranus = v.findViewById<TextView>(R.id.tv_uranus)
        tv_uranus.setOnClickListener {
            tv_uranus.setBackgroundResource(R.drawable.customborder_onboard_8dp)
            tv_uranus.textColor = R.color.black
            tv_pluto.setBackgroundResource(R.drawable.bg_edit_login)
            tv_pluto.textColor = R.color.dark_gray
            sponsor_level_id = CommonVariable.costlist.get(1).id
            sw_bgColor.openSwitcher()
            sw_headSticker.openSwitcher()
            background_is_show = "Y"
            frame_is_show = "Y"
            preView1.setImageResource(R.mipmap.uranusall)
            preView2.setImageResource(R.mipmap.uranusall2)
        }
        sw_bgColor = v.findViewById<EasySwitcher>(R.id.switchview_color)
        sw_bgColor.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if (isOpen) {
                    background_is_show = "Y"

                } else {
                    background_is_show = "N"

                }
            }
        })

        sw_headSticker = v.findViewById<EasySwitcher>(R.id.switchview_headsticker)
        sw_headSticker.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if (isOpen) {
                    frame_is_show = "Y"

                } else {
                    frame_is_show = "N"

                }
            }
        })
        val save = v.findViewById<ImageButton>(R.id.iv_save)
        save.setOnClickListener {
            val url = ApiConstants.API_HOST + "sponsor/edit/"
            do_SponserEdit(url,"",userId,sponsor_level_id.toString(),background_is_show,"",frame_is_show)
        }

        getUserProfile(userId)

        return v
    }
    private fun do_SponserEdit(url:String,shop_id: String,user_id: String,sponsor_level_id: String,background_is_show: String,badge_is_show: String,frame_is_show: String) {
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("Do_SponserEdit", "返回資料 resStr：" + resStr)
                    Log.d("Do_SponserEdit", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {
                        activity!!.runOnUiThread {
                            Toast.makeText(requireActivity(), ret_val.toString(), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        activity!!.runOnUiThread {
                            Toast.makeText(requireActivity(), ret_val.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    Log.d("Do_SponserEdit", "JSONException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("Do_SponserEdit", "IOException：" + e.toString())
                    activity!!.runOnUiThread {
                        Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("Do_SponserEdit", "onErrorResponse：" + ErrorResponse.toString())
                activity!!.runOnUiThread {
                    Toast.makeText(requireActivity(), "網路異常", Toast.LENGTH_SHORT).show()
                }
            }
        })

        web.Do_SponserEdit(
            url,shop_id,user_id,sponsor_level_id,background_is_show,badge_is_show,frame_is_show
        )
    }

    private fun getUserProfile(user_id: String) {

        var url = ApiConstants.API_HOST + "user_detail/"+user_id+"/profile/"
        val web = Web(object : WebListener {
            override fun onResponse(response: Response) {
                var resStr: String? = ""
                val list = ArrayList<BuyerProfileBean>()
                list.clear()
                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    val status = json.get("status")
                    Log.d("getUserProfile", "返回資料 resStr：" + resStr)
                    Log.d("getUserProfile", "返回資料 ret_val：" + ret_val)

                    if (status == 0) {

                        val jsonObject: JSONObject = json.getJSONObject("data")

                        val buyerProfileBean: BuyerProfileBean =
                            Gson().fromJson(jsonObject.toString(), BuyerProfileBean::class.java)
                        list.add(buyerProfileBean)

                        MMKV.mmkvWithID("http").putString("SponserBuyerId", list[0].identity)

                        runOnUiThread {

                            when(buyerProfileBean.identity){
                                "冥王星"->{
                                    when(buyerProfileBean.background_is_show){
                                        "Y"->{
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"}
                                    }

                                    when(buyerProfileBean.frame_is_show){
                                        "Y"->{
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }

                                }
                                "天王星"->{
                                    when(buyerProfileBean.background_is_show){
                                        "Y"->{
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"}
                                    }

                                    when(buyerProfileBean.frame_is_show){
                                        "Y"->{
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }
                                }
                                "金星"->{
                                    when(buyerProfileBean.background_is_show){
                                        "Y"->{
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"}
                                    }

                                    when(buyerProfileBean.frame_is_show){
                                        "Y"->{
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }
                                }
                                "水星"->{
                                    when(buyerProfileBean.background_is_show){
                                        "Y"->{
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"}
                                    }

                                    when(buyerProfileBean.frame_is_show){
                                        "Y"->{
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N"->{
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }
                                }
                            }
                        }

                    }else{

                    }

                } catch (e: JSONException) {
                    Log.d("getUserProfile_errorMessage", "JSONException: ${e.toString()}")

                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d("getUserProfile_errorMessage", "IOException: ${e.toString()}")

                }
            }

            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d("getUserProfile_errorMessage", "ErrorResponse: ${ErrorResponse.toString()}")

            }
        })
        web.Get_Data(url)
    }

}