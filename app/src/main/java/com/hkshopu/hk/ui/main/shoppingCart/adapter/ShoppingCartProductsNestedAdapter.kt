package com.HKSHOPU.hk.ui.main.shoppingCart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ShoppingCartProductItemNestedLayer
import com.HKSHOPU.hk.data.bean.ShoppingCartProductShipmentItem
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ITHelperInterface
import com.squareup.picasso.Picasso
import java.util.*


class ShoppingCartProductsNestedAdapter(
    var mutableList_shoppingCartProductItem: MutableList<ShoppingCartProductItemNestedLayer>,
    var shipmentList: MutableList<ShoppingCartProductShipmentItem>,
    var edit_mode: Boolean
    ): RecyclerView.Adapter<ShoppingCartProductsNestedAdapter.mViewHolder>(), ITHelperInterface {

    var hkd_dollarSign = ""

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var product_id: String = ""
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
        var unit_price = 0
        var total_price = 0
        var container_logistics_spinner = itemView.findViewById<Spinner>(R.id.container_logistics_spinner)
        var textView_logistics_fare_selecting = itemView.findViewById<TextView>(R.id.textView_logistics_fare_selecting)
        var shopping_cart_tv_value_quantitiy_confirmed = itemView.findViewById<TextView>(R.id.shopping_cart_tv_value_quantitiy_confirmed)
        var layout_quantity_abacus = itemView.findViewById<LinearLayout>(R.id.layout_quantity_abacus)
        var layout_logistics_selecting = itemView.findViewById<LinearLayout>(R.id.layout_logistics_selecting)
        var layout_logistics_selecting_confirmed = itemView.findViewById<LinearLayout>(R.id.layout_logistics_selecting_confirmed)
        var btn_delete_shopping_cart_prodcut = itemView.findViewById<ImageView>(R.id.btn_delete_shopping_cart_prodcut)

        init {
            hkd_dollarSign = itemView.context.getResources().getString(R.string.hkd_dollarSign)
        }

        fun bind(item: ShoppingCartProductItemNestedLayer){

            product_id = item.product_id
            Picasso.with(itemView.context).load(item.product_pic).into( imgView_product_icon)
            textView_product_name.setText(item.product_title.toString())
            textView_product_first_spec_item.setText(item.product_spec.get(0).spec_desc_1.toString())
            textView_product_first_spec_content.setText(item.product_spec.get(0).spec_desc_2.toString())
            textView_product_second_spec_name.setText(item.product_spec.get(0).spec_dec_1_items.toString())
            textView_product_second_spec_item.setText(item.product_spec.get(0).spec_dec_2_items.toString())
            unit_price = item.product_spec.get(0).spec_price
            total_price = unit_price*item.product_spec.get(0).shopping_cart_quantity
            textView_price.setText(total_price.toString())
            shopping_cart_ic_math_subtract.setOnClickListener(this)
            shopping_cart_tv_value_quantitiy.setText(item.product_spec.get(0).shopping_cart_quantity.toString())
            shopping_cart_ic_math_add.setOnClickListener(this)
            textView_logistics_fare_selecting.setText(shipmentList.get(0).shipment_price.toString())

            val logistics_list: MutableList<String> = ArrayList<String>()

            for (i in 0 until shipmentList.size) {
                logistics_list.add("${shipmentList.get(i).shipment_desc.toString()}${"\r"}${hkd_dollarSign}${shipmentList.get(i).shipment_price.toString()}")
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
                    textView_logistics_fare_selecting.setText(shipmentList.get(position).shipment_price.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

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

                    total_price+=unit_price
                    textView_price.setText(total_price.toString())
                }
                R.id.shopping_cart_ic_math_subtract ->{
                    var quant =  shopping_cart_tv_value_quantitiy.text.toString().toInt()

                    if(quant>0){
                        quant -= 1
                        total_price-=unit_price
                    }
                    shopping_cart_tv_value_quantitiy.setText(quant.toString())
                    textView_price.setText(total_price.toString())
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