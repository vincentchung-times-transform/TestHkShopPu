package com.HKSHOPU.hk.ui.main.productBuyer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*

import com.HKSHOPU.hk.data.bean.ItemSpecificationSeleting
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ITHelperInterface
import com.HKSHOPU.hk.utils.rxjava.RxBus
import java.util.*

class SpecificationFirstSelectingAdapter: RecyclerView.Adapter<SpecificationFirstSelectingAdapter.mViewHolder>(),
    ITHelperInterface {

    var mutableList_first_specifications = mutableListOf<ItemSpecificationSeleting>()
    var nextStepBtnStatus  = false

    var last_position = 999


    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        //把layout檔的元件們拉進來，指派給當地變數 )
        val txtView_specification = itemView.findViewById<TextView>(R.id.txtView_specification)
        val container_specification = itemView.findViewById<RelativeLayout>(R.id.container_specification_item)
        var ic_detailed_product_spec_selecting_out_of_stock = itemView.findViewById<ImageView>(R.id.ic_detailed_product_spec_selecting_out_of_stock)
        var transparent_space_top = itemView.findViewById<ImageView>(R.id.transparent_space_top)
        var transparent_space_right = itemView.findViewById<ImageView>(R.id.transparent_space_right)
        var spec_id: String = ""
        var price_range = ""
        var quant_range = ""
        var total_quantity = 0
        var seleted_status = false

        init {

            itemView.setOnClickListener(this)


        }

        fun bind(item: ItemSpecificationSeleting){
            //綁定當地變數與dataModel中的每個值
            spec_id = item.spec_id
            txtView_specification.setText(item.spec_name)
            price_range = item.price_range
            quant_range = item.quant_range
            total_quantity = item.total_quantity
            seleted_status = item.seleted_status

            if(total_quantity == 0){
                ic_detailed_product_spec_selecting_out_of_stock.visibility = View.VISIBLE
                transparent_space_top.visibility = View.VISIBLE
                transparent_space_right.visibility = View.VISIBLE
                itemView.isEnabled = false
            }else{
                ic_detailed_product_spec_selecting_out_of_stock.visibility = View.GONE
                transparent_space_top.visibility = View.GONE
                transparent_space_right.visibility = View.GONE
                itemView.isEnabled = true
            }


            //預設第一個項目為selected
            if(position == last_position) {

                txtView_specification.setTextColor(itemView.context.resources.getColor(R.color.white))
                txtView_specification.setBackgroundResource(R.drawable.customview_specification_selected)

                seleted_status = true

                upadteData(ItemSpecificationSeleting(spec_id, txtView_specification.text.toString(), price_range, quant_range, total_quantity, seleted_status), adapterPosition)


            }else{

                txtView_specification.setTextColor(itemView.context.resources.getColor(R.color.black))
                txtView_specification.setBackgroundResource(R.drawable.customview_specification_unselected)

                seleted_status = false

                upadteData(ItemSpecificationSeleting(spec_id, txtView_specification.text.toString(), price_range, quant_range, total_quantity, seleted_status), adapterPosition)

            }


        }

        override fun onClick(v: View?) {
            when(v) {
                itemView ->{

                    if(adapterPosition != last_position) {

                        txtView_specification.setTextColor(itemView.context.resources.getColor(R.color.white))
                        txtView_specification.setBackgroundResource(R.drawable.customview_specification_selected)

                        seleted_status = true
                        upadteData(ItemSpecificationSeleting(spec_id, txtView_specification.text.toString(), price_range, quant_range, total_quantity, seleted_status), adapterPosition)

                        notifyItemChanged(last_position)
                        last_position = adapterPosition


                        RxBus.getInstance().post(EventBuyerDetailedProductBtnStatusFirst(true, adapterPosition, spec_id, txtView_specification.text.toString(), price_range, quant_range, total_quantity))

                    }


                }
            }
        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.specification_selecting_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = mutableList_first_specifications.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(mutableList_first_specifications[position])

    }


    fun setDatas(list: MutableList<ItemSpecificationSeleting>){
        mutableList_first_specifications = list
        notifyDataSetChanged()
    }

    fun upadteData(item:ItemSpecificationSeleting, positon: Int) {
        mutableList_first_specifications[positon] = item
    }
    override fun onItemDissmiss(position: Int) {
        mutableList_first_specifications.removeAt(position)
        notifyItemRemoved(position)

//        RxBus.getInstance().post(EventInvenSpecDatasRebuild(true))
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mutableList_first_specifications,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }



    fun  nextStepEnableOrNot(): Boolean {

        var check_empty_num = 0

//        if(mutableList_specifications.size>0){
//            for(i in 0..mutableList_specifications.size-1){
//                var spec_name = mutableList_specifications.get(i).spec_name
//                if(spec_name.isNullOrEmpty()){
//                    check_empty_num += 1
//                }
//            }
//        }
//
//
//        if(mutableList_specifications.size > 0 && check_empty_num.equals(0)) {
//            nextStepBtnStatus = true
//        }else{
//            nextStepBtnStatus = false
//        }
        return nextStepBtnStatus

    }


    fun get_spec_list(): MutableList<ItemSpecificationSeleting> {
        return mutableList_first_specifications
    }


}