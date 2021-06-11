package com.hkshopu.hk.ui.main.shoppingCart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemInvenFirstNestedLayer
import com.hkshopu.hk.data.bean.ShoppingCartShopItemNestedLayer
import com.hkshopu.hk.ui.main.productSeller.adapter.InventoryAndPriceSecondLayerNestedAdapter
import com.hkshopu.hk.ui.main.store.adapter.ITHelperInterface
import java.util.*

class ShoppingCartShopsNestedAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() ,
    ITHelperInterface {


    var edit_status = true

    var mutableList_shoppingCartShopItems = mutableListOf<ShoppingCartShopItemNestedLayer>()

    fun set_edit_mode(mode: Boolean){
        this.edit_status = mode
        notifyDataSetChanged()
    }

    //把水平rview元件拉進來
    inner class FirstLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView){
        val checkBox_shopping_cart_shop = itemView.findViewById<CheckBox>(R.id.checkBox_shopping_cart_shop)
        val imgView_shop_icon = itemView.findViewById<ImageView>(R.id.imgView_shop_icon)
        val txtView_shop_name = itemView.findViewById<TextView>(R.id.txtView_shop_name)
        val r_view_shopping_cart_products = itemView.findViewById<RecyclerView>(R.id.r_view_shopping_cart_products)
        val btn_delete_shopping_cart_shop = itemView.findViewById<ImageView>(R.id.btn_delete_shopping_cart_shop)
        val transparent_space = itemView.findViewById<ImageView>(R.id.transparent_space)


        init {

        }

        fun bind(item: ShoppingCartShopItemNestedLayer){

            checkBox_shopping_cart_shop //尚未設定
            imgView_shop_icon //尚未設定
            txtView_shop_name.setText(item.shop_name)

            if(edit_status){
                btn_delete_shopping_cart_shop.visibility = View.VISIBLE
                transparent_space.visibility = View.VISIBLE

                val mAdapter = ShoppingCartProductsNestedAdapter(item.mutableList_shoppingCartProductItem, edit_status)
                r_view_shopping_cart_products.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                r_view_shopping_cart_products.adapter = mAdapter
                mAdapter.notifyDataSetChanged()

            }else{
                btn_delete_shopping_cart_shop.visibility = View.GONE
                transparent_space.visibility = View.GONE

                val mAdapter = ShoppingCartProductsNestedAdapter(item.mutableList_shoppingCartProductItem, edit_status)
                r_view_shopping_cart_products.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                r_view_shopping_cart_products.adapter = mAdapter
                mAdapter.notifyDataSetChanged()
            }



        }

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.shopping_cart_shops_nested_item,parent,false)

        return FirstLayerViewHolder(itemView)

    }

    override fun getItemCount() = mutableList_shoppingCartShopItems.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {

            is FirstLayerViewHolder -> {

                holder.bind(mutableList_shoppingCartShopItems[position])


            }
        }

    }


    fun setDatas(list: MutableList<ShoppingCartShopItemNestedLayer>) {
        mutableList_shoppingCartShopItems = list

//        notifyDataSetChanged()

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