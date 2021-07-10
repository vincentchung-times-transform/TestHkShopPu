package com.HKSHOPU.hk.ui.main.shoppingCart.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.Base.BaseActivity
import com.facebook.FacebookSdk.getApplicationContext
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*

import com.HKSHOPU.hk.data.bean.ShoppingCartItemIdBean
import com.HKSHOPU.hk.data.bean.ShoppingCartProductItemNestedLayer
import com.HKSHOPU.hk.data.bean.ShoppingCartProductShipmentItem
import com.HKSHOPU.hk.net.ApiConstants
import com.HKSHOPU.hk.net.Web
import com.HKSHOPU.hk.net.WebListener
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ITHelperInterface
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.squareup.picasso.Picasso
import com.tencent.mmkv.MMKV
import okhttp3.Response
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class ShoppingCartProductsNestedAdapter(
    var mutableList_shoppingCartProductItem: MutableList<ShoppingCartProductItemNestedLayer>,
    var edit_mode: Boolean,
    var activity: BaseActivity,
    var shop_index: Int
): RecyclerView.Adapter<ShoppingCartProductsNestedAdapter.mViewHolder>(), ITHelperInterface {

    var MMKV_user_id: String = MMKV.mmkvWithID("http").getString("UserId", "").toString()
    var hkd_dollarSign = ""

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var product_id: String = ""
        var product_checked = false
        //把layout檔的元件們拉進來，指派給當地變數
        val imgView_product_icon = itemView.findViewById<ImageView>(R.id.imgView_product_icon)
        val textView_product_name = itemView.findViewById<TextView>(R.id.textView_product_name)
        var shopping_cart_item_id = ""
        val textView_product_first_spec_name = itemView.findViewById<TextView>(R.id.textView_product_first_spec_name)
        var textView_product_first_spec_item =  itemView.findViewById<TextView>(R.id.textView_product_first_spec_item)
        var textView_product_first_colon = itemView.find<TextView>(R.id.textView_product_first_colon)
        val textView_product_second_spec_name = itemView.findViewById<TextView>(R.id.textView_product_second_spec_name)
        var textView_product_second_spec_item =  itemView.findViewById<TextView>(R.id.textView_product_second_spec_item)
        var textView_product_second_colon = itemView.find<TextView>(R.id.textView_product_second_colon)
        var shopping_cart_ic_math_subtract =  itemView.findViewById<ImageView>(R.id.shopping_cart_ic_math_subtract)
        val shopping_cart_tv_value_quantitiy = itemView.findViewById<TextView>(R.id.shopping_cart_tv_value_quantitiy)
        var shopping_cart_ic_math_add =  itemView.findViewById<ImageView>(R.id.shopping_cart_ic_math_add)
        val textView_price = itemView.findViewById<TextView>(R.id.textView_price)
        var unit_price = 0
        var spec_quantity_sum_price = 0
        var stock_quantity = 0
        var container_logistics_spinner = itemView.findViewById<Spinner>(R.id.container_logistics_spinner)
        var logistics_fare_selected_id = ""
        var logistics_fare_selected_name = ""
        var textView_logistics_fare_selecting = itemView.findViewById<TextView>(R.id.textView_logistics_fare_selecting)
        var textView_logistics_selecting_confirmed = itemView.findViewById<TextView>(R.id.textView_logistics_selecting_confirmed)
        var textView_logistics_fare_selecting_confirmed = itemView.findViewById<TextView>(R.id.textView_logistics_fare_selecting_confirmed)
        var shopping_cart_tv_value_quantitiy_confirmed = itemView.findViewById<TextView>(R.id.shopping_cart_tv_value_quantitiy_confirmed)
        var layout_quantity_abacus = itemView.findViewById<LinearLayout>(R.id.layout_quantity_abacus)
        var layout_logistics_selecting = itemView.findViewById<LinearLayout>(R.id.layout_logistics_selecting)
        var layout_logistics_selecting_confirmed = itemView.findViewById<LinearLayout>(R.id.layout_logistics_selecting_confirmed)
        var btn_delete_shopping_cart_prodcut = itemView.findViewById<ImageView>(R.id.btn_delete_shopping_cart_prodcut)
        var item_id_list_json = ""

        init {
            hkd_dollarSign = itemView.context.getResources().getString(R.string.hkd_dollarSign)
        }

        fun bind(item: ShoppingCartProductItemNestedLayer){

            product_id = item.product_id
            product_checked = item.product_checked
            Picasso.get().load(item.product_pic).into(imgView_product_icon)

            textView_product_name.setText(item.product_title.toString())
            shopping_cart_item_id = item.product_spec.shopping_cart_item_id.toString()
            textView_product_first_spec_name.setText(item.product_spec.spec_desc_1.toString())
            textView_product_first_spec_item.setText(item.product_spec.spec_dec_1_items.toString())
            textView_product_second_spec_name.setText(item.product_spec.spec_desc_2.toString())
            textView_product_second_spec_item.setText(item.product_spec.spec_dec_2_items.toString())
            if(item.product_spec.spec_desc_1.isNullOrEmpty() && item.product_spec.spec_dec_1_items.isNullOrEmpty()){
                textView_product_first_spec_name.visibility = View.GONE
                textView_product_first_colon.visibility = View.GONE
                textView_product_first_spec_item.visibility = View.GONE
            }
            if(item.product_spec.spec_desc_2.isNullOrEmpty() && item.product_spec.spec_dec_2_items.isNullOrEmpty()){
                textView_product_second_spec_name.visibility = View.GONE
                textView_product_second_colon.visibility = View.GONE
                textView_product_second_spec_item.visibility = View.GONE
            }

            unit_price = item.product_spec.spec_price
            stock_quantity = item.product_spec.spec_quantity
            spec_quantity_sum_price = unit_price*item.product_spec.shopping_cart_quantity
            textView_price.setText(spec_quantity_sum_price.toString())
            shopping_cart_ic_math_subtract.setOnClickListener(this)
            shopping_cart_tv_value_quantitiy.setText(item.product_spec.shopping_cart_quantity.toString())
            shopping_cart_ic_math_add.setOnClickListener(this)
            logistics_fare_selected_id = item.selected_shipment.product_shipment_id.toString()
            logistics_fare_selected_name = item.selected_shipment.shipment_desc.toString()
            textView_logistics_fare_selecting.setText(item.selected_shipment.shipment_price.toString())
            btn_delete_shopping_cart_prodcut.setOnClickListener(this)
            shopping_cart_tv_value_quantitiy_confirmed.setText("x${item.product_spec.shopping_cart_quantity.toString()}")
            textView_logistics_selecting_confirmed.setText(item.selected_shipment.shipment_desc.toString())
            textView_logistics_fare_selecting_confirmed.setText(item.selected_shipment.shipment_price.toString())

            var item_id_list = arrayListOf<String>()
            item_id_list.add(item.product_spec.shopping_cart_item_id)

            var gson = Gson()
            item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))

            getProductShipmentForBuyer(item.product_id.toString(), item.shipmentList)

//            val logistics_list: MutableList<String> = ArrayList<String>()
//            for (i in 0 until item.shipmentList.size) {
//                logistics_list.add("${item.shipmentList.get(i).shipment_desc.toString()}${"\r"}${hkd_dollarSign}${item.shipmentList.get(i).shipment_price.toString()}")
//            }
//
//            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
//                getApplicationContext(),
//                android.R.layout.simple_spinner_dropdown_item,
//                logistics_list
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//            container_logistics_spinner.setAdapter(adapter)
//            container_logistics_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
//
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long,
//                ) {
//
//                    textView_logistics_fare_selecting.setText(item.shipmentList.get(position).shipment_price.toString())
//                    mutableList_shoppingCartProductItem.get(adapterPosition).selected_shipment.product_shipment_id = item.shipmentList.get(position).product_shipment_id.toString()
//                    mutableList_shoppingCartProductItem.get(adapterPosition).selected_shipment.shipment_desc = item.shipmentList.get(position).shipment_desc.toString()
//                    mutableList_shoppingCartProductItem.get(adapterPosition).selected_shipment.shipment_price = item.shipmentList.get(position).shipment_price.toInt()
//                    RxBus.getInstance().post(EventUpdateShoppingCartItem(item_id_list_json, item.shipmentList.get(position).product_shipment_id.toString(), "", "", ""))
//
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    TODO("Not yet implemented")
//                }
//
//            }
            
            mutableList_shoppingCartProductItem.get(adapterPosition).product_spec.spec_quantity_sum_price = spec_quantity_sum_price


            if(edit_mode){

                btn_delete_shopping_cart_prodcut.visibility = View.VISIBLE
                shopping_cart_tv_value_quantitiy_confirmed.visibility = View.GONE
                layout_quantity_abacus.visibility = View.VISIBLE
                layout_logistics_selecting_confirmed.visibility = View.GONE
                layout_logistics_selecting.visibility = View.VISIBLE

            }else{

                btn_delete_shopping_cart_prodcut.visibility = View.GONE
                shopping_cart_tv_value_quantitiy_confirmed.visibility = View.VISIBLE
                layout_quantity_abacus.visibility = View.GONE
                layout_logistics_selecting_confirmed.visibility = View.VISIBLE
                layout_logistics_selecting.visibility = View.GONE

            }


        }

        override fun onClick(v: View?) {

            when(v?.id) {
                R.id.shopping_cart_ic_math_add ->{
                    var quant =  shopping_cart_tv_value_quantitiy.text.toString().toInt()

                    if(stock_quantity>quant){

                        quant += 1
                        shopping_cart_tv_value_quantitiy.setText(quant.toString())

                        spec_quantity_sum_price+=unit_price
                        textView_price.setText(spec_quantity_sum_price.toString())

                        RxBus.getInstance().post(EventUpdateShoppingCartItem(item_id_list_json, quant.toString(), "", "", ""))

                        mutableList_shoppingCartProductItem.get(adapterPosition).product_spec.shopping_cart_quantity = quant
                        mutableList_shoppingCartProductItem.get(adapterPosition).product_spec.spec_quantity_sum_price = spec_quantity_sum_price

                    }

                }
                R.id.shopping_cart_ic_math_subtract ->{

                    var quant =  shopping_cart_tv_value_quantitiy.text.toString().toInt()

                    if(quant>0){
                        quant -= 1
                        spec_quantity_sum_price-=unit_price
                        shopping_cart_tv_value_quantitiy.setText(quant.toString())
                        textView_price.setText(spec_quantity_sum_price.toString())

                        RxBus.getInstance().post(EventUpdateShoppingCartItem(item_id_list_json, quant.toString(), "", "", ""))

                        mutableList_shoppingCartProductItem.get(adapterPosition).product_spec.shopping_cart_quantity = quant
                        mutableList_shoppingCartProductItem.get(adapterPosition).product_spec.spec_quantity_sum_price = spec_quantity_sum_price
                    }

                }
                R.id.btn_delete_shopping_cart_prodcut ->{

                    Log.d("shopping_cart_item_id", shopping_cart_item_id.toString())
//                    doDeleteShoppingCartitems(item_id_list_json)
                    doDeleteShoppingCartitems(MMKV_user_id, "", shopping_cart_item_id)

                }
            }
        }


        private fun doDeleteShoppingCartitems (user_id: String, shop_id: String, shopping_cart_item_id: String) {

            val url = ApiConstants.API_HOST+"shopping_cart/delete/"

            val web = Web(object : WebListener {
                override fun onResponse(response: Response) {
                    var resStr: String? = ""
                    try {

                        resStr = response.body()!!.string()
                        val json = JSONObject(resStr)
                        Log.d("doDeleteShoppingCartitems", "返回資料 resStr：" + resStr)
                        Log.d("doDeleteShoppingCartitems", "返回資料 ret_val：" + json.get("ret_val"))
                        val ret_val = json.get("ret_val")

                        if (ret_val.equals("刪除成功")) {

                            itemView.context.runOnUiThread {
                                Toast.makeText(itemView.context, ret_val.toString(), Toast.LENGTH_SHORT).show()
                                onItemDissmiss(adapterPosition)

                                if(mutableList_shoppingCartProductItem.size==0){
                                    RxBus.getInstance().post(EventRemoveShoppingCartItem("", "", shop_index))
                                }
                            }

                            RxBus.getInstance().post(EventRefreshShoppingCartItemCount())

                        }else{

                            itemView.context.runOnUiThread {
                                Toast.makeText(itemView.context, "網路異常請重新嘗試", Toast.LENGTH_SHORT).show()
                            }

                        }

                    } catch (e: JSONException) {
                        Log.d("doDeleteShoppingCartitems", "JSONException：" + e.toString())

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("doDeleteShoppingCartitems", "IOException：" + e.toString())

                    }
                }

                override fun onErrorResponse(ErrorResponse: IOException?) {
                    Log.d("doDeleteShoppingCartitems", "ErrorResponse：" + ErrorResponse.toString())

                }
            })
            web.doDeleteShoppingCartitems(url, user_id, shop_id, shopping_cart_item_id)
        }

        private fun getProductShipmentForBuyer (product_id: String, item_shipment_List: MutableList<ShoppingCartProductShipmentItem>) {

            val url = ApiConstants.API_HOST+"shopping_cart/${product_id}/product_shipment/"

            val web = Web(object : WebListener {
                override fun onResponse(response: Response) {
                    var resStr: String? = ""
                    try {

                        resStr = response.body()!!.string()
                        val json = JSONObject(resStr)
                        Log.d("getProductShipmentForBuyer", "返回資料 resStr：" + resStr)
                        Log.d("getProductShipmentForBuyer", "返回資料 ret_val：" + json.get("ret_val"))
                        val ret_val = json.get("ret_val")
                        if (ret_val.equals("運送方式取得成功!")) {

                            val jsonArray: JSONArray = json.getJSONArray("data")
                            Log.d("getProductShipmentForBuyer", "返回資料 jsonArray：" + jsonArray.toString())

                            //每次call getProductShipmentForBuyer api 會重複疊加所以要clear
                            item_shipment_List.clear()
                            if( jsonArray.length()>0 ){

                                for (i in 0 until jsonArray.length()) {

                                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                                    item_shipment_List.add(
                                        Gson().fromJson(
                                            jsonObject.toString(),
                                            ShoppingCartProductShipmentItem::class.java
                                        ))
                                }

                                runOnUiThread {

                                    val logistics_list: MutableList<String> = ArrayList<String>()
                                    for (i in 0 until item_shipment_List.size) {
                                        logistics_list.add("${item_shipment_List.get(i).shipment_desc.toString()}${"\r"}${hkd_dollarSign}${item_shipment_List.get(i).shipment_price.toString()}")
                                    }
                                    
                                    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                        getApplicationContext(),
                                        android.R.layout.simple_spinner_dropdown_item,
                                        logistics_list
                                    )
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


                                    container_logistics_spinner.setAdapter(adapter)
                                    container_logistics_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

                                        override fun onItemSelected(
                                            parent: AdapterView<*>?,
                                            view: View?,
                                            position: Int,
                                            id: Long,
                                        ) {

                                            textView_logistics_fare_selecting.setText(item_shipment_List.get(position).shipment_price.toString())
                                            mutableList_shoppingCartProductItem.get(adapterPosition).selected_shipment.product_shipment_id = item_shipment_List.get(position).product_shipment_id.toString()
                                            mutableList_shoppingCartProductItem.get(adapterPosition).selected_shipment.shipment_desc = item_shipment_List.get(position).shipment_desc.toString()
                                            mutableList_shoppingCartProductItem.get(adapterPosition).selected_shipment.shipment_price = item_shipment_List.get(position).shipment_price.toInt()
                                            RxBus.getInstance().post(EventUpdateShoppingCartItem(item_id_list_json,"", item_shipment_List.get(position).product_shipment_id.toString(), "", ""))

                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>?) {
                                            TODO("Not yet implemented")
                                        }

                                    }

                                    var default_Position = 0

                                    for (i in 0 until item_shipment_List.size) {
                                        if(item_shipment_List.get(i).product_shipment_id.toString() == logistics_fare_selected_id.toString()){
                                            default_Position = i
                                        }
                                    }

                                    container_logistics_spinner.setSelection(default_Position)
                                }

                            }else{

                            }

                        }else{

                        }

                    } catch (e: JSONException) {
                        Log.d("errormessage", "getProductShipmentForBuyer: JSONException" + "${e.toString()}")
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.d("errormessage", "getProductShipmentForBuyer: IOException" + "${e.toString()}")
                    }
                }

                override fun onErrorResponse(ErrorResponse: IOException?) {
                    Log.d("errormessage", "getProductShipmentForBuyer: ErrorResponse" + "${ErrorResponse.toString()}")
                }
            })
            web.Get_Data(url)

        }

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.shopping_cart_products_nested_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = mutableList_shoppingCartProductItem.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(mutableList_shoppingCartProductItem.get(position))

    }


    override fun onItemDissmiss(position: Int) {
        mutableList_shoppingCartProductItem.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_shoppingCartProductItem,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }


    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun setDatas(list: MutableList<ShoppingCartProductItemNestedLayer>){
        mutableList_shoppingCartProductItem = list
        notifyDataSetChanged()
    }
    fun getDatas(): MutableList<ShoppingCartProductItemNestedLayer> {
        return mutableList_shoppingCartProductItem
    }
}