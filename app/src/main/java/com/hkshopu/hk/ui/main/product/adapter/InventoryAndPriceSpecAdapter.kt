package com.hkshopu.hk.ui.main.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.ui.main.store.adapter.ITHelperInterface
import java.util.*

class InventoryAndPriceSpecAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ITHelperInterface {


    var mutableList_InvenSpec = mutableListOf<InventoryItemSpec>()
    var mutableList_InvenSize = mutableListOf<InventoryItemSize>()
    var specGroup_only:Boolean = false
    var datas_spec_title_first :String = ""
    var datas_spec_title_second :String = ""

    val mAdapters_InvenSize = InventoryAndPriceSizeAdapter()


    //把水平rview元件拉進來
    inner class FirstLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView){
        val item_spec_title_name = itemView.findViewById<TextView>(R.id.title_spec)
        val item_spec_column_name = itemView.findViewById<TextView>(R.id.item_spec_column_name)
        var item_spec_name = itemView.findViewById<TextView>(R.id.value_spec)
        val r_view_inventory_spec = itemView.findViewById<RecyclerView>(R.id.r_view_inventory_item_spec)

        init {

           if(specGroup_only==true){
               item_spec_title_name.isVisible = false
               item_spec_title_name.text  = ""
               item_spec_column_name.text = datas_spec_title_first
           }else{
               item_spec_title_name.isVisible = true
               item_spec_title_name.text = datas_spec_title_first
               item_spec_column_name.text = datas_spec_title_second
           }

        }


        fun bind(item: InventoryItemSpec){
            item_spec_name.text = item.spec

        }

    }

    //這裡跟原本一樣，把大卡片項目模板的元件們拉進來
    inner class SecondLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView){
        //把layout檔的元件們拉進來，指派給當地變數

        val item_size_name = itemView.findViewById<TextView>(R.id.item_spec_column_name)


        fun bind(item: InventoryItemSize){

            //綁定當地變數與dataModel中的每個值
            item_size_name.setText(item.size_name)

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

                mAdapters_InvenSize.updateList(mutableList_InvenSize)

                val linearlayout = LinearLayoutManager(parent.context)
                linearlayout.orientation = RecyclerView.VERTICAL

                FirstLayerViewHolder(itemView).r_view_inventory_spec.layoutManager = linearlayout
                FirstLayerViewHolder(itemView).r_view_inventory_spec.adapter = mAdapters_InvenSize
                FirstLayerViewHolder(itemView)


            }
            1->{
                //載入大卡片的項目模板
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.inventoryandprice_spec_list_item,parent,false)
                SecondLayerViewHolder(itemView)
            }


            else -> {

                //暫時替代
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.inventoryandprice_spec_list_item,parent,false)
                SecondLayerViewHolder(itemView)

            }
        }



    }

    override fun getItemCount() = mutableList_InvenSpec.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //呼叫上面的bind方法來綁定資料

        when (holder) {
            is FirstLayerViewHolder -> {
                holder.bind(mutableList_InvenSpec[position])
            }
            is SecondLayerViewHolder -> {
                holder.bind(mutableList_InvenSize[position])
            }

        }

    }

    //更新資料用
    fun updateList(list_spec: MutableList<InventoryItemSpec>, list_size: MutableList<InventoryItemSize>, specGroup_only: Boolean, datas_spec_title_first: String, datas_spec_title_second: String) {
        mutableList_InvenSpec = list_spec
        mutableList_InvenSize = list_size
        this.specGroup_only = specGroup_only
        this.datas_spec_title_first = datas_spec_title_first
        this.datas_spec_title_second = datas_spec_title_second
    }

    override fun onItemDissmiss(position: Int) {
        mutableList_InvenSpec.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_InvenSpec, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getDatas_invenSpec(): MutableList<InventoryItemSpec> {

        return mutableList_InvenSpec
    }
    fun getDatas_invenSize(): MutableList<InventoryItemSize> {
        return mAdapters_InvenSize.getDatas_invenSize()
    }
}