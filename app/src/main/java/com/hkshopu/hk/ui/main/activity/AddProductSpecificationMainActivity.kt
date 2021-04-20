package com.hkshopu.hk.ui.main.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemShippingFare
import com.hkshopu.hk.data.bean.ItemSpecification
import com.hkshopu.hk.databinding.ActivityAddProductDescriptionMainBinding
import com.hkshopu.hk.ui.main.adapter.ItemTouchHelperCallback
import com.hkshopu.hk.ui.main.adapter.SpecificationSizeAdapter
import com.hkshopu.hk.ui.main.adapter.SpecificationSpecAdapter
import com.hkshopu.hk.ui.main.fragment.SpecificationInfoDialogFragment
import org.jetbrains.anko.singleLine

class AddProductSpecificationMainActivity : BaseActivity() {

    private lateinit var binding : ActivityAddProductDescriptionMainBinding

    val mAdapter_spec = SpecificationSpecAdapter()
    val mAdapter_size = SpecificationSizeAdapter()
    var mutableList_spec = mutableListOf<ItemSpecification>()
    var mutableList_size = mutableListOf<ItemSpecification>()
    var EDIT_MODE_SPEC = "0"
    var EDIT_MODE_SIZE = "0"

    var firstSpecGrpTitle_check = 0
    var secondSpecGrpTitle_check = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductDescriptionMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    fun initView() {


        //預設btnClearAllSpec和btnClearAllSpec隱藏
        binding.btnClearAllSpec.isVisible = false
        binding.btnClearAllSize.isVisible = false



        //Spec and Size item recyclerview setting
        binding.rViewSpecificationItemSpec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.rViewSpecificationItemSpec.adapter = mAdapter_spec

        binding.rviewSpecificationitemSize.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.rviewSpecificationitemSize.adapter = mAdapter_size


        //預設btnNextStep disable
        binding.btnNextStep.isEnabled = false
        binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)


        initEditText()
        initClick()

    }

    fun initClick(){

        binding.titleBackAddshop.setOnClickListener {
            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)
        }

        binding.iconSpecificationhelp.setOnClickListener {
            SpecificationInfoDialogFragment().show(supportFragmentManager, "MyCustomFragment")
        }

        binding.btnClearAllSpec.setOnClickListener {

            binding.editTextProductSpecFirst.setText("")
            clearAllSpecItem()

        }
        binding.btnClearAllSize.setOnClickListener {

            binding.editTextProductSpecSecond.setText("")
            clearAllSizeItem()

        }

        binding.btnNextStep.setOnClickListener {

            val intent = Intent(this, InventoryAndPriceActivity::class.java)
            var datas_spec_item : MutableList<ItemSpecification> = mAdapter_spec.get_spec_list()
            var datas_size_item : MutableList<ItemSpecification> = mAdapter_size.get_size_list()
            var datas_spec_size : Int = mAdapter_spec.get_datas_spec_size()
            var datas_size_size : Int = mAdapter_size.get_datas_size_size()
            var datas_spec_title_first: String =  binding.editTextProductSpecFirst.text.toString()
            var datas_spec_title_second: String = binding.editTextProductSpecSecond.text.toString()

            var bundle = Bundle()

            bundle.putInt("datas_spec_size", datas_spec_size)
            bundle.putInt("datas_size_size", datas_size_size)
            bundle.putString("datas_spec_title_first", datas_spec_title_first)
            bundle.putString("datas_spec_title_second", datas_spec_title_second)

            for(key in 0..datas_spec_item.size-1) {
                bundle.putParcelable("spec"+key.toString(), datas_spec_item.get(key)!!)
            }

            for(key in 0..datas_size_item.size-1) {
                bundle.putParcelable("size"+key.toString(), datas_size_item.get(key)!!)
            }

            intent.putExtra("bundle_AddProductSpecificationMainActivity", bundle)

            startActivity(intent)
            finish()
        }

        //first specification "spec" item add
        binding.btnAddspecificationSpec.setOnClickListener {
            if (mutableList_spec.size <3) {

                if(EDIT_MODE_SPEC=="0"){

                    if(mAdapter_spec.get_check_empty() == true && mutableList_spec.size >0 ){
                        Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                    }else{

                        Thread(Runnable {

                            mutableList_spec = mAdapter_spec.get_spec_list()
                            mutableList_spec.add(ItemSpecification("", R.drawable.custom_unit_transparent))

                            runOnUiThread {
                                //更新或新增item
                                mAdapter_spec.updateList(mutableList_spec)
                                mAdapter_spec.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()

                            }

                        }).start()
                    }


                }else{

                    if(mAdapter_spec.get_check_empty() == true && mutableList_spec.size >0 ){
                        Toast.makeText(this, "請先完成輸入才能新增下個項目", Toast.LENGTH_SHORT).show()
                    }else{
                        Thread(Runnable {

                            mutableList_spec.add(ItemSpecification("", R.mipmap.btn_cancel))

                            runOnUiThread {

                                //更新或新增item
                                mAdapter_spec.updateList(mutableList_spec)
                                mAdapter_spec.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()

                            }

                        }).start()
                    }

                }

            }else {

                Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()

            }
        }

        //second specification "spec" setting enable
        binding.btnEditspecificationEnableSpec.setOnClickListener {

            binding.btnClearAllSpec.isVisible = true

            EDIT_MODE_SPEC = "1"

            binding.btnEditspecificationEnableSpec.isEnabled = false
            binding.btnEditspecificationEnableSpec.isVisible = false
            binding.btnEditspecificationDisableSpec.isEnabled = true
            binding.btnEditspecificationDisableSpec.isVisible = true

            Thread(Runnable {

                mutableList_spec = mAdapter_spec.get_spec_list()

                for ( i in 0..mutableList_spec.size-1) {
                    mutableList_spec[i] = ItemSpecification(mutableList_spec[i].spec_name.toString() , R.mipmap.btn_cancel)
                }

                runOnUiThread {

                    //更新或新增item
                    mAdapter_spec.updateList(mutableList_spec)
                    mAdapter_spec.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()

                }

            }).start()

        }

        //second specification "spec" item setting cancel
        binding.btnEditspecificationDisableSpec.setOnClickListener {

            binding.btnClearAllSpec.isVisible = false

            EDIT_MODE_SPEC = "0"

            binding.btnEditspecificationDisableSpec.isEnabled = false
            binding.btnEditspecificationDisableSpec.isVisible = false
            binding.btnEditspecificationEnableSpec.isEnabled = true
            binding.btnEditspecificationEnableSpec.isVisible = true

            Thread(Runnable {

                mutableList_spec = mAdapter_spec.get_spec_list()

                for ( i in 0..mutableList_spec.size-1) {
                    mutableList_spec[i] = ItemSpecification(mutableList_spec[i].spec_name , R.drawable.customborder_specification)
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_spec.updateList(mutableList_spec)
                    mAdapter_spec.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()
                }

            }).start()

        }


        //second specification "size" item add
        binding.btnAddspecificationSize.setOnClickListener {

            mutableList_size = mAdapter_size.get_size_list()

            if (mutableList_size.size<3){

                if(EDIT_MODE_SIZE == "0") {

                    if(mAdapter_size.get_check_empty() == true && mutableList_size.size >0 ){
                        Toast.makeText(this,"請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                    }else{
                        Thread(Runnable {

                            mutableList_size = mAdapter_size.get_size_list()
                            mutableList_size.add(ItemSpecification("", R.drawable.custom_unit_transparent))

                            runOnUiThread {

                                //更新或新增item
                                mAdapter_size.updateList(mutableList_size)
                                mAdapter_size.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()
                            }

                        }).start()

                    }



                }else{


                    if(mAdapter_size.get_check_empty() == true && mutableList_size.size >0 ){
                        Toast.makeText(this,"請先完成輸入才能新增項目", Toast.LENGTH_SHORT).show()

                    }else{

                        Thread(Runnable {

                            mutableList_size = mAdapter_size.get_size_list()
                            mutableList_size.add(ItemSpecification("", R.mipmap.btn_cancel))

                            runOnUiThread {

                                //更新或新增item
                                mAdapter_size.updateList(mutableList_size)
                                mAdapter_size.notifyDataSetChanged()

                                //判斷Next Step Btn is enable or disable
                                changeStatusOfNextStepBtn()
                            }


                        }).start()

                    }



                }

            }else {
                Toast.makeText(this, "只能新增最多三個規格", Toast.LENGTH_SHORT).show()
            }

        }

        //second specification "size" item settings cancel
        binding.btnEditspecificationDisableSize.setOnClickListener {

            binding.btnClearAllSize.isVisible = false

            EDIT_MODE_SIZE = "0"

            binding.btnEditspecificationDisableSize.isEnabled = false
            binding.btnEditspecificationDisableSize.isVisible = false
            binding.btnEditspecificationEnableSize.isEnabled = true
            binding.btnEditspecificationEnableSize.isVisible = true

            Thread(Runnable {

                mutableList_size = mAdapter_size.get_size_list()

                for ( i in 0..mutableList_size.size-1) {
                    mutableList_size[i] = ItemSpecification(mutableList_size[i].spec_name , R.drawable.customborder_specification)
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_size.updateList(mutableList_size)
                    mAdapter_size.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()
                }

            }).start()


        }

        //second specification "size" item settings enable
        binding.btnEditspecificationEnableSize.setOnClickListener {

            binding.btnClearAllSize.isVisible = true

            EDIT_MODE_SIZE = "1"

            binding.btnEditspecificationEnableSize.isEnabled = false
            binding.btnEditspecificationEnableSize.isVisible = false
            binding.btnEditspecificationDisableSize.isEnabled = true
            binding.btnEditspecificationDisableSize.isVisible = true

            Thread(Runnable {

                mutableList_size = mAdapter_size.get_size_list()

                for ( i in 0..mutableList_size.size-1) {
                    mutableList_size[i] = ItemSpecification(mutableList_size[i].spec_name , R.mipmap.btn_cancel)
                }


                runOnUiThread {

                    //更新或新增item
                    mAdapter_size.updateList(mutableList_size)
                    mAdapter_size.notifyDataSetChanged()

                    //判斷Next Step Btn is enable or disable
                    changeStatusOfNextStepBtn()
                }

            }).start()

        }
    }


    fun initEditText(){

        val first_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(s.toString() == ""){

                    firstSpecGrpTitle_check = 0

                }else{

                    firstSpecGrpTitle_check = 1


                }
            }
        }
        val second_textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {

                if(s.toString() == ""){

                    secondSpecGrpTitle_check = 0

                }else{

                    secondSpecGrpTitle_check = 1


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

                    binding.editTextProductSpecFirst.hideKeyboard()
                    binding.editTextProductSpecFirst.clearFocus()

                    changeStatusOfNextStepBtn()

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

                    binding.editTextProductSpecSecond.hideKeyboard()
                    binding.editTextProductSpecSecond.clearFocus()

                    changeStatusOfNextStepBtn()

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

    fun changeStatusOfNextStepBtn(){

        if( firstSpecGrpTitle_check != 0 && mAdapter_spec.nextStepEnableOrNot() ){
            binding.btnNextStep.isEnabled = true
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
        }else if(secondSpecGrpTitle_check != 0 && mAdapter_size.nextStepEnableOrNot()){
            binding.btnNextStep.isEnabled = true
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstep_enable)
        }else{
            binding.btnNextStep.isEnabled = false
            binding.btnNextStep.setImageResource(R.mipmap.btn_nextstepdisable)
        }

    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}