package com.hkshopu.hk.ui.main.store.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R

import com.hkshopu.hk.data.bean.ItemData
import com.hkshopu.hk.utils.extension.inflate


import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class CategorySingleAdapter : RecyclerView.Adapter<CategorySingleAdapter.CategoryLinearHolder>(){

    private var selected = -1
    private var mData: ArrayList<ItemData> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list : ArrayList<ItemData>){
        list?:return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryLinearHolder {
        val v = parent.context.inflate(R.layout.listview_item,parent,false)

        return CategoryLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: CategoryLinearHolder, position: Int) {
        val viewHolder: CategoryLinearHolder = holder
        val item = mData.get(position)
        viewHolder.title.text = item.title
        if (selected == position) {
            holder.mCheckBox.isChecked = true
            holder.itemView.isSelected = true
        } else {
            holder.mCheckBox.isChecked = false
            holder.itemView.isSelected = false
        }
        if (onClick != null)viewHolder.itemView.setOnClickListener {
            item.isSelect = !item.isSelect
            notifyDataSetChanged()
            onClick!!.onItemClick(
                holder.itemView,
                holder.adapterPosition,
                item)
        }
    }
    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int,bean:ItemData)
    }

    inner class CategoryLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.find<TextView>(R.id.label)
        var mCheckBox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val sdf = SimpleDateFormat("yyyy-MM-dd")

    }



}