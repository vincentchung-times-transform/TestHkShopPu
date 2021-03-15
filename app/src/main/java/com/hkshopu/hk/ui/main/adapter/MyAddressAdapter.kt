package com.hkshopu.hk.ui.main.adapter

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.MyAddresssItem

class MyAddressAdapter : RecyclerView.Adapter<MyAddressAdapter.mViewHolder>(){

    var unAssignList = listOf<MyAddresssItem>()

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        //把layout檔的元件們拉進來，指派給當地變數
        val name= itemView.findViewById<TextView>(R.id.txtView_buyername)
        val phone= itemView.findViewById<TextView>(R.id.txtView_phone)
        val address = itemView.findViewById<TextView>(R.id.layout_address)
        val status = itemView.findViewById<TextView>(R.id.txtView_status)

        fun bind(item: MyAddresssItem){
            //綁定當地變數與dataModel中的每個值
            name.text = item.name
            phone.text = item.phone
            address.text = item.address
            status.text = item.status

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.item_myaddress, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = unAssignList.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(unAssignList[position])

    }

    //更新資料用
    fun updateList(list:ArrayList<MyAddresssItem>){
        unAssignList = list
    }
}