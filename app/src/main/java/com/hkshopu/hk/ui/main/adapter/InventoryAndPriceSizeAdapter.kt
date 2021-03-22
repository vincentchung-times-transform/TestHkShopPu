package com.hkshopu.hk.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.ItemSpecification
import java.util.*

class InventoryAndPriceSizeAdapter: RecyclerView.Adapter<InventoryAndPriceSizeAdapter.mViewHolder>(),ITHelperInterface  {

    var unAssignList = mutableListOf<InventoryItemSize>()




    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

            //把layout檔的元件們拉進來，指派給當地變數
            val value_size = itemView.findViewById<TextView>(R.id.value_size)
            val value_price = itemView.findViewById<TextView>(R.id.value_price)
            val value_quantity = itemView.findViewById<TextView>(R.id.value_quantity)

            init {
                value_size
                value_price
                value_quantity
            }


            fun bind(item: InventoryItemSize){

                //綁定當地變數與dataModel中的每個值
                value_size.setText(item.size)
                value_price.setText(item.price)
                value_quantity.setText(item.quantity)

            }

            override fun onClick(v: View?) {

                when(v?.id) {
    //                R.id.btn_cancel_specification ->{
    //                    onItemDissmiss(adapterPosition)
    //                }
                }

            }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int):mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.inventoryandprice_size_list_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = unAssignList.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(unAssignList[position])

    }

    //更新資料用
    fun updateList(list:MutableList<InventoryItemSize>){
        unAssignList = list
    }
    override fun onItemDissmiss(position: Int) {
        unAssignList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(unAssignList,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }


}