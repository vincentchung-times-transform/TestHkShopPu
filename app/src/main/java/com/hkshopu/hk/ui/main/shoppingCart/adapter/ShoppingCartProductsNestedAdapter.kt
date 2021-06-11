package com.hkshopu.hk.ui.main.shoppingCart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.internal.Mutable
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ShoppingCartProductItemNestedLayer
import com.hkshopu.hk.ui.main.store.adapter.ITHelperInterface
import java.util.*


class ShoppingCartProductsNestedAdapter(var mutableList_shoppingCartProductItem: MutableList<ShoppingCartProductItemNestedLayer>, var edit_mode: Boolean): RecyclerView.Adapter<ShoppingCartProductsNestedAdapter.mViewHolder>(),
    ITHelperInterface {


    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //把layout檔的元件們拉進來，指派給當地變數
        val imgView_product_icon = itemView.findViewById<ImageView>(R.id.imgView_product_icon)
        val textView_product_name = itemView.findViewById<TextView>(R.id.textView_product_name)
        val textView_product_first_spec_item = itemView.findViewById<TextView>(R.id.textView_product_first_spec_name)
        var textView_product_first_spec_content =  itemView.findViewById<TextView>(R.id.textView_product_first_spec_item)
        val textView_product_second_spec_name = itemView.findViewById<TextView>(R.id.textView_product_second_spec_name)
        var textView_product_second_spec_item =  itemView.findViewById<TextView>(R.id.textView_product_second_spec_item)
        var shopping_cart_ic_math_subtract =  itemView.findViewById<ImageView>(R.id.shopping_cart_ic_math_subtract)
        val shopping_cart_tv_value_quantitiy = itemView.findViewById<TextView>(R.id.shopping_cart_tv_value_quantitiy)
        var shopping_cart_ic_math_add =  itemView.findViewById<ImageView>(R.id.shopping_cart_ic_math_add)
        val textView_price = itemView.findViewById<TextView>(R.id.textView_price)
        var container_logistics_spinner = itemView.findViewById<Spinner>(R.id.container_logistics_spinner)
        var shopping_cart_tv_value_quantitiy_confirmed = itemView.findViewById<TextView>(R.id.shopping_cart_tv_value_quantitiy_confirmed)
        var layout_quantity_abacus = itemView.findViewById<LinearLayout>(R.id.layout_quantity_abacus)
        var layout_logistics_selecting = itemView.findViewById<LinearLayout>(R.id.layout_logistics_selecting)
        var layout_logistics_selecting_confirmed = itemView.findViewById<LinearLayout>(R.id.layout_logistics_selecting_confirmed)
        var btn_delete_shopping_cart_prodcut = itemView.findViewById<ImageView>(R.id.btn_delete_shopping_cart_prodcut)



        init {

            val logistics_list: MutableList<String> = ArrayList<String>()

            for (i in 0..3) {
                logistics_list.add("Logistics_${i}")
            }

            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                logistics_list
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            container_logistics_spinner.setAdapter(adapter)
            container_logistics_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }


        fun bind(item: ShoppingCartProductItemNestedLayer){

            imgView_product_icon //尚未設定
            textView_product_name.setText(item.product_name.toString())
            textView_product_first_spec_item.setText(item.product_fist_spec_name.toString())
            textView_product_first_spec_content.setText(item.product_fist_spec_item.toString())
            textView_product_second_spec_name.setText(item.product_second_spec_name.toString())
            textView_product_second_spec_item.setText(item.product_second_spec_item.toString())
            textView_product_first_spec_item.setText(item.product_fist_spec_name.toString())
            shopping_cart_ic_math_subtract.setOnClickListener(this)
            shopping_cart_tv_value_quantitiy //尚未設定
            shopping_cart_ic_math_add.setOnClickListener(this)
            textView_price //尚未設定


            if(edit_mode){
                btn_delete_shopping_cart_prodcut.visibility = View.VISIBLE
                shopping_cart_tv_value_quantitiy_confirmed.visibility = View.GONE
                layout_quantity_abacus.visibility = View.VISIBLE
                layout_logistics_selecting_confirmed.visibility = View.GONE
                layout_logistics_selecting.visibility = View.VISIBLE

            }else{
                btn_delete_shopping_cart_prodcut.visibility = View.GONE
                shopping_cart_tv_value_quantitiy_confirmed.visibility = View.VISIBLE
                layout_quantity_abacus.visibility = View.GONE
                layout_logistics_selecting_confirmed.visibility = View.VISIBLE
                layout_logistics_selecting.visibility = View.GONE
            }

        }

        override fun onClick(v: View?) {

            when(v?.id) {
                R.id.shopping_cart_ic_math_add ->{
                    var quant =  shopping_cart_tv_value_quantitiy.text.toString().toInt()
                    quant += 1
                    shopping_cart_tv_value_quantitiy.setText(quant.toString())
                }
                R.id.shopping_cart_ic_math_subtract ->{
                    var quant =  shopping_cart_tv_value_quantitiy.text.toString().toInt()

                    if(quant>0){
                        quant -= 1
                    }
                    shopping_cart_tv_value_quantitiy.setText(quant.toString())
                }

            }

        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.shopping_cart_products_nested_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = mutableList_shoppingCartProductItem.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(mutableList_shoppingCartProductItem.get(position))

    }


    override fun onItemDissmiss(position: Int) {
        mutableList_shoppingCartProductItem.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_shoppingCartProductItem,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }


    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

}