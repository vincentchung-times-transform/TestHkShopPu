package com.hkshopu.hk.ui.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hkshopu.hk.databinding.ActivityAddNewProductBinding
import com.hkshopu.hk.databinding.ActivityAddProductDescriptionMainBinding
import com.hkshopu.hk.databinding.ActivityMerchandiseBinding

class MerchanCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var binding : ActivityMerchandiseBinding

        super.onCreate(savedInstanceState)
        binding = ActivityMerchandiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}