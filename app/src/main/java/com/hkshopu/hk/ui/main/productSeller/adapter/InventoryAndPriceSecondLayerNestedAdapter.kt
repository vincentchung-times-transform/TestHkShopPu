package com.HKSHOPU.hk.ui.main.productSeller.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R

import com.HKSHOPU.hk.data.bean.ItemInvenSecondNestedLayer
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ITHelperInterface
import java.util.*

class InventoryAndPriceSecondLayerNestedAdapter(var unAssignList: MutableList<ItemInvenSecondNestedLayer>): RecyclerView.Adapter<InventoryAndPriceSecondLayerNestedAdapter.mViewHolder>(),
    ITHelperInterface {


    var parentPosition =0


    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //把layout檔的元件們拉進來，指派給當地變數
        val textView_value_name = itemView.findViewById<TextView>(R.id.second_spec_name)
        val textView_value_price = itemView.findViewById<TextView>(R.id.value_price)
        val textView_value_quantity = itemView.findViewById<TextView>(R.id.value_quantity)
        var textView_Hkdollars =  itemView.findViewById<TextView>(R.id.textView_HKdolors)
        var unAssignList : MutableList<ItemInvenSecondNestedLayer> = mutableListOf()

        //選高資料變數
        var value_name:String =""
        var value_price :String = ""
        var value_quantity : String = ""

        init {

        }


        fun bind(item: ItemInvenSecondNestedLayer){

            //綁定當地變數與dataModel中的每個值
            textView_value_name.setText(item.spec_dec_2_items)
            textView_value_price.setText(item.price.toString())
            textView_value_quantity.setText(item.quantity.toString())


            if (!item.price.equals("")){
                textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.black))
                textView_value_price.setTextColor(itemView.context.resources.getColor(R.color.black))
            }else{
                textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
                textView_value_price.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
            }

            if (!item.quantity.equals("")){
                textView_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.black))
            }else{
                textView_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.bright_gray))
            }



        }

        override fun onClick(v: View?) {

            when(v?.id) {
            }

        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.inventoryandprice_size_list_nested_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = unAssignList.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(unAssignList.get(position))

    }


    override fun onItemDissmiss(position: Int) {
        unAssignList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(unAssignList,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }



    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}