package com.hkshopu.hk.ui.main.fragment


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.hkshopu.hk.Base.BaseFragment
import com.hkshopu.hk.R
import com.hkshopu.hk.application.App
import com.hkshopu.hk.component.EventShopDesUpdated
import com.hkshopu.hk.component.EventShopNameUpdated
import com.hkshopu.hk.data.bean.ItemData
import com.hkshopu.hk.databinding.FragmentShopinfoBinding
import com.hkshopu.hk.ui.main.adapter.CategoryMultiAdapter
import com.hkshopu.hk.ui.main.adapter.CategorySingleAdapter
import com.hkshopu.hk.utils.rxjava.RxBus


class ShopInfoFragment : Fragment(R.layout.fragment_shopinfo) {

    companion object {
        fun newInstance(): ShopInfoFragment {
            val args = Bundle()
            val fragment = ShopInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private var binding: FragmentShopinfoBinding? = null

    private var fragmentShopInfoBinding: FragmentShopinfoBinding? = null
//    val selectDatas: ArrayList<ItemData> = ArrayList()
    private val pickCoverImage = 100
    private val pickImage = 200
    private var imageUri: Uri? = null
    private val adapter = CategorySingleAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShopinfoBinding.bind(view)
        fragmentShopInfoBinding = binding
        initView()
    }

    fun initView() {
        initClick()
        initVM()
        initEvent()
        initListView()

    }

    private fun initListView() {
        val layoutManager = LinearLayoutManager(context)

        binding!!.listCategory!!.layoutManager = layoutManager
        binding!!.listCategory!!.isNestedScrollingEnabled = false
        binding!!.listCategory!!.itemAnimator = DefaultItemAnimator()
        (binding!!.listCategory!!.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding!!.listCategory!!.adapter = adapter
        adapter!!.setOnItemClickLitener(object : CategorySingleAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int,bean:ItemData) {
                adapter.setSelection(position)
                binding!!.tvCategory1.text = bean.title
                binding!!.layoutCategory1.visibility = View.VISIBLE
                binding!!.tvCategory.visibility = View.GONE
                binding!!.layoutCategorylist.visibility = View.GONE
//                if (bean.isSelect) {
//                    selectDatas.add(bean)
//
//                } else {
//                    selectDatas.remove(bean)
//                }
//                if (selectDatas.size == 1) {
//                    tv_category1.text = selectDatas[0].title
//                    layout_category1.visibility = View.VISIBLE
//                } else if (selectDatas.size == 2) {
//                    tv_category1.text = selectDatas[0].title
//                    layout_category1.visibility = View.VISIBLE
//                    tv_category2.text = selectDatas[1].title
//                    layout_category2.visibility = View.VISIBLE
//                } else {
//                    tv_category1.text = selectDatas[0].title
//                    layout_category1.visibility = View.VISIBLE
//                    tv_category2.text = selectDatas[1].title
//                    layout_category2.visibility = View.VISIBLE
//                    tv_category3.text = selectDatas[2].title
//                    layout_category3.visibility = View.VISIBLE
//                }
            }

        })

    }

    fun initVM() {
        val list: ArrayList<ItemData> = ArrayList()
        list.add(ItemData("護膚化妝", false))
        list.add(ItemData("護理保健", false))
        list.add(ItemData("母嬰育兒", false))
        list.add(ItemData("寵物用品", false))
        list.add(ItemData("電子電器", false))
        list.add(ItemData("家品傢俬", false))
        list.add(ItemData("吃喝玩樂", false))
        list.add(ItemData("運動旅行", false))
        list.add(ItemData("時尚服飾", false))
        adapter.setData(list)
    }

    fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(App.instance, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventShopNameUpdated -> {
                        binding!!.tvShoptitle.text = it.shopName
                    }

                }
            }, {
                it.printStackTrace()
            })
        RxBus.getInstance().toMainThreadObservable(App.instance, Lifecycle.Event.ON_DESTROY)
            .subscribe({
                when (it) {
                    is EventShopDesUpdated -> {
                        binding!!.addShop.text = it.shopDes
                    }

                }
            }, {
                it.printStackTrace()
            })

    }

    fun initClick() {
        binding!!.ivShopCover.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickCoverImage)
        }
        binding!!.ivShopImgadd.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }
        binding!!.btnShopSetting.setOnClickListener {
            if (binding!!.layoutShopsetting.visibility == View.GONE) {
                binding!!.layoutShopsetting.visibility = View.VISIBLE

            } else {
                binding!!.layoutShopsetting.visibility = View.GONE
            }
        }

        binding!!.layoutShoptitle.setOnClickListener {
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            val ft = fragmentManager!!.beginTransaction()
            val prev = fragmentManager!!.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val addNameDialogFragment = AddNameDialogFragment.newInstance()
            addNameDialogFragment.setCancelable(false)
            addNameDialogFragment.show(ft, "ShowEditName")
        }


        binding!!.layoutAddShopDetail.setOnClickListener {
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            val ft = fragmentManager!!.beginTransaction()
            val prev = fragmentManager!!.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val addDesDialogFragment = AddDesDialogFragment.newInstance()
            addDesDialogFragment.setCancelable(false)
            addDesDialogFragment.show(ft, "ShowEditDetail")
        }


        binding!!.tvCategory.setOnClickListener {
            if (binding!!.layoutCategorylist.visibility == View.GONE) {
                binding!!.layoutCategorylist.visibility = View.VISIBLE
                binding!!.layoutCategorylist.bringToFront()
            } else {
                binding!!.layoutCategorylist.visibility = View.GONE
            }
        }
        binding!!.deleteCategory1.setOnClickListener {
            binding!!.layoutCategory1.visibility = View.GONE
            binding!!.tvCategory1.text = ""
            binding!!.tvCategory.visibility = View.VISIBLE
        }
        binding!!.deleteCategory2.setOnClickListener {
            binding!!.layoutCategory2.visibility = View.GONE
            binding!!.tvCategory2.text = ""
        }
        binding!!.deleteCategory3.setOnClickListener {
            binding!!.layoutCategory3.visibility = View.GONE
            binding!!.tvCategory3.text = ""
        }
        binding!!.deleteFee1.setOnClickListener {
            binding!!.layoutFeeNo.visibility = View.GONE
        }
        binding!!.deleteFee2.setOnClickListener {
            binding!!.layoutFeeFix.visibility = View.GONE
        }
        binding!!.tvFeeSettingAdd.setOnClickListener {

            if (binding!!.layoutFeeSetting.visibility == View.GONE) {
                binding!!.layoutFeeSetting.visibility = View.VISIBLE
            } else {
                binding!!.layoutFeeSetting.visibility = View.GONE
            }
        }
        binding!!.tvConfirmShipping.setOnClickListener {
            binding!!.layoutCategorylist.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        // Consider not storing the binding instance in a field, if not needed.
        fragmentShopInfoBinding = null
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickCoverImage) {
            imageUri = data?.data
            binding!!.ivShopCover.setImageURI(imageUri)
            binding!!.tvShopCoverset.visibility = View.INVISIBLE
        } else if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding!!.ivShopImg.setImageURI(imageUri)
        }
    }

}