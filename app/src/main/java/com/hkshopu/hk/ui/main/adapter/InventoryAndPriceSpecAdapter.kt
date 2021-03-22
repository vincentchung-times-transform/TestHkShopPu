package com.hkshopu.hk.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ItemSpecification
import java.util.*

class InventoryAndPriceSpecAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(),ITHelperInterface  {


    var mutableList_InvenSpec = mutableListOf<InventoryItemSpec>()
    var mutableList_InvenSize = mutableListOf<InventoryItemSize>()




    //把水平rview元件拉進來
    inner class secondLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView){
        val r_view_inventory_spec = itemView.findViewById<RecyclerView>(R.id.r_view_inventory_item_spec)

        fun bind(item: InventoryItemSpec){

        }

        }

    //這裡跟原本一樣，把大卡片項目模板的元件們拉進來
    inner class MainLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView){
        //把layout檔的元件們拉進來，指派給當地變數

        val spec = itemView.findViewById<EditText>(R.id.value_spec)


        fun bind(item:InventoryItemSpec){

            //綁定當地變數與dataModel中的每個值
            spec.setText(item.spec)

        }
    }



    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            0->{

                //這裡把container載入進來，做出巢狀recyclerview
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.inventoryandprice_spec_list_item,parent,false)

                //設定水平滑動的recyclerview
                //因為綁adapter跟layoutmanager這些是一次性的東西，如果寫在onBindViewHolder，就會在使用者每一次下滑捲動 系統更新項目資料的時候一直重複呼叫!
                val mAdapters_InvenSize = InventoryAndPriceSizeAdapter()

                mAdapters_InvenSize.updateList(mutableList_InvenSize)

                val linearlayout = LinearLayoutManager(parent.context)
                linearlayout.orientation = RecyclerView.VERTICAL
                secondLayerViewHolder(itemView).r_view_inventory_spec.layoutManager = linearlayout
                secondLayerViewHolder(itemView).r_view_inventory_spec.adapter = mAdapters_InvenSize
                secondLayerViewHolder(itemView)
            }
            1->{
                //載入大卡片的項目模板
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.inventoryandprice_spec_list_item,parent,false)
                MainLayerViewHolder(itemView)
            }


            else -> {
                //暫時替代
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.inventoryandprice_spec_list_item,parent,false)
                MainLayerViewHolder(itemView)

            }
        }



    }

    override fun getItemCount() = mutableList_InvenSpec.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //呼叫上面的bind方法來綁定資料

        when(holder ){
            is secondLayerViewHolder->{}
            is MainLayerViewHolder -> holder.bind(mutableList_InvenSpec[position])
        }

    }


    //更新資料用
    fun updateList(list_spec:MutableList<InventoryItemSpec>, list_size:MutableList<InventoryItemSize>){
        mutableList_InvenSpec = list_spec
        mutableList_InvenSize = list_size
    }
    override fun onItemDissmiss(position: Int) {
        mutableList_InvenSpec.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_InvenSpec,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }


}