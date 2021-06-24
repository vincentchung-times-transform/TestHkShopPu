package com.HKSHOPU.hk.ui.main.productSeller.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.component.*
import com.HKSHOPU.hk.data.bean.*
import com.HKSHOPU.hk.databinding.ActivityAddProductDescriptionMainBinding
import com.HKSHOPU.hk.ui.main.productSeller.adapter.SpecificationSecondSpecsAdapter
import com.HKSHOPU.hk.ui.main.productSeller.adapter.SpecificationFirstSpecsAdapter
import com.HKSHOPU.hk.ui.main.productSeller.fragment.SpecificationInfoDialogFragment
import com.HKSHOPU.hk.utils.rxjava.RxBus
import com.HKSHOPU.hk.widget.view.disable
import com.HKSHOPU.hk.widget.view.enable
import com.tencent.mmkv.MMKV
import org.jetbrains.anko.singleLine

class EditProductSpecificationMainActivity : BaseActivity() {

    private lateinit var binding: ActivityAddProductDescriptionMainBinding

    val mAdapter_spec = SpecificationFirstSpecsAdapter()
    val mAdapter_size = SpecificationSecondSpecsAdapter()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var EDIT_MODE_SPEC = "0"
    var EDIT_MODE_SIZE = "0"

    //頁面資料變數宣告
    var MMKV_user_id: String = ""
    var MMKV_shop_id: String = ""
    var MMKV_product_id: String = ""
    var value_editTextProductSpecFirst = ""
    var value_editTextProductSpecSecond = ""
    var value_datas_spec_size = 0
    var value_datas_size_size = 0

    lateinit var productInfoList: ProductInfoBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductDescriptionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBarSpecItemsEditation.visibility =View.VISIBLE
        binding.imgViewLoadingBackgroundSpecItemsEditation.visibility =View.VISIBLE

        MMKV_user_id = MMKV.mmkvWithID("http").getString("UserId", "").toString()
        MMKV_shop_id = MMKV.mmkvWithID("http").getString("ShopId", "").toString()
        MMKV_product_id = MMKV.mmkvWithID("http").getString("ProductId", "").toString()

        MMKV.mmkvWithID("editPro_temp").putBoolean("rebuild_datas", false)

        Thread(Runnable {
            initMMKV()
        }).start()

        initView()
        initEvent()



        binding.progressBarSpecItemsEditation.visibility =View.GONE
        binding.imgViewLoadingBackgroundSpecItemsEditation.visibility =View.GONE
    }

    fun initMMKV() {

        var get_temp = MMKV.mmkvWithID("editPro_temp").getBoolean("get_temp", false)

        binding.btnClearAllSpec.isVisible = false
        binding.btnClearAllSize.isVisible = false


        //Spec and Size item recyclerview setting
        binding.rViewSpecificationItemSpec.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rViewSpecificationItemSpec.adapter = mAdapter_spec

        binding.rviewSpecificationitemSize.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rviewSpecificationitemSize.adapter = mAdapter_size



        if(get_temp){

            value_editTextProductSpecFirst =
                MMKV.mmkvWithID("editPro_temp").getString("value_editTextProductSpecFirst", "").toString()
            value_editTextProductSpecSecond =
                MMKV.mmkvWithID("editPro_temp").getString("value_editTextProductSpecSecond", "").toString()
            value_datas_spec_size =
                MMKV.mmkvWithID("editPro_temp").getString("datas_spec_size", "0").toString().toInt()
            value_datas_size_size =
                MMKV.mmkvWithID("editPro_temp").getString("datas_size_size", "0").toString().toInt()

            binding.editTextProductSpecFirst.setText(value_editTextProductSpecFirst)
            binding.editTextProductSpecSecond.setText(value_editTextProductSpecSecond)

            Thread(Runnable {

                if(value_datas_spec_size>0){
                    for (i in 0..value_datas_spec_size - 1) {
                        var item_name = MMKV.mmkvWithID("editPro_temp").getString("datas_spec_item${i}", "")
                        mutableList_spec.add(
                            ItemSpecification(
                                item_name.toString()
                            )
                        )
                    }

                    runOnUiThread {
                        //更新或新增item
                        mAdapter_spec.updateList(mutableList_spec)
                        binding.tvFirstLayerHint.setText("(${value_datas_spec_size}/10)")

                        checkButtonNextStep_single()
                    }
                }

                if(!value_editTextProductSpecSecond.isNullOrEmpty()){

                    if(value_datas_size_size>0){
                        for (i in 0..value_datas_size_size - 1) {
                            var item_name = MMKV.mmkvWithID("editPro_temp").getString("datas_size_item${i}", "")
                            mutableList_size.add(
                                ItemSpecification(
                                    item_name.toString()
                                )
                            )
                        }

                        runOnUiThread {

                            //更新或新增item
                            mAdapter_size.updateList(mutableList_size)
                            binding.tvSecondLayerHint.setText("(${value_datas_size_size}/10)")

                            checkButtonNextStep_double()
                        }
                    }
                }

            }).start()


        }else{


            value_editTextProductSpecFirst =
                MMKV.mmkvWithID("editPro").getString("value_editTextProductSpecFirst", "").toString()
            value_editTextProductSpecSecond =
                MMKV.mmkvWithID("editPro").getString("value_editTextProductSpecSecond", "").toString()
            value_datas_spec_size =
                MMKV.mmkvWithID("editPro").getString("datas_spec_size", "0").toString().toInt()
            value_datas_size_size =
                MMKV.mmkvWithID("editPro").getString("datas_size_size", "0").toString().toInt()

            binding.editTextProductSpecFirst.setText(value_editTextProductSpecFirst)
            binding.editTextProductSpecSecond.setText(value_editTextProductSpecSecond)


            Thread(Runnable {

                if(value_datas_spec_size>0){
                    for (i in 0..value_datas_spec_size - 1) {
                        var item_name = MMKV.mmkvWithID("editPro").getString("datas_spec_item${i}", "")
                        mutableList_spec.add(
                            ItemSpecification(
                                item_name.toString()
                            )
                        )
                    }

                    runOnUiThread {
                        //更新或新增item
                        mAdapter_spec.updateList(mutableList_spec)
                        binding.tvFirstLayerHint.setText("(${value_datas_spec_size}/10)")

                        checkButtonNextStep_single()
                    }
                }


                if(!value_editTextProductSpecSecond.isNullOrEmpty()){
                    if(value_datas_size_size>0){
                        for (i in 0..value_datas_size_size - 1) {

                            var item_name = MMKV.mmkvWithID("editPro").getString("datas_size_item${i}", "")
                            mutableList_size.add(
                                ItemSpecification(
                                    item_name.toString()
                                )
                            )
                        }


                        runOnUiThread {

                            //更新或新增item
                            mAdapter_size.updateList(mutableList_size)
                            binding.tvSecondLayerHint.setText("(${value_datas_size_size}/10)")

                            checkButtonNextStep_double()
                        }

                    }
                }


            }).start()


        }



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
        binding.titleSpec.setText(R.string.title_editSpecification)
        //預設btnClearAllSpec和btnClearAllSpec隱藏

        initEditText()
        initClick()
    }


    fun initClick() {

        binding.titleBackAddshop.setOnClickListener {


            MMKV.mmkvWithID("editPro_temp").clear()

            val intent = Intent(this, EditProductActivity::class.java)
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


            binding.tvFirstLayerHint.setText("(${mAdapter_spec.get_datas_spec_size()}/10)")

        }
        binding.btnClearAllSize.setOnClickListener {

            binding.editTextProductSpecSecond.setText("")
            clearAllSizeItem()

            checkButtonNextStep_double()

            binding.tvSecondLayerHint.setText("(${mAdapter_size.get_datas_size_size()}/10)")

        }

        binding.btnNextStep.setOnClickListener {


            var datas_spec_item: MutableList<ItemSpecification> = mAdapter_spec.get_spec_list()
            var datas_size_item: MutableList<ItemSpecification> = mAdapter_size.get_size_list()
            var datas_spec_size: Int = mAdapter_spec.get_datas_spec_size()
            var datas_size_size: Int = mAdapter_size.get_datas_size_size()
            var datas_spec_title_first: String = binding.editTextProductSpecFirst.text.toString()
            var datas_spec_title_second: String = binding.editTextProductSpecSecond.text.toString()



            //MMKV input datas
            MMKV.mmkvWithID("editPro_temp").putString("datas_spec_size", datas_spec_size.toString())
            MMKV.mmkvWithID("editPro_temp").putString("datas_size_size", datas_size_size.toString())
            MMKV.mmkvWithID("editPro_temp")
                .putString("value_editTextProductSpecFirst", datas_spec_title_first)
            MMKV.mmkvWithID("editPro_temp")
                .putString("value_editTextProductSpecSecond", datas_spec_title_second)


            for (i in 0..datas_spec_size - 1) {

                MMKV.mmkvWithID("editPro_temp")
                    .putString("datas_spec_item${i}", datas_spec_item.get(i).spec_name.toString())
            }

            for (i in 0..datas_size_size - 1) {

                MMKV.mmkvWithID("editPro_temp")
                    .putString("datas_size_item${i}", datas_size_item.get(i).spec_name.toString())

            }


            val intent = Intent(this, EditInventoryAndPriceActivity::class.java)
            startActivity(intent)
            finish()
        }

        //"First Layer Spec" adding
        binding.btnAddSpecificationFirstLayer.setOnClickListener {

            if(binding.editTextProductSpecFirst.text.isNullOrEmpty()){
                Toast.makeText(this, "請先輸入第一層商品規格名稱", Toast.LENGTH_SHORT).show()
            }else{
                mutableList_spec = mAdapter_spec.get_spec_list()
                if (mutableList_spec.size < 10) {

                    if (EDIT_MODE_SPEC == "0") {

                        if (mAdapter_spec.get_check_empty() == true && mutableList_spec.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                        } else {

                            Thread(Runnable {

                                mutableList_spec = mAdapter_spec.get_spec_list()
                                mutableList_spec.add(
                                    ItemSpecification(
                                        ""
                                    )
                                )


                                runOnUiThread {

                                    //更新或新增item
                                    mAdapter_spec.updateList(mutableList_spec)

                                    //更新或新增item
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                                    binding.tvFirstLayerHint.setText("(${mAdapter_spec.get_datas_spec_size()}/10)")

                                    RxBus.getInstance().post(EventInvenSpecDatasRebuild(true))

                                }

                            }).start()



                        }


                    } else {

                        if (mAdapter_spec.get_check_empty() == true && mutableList_spec.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                        } else {
                            Thread(Runnable {

                                mutableList_spec.add(
                                    ItemSpecification(
                                        ""
                                    )
                                )

                                runOnUiThread {
                                    //更新或新增item
                                    mAdapter_spec.updateList(mutableList_spec)

                                    //更新或新增item
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                                    binding.tvFirstLayerHint.setText("(${mAdapter_spec.get_datas_spec_size()}/10)")

                                    RxBus.getInstance().post(EventInvenSpecDatasRebuild(true))

                                }

                            }).start()



                        }

                    }

                } else {

                    Toast.makeText(this, "只能新增最多十個規格", Toast.LENGTH_SHORT).show()

                }
            }

        }

        //"First Layer Spec" editation enabling
        binding.btnEditSpecificationEnableFirstLayer.setOnClickListener {


            binding.btnClearAllSpec.isVisible = true
            binding.btnEditSpecificationEnableSecondLayer.disable()

            EDIT_MODE_SPEC = "1"

            binding.btnEditSpecificationEnableFirstLayer.isEnabled = false
            binding.btnEditSpecificationEnableFirstLayer.isVisible = false
            binding.btnEditSpecificationDisableFirstLayer.isEnabled = true
            binding.btnEditSpecificationDisableFirstLayer.isVisible = true

            Thread(Runnable {

                runOnUiThread {

                    binding.progressBarSpecItemsEditation.visibility =View.VISIBLE
                    binding.imgViewLoadingBackgroundSpecItemsEditation.visibility =View.VISIBLE

                    mAdapter_spec.set_edit_status(true)
                }

                try{
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                runOnUiThread {

                    binding.btnNextStep.disable()
                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                    binding.progressBarSpecItemsEditation.visibility =View.GONE
                    binding.imgViewLoadingBackgroundSpecItemsEditation.visibility =View.GONE
                }


            }).start()


        }

        //"First Layer Spec" editation  canceling
        binding.btnEditSpecificationDisableFirstLayer.setOnClickListener {

            binding.btnNextStep.enable()
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)

            binding.btnClearAllSpec.isVisible = false
            binding.btnEditSpecificationEnableSecondLayer.enable()

            EDIT_MODE_SPEC = "0"

            binding.btnEditSpecificationDisableFirstLayer.isEnabled = false
            binding.btnEditSpecificationDisableFirstLayer.isVisible = false
            binding.btnEditSpecificationEnableFirstLayer.isEnabled = true
            binding.btnEditSpecificationEnableFirstLayer.isVisible = true

            Thread(Runnable {


                runOnUiThread {

                   mAdapter_spec.set_edit_status(false)

                }

            }).start()

            if(mAdapter_spec.get_datas_spec_size()==0){
                binding.btnNextStep.disable()
                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
            }

        }


        //"Second Layer Spec" adding
        binding.btnAddSpecificationSecondLayer.setOnClickListener {


            if(binding.editTextProductSpecSecond.text.isNullOrEmpty()){
                Toast.makeText(this, "請先完成輸入第一層規格內容", Toast.LENGTH_SHORT).show()
            }else{
                mutableList_size = mAdapter_size.get_size_list()
                if (mutableList_size.size < 10) {

                    if (EDIT_MODE_SIZE == "0") {

                        if (mAdapter_size.get_check_empty() == true && mutableList_size.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                        } else {
                            Thread(Runnable {

                                mutableList_size = mAdapter_size.get_size_list()
                                mutableList_size.add(
                                    ItemSpecification(
                                        ""
                                    )
                                )

                                runOnUiThread {

                                    //更新或新增item
                                    mAdapter_size.updateList(mutableList_size)

                                }
                                runOnUiThread {

                                    //更新或新增item
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                                    binding.tvSecondLayerHint.setText("(${mAdapter_size.get_datas_size_size()}/10)")

                                    RxBus.getInstance().post(EventInvenSpecDatasRebuild(true))

                                }

                            }).start()


                        }


                    } else {


                        if (mAdapter_size.get_check_empty() == true && mutableList_size.size > 0) {
                            Toast.makeText(this, "請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                        } else {

                            Thread(Runnable {

                                mutableList_size = mAdapter_size.get_size_list()
                                mutableList_size.add(
                                    ItemSpecification(
                                        ""
                                    )
                                )

                                runOnUiThread {

                                    //更新或新增item
                                    mAdapter_size.updateList(mutableList_size)

                                }
                                runOnUiThread {

                                    //更新或新增item
                                    binding.btnNextStep.disable()
                                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                                    binding.tvSecondLayerHint.setText("(${mAdapter_size.get_datas_size_size()}/10)")

                                    RxBus.getInstance().post(EventInvenSpecDatasRebuild(true))

                                }


                            }).start()

                        }


                    }

                } else {
                    Toast.makeText(this, "只能新增最多十個規格", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //"Second Layer Spec" editation canceling
        binding.btnEditSpecificationDisableSecondLayer.setOnClickListener {

            binding.btnNextStep.enable()
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)

            binding.btnClearAllSize.isVisible = false
            binding.btnEditSpecificationEnableFirstLayer.enable()

            EDIT_MODE_SIZE = "0"

            binding.btnEditSpecificationDisableSecondLayer.isEnabled = false
            binding.btnEditSpecificationDisableSecondLayer.isVisible = false
            binding.btnEditSpecificationEnableSecondLayer.isEnabled = true
            binding.btnEditSpecificationEnableSecondLayer.isVisible = true

            Thread(Runnable {


                runOnUiThread {

                    mAdapter_size.set_edit_status(false)

                }

            }).start()



            if(mAdapter_size.get_datas_size_size()==0){
                binding.btnNextStep.disable()
                binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
            }

        }

        //"Second Layer Spec" editation  enabling
        binding.btnEditSpecificationEnableSecondLayer.setOnClickListener {


            binding.btnClearAllSize.isVisible = true
            binding.btnEditSpecificationEnableFirstLayer.disable()

            EDIT_MODE_SIZE = "1"

            binding.btnEditSpecificationEnableSecondLayer.isEnabled = false
            binding.btnEditSpecificationEnableSecondLayer.isVisible = false
            binding.btnEditSpecificationDisableSecondLayer.isEnabled = true
            binding.btnEditSpecificationDisableSecondLayer.isVisible = true

            Thread(Runnable {


                runOnUiThread {
                    binding.progressBarSpecItemsEditation.visibility =View.VISIBLE
                    binding.imgViewLoadingBackgroundSpecItemsEditation.visibility =View.VISIBLE

                    mAdapter_size.set_edit_status(true)
                }

                try{
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                runOnUiThread {

                    binding.btnNextStep.disable()
                    binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)

                    binding.progressBarSpecItemsEditation.visibility =View.GONE
                    binding.imgViewLoadingBackgroundSpecItemsEditation.visibility =View.GONE

                }



            }).start()




        }
    }


    fun initEditText() {

        val first_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

//                if (s.toString() == "") {
//
//                    MMKV.mmkvWithID("editPro").putString("value_editTextProductSpecFirst", "")
//
//                } else {
//
//                    MMKV.mmkvWithID("editPro")
//                        .putString("value_editTextProductSpecFirst", s.toString())
//
//
//                }

            }
        }
        val second_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

//                if (s.toString() == "") {
//
//                    MMKV.mmkvWithID("editPro").putString("value_editTextProductSpecSecondt", "")
//
//
//                } else {
//
//                    MMKV.mmkvWithID("editPro")
//                        .putString("value_editTextProductSpecSecond", s.toString())
//
//                }

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
                        if(binding.editTextProductSpecFirst.text.isEmpty()&&!mAdapter_spec.nextStepEnableOrNot()){
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

        RxBus.getInstance().post(EventInvenSpecDatasRebuild(true))
    }

    fun clearAllSizeItem() {
        mutableList_size.clear()
        mAdapter_size.notifyDataSetChanged()

        RxBus.getInstance().post(EventInvenSpecDatasRebuild(true))
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onBackPressed() {

        MMKV.mmkvWithID("editPro_temp").clear()

        val intent = Intent(this, EditProductActivity::class.java)
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
                        binding.tvFirstLayerHint.setText("(${mAdapter_spec.get_datas_spec_size()}/10)")

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
                        binding.tvSecondLayerHint.setText("(${mAdapter_size.get_datas_size_size()}/10)")
                    }
                    is EventInvenSpecDatasRebuild ->{

                        MMKV.mmkvWithID("editPro_temp").putBoolean("rebuild_datas", true)

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