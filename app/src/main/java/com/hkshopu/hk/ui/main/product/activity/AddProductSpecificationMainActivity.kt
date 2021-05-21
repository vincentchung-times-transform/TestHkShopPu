package com.hkshopu.hk.ui.main.product.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.component.EventCheckFirstSpecEnableBtnOrNot
import com.hkshopu.hk.component.EventCheckSecondSpecEnableBtnOrNot
import com.hkshopu.hk.data.bean.ItemSpecification
import com.hkshopu.hk.data.bean.ProductInfoBean
import com.hkshopu.hk.databinding.ActivityAddProductDescriptionMainBinding
import com.hkshopu.hk.ui.main.product.adapter.SpecificationSizeAdapter
import com.hkshopu.hk.ui.main.product.adapter.SpecificationSpecAdapter
import com.hkshopu.hk.ui.main.product.fragment.SpecificationInfoDialogFragment
import com.hkshopu.hk.utils.rxjava.RxBus
import com.hkshopu.hk.widget.view.disable
import com.hkshopu.hk.widget.view.enable
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.singleLine

class AddProductSpecificationMainActivity : BaseActivity() {

    private lateinit var binding : ActivityAddProductDescriptionMainBinding

    val mAdapter_spec = SpecificationSpecAdapter()
    val mAdapter_size = SpecificationSizeAdapter()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var EDIT_MODE_SPEC = "0"
    var EDIT_MODE_SIZE = "0"

    //頁面資料變數宣告
    var MMKV_user_id: Int = 0
    var MMKV_shop_id: Int = 1
    var MMKV_product_id: Int = 1
    var value_editTextProductSpecFirst = ""
    var value_editTextProductSpecSecond = ""
    var value_datas_spec_size = 0
    var value_datas_size_size = 0

    lateinit var productInfoList: ProductInfoBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductDescriptionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MMKV_user_id = MMKV.mmkvWithID("http").getInt("UserId", 0)
        MMKV_shop_id = MMKV.mmkvWithID("http").getInt("ShopId", 0)
        MMKV_product_id = MMKV.mmkvWithID("http").getInt("ProductId", 0)


        initMMKV()
        initView()
        initEvent()
    }

    fun initMMKV() {

        //預設btnClearAllSpec和btnClearAllSpec隱藏
        binding.btnClearAllSpec.isVisible = false
        binding.btnClearAllSize.isVisible = false


        //Spec and Size item recyclerview setting
        binding.rViewSpecificationItemSpec.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rViewSpecificationItemSpec.adapter = mAdapter_spec

        binding.rviewSpecificationitemSize.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rviewSpecificationitemSize.adapter = mAdapter_size


        value_editTextProductSpecFirst =
            MMKV.mmkvWithID("addPro").getString("value_editTextProductSpecFirst", "").toString()
        value_editTextProductSpecSecond =
            MMKV.mmkvWithID("addPro").getString("value_editTextProductSpecSecond", "").toString()
        value_datas_spec_size =
            MMKV.mmkvWithID("addPro").getString("datas_spec_size", "0").toString().toInt()
        value_datas_size_size =
            MMKV.mmkvWithID("addPro").getString("datas_size_size", "0").toString().toInt()

        binding.editTextProductSpecFirst.setText(value_editTextProductSpecFirst)
        binding.editTextProductSpecSecond.setText(value_editTextProductSpecSecond)

        Thread(Runnable {

            for (i in 0..value_datas_spec_size - 1) {
                var item_name = MMKV.mmkvWithID("addPro").getString("datas_spec_item${i}", "")
                mutableList_spec.add(
                    ItemSpecification(
                        item_name.toString(),
                        R.drawable.custom_unit_transparent
                    )
                )
            }

            runOnUiThread {
                //更新或新增item
                mAdapter_spec.updateList(mutableList_spec)
            }

        }).start()


        Thread(Runnable {

            for (i in 0..value_datas_size_size - 1) {
                var item_name = MMKV.mmkvWithID("addPro").getString("datas_size_item${i}", "")
                mutableList_size.add(
                    ItemSpecification(
                        item_name.toString(),
                        R.drawable.custom_unit_transparent
                    )
                )
            }

            runOnUiThread {

                //更新或新增item
                mAdapter_size.updateList(mutableList_size)
            }

        }).start()

        try{
            Thread.sleep(800)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        if(value_datas_spec_size>0 && value_datas_size_size==0){
            checkButtonNextStep_single()
        }else if (value_datas_spec_size>0 && value_datas_size_size>0){
            checkButtonNextStep_double()
        }

    }

    fun initView() {

        initEditText()
        initClick()

    }


    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.iconSpecificationhelp.setOnClickListener {
            SpecificationInfoDialogFragment().show(supportFragmentManager, "MyCustomFragment")
        }

        binding.btnClearAllSpec.setOnClickListener {

            binding.editTextProductSpecFirst.setText("")
            clearAllSpecItem()

            checkButtonNextStep_single()

        }
        binding.btnClearAllSize.setOnClickListener {

            binding.editTextProductSpecSecond.setText("")
            clearAllSizeItem()

            checkButtonNextStep_double()

        }

        binding.btnNextStep.setOnClickListener {

            var datas_spec_item: MutableList<ItemSpecification> = mAdapter_spec.get_spec_list()
            var datas_size_item: MutableList<ItemSpecification> = mAdapter_size.get_size_list()
            var datas_spec_size: Int = mAdapter_spec.get_datas_spec_size()
            var datas_size_size: Int = mAdapter_size.get_datas_size_size()
            var datas_spec_title_first: String = binding.editTextProductSpecFirst.text.toString()
            var datas_spec_title_second: String = binding.editTextProductSpecSecond.text.toString()



            //MMKV input datas
            MMKV.mmkvWithID("addPro").putString("datas_spec_size", datas_spec_size.toString())
            MMKV.mmkvWithID("addPro").putString("datas_size_size", datas_size_size.toString())
            MMKV.mmkvWithID("addPro")
                .putString("value_editTextProductSpecFirst", datas_spec_title_first)
            MMKV.mmkvWithID("addPro")
                .putString("value_editTextProductSpecSecond", datas_spec_title_second)


            for (i in 0..datas_spec_size - 1) {

                MMKV.mmkvWithID("addPro")
                    .putString("datas_spec_item${i}", datas_spec_item.get(i).spec_name.toString())
            }

            for (i in 0..datas_size_size - 1) {

                MMKV.mmkvWithID("addPro")
                    .putString("datas_size_item${i}", datas_size_item.get(i).spec_name.toString())

            }


            val intent = Intent(this, AddInventoryAndPriceOldActivity::class.java)
            startActivity(intent)
            finish()

        }

        //first specification "spec" item add
        binding.btnAddspecificationSpec.setOnClickListener {

            if(binding.editTextProductSpecFirst.text.isNullOrEmpty()){
                Toast.makeText(this, "請先輸入第一層商品規格名稱", Toast.LENGTH_SHORT).show()
            }else{
                mutableList_spec = mAdapter_spec.get_spec_list()
                if (mutableList_spec.size < 3) {

                    if (EDIT_MODE_SPEC == "0") {

                        if (mAdapter_spec.get_check_empty() == true && mutableList_spec.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                        } else {

                            Thread(Runnable {

                                mutableList_spec = mAdapter_spec.get_spec_list()
                                mutableList_spec.add(
                                    ItemSpecification(
                                        "",
                                        R.drawable.custom_unit_transparent
                                    )
                                )

                                runOnUiThread {
                                    //更新或新增item
                                    mAdapter_spec.updateList(mutableList_spec)
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                                }

                            }).start()
                            MMKV.mmkvWithID("addPro").putBoolean("rebuild_datas", true)
                        }


                    } else {

                        if (mAdapter_spec.get_check_empty() == true && mutableList_spec.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                        } else {
                            Thread(Runnable {

                                mutableList_spec.add(
                                    ItemSpecification(
                                        "",
                                        R.mipmap.btn_delete_spec_item
                                    )
                                )

                                runOnUiThread {

                                    //更新或新增item
                                    mAdapter_spec.updateList(mutableList_spec)
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                                }

                            }).start()

                            MMKV.mmkvWithID("addPro").putBoolean("rebuild_datas", true)
                        }

                    }

                } else {

                    Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()

                }
            }

        }

        //second specification "spec" setting enable
        binding.btnEditspecificationEnableSpec.setOnClickListener {

            binding.btnNextStep.disable()
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

            binding.btnClearAllSpec.isVisible = true
            binding.btnEditspecificationEnableSize.disable()

            EDIT_MODE_SPEC = "1"

            binding.btnEditspecificationEnableSpec.isEnabled = false
            binding.btnEditspecificationEnableSpec.isVisible = false
            binding.btnEditspecificationDisableSpec.isEnabled = true
            binding.btnEditspecificationDisableSpec.isVisible = true

            Thread(Runnable {

                mutableList_spec = mAdapter_spec.get_spec_list()

                for (i in 0..mutableList_spec.size - 1) {
                    mutableList_spec[i] = ItemSpecification(
                        mutableList_spec[i].spec_name.toString(),
                        R.mipmap.btn_delete_spec_item
                    )
                }

                runOnUiThread {

                    //更新或新增item
                    mAdapter_spec.updateList(mutableList_spec)

                }

            }).start()
            MMKV.mmkvWithID("addPro").putBoolean("rebuild_datas", true)

        }

        //second specification "spec" item setting cancel
        binding.btnEditspecificationDisableSpec.setOnClickListener {

            binding.btnNextStep.enable()
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)

            binding.btnClearAllSpec.isVisible = false
            binding.btnEditspecificationEnableSize.enable()

            EDIT_MODE_SPEC = "0"

            binding.btnEditspecificationDisableSpec.isEnabled = false
            binding.btnEditspecificationDisableSpec.isVisible = false
            binding.btnEditspecificationEnableSpec.isEnabled = true
            binding.btnEditspecificationEnableSpec.isVisible = true

            Thread(Runnable {

                mutableList_spec = mAdapter_spec.get_spec_list()

                for (i in 0..mutableList_spec.size - 1) {
                    mutableList_spec[i] = ItemSpecification(
                        mutableList_spec[i].spec_name,
                        R.drawable.customborder_specification
                    )
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_spec.updateList(mutableList_spec)

                }

            }).start()

            if(mAdapter_spec.get_datas_spec_size()==0||mAdapter_spec.nextStepEnableOrNot().equals(false)){
                binding.btnNextStep.disable()
                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
            }

        }


        //second specification "size" item add
        binding.btnAddspecificationSize.setOnClickListener {



            if(binding.editTextProductSpecSecond.text.isNullOrEmpty()){
                Toast.makeText(this, "請先完成輸入第一層規格內容", Toast.LENGTH_SHORT).show()
            }else{
                mutableList_size = mAdapter_size.get_size_list()
                if (mutableList_size.size < 3) {

                    if (EDIT_MODE_SIZE == "0") {

                        if (mAdapter_size.get_check_empty() == true && mutableList_size.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                        } else {
                            Thread(Runnable {

                                mutableList_size = mAdapter_size.get_size_list()
                                mutableList_size.add(
                                    ItemSpecification(
                                        "",
                                        R.drawable.custom_unit_transparent
                                    )
                                )

                                runOnUiThread {

                                    //更新或新增item
                                    mAdapter_size.updateList(mutableList_size)
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
                                }

                            }).start()
                            MMKV.mmkvWithID("addPro").putBoolean("rebuild_datas", true)

                        }


                    } else {


                        if (mAdapter_size.get_check_empty() == true && mutableList_size.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                        } else {

                            Thread(Runnable {

                                mutableList_size = mAdapter_size.get_size_list()
                                mutableList_size.add(
                                    ItemSpecification(
                                        "",
                                        R.mipmap.btn_delete_spec_item
                                    )
                                )

                                runOnUiThread {

                                    //更新或新增item
                                    mAdapter_size.updateList(mutableList_size)
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                                }


                            }).start()
                            MMKV.mmkvWithID("addPro").putBoolean("rebuild_datas", true)

                        }


                    }

                } else {
                    Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //second specification "size" item settings cancel
        binding.btnEditspecificationDisableSize.setOnClickListener {


            binding.btnNextStep.enable()
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)

            binding.btnClearAllSize.isVisible = false
            binding.btnEditspecificationEnableSpec.enable()

            EDIT_MODE_SIZE = "0"

            binding.btnEditspecificationDisableSize.isEnabled = false
            binding.btnEditspecificationDisableSize.isVisible = false
            binding.btnEditspecificationEnableSize.isEnabled = true
            binding.btnEditspecificationEnableSize.isVisible = true

            Thread(Runnable {

                mutableList_size = mAdapter_size.get_size_list()

                for (i in 0..mutableList_size.size - 1) {
                    mutableList_size[i] = ItemSpecification(
                        mutableList_size[i].spec_name,
                        R.drawable.customborder_specification
                    )
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_size.updateList(mutableList_size)
                }

            }).start()


            if(mAdapter_size.get_datas_size_size()==0||mAdapter_size.nextStepEnableOrNot().equals(false)){
                binding.btnNextStep.disable()
                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
            }



        }

        //second specification "size" item settings enable
        binding.btnEditspecificationEnableSize.setOnClickListener {


            binding.btnNextStep.disable()
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

            binding.btnClearAllSize.isVisible = true
            binding.btnEditspecificationEnableSpec.disable()

            EDIT_MODE_SIZE = "1"

            binding.btnEditspecificationEnableSize.isEnabled = false
            binding.btnEditspecificationEnableSize.isVisible = false
            binding.btnEditspecificationDisableSize.isEnabled = true
            binding.btnEditspecificationDisableSize.isVisible = true

            Thread(Runnable {

                mutableList_size = mAdapter_size.get_size_list()

                for (i in 0..mutableList_size.size - 1) {
                    mutableList_size[i] = ItemSpecification(
                        mutableList_size[i].spec_name,
                        R.mipmap.btn_delete_spec_item
                    )
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_size.updateList(mutableList_size)

                }

            }).start()

            MMKV.mmkvWithID("addPro").putBoolean("rebuild_datas", true)

        }
    }


    fun initEditText() {

        val first_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (s.toString() == "") {

                    MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecFirst", "")

                } else {

                    MMKV.mmkvWithID("addPro")
                        .putString("value_editTextProductSpecFirst", s.toString())


                }

            }
        }
        val second_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (s.toString() == "") {

                    MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecSecondt", "")


                } else {

                    MMKV.mmkvWithID("addPro")
                        .putString("value_editTextProductSpecSecond", s.toString())

                }

            }
        }
        binding.editTextProductSpecFirst.addTextChangedListener(first_textWatcher)
        binding.editTextProductSpecSecond.addTextChangedListener(second_textWatcher)

        //editTextProductSpecFirst編輯模式
        binding.editTextProductSpecFirst.singleLine = true
        binding.editTextProductSpecFirst.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    if( binding.editTextProductSpecFirst.text.toString().isNullOrEmpty()){
                        binding.btnNextStep.isEnabled = false
                        binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
                    }


                    if(binding.editTextProductSpecSecond.text.toString() == binding.editTextProductSpecFirst.text.toString()){
                        Toast.makeText(this, "規格名稱不可重複", Toast.LENGTH_SHORT).show()
                        binding.editTextProductSpecFirst.setText("")
                    }else {
                        MMKV.mmkvWithID("addPro").putString(
                            "value_editTextProductSpecFirst",
                            binding.editTextProductSpecFirst.text.toString()
                        )
                    }

                    binding.editTextProductSpecFirst.hideKeyboard()
                    binding.editTextProductSpecFirst.clearFocus()


                    true
                }
                else -> false
            }
        }
        //editTextProductSpecFirst編輯模式
        binding.editTextProductSpecSecond.singleLine = true
        binding.editTextProductSpecSecond.setOnEditorActionListener() { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {

                    if(binding.editTextProductSpecSecond.text.toString().isNullOrEmpty()||mAdapter_size.get_datas_size_size()==0){
                        binding.btnNextStep.isEnabled = false
                        binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
                    }else{
                        binding.btnNextStep.isEnabled = true
                        binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
                    }


                    if(binding.editTextProductSpecSecond.text.toString() == binding.editTextProductSpecFirst.text.toString()){
                        Toast.makeText(this, "規格名稱不可重複", Toast.LENGTH_SHORT).show()
                        binding.editTextProductSpecSecond.setText("")
                    }else{
                        if(binding.editTextProductSpecFirst.text.isNotEmpty()&&mAdapter_spec.nextStepEnableOrNot()){
                            if (binding.editTextProductSpecFirst.text.toString() == "") {

                                MMKV.mmkvWithID("addPro").putString("value_editTextProductSpecSecondt", "")


                            } else {

                                MMKV.mmkvWithID("addPro")
                                    .putString("value_editTextProductSpecSecond", binding.editTextProductSpecFirst.text.toString())

                            }
                        }else{
                            binding.editTextProductSpecSecond.setText("")
                            Toast.makeText(this, "請先完成輸入第一層規格", Toast.LENGTH_SHORT).show()
                        }

                    }



                    binding.editTextProductSpecSecond.hideKeyboard()
                    binding.editTextProductSpecSecond.clearFocus()

                    true
                }
                else -> false
            }
        }
    }

    fun clearAllSpecItem() {
        mutableList_spec.clear()
        mAdapter_spec.notifyDataSetChanged()
    }

    fun clearAllSizeItem() {
        mutableList_size.clear()
        mAdapter_size.notifyDataSetChanged()
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onBackPressed() {
        val intent = Intent(this, AddNewProductActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("CheckResult")
    fun initEvent() {
        var boolean_first_spec: Boolean
        var boolean_second_spec: Boolean

        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventCheckFirstSpecEnableBtnOrNot -> {
                        boolean_first_spec = it.boolean

                        when(boolean_first_spec){
                            true->{
                                checkButtonNextStep_single()

                            }
                            false->{
                                binding.btnNextStep.isEnabled = false
                                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                            }
                        }


                    }
                    is EventCheckSecondSpecEnableBtnOrNot -> {
                        boolean_second_spec=it.boolean

                        when(boolean_second_spec){
                            true->{
                                checkButtonNextStep_double()
                            }
                            false->{
                                binding.btnNextStep.isEnabled = false
                                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
                            }
                        }

                    }
                }
            }, {
                it.printStackTrace()
            })
    }

    fun checkButtonNextStep_single(){
        //預設btnNextStep disable or enable
        if (( binding.editTextProductSpecFirst.text.isNotEmpty())) {
            if(binding.editTextProductSpecSecond.text.isNotEmpty() && mAdapter_size.get_datas_size_size()>0){
                binding.btnNextStep.isEnabled = true
                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
            }else if(binding.editTextProductSpecSecond.text.isEmpty() && mAdapter_size.get_datas_size_size()==0){
                binding.btnNextStep.isEnabled = true
                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
            }else{
                binding.btnNextStep.isEnabled = false
                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
            }

        } else {
            binding.btnNextStep.isEnabled = false
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
        }
    }

    fun checkButtonNextStep_double(){
        //預設btnNextStep disable or enable
        if ( binding.editTextProductSpecFirst.text.isNotEmpty() && mAdapter_spec.get_datas_spec_size()>0 && binding.editTextProductSpecSecond.text.isNotEmpty()) {
            binding.btnNextStep.isEnabled = true
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
        } else {
            binding.btnNextStep.isEnabled = false
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
        }
    }
}