package com.hkshopu.hk.ui.main.adapter

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.Base.BaseActivity

import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.zilchzz.library.widgets.EasySwitcher
import org.jetbrains.anko.singleLine
import java.util.*


class ShippingFareAdapter(var activity: Activity): RecyclerView.Adapter<ShippingFareAdapter.mViewHolder>(), ITHelperInterface {

    var mutableList_shipMethod = mutableListOf<ItemShippingFare>()
    var empty_item_num = 0

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //資料變數宣告
        lateinit var value_shipping_name : String
        var value_shipping_fare : String = ""
        var value_shipping_isChecked : Boolean = false

        //把layout檔的元件們拉進來，指派給當地變數
        val editText_shipping_name = itemView.findViewById<EditText>(R.id.editText_value_shipping_name)
        val editText_shipping_fare = itemView.findViewById<EditText>(R.id.edtText_shipping_fare)
        val imgv_delFare = itemView.findViewById<ImageView>(R.id.imgView_deleteFare)
        val switch_view = itemView.findViewById<EasySwitcher>(R.id.mEasySwitcher)
        val textView_HKdolors = itemView.findViewById<TextView>(R.id.textView_HKdolors)


        init {

            //僅監控editText_shipping_name是否為空值而disable switchView
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {

                }
            }
            editText_shipping_name.addTextChangedListener(textWatcher)
            editText_shipping_fare.addTextChangedListener(textWatcher)

            //editText_shipping_name編輯鍵盤監控
            editText_shipping_name.singleLine = true
            editText_shipping_name.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

//                        storeStatus()

                        value_shipping_name = editText_shipping_name.text.toString()
                        value_shipping_fare = editText_shipping_fare.text.toString()
                        value_shipping_isChecked = switch_view.isOpened()

                        //檢查名稱是否重複
                        var check_duplicate = 0

                        for (i in 0..mutableList_shipMethod.size - 1) {
                            if (value_shipping_name == mutableList_shipMethod[i].ship_method_name) {
                                check_duplicate = check_duplicate + 1
                            } else {
                                check_duplicate = check_duplicate + 0
                            }
                        }
                        if (check_duplicate > 0) {
                            editText_shipping_name.setText("")
                            Toast.makeText(itemView.context, "貨運商不可重複", Toast.LENGTH_SHORT).show()

                        } else {
                            onItemUpdate(
                                value_shipping_name,
                                value_shipping_fare.toInt(),
                                value_shipping_isChecked,
                                adapterPosition
                            )
                            editText_shipping_name.clearFocus()
                        }

                        true
                    }
                    else -> false
                }
            }

            //editText_shipping_fare編輯鍵盤監聽
            editText_shipping_fare.singleLine = true
            editText_shipping_fare.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

//                        storeStatus()

                        value_shipping_name = editText_shipping_name.text.toString()
                        value_shipping_fare = editText_shipping_fare.text.toString()
                        value_shipping_isChecked = switch_view.isOpened()

                        if (value_shipping_fare == "") {
                            value_shipping_fare = "0"
                            onItemUpdate(
                                value_shipping_name,
                                value_shipping_fare.toInt(),
                                value_shipping_isChecked,
                                adapterPosition
                            )


                        } else {

                            onItemUpdate(
                                value_shipping_name,
                                value_shipping_fare.toInt(),
                                value_shipping_isChecked,
                                adapterPosition
                            )

                        }
                        editText_shipping_fare.clearFocus()

                        true
                    }
                    else -> false
                }
            }

            //item上的刪除按鈕設定
            imgv_delFare.setOnClickListener(this)

            //打開switch_view則增加空白item，關掉則刪除空白item
            switch_view.setOnStateChangedListener(object : EasySwitcher.SwitchStateChangedListener {
                override fun onStateChanged(isOpen: Boolean) {
                    if (isOpen) {

                        value_shipping_name = editText_shipping_name.text.toString()
                        value_shipping_fare = editText_shipping_fare.text.toString()

                        if (value_shipping_name == "") {
                            Toast.makeText(itemView.context, "請先填入自訂項目名稱", Toast.LENGTH_SHORT)
                                .show()
                            switch_view.closeSwitcher()
                        } else {

                            value_shipping_isChecked = true

                            onItemUpdate(
                                value_shipping_name,
                                value_shipping_fare.toInt(),
                                value_shipping_isChecked,
                                adapterPosition
                            )

                            addEmptyItem()

                        }

                    } else {

                        value_shipping_name = editText_shipping_name.text.toString()
                        value_shipping_fare = editText_shipping_fare.text.toString()

                        value_shipping_isChecked = false

                        onItemUpdate(
                            value_shipping_name,
                            value_shipping_fare.toInt(),
                            value_shipping_isChecked,
                            adapterPosition
                        )

                        delEmptyItem()

                    }
                }
            })





        }

        fun bind(item: ItemShippingFare){

            //綁定當地變數與dataModel中的每個值
            imgv_delFare.setImageResource(item.btn_delete)
            editText_shipping_name.setText(item.ship_method_name)
            editText_shipping_fare.setText(item.ship_method_fare.toString())

            if(item.is_checked==true) {
                switch_view.openSwitcher()
            }else{
                switch_view.closeSwitcher()
            }

            if(item.is_checked){

                textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.hkshop_color))
                editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.hkshop_color))

            }else{

                textView_HKdolors.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                editText_shipping_fare.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                editText_shipping_fare.setHintTextColor(itemView.context.resources.getColor(R.color.gray_txt))

            }

        }

        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.imgView_deleteFare -> onItemDissmiss(adapterPosition)
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
        holder.bind(mutableList_shipMethod[position] as ItemShippingFare)

    }

    fun initFareDatas() {

        mutableList_shipMethod.add(ItemShippingFare("郵局", 0, R.drawable.custom_unit_transparent))
        mutableList_shipMethod.add(ItemShippingFare("順豐速運", 0, R.drawable.custom_unit_transparent))
        mutableList_shipMethod.add(ItemShippingFare("", 0, R.drawable.custom_unit_transparent))

    }

    //新增空白項目
    fun addEmptyItem(){

        empty_item_num=0
        for(i in 0..mutableList_shipMethod.size-1){
            if (mutableList_shipMethod[i].ship_method_name == ""){
                empty_item_num += 1
            }else{
                empty_item_num += 0
            }
        }

        if(empty_item_num == 0 ){
            mutableList_shipMethod.add(
                ItemShippingFare(
                    "",
                    0,
                    R.drawable.custom_unit_transparent,
                    false
                )
            )

            notifyDataSetChanged()
        }


//        storeStatus()

    }

    //刪除空白項目
    fun delEmptyItem(){

        empty_item_num=0
        for(i in 0..mutableList_shipMethod.size-1){
            if (mutableList_shipMethod[i].ship_method_name == ""){
                empty_item_num += 1
            }else{
                empty_item_num += 0
            }
        }

        if(empty_item_num>1){
            mutableList_shipMethod.remove(
                ItemShippingFare(
                    "",
                    0,
                    R.drawable.custom_unit_transparent,
                    false
                )
            )
            notifyDataSetChanged()
        }


//        storeStatus()

    }

    //更新資料用
    fun updateList(list: MutableList<ItemShippingFare>){
        mutableList_shipMethod = list

//        storeStatus()
    }


    fun onItemUpdate(update_txt: String, update_fare: Int, is_checked: Boolean, position: Int) {

        mutableList_shipMethod[position] = ItemShippingFare(
            update_txt,
            update_fare,
            R.drawable.custom_unit_transparent,
            is_checked
        )

        Thread(Runnable {

            activity.runOnUiThread {

                notifyItemChanged(position)

            }

        }).start()


    }




    override fun onItemDissmiss(position: Int) {
        mutableList_shipMethod.removeAt(position)
        notifyItemRemoved(position)

//        storeStatus()
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_shipMethod, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    fun get_shipping_method_datas(): MutableList<ItemShippingFare> {
        return mutableList_shipMethod
    }



}

