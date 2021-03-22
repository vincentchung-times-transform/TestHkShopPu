package com.hkshopu.hk.ui.main.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkshopu.hk.Base.BaseActivity
import com.hkshopu.hk.R
import com.hkshopu.hk.data.bean.ItemPics
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.ui.main.adapter.PicsAdapter
import vn.luongvo.widget.iosswitchview.SwitchView
import java.io.FileNotFoundException


class AddNewProductActivity : BaseActivity() {

    private lateinit var binding : ActivityAddNewProductBinding

    lateinit var switchView: SwitchView

    val REQUEST_EXTERNAL_STORAGE = 100

    var mutableList_pics = mutableListOf<ItemPics>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddPics.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE);
//                    return;
            } else {
                launchGalleryIntent();
            }

        }

        initView()
//        switchView = findViewById(R.id.swi)

//        switchView.setOnCheckedChangeListener(SwitchView.OnCheckedChangeListener { switchView, isChecked ->
//            Toast.makeText(
//                this,
//                "onCheckedChanged: $isChecked",
//                Toast.LENGTH_SHORT
//            ).show()
//        })

    }

    fun initView() {

        initClick()


    }

    fun initClick() {
        //choose product inventory status
        binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
        binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
        binding.tvBrandnew.setOnClickListener {
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_userinfo_gender)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_edit_login)
        }
        binding.tvSecondhand.setOnClickListener {
            binding.tvBrandnew.setBackgroundResource(R.drawable.bg_edit_login)
            binding.tvSecondhand.setBackgroundResource(R.drawable.bg_userinfo_gender)
        }

        //go to category page
        binding.btnAddcategory.setOnClickListener {

//            val intent = Intent(this, LoginPasswordActivity::class.java)
//            startActivity(intent)

        }

        binding.btnAddspecification.setOnClickListener {
            val intent = Intent(this, AddProductSpecificationMainActivity::class.java)
            startActivity(intent)


        }
    }

    fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    launchGalleryIntent()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }


    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            val imageView = findViewById<ImageView>(R.id.image_view)
//            val bitmaps: MutableList<Bitmap> = ArrayList()
            val clipData = data?.clipData
            if (clipData != null) {
                //multiple images selecetd
                for (i in 0 until clipData.itemCount) {
                    if (i ==0 ) {
                        //取得圖片uri存到變數imageUri並轉成bitmap
                        val imageUri = clipData.getItemAt(i).uri
                        Log.d("URI", imageUri.toString())
                        try {
                            val inputStream =
                                contentResolver.openInputStream(imageUri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)

                            //新增所選圖片以及第一張cover image至mutableList_pics中
                            mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))


                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }

                    }else {
                        //取得圖片uri存到變數imageUri並轉成bitmap
                        val imageUri = clipData.getItemAt(i).uri
                        Log.d("URI", imageUri.toString())
                        try {
                            val inputStream =
                                contentResolver.openInputStream(imageUri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)

                            //新增所選圖片以及第一張cover image至mutableList_pics中
                            mutableList_pics.add(ItemPics(bitmap, R.drawable.custom_unit_transparent))


                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }

                    }


                }
            } else {
                //single image selected
                val imageUri = data?.data
                Log.d("URI", imageUri.toString())
                try {
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    //新增所選圖片以及第一張cover image至mutableList_pics中
                    mutableList_pics.add(ItemPics(bitmap, R.mipmap.cover_pic))


                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
            Thread(Runnable {
                runOnUiThread {

                    val mAdapter = PicsAdapter()

                    mAdapter.updateList(mutableList_pics)     //傳入資料
                    binding.rView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.rView.adapter = mAdapter

//                    var listview: ListView = binding.listview
//                    var adapter: CustomAdapter = CustomAdapter(this, bitmaps)
//                    listview.adapter = adapter as ListAdapter?
                    try {
                        Thread.sleep(3000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

//                for (b in bitmaps) {
//                    runOnUiThread { imageView.setImageBitmap(b) }
//                    try {
//                        Thread.sleep(3000)
//                    } catch (e: InterruptedException) {
//                        e.printStackTrace()
//                    }
//                }
            }).start()
        }
    }


}