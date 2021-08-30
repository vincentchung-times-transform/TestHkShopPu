package com.HKSHOPU.hk.ui.main.seller.shop.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventAddShopBriefSuccess
import com.HKSHOPU.hk.component.EventGetShopCatSuccess
import com.HKSHOPU.hk.data.bean.ShopAddressBean
import com.HKSHOPU.hk.data.bean.ShopInfoBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.utils.extension.loadNovelCover
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.jetbrains.anko.textColor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class SponserSeller2EditFragment : Fragment() {
    companion object {
        fun newInstance(): SponserSeller2EditFragment {
            val args = Bundle()
            val fragment = SponserSeller2EditFragment()
            fragment.arguments = args
            return fragment
        }
    }
    val shopId = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
    var sponsor_level_id = CommonVariable.costlist.get(0).id
    var background_is_show: String = "Y"
    var badge_is_show: String = "Y"
    var frame_is_show: String = "Y"
    lateinit var tv_honor : TextView
    lateinit var tv_supreme : TextView
    lateinit var tv_honor_cost : TextView
    lateinit var tv_supreme_cost : TextView
    lateinit var sw_bgColor : EasySwitcher
    lateinit var sw_badge : EasySwitcher
    lateinit var sw_headSticker : EasySwitcher
    lateinit var preView1: ImageView
    lateinit var preView2: ImageView
    lateinit var preView3: ImageView
    lateinit var preView4: ImageView
    lateinit var preView5: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var v = inflater.inflate(R.layout.fragment_sponser_seller_honor_edit, container, false)
        tv_honor_cost = v.findViewById<TextView>(R.id.tv_honor_cost)
        tv_honor_cost.text = "HKD\$"+ CommonVariable.costlist.get(0).price.toString()+"/月"
        tv_supreme_cost = v.findViewById<TextView>(R.id.tv_supreme_cost)
        tv_supreme_cost.text = "HKD\$"+ CommonVariable.costlist.get(0).price.toString()+"/月"
        preView1 = v.findViewById<ImageView>(R.id.iv_1)
        preView2 = v.findViewById<ImageView>(R.id.iv_2)
        preView3 = v.findViewById<ImageView>(R.id.iv_3)
        preView4 = v.findViewById<ImageView>(R.id.iv_4)
        preView5 = v.findViewById<ImageView>(R.id.iv_5)
        tv_honor = v.findViewById<TextView>(R.id.tv_honor)
        tv_honor.setOnClickListener {
            tv_honor.setBackgroundResource(R.drawable.customborder_onboard_8dp)
            tv_honor.textColor = R.color.black
            tv_supreme.setBackgroundResource(R.drawable.bg_edit_login)
            tv_supreme.textColor = R.color.dark_gray
            sponsor_level_id = CommonVariable.costlist.get(0).id
            sw_bgColor.openSwitcher()
            sw_badge.openSwitcher()
            sw_headSticker.openSwitcher()
            background_is_show = "Y"
            badge_is_show = "Y"
            frame_is_show = "Y"
            preView1.setImageResource(R.mipmap.honorall1)
            preView2.setImageResource(R.mipmap.honorall2)
            preView3.setImageResource(R.mipmap.honorall3)
            preView4.setImageResource(R.mipmap.honorall4)
            preView5.setImageResource(R.mipmap.honorall5)
        }
        tv_supreme = v.findViewById<TextView>(R.id.tv_supreme)
        tv_supreme.setOnClickListener {
            tv_supreme.setBackgroundResource(R.drawable.customborder_onboard_8dp)
            tv_supreme.textColor = R.color.black
            tv_honor.setBackgroundResource(R.drawable.bg_edit_login)
            tv_honor.textColor = R.color.dark_gray
            sponsor_level_id = CommonVariable.costlist.get(1).id
            sw_bgColor.openSwitcher()
            sw_badge.openSwitcher()
            sw_headSticker.openSwitcher()
            background_is_show = "Y"
            badge_is_show = "Y"
            frame_is_show = "Y"
            preView1.setImageResource(R.mipmap.supremeall1)
            preView2.setImageResource(R.mipmap.supremeall2)
            preView3.setImageResource(R.mipmap.supremeall3)
            preView4.setImageResource(R.mipmap.supremeall4)
            preView5.setImageResource(R.mipmap.supremeall5)
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
        sw_badge = v.findViewById<EasySwitcher>(R.id.switchview_badge)
        sw_badge.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {
                if (isOpen) {
                    badge_is_show = "Y"

                } else {
                    badge_is_show = "N"

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
        var balance = MMKV.mmkvWithID("http").getInt("walletbalance", 0)
        val walletbalance = v.findViewById<TextView>(R.id.tv_wallet_left)
        walletbalance.text = "\$"+balance.toString()

        val save = v.findViewById<ImageButton>(R.id.iv_save)
        save.setOnClickListener {
        val url = ApiConstants.API_HOST + "sponsor/edit/"
            do_SponserEdit(url,shopId,"",sponsor_level_id.toString(),background_is_show,badge_is_show,frame_is_show)
        }

        getShopInfo()
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

    private fun getShopInfo() {

        var url = ApiConstants.API_HOST + "/shop/" + shopId + "/show/"

        val web = Web(object : WebListener {

            override fun onResponse(response: Response) {
                var resStr: String? = ""
                var list = ArrayList<ShopInfoBean>()
                list.clear()
                var shop_category_id_list = ArrayList<String>()

                try {
                    resStr = response.body()!!.string()
                    val json = JSONObject(resStr)
                    val ret_val = json.get("ret_val")
                    Log.d("getShopInfo", "返回資料 resStr：" + resStr)
                    Log.d("getShopInfo", "返回資料 ret_val：" + json.get("ret_val"))

                    if (ret_val.equals("已找到商店資料!")) {
                        val jsonObject: JSONObject = json.getJSONObject("data")
                        Log.d("getShopInfo", "返回資料 Object：" + jsonObject.toString())
                        val shopInfoBean: ShopInfoBean =
                            Gson().fromJson(jsonObject.toString(), ShopInfoBean::class.java)
                        list.add(shopInfoBean)

                        runOnUiThread {
                            when (shopInfoBean.identity) {
                                "尊榮" -> {
                                    when (shopInfoBean.background_is_show) {
                                        "Y" -> {
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.badge_is_show) {
                                        "Y" -> {
                                            sw_badge.openSwitcher()
                                            badge_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_badge.closeSwitcher()
                                            badge_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.frame_is_show) {
                                        "Y" -> {
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }
                                }
                                "至尊" -> {
                                    when (shopInfoBean.background_is_show) {
                                        "Y" -> {
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.badge_is_show) {
                                        "Y" -> {
                                            sw_badge.openSwitcher()
                                            badge_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_badge.closeSwitcher()
                                            badge_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.frame_is_show) {
                                        "Y" -> {
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }
                                }
                                "榮耀" -> {
                                    when (shopInfoBean.background_is_show) {
                                        "Y" -> {
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.badge_is_show) {
                                        "Y" -> {
                                            sw_badge.openSwitcher()
                                            badge_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_badge.closeSwitcher()
                                            badge_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.frame_is_show) {
                                        "Y" -> {
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }
                                }
                                "卓越" -> {
                                    when (shopInfoBean.background_is_show) {
                                        "Y" -> {
                                            sw_bgColor.openSwitcher()
                                            background_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_bgColor.closeSwitcher()
                                            background_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.badge_is_show) {
                                        "Y" -> {
                                            sw_badge.openSwitcher()
                                            badge_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_badge.closeSwitcher()
                                            badge_is_show = "N"
                                        }
                                    }
                                    when (shopInfoBean.frame_is_show) {
                                        "Y" -> {
                                            sw_headSticker.openSwitcher()
                                            frame_is_show = "Y"
                                        }
                                        "N" -> {
                                            sw_headSticker.closeSwitcher()
                                            frame_is_show = "N"
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (e: JSONException) {
                    Log.d(
                        "getShopInfo_errorMessage",
                        "JSONException：" + e.toString()
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.d(
                        "getShopInfo_errorMessage",
                        "IOException：" + e.toString()
                    )
                }
            }
            override fun onErrorResponse(ErrorResponse: IOException?) {
                Log.d(
                    "getShopInfo_errorMessage",
                    "ErrorResponse：" + ErrorResponse.toString()
                )
            }
        })
        web.Get_Data(url)
    }
}