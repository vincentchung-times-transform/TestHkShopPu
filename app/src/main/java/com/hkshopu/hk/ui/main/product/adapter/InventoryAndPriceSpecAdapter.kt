package com.hkshopu.hk.ui.main.product.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.NestedScrollingParent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.InventoryItemSpec
import com.hkshopu.hk.data.bean.ItemInvenFirstLayer
import com.hkshopu.hk.data.bean.ItemInvenSecondLayer
import com.hkshopu.hk.ui.main.store.adapter.ITHelperInterface
import java.util.*

class InventoryAndPriceSpecAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() ,
    ITHelperInterface {


    var mutableList_InvenSpec = mutableListOf<ItemInvenFirstLayer>()
    var specGroup_only:Boolean = false

    //把水平rview元件拉進來
    inner class FirstLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView){
        val container_spec_first_layer_title = itemView.findViewById<LinearLayout>(R.id.container_spec_first_layer_title)
        val item_spec_title_name = itemView.findViewById<TextView>(R.id.title_spec)
        val item_spec_column_name = itemView.findViewById<TextView>(R.id.item_spec_column_name)
        var item_spec_name = itemView.findViewById<TextView>(R.id.value_spec)
        val r_view_inventory_spec = itemView.findViewById<RecyclerView>(R.id.r_view_inventory_item_spec)

        init {

        }

        fun bind(item: ItemInvenFirstLayer){

            item_spec_name.setText(item.spec_dec_1_items)

            if(specGroup_only==true){
                container_spec_first_layer_title.visibility = View.GONE
                item_spec_column_name.text = item.spec_desc_1
            }else{
                item_spec_title_name.isVisible = true
                item_spec_title_name.text = item.spec_desc_1
                item_spec_column_name.text = item.spec_desc_2
            }

            val mAdapter = InventoryAndPriceSizeAdapter(item.mutableList_itemInvenSecondLayer)
            r_view_inventory_spec.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            r_view_inventory_spec.adapter = mAdapter

        }

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.inventoryandprice_spec_list_item,parent,false)

        return FirstLayerViewHolder(itemView)

    }

    override fun getItemCount() = mutableList_InvenSpec.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {

            is FirstLayerViewHolder -> {
                holder.bind(mutableList_InvenSpec[position])

            }
        }

    }


    //更新資料用
    fun updateList(list_spec: MutableList<ItemInvenFirstLayer>, specGroup_only: Boolean) {
        mutableList_InvenSpec = list_spec
        this.specGroup_only = specGroup_only
        notifyDataSetChanged()

    }

    override fun onItemDissmiss(position: Int) {
        mutableList_InvenSpec.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_InvenSpec, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun getDatas_invenSpec(): MutableList<ItemInvenFirstLayer> {

        return mutableList_InvenSpec
    }

}