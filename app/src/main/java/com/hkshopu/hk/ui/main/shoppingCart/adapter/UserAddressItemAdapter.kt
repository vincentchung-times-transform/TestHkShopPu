package com.HKSHOPU.hk.ui.main.shoppingCart.adapter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.EventBuyerDetailedProductNewProDetailedFragment
import com.HKSHOPU.hk.component.EventUpdateShoppingCartItemForConfirmed

import com.HKSHOPU.hk.data.bean.ShopAddressListBean
import com.HKSHOPU.hk.data.bean.UserAddressBean
import com.HKSHOPU.hk.ui.main.shopProfile.activity.BankPresetActivity
import com.HKSHOPU.hk.ui.main.shoppingCart.activity.ShoppingCartConfirmedActivity
import com.HKSHOPU.hk.utils.extension.inflate
import com.HKSHOPU.hk.utils.rxjava.RxBus


import org.jetbrains.anko.find
import java.util.*

class UserAddressItemAdapter(var activity: BaseActivity, var shoppingCartShopId: String, var specId_json: String) : RecyclerView.Adapter<UserAddressItemAdapter.AddressListLinearHolder>(){

    private var selected = -1
    private var mData: MutableList<UserAddressBean> = mutableListOf()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : MutableList<UserAddressBean>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressListLinearHolder {
        val v = parent.context.inflate(R.layout.item_user_address,parent,false)

        return AddressListLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun removeItem(position: Int) {
        this.mData.removeAt(position)
        notifyDataSetChanged()
    }

    var cancelClick: ((id: String) -> Unit)? = null
    var intentClick: ((id: String) -> Unit)? = null

    override fun onBindViewHolder(holder: AddressListLinearHolder, position: Int) {
        val viewHolder: AddressListLinearHolder = holder
        var item = mData.get(position)
        var id = item.id
        var name = item.name
        var phone = "${item.country_code} ${item.phone}"
        val address = item.address

        viewHolder.tv_userName.setText(name)
        viewHolder.tv_userPhone.setText(phone)
        viewHolder.tv_userAddress.setText(address)


        if(item.is_default.equals("Y")){
            viewHolder.tv_preset.visibility = View.VISIBLE
        }else{
            viewHolder.tv_preset.visibility = View.GONE
        }

        viewHolder.itemView.setOnClickListener {

//            intentClick?.invoke(id)
            RxBus.getInstance().post(EventUpdateShoppingCartItemForConfirmed( id.toString(), name.toString(), phone.toString(), address.toString(), shoppingCartShopId.toString(), specId_json))
            activity.finish()

        }

    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,address_id:String)
    }

    inner class AddressListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tv_preset = itemView.find<TextView>(R.id.tv_preset)
        val tv_userName = itemView.find<TextView>(R.id.tv_userName)
        var tv_userPhone = itemView.find<TextView>(R.id.tv_userPhone)
        var tv_userAddress = itemView.find<TextView>(R.id.tv_userAddress)

    }

}