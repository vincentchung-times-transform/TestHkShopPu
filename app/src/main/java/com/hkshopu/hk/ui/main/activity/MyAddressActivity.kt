package com.hkshopu.hk.ui.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.MyAddresssItem
import com.hkshopu.hk.databinding.ActivityBuildacntBinding
import com.hkshopu.hk.databinding.ActivityEmailverifyBinding
import com.hkshopu.hk.databinding.ActivityMyAddressBinding
import com.hkshopu.hk.ui.main.adapter.MyAddressAdapter

class MyAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyAddressBinding
    val mMyAddressItemsList = arrayListOf<MyAddresssItem>()
    val mMyAddressAdapter = MyAddressAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //生成資料
        for(i in 0..2){
            mMyAddressItemsList.add(MyAddresssItem("name", "phone", "address", "status"))
        }

        mMyAddressAdapter.updateList(mMyAddressItemsList)     //傳入資料
        binding.rView.layoutManager = LinearLayoutManager(this)
        binding.rView.adapter = mMyAddressAdapter
    }
}