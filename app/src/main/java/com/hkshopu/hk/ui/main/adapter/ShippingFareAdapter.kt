package com.hkshopu.hk.ui.main.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.widget.view.enable
import org.jetbrains.anko.singleLine
import vn.luongvo.widget.iosswitchview.SwitchView
import vn.luongvo.widget.iosswitchview.SwitchView.OnCheckedChangeListener
import java.util.*


class ShippingFareAdapter: RecyclerView.Adapter<ShippingFareAdapter.mViewHolder>(), ITHelperInterface {

    var mutableList_shipMethod = mutableListOf<ItemShippingFare>()
    lateinit var customPaymentName: String

    var btn_storeStatus = false


    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //把layout檔的元件們拉進來，指派給當地變數
        val editText_shipping_name = itemView.findViewById<EditText>(R.id.value_shipping_name)
        val imgv_delFare = itemView.findViewById<ImageView>(R.id.imgView_deleteFare)
        val switch_view = itemView.findViewById<SwitchView>(R.id.ios_switch)
        val switch_view_disable = itemView.findViewById<ImageView>(R.id.diable_switch)

        init {

            //僅監控editText_shipping_name是否為空值而disable switchView
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {

                    if (s.toString().isEmpty() || editText_shipping_name.hasFocus()){
                        switch_view_disable.isVisible = true
                        switch_view.isVisible = false
                    }else{
                        switch_view_disable.isVisible = false
                        switch_view.isVisible = true

                    }

                }
            }
            editText_shipping_name.addTextChangedListener(textWatcher)

            //editText_shipping_name編輯模式
            editText_shipping_name.singleLine = true
            editText_shipping_name.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        customPaymentName = editText_shipping_name.text.toString()
                        onItemUpdate(customPaymentName, adapterPosition)

                        if (editText_shipping_name.text.toString().isEmpty()){
                            switch_view_disable.isVisible = true
                            switch_view.isVisible = false
                        }else{
                            switch_view_disable.isVisible = false
                            switch_view.isVisible = true

                            storeStatus()
                        }

                        editText_shipping_name.clearFocus()

                        true
                    }
                    else -> false
                }
            }

            //打開switch_view則增加空白item，關掉則刪除空白item
            switch_view.setOnCheckedChangeListener(OnCheckedChangeListener { switchView, isChecked ->
                if(isChecked){
                    addEmptyItem()
                }else{
                    delEmptyItem()
                }
            })

            //item上的刪除按鈕設定
            imgv_delFare.setOnClickListener(this)
            switch_view_disable.setOnClickListener(this)
        }

        fun bind(item: ItemShippingFare){
            //綁定當地變數與dataModel中的每個值
            editText_shipping_name.setText(item.ship_method_name)
            imgv_delFare.setImageResource(item.btn_delete)
        }

        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.imgView_deleteFare -> onItemDissmiss(adapterPosition)
                R.id.diable_switch -> Toast.makeText(itemView.context, "請先填入自訂項目名稱", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.shipping_fare_list_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = mutableList_shipMethod.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(mutableList_shipMethod[position])

    }

    //新增空白項目
    fun addEmptyItem(){

        mutableList_shipMethod.add(ItemShippingFare("", R.drawable.custom_unit_transparent))
        notifyDataSetChanged()

        storeStatus()

    }

    //刪除空白項目
    fun delEmptyItem(){

        mutableList_shipMethod.remove(ItemShippingFare("", R.drawable.custom_unit_transparent))
        notifyDataSetChanged()

        storeStatus()

    }

    //更新資料用
    fun updateList(list: MutableList<ItemShippingFare>){
        mutableList_shipMethod = list

        storeStatus()
    }



    fun onItemUpdate(update_txt: String, position: Int) {
        mutableList_shipMethod[position] = ItemShippingFare(
            update_txt,
            R.drawable.custom_unit_transparent
        )
        notifyItemChanged(position)

        storeStatus()
    }


    override fun onItemDissmiss(position: Int) {
        mutableList_shipMethod.removeAt(position)
        notifyItemRemoved(position)

        storeStatus()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_shipMethod, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun storeStatus(){
        if(mutableList_shipMethod.size > 0){
            btn_storeStatus = true
        }else{
            btn_storeStatus = false
        }
    }

    fun getStoreStatus(): Boolean {
        return btn_storeStatus
    }
}

