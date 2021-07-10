package com.HKSHOPU.hk.ui.main.shoppingCart.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.ShoppingCartItemIdBean
import com.HKSHOPU.hk.data.bean.ShoppingCartShopItemNestedLayer
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ITHelperInterface
import com.HKSHOPU.hk.ui.main.shoppingCart.activity.AddBuyerAddressForShoppingCartActivity
import com.HKSHOPU.hk.ui.main.shoppingCart.activity.ShopAddressListForShoppingCartActivity
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.*

class ShoppingCartShopsNestedAdapter(var activity: BaseActivity): RecyclerView.Adapter<ShoppingCartShopsNestedAdapter.FirstLayerViewHolder>() ,
    ITHelperInterface {


    var edit_status = true
    var address_less = false

    var mutableList_shoppingCartShopItems = mutableListOf<ShoppingCartShopItemNestedLayer>()

    fun set_edit_mode(mode: Boolean){
        this.edit_status = mode
        notifyDataSetChanged()
    }

    //把水平rview元件拉進來
    inner class FirstLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var mAdapter = ShoppingCartProductsNestedAdapter(mutableListOf(), edit_status, activity, adapterPosition)
        var shop_id: String= ""
        val checkBox_shopping_cart_shop = itemView.findViewById<CheckBox>(R.id.checkBox_shopping_cart_shop)
        val imgView_shop_icon = itemView.findViewById<ImageView>(R.id.imgView_shop_icon)
        val txtView_shop_name = itemView.findViewById<TextView>(R.id.txtView_shop_name)
        val r_view_shopping_cart_products = itemView.findViewById<RecyclerView>(R.id.r_view_shopping_cart_products)
        val btn_delete_shopping_cart_shop = itemView.findViewById<ImageView>(R.id.btn_delete_shopping_cart_shop)
        val transparent_space = itemView.findViewById<ImageView>(R.id.transparent_space)
        val btn_shopping_cart_shop_address_spinner = itemView.findViewById<LinearLayout>(R.id.btn_shopping_cart_shop_address_spinner)
        var textView_selected_addresss_user_name = itemView.findViewById<TextView>(R.id.textView_selected_ddresss_user_name)
        var textView_selected_addresss_user_phone = itemView.findViewById<TextView>(R.id.textView_selected_addresss_user_phone)
        var textView_selected_addresss_user_address = itemView.findViewById<TextView>(R.id.textView_selected_addresss_user_address)
        var layout_alert_no_address = itemView.findViewById<LinearLayout>(R.id.layout_alert_no_address)
        var ic_address_spinner = itemView.findViewById<ImageView>(R.id.ic_address_spinner)
        val layout_price_total_price = itemView.findViewById<LinearLayout>(R.id.layout_price_total_price)
        var textView_product_price_total = itemView.findViewById<TextView>(R.id.textView_product_price_total)
        var product_price_total = 0
        var item_id_list_json = ""

        init {

        }

        fun bind(item: ShoppingCartShopItemNestedLayer){

            if(item.productList.size == 0){
                onItemDissmiss(adapterPosition)
            }

            var item_id_list = arrayListOf<String>()
            for(i in 0..item.productList.size-1){
                item_id_list.add(item.productList.get(i).product_spec.shopping_cart_item_id)
            }
            var gson = Gson()
            item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))

            if(item.shop_checked){
                checkBox_shopping_cart_shop.isChecked = true
            }else{
                checkBox_shopping_cart_shop.isChecked = false
            }

            shop_id=item.shop_id
            txtView_shop_name.setText(item.shop_title)
            Picasso.get().load(item.shop_icon).into(imgView_shop_icon)

            if(edit_status){
                
                checkBox_shopping_cart_shop.visibility = View.VISIBLE
                btn_delete_shopping_cart_shop.visibility = View.VISIBLE
                transparent_space.visibility = View.VISIBLE
                btn_shopping_cart_shop_address_spinner.visibility = View.GONE
                layout_price_total_price.visibility = View.GONE

                mAdapter = ShoppingCartProductsNestedAdapter(item.productList, edit_status, activity, adapterPosition)
                r_view_shopping_cart_products.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                r_view_shopping_cart_products.adapter = mAdapter
                mAdapter.notifyDataSetChanged()

            }else{
                checkBox_shopping_cart_shop.visibility = View.GONE
                btn_delete_shopping_cart_shop.visibility = View.GONE
                transparent_space.visibility = View.GONE
                btn_shopping_cart_shop_address_spinner.visibility = View.VISIBLE
                layout_price_total_price.visibility = View.VISIBLE

                mAdapter = ShoppingCartProductsNestedAdapter(item.productList, edit_status, activity, adapterPosition)
                r_view_shopping_cart_products.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                r_view_shopping_cart_products.adapter = mAdapter
                mAdapter.notifyDataSetChanged()

            }

            btn_delete_shopping_cart_shop.setOnClickListener(this)

            checkBox_shopping_cart_shop.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){

                    mutableList_shoppingCartShopItems.get(adapterPosition).shop_checked = true
//                    for(i in 0 until mutableList_shoppingCartShopItems.get(adapterPosition).productList.size){
//                        mutableList_shoppingCartShopItems.get(adapterPosition).productList.get(i).product_checked = true
//                    }
                    for(i in 0 until mutableList_shoppingCartShopItems.get(adapterPosition).productList.size){
                        mutableList_shoppingCartShopItems.get(adapterPosition).productList.get(i).product_checked = true
                    }
                    mAdapter.setDatas(mutableList_shoppingCartShopItems.get(adapterPosition).productList)

                    RxBus.getInstance().post(EventCheckedShoppingCartItem())

                }else{

                    mutableList_shoppingCartShopItems.get(adapterPosition).shop_checked = false
                    for(i in 0 until mutableList_shoppingCartShopItems.get(adapterPosition).productList.size){
                        mutableList_shoppingCartShopItems.get(adapterPosition).productList.get(i).product_checked = false
                    }

                    mAdapter.setDatas(mutableList_shoppingCartShopItems.get(adapterPosition).productList )

                    RxBus.getInstance().post(EventCheckedShoppingCartItem())

                }
            }

            btn_shopping_cart_shop_address_spinner.setOnClickListener {

                if(address_less){

                    var item_id_list = arrayListOf<String>()
                    for(j in 0 until item.productList.size){
                        item_id_list.add(item.productList.get(j).product_spec.shopping_cart_item_id.toString())
                    }
                    var gson = Gson()
                    var item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))

                    val intent = Intent( itemView.context , AddBuyerAddressForShoppingCartActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("addMode", "first_address")
                    bundle.putString("shoppingCartShopId", item.shop_id.toString())
                    bundle.putString("specId_json", item_id_list_json.toString())
                    intent.putExtra("bundle_addMode", bundle)
                    activity.startActivity(intent)


                }else{

                    var item_id_list = arrayListOf<String>()
                    for(j in 0 until item.productList.size){
                        item_id_list.add(item.productList.get(j).product_spec.shopping_cart_item_id.toString())
                    }
                    var gson = Gson()
                    var item_id_list_json = gson.toJson(ShoppingCartItemIdBean(item_id_list))


                    val intent = Intent( itemView.context , ShopAddressListForShoppingCartActivity::class.java)
                    var bundle = Bundle()
                    bundle.putString("shoppingCartId", shop_id)
                    bundle.putString("specId_json", item_id_list_json)
                    intent.putExtra("bundle_shoppingCart", bundle)
                    activity.startActivity(intent)

                }

            }

            for(i in 0 until item.productList.size){
                product_price_total += item.productList.get(i).product_spec.spec_quantity_sum_price.toInt() + item.productList.get(i).selected_shipment.shipment_price.toInt()
            }

            textView_product_price_total.setText(product_price_total.toString())

            textView_selected_addresss_user_name.setText(item.selected_addresss.selected_addresss_user_name)
            textView_selected_addresss_user_phone.setText(item.selected_addresss.selected_addresss_user_name)
            textView_selected_addresss_user_address.setText(item.selected_addresss.selected_addresss_user_address)


            if(address_less){
                btn_shopping_cart_shop_address_spinner.setBackgroundResource(R.drawable.customview_specification_spinner_empty_address)
                textView_selected_addresss_user_name.visibility = View.GONE
                textView_selected_addresss_user_phone.visibility = View.GONE
                textView_selected_addresss_user_address.visibility = View.GONE
                layout_alert_no_address.visibility = View.VISIBLE
                ic_address_spinner.setImageResource(R.mipmap.alert_red)
            }else{
                btn_shopping_cart_shop_address_spinner.setBackgroundResource(R.drawable.customview_specification_spinner)
                textView_selected_addresss_user_name.visibility = View.VISIBLE
                textView_selected_addresss_user_phone.visibility = View.VISIBLE
                textView_selected_addresss_user_address.visibility = View.VISIBLE
                layout_alert_no_address.visibility = View.GONE
                ic_address_spinner.setImageResource(R.mipmap.ic_shopping_cart_to_shop_addresss_setting)
            }
        }

        override fun onClick(v: View?) {

            when (v?.id) {
                R.id.btn_delete_shopping_cart_shop -> {
                    Log.d( "check_id_list", item_id_list_json.toString() )
//                    RxBus.getInstance().post(EventRemoveShoppingCartItem(item_id_list_json, adapterPosition))
                    RxBus.getInstance().post(EventRemoveShoppingCartItem(shop_id, "", adapterPosition))
                }
            }

        }

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): FirstLayerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.shopping_cart_shops_nested_item,parent,false)

        return FirstLayerViewHolder(itemView)

    }

    override fun getItemCount() = mutableList_shoppingCartShopItems.size


    override fun onBindViewHolder(holder: FirstLayerViewHolder, position: Int) {

        when (holder) {

            is FirstLayerViewHolder -> {

                holder.bind(mutableList_shoppingCartShopItems[position])

            }
        }

    }


    fun setDatas(list: MutableList<ShoppingCartShopItemNestedLayer>, address_less: Boolean) {
        mutableList_shoppingCartShopItems = list
        this.address_less = address_less
        notifyDataSetChanged()
    }

    fun getDatas(): MutableList<ShoppingCartShopItemNestedLayer> {
        return mutableList_shoppingCartShopItems
    }

    override fun onItemDissmiss(position: Int) {
        mutableList_shoppingCartShopItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_shoppingCartShopItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

}