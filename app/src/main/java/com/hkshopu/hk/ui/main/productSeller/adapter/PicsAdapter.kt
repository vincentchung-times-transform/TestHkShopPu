package com.HKSHOPU.hk.ui.main.productSeller.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.data.bean.ItemPics
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.ITHelperInterface
import java.util.*

class PicsAdapter: RecyclerView.Adapter<PicsAdapter.mViewHolder>(), ITHelperInterface {

    var unAssignList = mutableListOf<ItemPics>()

    inner class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        //把layout檔的元件們拉進來，指派給當地變數
        val image = itemView.findViewById<ImageView>(R.id.imgView)
        val btn_deletePics = itemView.findViewById<ImageView>(R.id.btn_deletePics)
        val imgView_coverPic = itemView.findViewById<ImageView>(R.id.imgView_coverPic)

        init {
            btn_deletePics.setOnClickListener(this)
        }




        fun bind(item: ItemPics){
            //綁定當地變數與dataModel中的每個值
            image.setImageBitmap(item.bitmap)
            imgView_coverPic.setImageResource(item.cover_pic)

        }

        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.btn_deletePics ->{
                    onItemDissmiss(adapterPosition)
                    //如果unAssignList內有資料，則能刪除照片並讓第一張為封面
                    if (unAssignList.size > 0) {
                        unAssignList[0].cover_pic = R.mipmap.cover_pic
                        notifyItemChanged(0)
                    }else{
                        //若刪到沒有照片則不執行任何東西，防止unAssignList抓不到資料而閃退
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType: Int): mViewHolder {

        //載入項目模板
        val inflater = LayoutInflater.from(parent.context)
        val example = inflater.inflate(R.layout.pics_list_item, parent, false)
        return mViewHolder(example)

    }

    override fun getItemCount() = unAssignList.size

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        //呼叫上面的bind方法來綁定資料
        holder.bind(unAssignList[position])

    }

    //更新資料用
    fun updateList(list:MutableList<ItemPics>){
        unAssignList = list
    }

    override fun onItemDissmiss(position: Int) {
        unAssignList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(unAssignList,fromPosition,toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }



}