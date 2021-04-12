package com.hkshopu.hk.ui.main.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.InventoryItemSize
import com.hkshopu.hk.data.bean.ItemSpecification
import org.jetbrains.anko.singleLine
import java.util.*

class InventoryAndPriceSizeAdapter: RecyclerView.Adapter<InventoryAndPriceSizeAdapter.mViewHolder>(),ITHelperInterface  {

    var unAssignList = mutableListOf<InventoryItemSize>()

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

            //把layout檔的元件們拉進來，指派給當地變數
            val textView_value_name = itemView.findViewById<TextView>(R.id.value_size)
            val editText_value_price = itemView.findViewById<EditText>(R.id.value_price)
            val editText_value_quantity = itemView.findViewById<EditText>(R.id.value_quantity)
            var textView_Hkdollars =  itemView.findViewById<TextView>(R.id.textView_HKdolors)

            //選高資料變數
            var value_name:String =""
            var value_price :String = ""
            var value_quantity : String = ""

            init {

                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                    override fun afterTextChanged(s: Editable?) {

                    }
                }
                editText_value_price.addTextChangedListener(textWatcher)
                editText_value_quantity.addTextChangedListener(textWatcher)

                editText_value_price.singleLine = true
                editText_value_price.setOnEditorActionListener() { v, actionId, event ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE -> {

                            value_name = textView_value_name.text.toString()
                            value_price = editText_value_price.text.toString()
                            value_quantity = editText_value_quantity.text.toString()

                            if(value_price==""){
                                value_price = "0"
                                onItemUpdate(value_name, value_price.toInt(), value_quantity.toInt(), adapterPosition)
                            }else{
                                onItemUpdate(value_name, value_price.toInt(), value_quantity.toInt(), adapterPosition)

                            }

                            editText_value_price.clearFocus()

                            true
                        }
                        else -> false
                    }
                }

                editText_value_quantity.singleLine = true
                editText_value_quantity.setOnEditorActionListener() { v, actionId, event ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE -> {

                            value_name = textView_value_name.text.toString()
                            value_price = editText_value_price.text.toString()
                            value_quantity = editText_value_quantity.text.toString()

                            if(value_quantity==""){
                                value_quantity="0"
                                onItemUpdate(value_name, value_price.toInt(), value_quantity.toInt(), adapterPosition)
                            }else{
                                onItemUpdate(value_name, value_price.toInt(), value_quantity.toInt(), adapterPosition)

                            }

                            editText_value_quantity.clearFocus()

                            true
                        }
                        else -> false
                    }
                }
            }


            fun bind(item: InventoryItemSize){

                //綁定當地變數與dataModel中的每個值
                textView_value_name.setText(item.size_name)
                editText_value_price.setText(item.price.toString())
                editText_value_quantity.setText(item.quantity.toString())


                if (item.price > 0){
                    textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.black))
                    editText_value_price.setTextColor(itemView.context.resources.getColor(R.color.black))
                }else{
                    textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                    editText_value_price.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                }

                if (item.quantity > 0){
                    editText_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.black))
                }else{
                    editText_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                }



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

    fun onItemUpdate(name:String, price: Int,  quanu:Int, position: Int) {

        unAssignList[position] = InventoryItemSize(name, price, quanu)
        notifyItemChanged(position)

    }

    override fun onItemDissmiss(position: Int) {
        unAssignList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(unAssignList,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }

   fun getDatas_invenSize(): MutableList<InventoryItemSize> {
       return unAssignList
   }
}