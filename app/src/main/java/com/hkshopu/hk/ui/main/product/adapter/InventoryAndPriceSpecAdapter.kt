package com.hkshopu.hk.ui.main.product.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventCheckInvenSpecEnableBtnOrNot
import com.hkshopu.hk.component.EventCheckShipmentEnableBtnOrNot

import com.hkshopu.hk.data.bean.ItemInventory
import com.hkshopu.hk.ui.main.store.adapter.ITHelperInterface
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.show
import org.jetbrains.anko.singleLine
import java.util.*


class InventoryAndPriceSpecAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() ,
    ITHelperInterface  {

    private val viewPool = RecycledViewPool()
    var mutableList_InvenSpec = mutableListOf<ItemInventory>()
    var specGroup_only:Boolean = false

    var second_layer_size:Int = 0

    //把水平rview元件拉進來
    inner class FirstLayerViewHolder(itemView:View)
        :RecyclerView.ViewHolder(itemView){
        val container_spec_first_layer_title = itemView.findViewById<LinearLayout>(R.id.container_spec_first_layer_title)
        val container_spec_second_layer_title = itemView.findViewById<LinearLayout>(R.id.container_spec_second_layer_title)

        val item_spec_title_name = itemView.findViewById<TextView>(R.id.title_spec)
        val item_spec_column_name = itemView.findViewById<TextView>(R.id.item_spec_column_name)
        var item_spec_name = itemView.findViewById<TextView>(R.id.value_spec)
//        val r_view_inventory_spec = itemView.findViewById<RecyclerView>(R.id.r_view_inventory_item_spec)

        //把layout檔的元件們拉進來，指派給當地變數
        val textView_value_name = itemView.findViewById<TextView>(R.id.second_spec_name)
        val editText_value_price = itemView.findViewById<EditText>(R.id.value_price)
        val editText_value_quantity = itemView.findViewById<EditText>(R.id.value_quantity)
        var textView_Hkdollars =  itemView.findViewById<TextView>(R.id.textView_HKdolors)

        //選高資料變數
        var value_name:String =""
        var value_price :String = ""
        var value_quantity : String = ""

        init {


            editText_value_price.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus ){
                    RxBus.getInstance().post(EventCheckInvenSpecEnableBtnOrNot(false))
                }
            }

            val textWatcher_price = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                @SuppressLint("ResourceAsColor")
                override fun afterTextChanged(s: Editable?) {

                    if (!s.toString().equals("")){
                        textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.black))
                        editText_value_price.setTextColor(itemView.context.resources.getColor(R.color.black))
                    }else{
                        textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                        editText_value_price.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                    }

                }
            }
            val textWatcher_quant = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {



                    if (!s.toString().equals("")){
                        editText_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.black))
                    }else{
                        editText_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                    }


                }
            }
            editText_value_price.addTextChangedListener(textWatcher_price)
            editText_value_quantity.addTextChangedListener(textWatcher_quant)

            editText_value_price.singleLine = true
            editText_value_price.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

                        if(editText_value_price.text.toString().length >= 2 && editText_value_price.text.toString().startsWith("0")){
                            editText_value_price.setText(editText_value_price.text.toString().replace("0", "", false))
                            editText_value_price.setSelection(editText_value_price.text.toString().length)
                        }

                        value_name = textView_value_name.text.toString()
                        value_price = editText_value_price.text.toString()
                        value_quantity = editText_value_quantity.text.toString()


                        onItemUpdate(value_price, value_quantity, adapterPosition)

                        RxBus.getInstance().post(EventCheckInvenSpecEnableBtnOrNot(true))

                        editText_value_price.hideKeyboard()
                        editText_value_price.clearFocus()

                        true
                    }
                    else -> false
                }
            }


            editText_value_quantity.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus ){
                    RxBus.getInstance().post(EventCheckInvenSpecEnableBtnOrNot(false))
                }
            }
            editText_value_quantity.singleLine = true
            editText_value_quantity.setOnEditorActionListener() { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {

                        if(editText_value_quantity.text.toString().length >= 2 && editText_value_quantity.text.toString().startsWith("0")){
                            editText_value_quantity.setText(editText_value_quantity.text.toString().replace("0", "", false))
                            editText_value_quantity.setSelection(editText_value_quantity.text.toString().length)
                        }

                        value_name = textView_value_name.text.toString()
                        value_price = editText_value_price.text.toString()
                        value_quantity = editText_value_quantity.text.toString()

                        onItemUpdate(value_price, value_quantity, adapterPosition)

                        RxBus.getInstance().post(EventCheckInvenSpecEnableBtnOrNot(true))

                        editText_value_quantity.hideKeyboard()
                        editText_value_quantity.clearFocus()

                        true
                    }
                    else -> false
                }
            }
        }

        fun bind(item: ItemInventory){



            if(specGroup_only==true){
                container_spec_first_layer_title.visibility = View.GONE
                item_spec_column_name.text = item.spec_desc_1
                item_spec_name.setText(item.spec_dec_1_items)
                textView_value_name.setText(item.spec_dec_1_items)

                editText_value_price.setText(item.price.toString())
                editText_value_quantity.setText(item.quantity.toString())

            }else{
                item_spec_title_name.isVisible = true
                item_spec_title_name.text = item.spec_desc_1
                item_spec_column_name.text = item.spec_desc_2
                item_spec_name.setText(item.spec_dec_1_items)

                textView_value_name.setText(item.spec_dec_2_items)
                editText_value_price.setText(item.price.toString())
                editText_value_quantity.setText(item.quantity.toString())
            }


            if (!item.price.equals("")){
                textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.black))
                editText_value_price.setTextColor(itemView.context.resources.getColor(R.color.black))
            }else{
                textView_Hkdollars.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
                editText_value_price.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
            }

            if (!item.quantity.equals("")){
                editText_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.black))
            }else{
                editText_value_quantity.setTextColor(itemView.context.resources.getColor(R.color.gray_txt))
            }


            RxBus.getInstance().post(EventCheckInvenSpecEnableBtnOrNot(true))

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
//                holder.r_view_inventory_spec.setRecycledViewPool(viewPool)

                if(!second_layer_size.equals(0)){

                    if(second_layer_size.equals(1)){
                        holder.container_spec_first_layer_title.visibility = View.VISIBLE
                        holder.container_spec_second_layer_title.visibility = View.VISIBLE
                    }else{
                        if(position.equals(0) || (position+1)%second_layer_size == 1){
                            holder.container_spec_first_layer_title.visibility = View.VISIBLE
                            holder.container_spec_second_layer_title.visibility = View.VISIBLE
                        }else{
                            holder.container_spec_first_layer_title.visibility = View.GONE
                            holder.container_spec_second_layer_title.visibility = View.GONE
                        }
                    }

                }else{
                    if(position.equals(0)){
                        holder.container_spec_first_layer_title.visibility = View.GONE
                        holder.container_spec_second_layer_title.visibility = View.VISIBLE
                    }else{
                        holder.container_spec_first_layer_title.visibility = View.GONE
                        holder.container_spec_second_layer_title.visibility = View.GONE
                    }
                }

            }


        }

    }


    fun onItemUpdate( price: String,  quant:String, position: Int) {

        mutableList_InvenSpec[position].price = price
        mutableList_InvenSpec[position].quantity = quant
//        notifyItemChanged(position)

    }
    //更新資料用
    fun updateList(list_spec: MutableList<ItemInventory>, specGroup_only: Boolean, second_layer_size:Int) {
        mutableList_InvenSpec = list_spec
        this.second_layer_size = second_layer_size
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

    fun getDatas_invenSpec(): MutableList<ItemInventory> {

        return mutableList_InvenSpec

    }


    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}