package com.HKSHOPU.hk.ui.main.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventCheckShipmentEnableBtnOrNot
import com.HKSHOPU.hk.data.bean.ItemKeywordAd
import com.HKSHOPU.hk.data.bean.ItemShippingFare
import com.HKSHOPU.hk.data.bean.ItemShippingFare_Filtered
import com.HKSHOPU.hk.data.bean.ShopLogisticBean
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.GsonProvider
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.seller.product.fragment.StoreOrNotDialogStoreProductsFragment
import com.HKSHOPU.hk.ui.main.seller.shop.adapter.ITHelperInterface
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import okhttp3.Response
import org.jetbrains.anko.singleLine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class KeywordAdvertisementAdapter(var activity: Activity, var category: String, var type: String): RecyclerView.Adapter<KeywordAdvertisementAdapter.mViewHolder>(),
    ITHelperInterface {
    var deleteClick : ((position: Int) -> Unit)? = null

    var queue_keywordAd = LinkedList<ItemKeywordAd>()
    var empty_item_num = 0
    private var editStatus: Boolean = false

    var MMKV_shop_id : String =MMKV.mmkvWithID("http").getString("ShopId", "").toString()

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //把layout檔的元件們拉進來，指派給當地變數
        val et_please_enter_keyword = itemView.findViewById<EditText>(R.id.et_please_enter_keyword)
        val et_please_enter_click_bid = itemView.findViewById<EditText>(R.id.et_please_enter_click_bid)
        val tv_search_volume_value = itemView.findViewById<TextView>(R.id.tv_search_volume_value)
        val tv_higher_bidding_ad_value = itemView.findViewById<TextView>(R.id.tv_higher_bidding_ad_value)
        val tv_recommended_bid_value = itemView.findViewById<TextView>(R.id.tv_recommended_bid_value)
        val iv_delete = itemView.findViewById<ImageView>(R.id.iv_delete)
        val iv_space = itemView.findViewById<ImageView>(R.id.iv_space)

        init {

            et_please_enter_keyword.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus ){

                }
            }


            val textWatcher_keyword = object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {

                    if(et_please_enter_keyword.text.toString().isNullOrEmpty()){

                        onItemUpdate(
                            et_please_enter_keyword.text.toString(),
                            et_please_enter_click_bid.text.toString(),
                            tv_search_volume_value.text.toString(),
                            tv_higher_bidding_ad_value.text.toString(),
                            tv_recommended_bid_value.text.toString(),
                            adapterPosition
                        )

//                        RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(false))

                    }else {

                        if(et_please_enter_keyword.text.toString().equals(queue_keywordAd.get(adapterPosition).keyword)){

                            onItemUpdate(
                                et_please_enter_keyword.text.toString(),
                                et_please_enter_click_bid.text.toString(),
                                tv_search_volume_value.text.toString(),
                                tv_higher_bidding_ad_value.text.toString(),
                                tv_recommended_bid_value.text.toString(),
                                adapterPosition
                            )

                        }else{
                            //檢查名稱是否重複
                            var check_duplicate = 0

                            for (i in 0..queue_keywordAd.size - 1) {
                                if (et_please_enter_keyword.text.toString() == queue_keywordAd[i].keyword) {
                                    check_duplicate = check_duplicate + 1
                                } else {
                                    check_duplicate = check_duplicate + 0
                                }
                            }

                            if (check_duplicate > 0) {
                                et_please_enter_keyword.setText("")
                                Toast.makeText(itemView.context, "名稱不可重複", Toast.LENGTH_SHORT).show()

                            } else {

                                onItemUpdate(
                                    et_please_enter_keyword.text.toString(),
                                    et_please_enter_click_bid.text.toString(),
                                    tv_search_volume_value.text.toString(),
                                    tv_higher_bidding_ad_value.text.toString(),
                                    tv_recommended_bid_value.text.toString(),
                                    adapterPosition
                                )

                            }

                        }

                        RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(true))
                    }

                }
            }
            et_please_enter_keyword.addTextChangedListener(textWatcher_keyword)

            val textWatcher_bid = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {


                    if(et_please_enter_click_bid.text.toString().length >= 2 && et_please_enter_click_bid.text.toString().startsWith("0")){
                        et_please_enter_click_bid.setText(et_please_enter_click_bid.text.toString().replace("0", "", false))
                        et_please_enter_click_bid.setSelection(et_please_enter_click_bid.text.toString().length)
                    }



                    if (et_please_enter_click_bid.text.toString() == "") {

                        onItemUpdate(
                            et_please_enter_keyword.text.toString(),
                            et_please_enter_click_bid.text.toString(),
                            tv_search_volume_value.text.toString(),
                            tv_higher_bidding_ad_value.text.toString(),
                            tv_recommended_bid_value.text.toString(),
                            adapterPosition
                        )

                    } else {

                        onItemUpdate(
                            et_please_enter_keyword.text.toString(),
                            et_please_enter_click_bid.text.toString(),
                            tv_search_volume_value.text.toString(),
                            tv_higher_bidding_ad_value.text.toString(),
                            tv_recommended_bid_value.text.toString(),
                            adapterPosition
                        )

                    }

                    getBidRanking(category, type, et_please_enter_keyword.text.toString(), et_please_enter_click_bid.text.toString())
                }
            }
            et_please_enter_click_bid.addTextChangedListener(textWatcher_bid)

            et_please_enter_keyword.singleLine = true
            et_please_enter_keyword.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

                        et_please_enter_keyword.clearFocus()
                        et_please_enter_keyword.hideKeyboard()

                        true
                    }
                    else -> false
                }
            }

            et_please_enter_click_bid.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus ){
//                    RxBus.getInstance().post(EventCheckShipmentEnableBtnOrNot(false))
                }
            }

            //editText_shipping_fare編輯鍵盤監聽
            et_please_enter_click_bid.singleLine = true
            et_please_enter_click_bid.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

                        et_please_enter_click_bid.clearFocus()
                        et_please_enter_click_bid.hideKeyboard()

                        true
                    }
                    else -> false
                }
            }

            //item上的刪除按鈕設定
//            iv_delete.setOnClickListener(this)
            iv_delete.setOnClickListener {
                deleteClick!!.invoke(adapterPosition)
            }

        }

        fun bind(item: ItemKeywordAd){

            et_please_enter_keyword.setText(item.keyword)
            et_please_enter_click_bid.setText(item.bid)
//            tv_search_volume_value.setText(item.search_volume)
//            tv_higher_bidding_ad_value.setText(item.higher_bidding_ad)
//            tv_recommended_bid_value.setText(item.recommended_bid)

            iv_delete.visibility = View.GONE
            iv_space.visibility = View.GONE
            if (editStatus) {
                iv_delete.visibility = View.VISIBLE
                iv_space.visibility = View.VISIBLE
            } else {
                iv_delete.visibility = View.GONE
                iv_space.visibility = View.GONE
            }

            getBidRanking(category, type, item.keyword, item.bid)

        }

        override fun onClick(v: View?) {
//            when(v?.id) {
//                R.id.iv_delete -> onItemDissmiss(adapterPosition)
//            }
        }

        private fun getBidRanking(ad_category:String, ad_type:String, keyword:String, bid:String) {
            Log.d("getBidRanking", "ad_category: ${ad_category}, ad_type: ${ad_type}, keyword: ${keyword}, bid: ${bid}")
            if(keyword.isNullOrEmpty() || bid.isNullOrEmpty()){
                tv_search_volume_value.setText("")
                tv_higher_bidding_ad_value.setText("")
            }else{
                // ad_category: keyword | recommend | store
                // ad_type: shop | product
                var url = ApiConstants.API_HOST + "user/adSettingRanking/${ad_category}/${ad_type}/?keyword=${keyword}&bid=${bid}"

                val web = Web(object : WebListener {
                    @RequiresApi(Build.VERSION_CODES.P)
                    override fun onResponse(response: Response) {
                        var resStr: String? = ""

                        try {
                            resStr = response.body()!!.string()
                            val json = JSONObject(resStr)
                            val ret_val = json.get("ret_val")
                            val status = json.get("status")
                            Log.d("getBidRanking", "返回資料 resStr：" + resStr)
                            Log.d("getBidRanking", "返回資料 ret_val：" + ret_val)

                            if (status == 0) {

                                val translations: JSONObject = json.getJSONObject("data")
                                var ranking = translations.getString("ranking")
                                var search_volume= translations.getString("search_count")

                                runOnUiThread {
                                    tv_search_volume_value.setText(ranking)
                                    tv_higher_bidding_ad_value.setText(search_volume)
                                }

                            }

                        } catch (e: JSONException) {
                            runOnUiThread {
                                Log.d("getBidRanking", "JSONException: ${e.toString()}")
                            }


                        } catch (e: IOException) {
                            e.printStackTrace()
                            runOnUiThread {
                                Log.d("getBidRanking", "IOException: ${e.toString()}")
                            }


                        }
                    }

                    override fun onErrorResponse(ErrorResponse: IOException?) {
                        runOnUiThread {
                            Log.d("getBidRanking", "ErrorResponse: ${ErrorResponse.toString()}")
                        }
                    }
                })
                web.Do_GetBidRanking(url)
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.item_keyword_advertisement, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = queue_keywordAd.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        holder.bind(queue_keywordAd[position] as ItemKeywordAd)

    }



    //新增空白項目
//    fun addEmptyItem(){
//
//        empty_item_num=0
//        if(mutableList_shipMethod.size>0){
//
//            for(i in 0..mutableList_shipMethod.size-1){
//                if (mutableList_shipMethod[i].shipment_desc == ""){
//                    empty_item_num += 1
//                }else{
//                    empty_item_num += 0
//                }
//            }
//
//            if(empty_item_num == 0 ){
//                mutableList_shipMethod.add(
//                    ItemShippingFare(
//                        "",
//                        "",
//                        "off",
//                        MMKV_shop_id
//                    )
//                )
//
//                try{
//                    Thread.sleep(300)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//
//                notifyDataSetChanged()
//            }
//
//        }else{
//            mutableList_shipMethod.add(
//                ItemShippingFare(
//                    "",
//                    "",
//                    "off",
//                    MMKV_shop_id
//                )
//            )
//            try{
//                Thread.sleep(300)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//            notifyDataSetChanged()
//        }
//
//    }

    //更新資料用
    fun setDatas(queue: Queue<ItemKeywordAd>){
        queue_keywordAd = queue as LinkedList<ItemKeywordAd>
        notifyDataSetChanged()
    }

    fun onItemUpdate(keyword: String, bid: String, search_volume: String, higher_bidding_ad: String, recommended_bid: String, position: Int) {

        if(!position.equals(-1)){
            var itemKeywordAd = ItemKeywordAd()
            itemKeywordAd.keyword = keyword
            itemKeywordAd.bid = bid
//            itemKeywordAd.search_volume = search_volume
//            itemKeywordAd.higher_bidding_ad = higher_bidding_ad
//            itemKeywordAd.recommended_bid = recommended_bid

            queue_keywordAd[position] = itemKeywordAd
        }

    }

    override fun onItemDissmiss(position: Int) {
        queue_keywordAd.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(queue_keywordAd, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getDatas(): LinkedList<ItemKeywordAd> {
        return queue_keywordAd
    }

    //更新資料用
    fun setEditStatus(status: Boolean) {
        editStatus = status
        this.notifyDataSetChanged()
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}

