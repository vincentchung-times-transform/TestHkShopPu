package com.HKSHOPU.hk.ui.main.shopProfile.activity


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.CommonVariable
import com.HKSHOPU.hk.component.EventChangeShopCategory
import com.HKSHOPU.hk.component.EventShopCatSelected
import com.HKSHOPU.hk.data.bean.ShopCategoryBean
import com.HKSHOPU.hk.databinding.ActivityShopcategoryBinding
import com.HKSHOPU.hk.ui.main.shopProfile.adapter.CategoryMultiAdapter
import com.HKSHOPU.hk.ui.user.vm.ShopVModel
import com.HKSHOPU.hk.utils.rxjava.RxBus


class ShopCategoryForAddShopActivity : BaseActivity() {
    private lateinit var binding: ActivityShopcategoryBinding

    private val VM = ShopVModel()
    private val adapter = CategoryMultiAdapter()
    var toShopFunction: Boolean = false
    val choseListFiltered= ArrayList<ShopCategoryBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopcategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toShopFunction = intent.getBundleExtra("bundle")!!.getBoolean("toShopFunction")

        binding.progressBarShopCategory.visibility = View.GONE
        binding.imgViewLoadingBackgroundShopCategory.visibility = View.GONE


        initView()
        initRecyclerView()
        initVM()
        initEditText()
        initClick()

    }

    private fun initVM() {

    }

    private fun initView() {

        if (binding.tvSelected.text == "未選擇分類") {
            binding.tvSelected.isClickable = false
        }

    }

    private fun initRecyclerView() {

        //---------------Default RecylcerView---------------
        for(i in 0 until CommonVariable.shopCategoryListForAdd.size ){
            if( CommonVariable.shopCategoryListForAdd.get(i).isSelect.equals(true)){
                choseListFiltered.add(CommonVariable.shopCategoryListForAdd.get(i))
            }
        }

        if (choseListFiltered.isEmpty()) {
            binding.tvSelected.text = "未選擇分類"
            binding.tvSelected.setTextColor(getColor(R.color.turquoise))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_turquise)
        }else{
            val items = choseListFiltered.size
            binding.tvSelected.text = "已選擇"+items+"項分類"
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        }

        if(choseListFiltered.size > 3){
            binding.tvSelected.isClickable = false
            runOnUiThread {
                Toast.makeText(this@ShopCategoryForAddShopActivity, "最多只能選擇3項分類", Toast.LENGTH_SHORT).show()
            }
        }else{
            binding.tvSelected.isClickable = true
        }

        if (choseListFiltered.size == 1) {
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        } else if (choseListFiltered.size == 2) {
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        } else if (choseListFiltered.size == 3) {
            binding.tvSelected.setTextColor(getColor(R.color.white))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
        }else{
            binding.tvSelected.setTextColor(getColor(R.color.turquoise))
            binding.tvSelected.setBackgroundResource(R.drawable.customborder_turquise)
        }
        //---------------Default RecylcerView---------------

        val layoutManager = GridLayoutManager(this, 3)
        binding.recyclerview.layoutManager = layoutManager

        adapter.setData(CommonVariable.shopCategoryListForAdd)
        binding.recyclerview.adapter = adapter

        adapter.itemClick = {

//            Log.d("ShopCategoryActivity", "Item ID：" + id_cat)
//            Log.d("ShopCategoryActivity", "Item selected：" + it.isSelect)


            if (it.isSelect == true) {
                choseListFiltered.add(it)
            }
            if (it.isSelect == false) {
                choseListFiltered.remove(it)
            }

            if (choseListFiltered.isEmpty()) {
                binding.tvSelected.text = "未選擇分類"
                binding.tvSelected.setTextColor(getColor(R.color.turquoise))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_turquise)
            }else{
                val items = choseListFiltered.size
                binding.tvSelected.text = "已選擇"+items+"項分類"
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            }

            if(choseListFiltered.size > 3){
                binding.tvSelected.isClickable = false
                runOnUiThread {
                    Toast.makeText(this@ShopCategoryForAddShopActivity, "最多只能選擇3項分類", Toast.LENGTH_SHORT).show()
                }
            }else{
                binding.tvSelected.isClickable = true
            }

            if (choseListFiltered.size == 1) {
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else if (choseListFiltered.size == 2) {
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            } else if (choseListFiltered.size == 3) {
                binding.tvSelected.setTextColor(getColor(R.color.white))
                binding.tvSelected.setBackgroundResource(R.drawable.customborder_onboard_turquise_40p)
            }

        }
    }

    private fun initClick() {
        binding.titleBackShopcategory.setOnClickListener {
            finish()
        }
        binding.tvSelected.setOnClickListener {

            if (toShopFunction) {
                RxBus.getInstance().post(EventChangeShopCategory(choseListFiltered))
            } else {
                RxBus.getInstance().post(EventShopCatSelected(choseListFiltered))
            }

            CommonVariable.shopCategoryListForAdd = adapter.getDatas()

            Log.d("shopCategoryListForAdd", CommonVariable.shopCategoryListForAdd.toString())

            finish()
        }
    }


    private fun initEditText() {
//        binding.etShopname.addTextChangedListener(this)
//        password1.addTextChangedListener(this)
    }

    override fun onBackPressed() {

        finish()
    }

}