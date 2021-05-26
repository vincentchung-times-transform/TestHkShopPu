package com.hkshopu.hk.ui.main.store.adapter


import android.app.Activity
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemData
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ShopLogisticBean
import com.hkshopu.hk.net.Web
import com.hkshopu.hk.utils.extension.inflate
import com.tencent.mmkv.MMKV
import com.zilchzz.library.widgets.EasySwitcher
import org.jetbrains.anko.find
import org.jetbrains.anko.singleLine
import java.util.*
import kotlin.concurrent.schedule


class LogisticsListAdapter :
    RecyclerView.Adapter<LogisticsListAdapter.LogisticsListLinearHolder>() {

    private var selected = -1
    private var cancel_inner: Boolean = false
    var empty_item_num = 0
    var shop_id: Int = 0
    var value_shipping_name: String = ""
    var value_shipping_isChecked: String = "off"
    private var mData: ArrayList<ShopLogisticBean> = ArrayList()
    var onClick: OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    fun setData(list: ArrayList<ShopLogisticBean>) {
        list ?: return
        this.mData = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    //更新資料用
    fun updateData(cancel: Boolean) {
        cancel_inner = cancel
        this.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        this.mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogisticsListLinearHolder {
        val v = parent.context.inflate(R.layout.item_logistic, parent, false)

        return LogisticsListLinearHolder(v)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    var cancelClick: ((id: String) -> Unit)? = null
    override fun onBindViewHolder(holder: LogisticsListLinearHolder, position: Int) {
        val viewHolder: LogisticsListLinearHolder = holder
        val item = mData.get(position)

        viewHolder.name.setText(item.shipment_desc)

        if (item.onoff.equals("on")) {
            viewHolder.OnOff.openSwitcher()
        }else{
            viewHolder.OnOff.closeSwitcher()
        }

        var value_shipping_name_check = viewHolder.name.text.toString()
        if (cancel_inner) {
            if (value_shipping_name_check.isNotEmpty()) {
                viewHolder.cancel.visibility = View.VISIBLE
            }else{
                viewHolder.cancel.visibility = View.GONE
            }

        } else {

            viewHolder.cancel.visibility = View.GONE

        }
        viewHolder.cancel.setOnClickListener {
            removeItem(viewHolder.adapterPosition)
        }
//        value_shipping_name = viewHolder.name.text.toString()

        //editText_shipping_name編輯鍵盤監控
        viewHolder.name.singleLine = true
        viewHolder.name.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    value_shipping_name = viewHolder.name.text.toString()

                    if(viewHolder.OnOff.isOpened()){
                        value_shipping_isChecked = "on"
                    }else{
                        value_shipping_isChecked = "off"
                    }

                    //檢查名稱是否重複
                    var check_duplicate = 0

                    for (i in 0..mData.size - 1) {
                        if (value_shipping_name == mData[i].shipment_desc) {
                            check_duplicate = check_duplicate + 1
                        } else {
                            check_duplicate = check_duplicate + 0
                        }
                    }

                    if (check_duplicate > 0) {
                        viewHolder.name.setText("")
                        Toast.makeText(viewHolder.name.context, "貨運商不可重複", Toast.LENGTH_SHORT).show()

                    } else {
                        onItemUpdate(
                            value_shipping_name,
                            value_shipping_isChecked,
                            holder.absoluteAdapterPosition
                        )

                        viewHolder.name.clearFocus()
                    }

                    true
                }
                else -> false
            }
        }

        viewHolder.OnOff.setOnStateChangedListener(object :
            EasySwitcher.SwitchStateChangedListener {
            override fun onStateChanged(isOpen: Boolean) {

                Log.d("isOpen",mData.toString())


                if (isOpen) {

                    value_shipping_name = viewHolder.name.text.toString()

                    if (value_shipping_name.isEmpty()) {
                        Toast.makeText(viewHolder.OnOff.context, "請先填入自訂項目名稱", Toast.LENGTH_SHORT)
                            .show()
                        viewHolder.OnOff.closeSwitcher()
                    } else {

                        value_shipping_isChecked = "on"

                        onItemUpdate(
                            value_shipping_name,
                            value_shipping_isChecked,
                            holder.adapterPosition)
                        Handler(Looper.getMainLooper()).post(Runnable {
                            addEmptyItem()
                        })

                    }

                } else {

                    value_shipping_name = viewHolder.name.text.toString()
                    value_shipping_isChecked = "off"

                    onItemUpdate(
                        value_shipping_name,
                        value_shipping_isChecked,
                        holder.adapterPosition
                    )

                    Log.d("dfjjifjd", mData[position].onoff.toString())
                    Handler(Looper.getMainLooper()).post(Runnable {
                        delEmptyItem(position)
                    })

                }
            }
        })

    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, bean: ItemData)
    }

    inner class LogisticsListLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cancel = itemView.find<ImageView>(R.id.iv_cancel)
        val name = itemView.find<EditText>(R.id.tv_logistic)
        val OnOff = itemView.find<EasySwitcher>(R.id.switchview)

        init {

            Handler(Looper.getMainLooper()).post(Runnable {
                shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
                addEmptyItem()
            })






        }

    }

    //新增空白項目
    fun addEmptyItem() {

        empty_item_num = 0
        if (mData.size > 0) {
            for (i in 0..mData.size - 1) {
                if (mData.get(i).getShipmentDesc()!!.isEmpty()) {
                    empty_item_num += 1
                } else {
                    empty_item_num += 0
                }
            }
            if (empty_item_num == 0) {
                val shopLogisticBean = ShopLogisticBean()
                shopLogisticBean.id = 0
                shopLogisticBean.shipment_desc = ""
                shopLogisticBean.shop_id = shop_id.toInt()
                shopLogisticBean.onoff = "off"
                mData.add(shopLogisticBean)

                try {
                    Thread.sleep(300)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                notifyDataSetChanged()
            }
        } else {
            val shopLogisticBean = ShopLogisticBean()
            shopLogisticBean.id = 0
            shopLogisticBean.shipment_desc = ""
            shopLogisticBean.shop_id = shop_id.toInt()
            shopLogisticBean.onoff = "off"
            mData.add(shopLogisticBean)
            try {
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            notifyDataSetChanged()
        }
    }

    //刪除空白項目
    fun delEmptyItem(position: Int) {

        empty_item_num = 0
        for (i in 0..mData.size - 1) {
            if (mData.get(i).getShipmentDesc()== "") {
                empty_item_num += 1
            } else {
                empty_item_num += 0
            }
        }

        if (empty_item_num > 1) {

            val shopLogisticBean = ShopLogisticBean()
            shopLogisticBean.id = 0
            shopLogisticBean.shipment_desc = ""
            shopLogisticBean.shop_id = shop_id.toInt()
            shopLogisticBean.onoff = "off"

            mData.remove(
                shopLogisticBean
            )
//            mData.removeAt(position)


            try {
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            notifyDataSetChanged()
        }


//        storeStatus()

    }

    fun onItemUpdate(update_txt: String, is_checked: String, position: Int) {
        Log.d("LogisticsListAdapter", "Content ＝ " + update_txt)
        mData[position].setID(0)
        mData[position].setShipmentDesc(update_txt)
        mData[position].setShopID(shop_id.toInt())
        mData[position].setOnOff(is_checked)

        Handler(Looper.getMainLooper()).post(Runnable {
            notifyItemChanged(position)
        })

    }

    fun get_shipping_method_datas(): ArrayList<ShopLogisticBean> {
        return mData
    }
}