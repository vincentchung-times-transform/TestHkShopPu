package com.HKSHOPU.hk.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.HKSHOPU.hk.Base.BaseActivity
import com.HKSHOPU.hk.R
import com.HKSHOPU.hk.databinding.ActivityLaunchBinding
import com.HKSHOPU.hk.ui.main.shopProfile.activity.OnBoardActivity


class LaunchActivity : BaseActivity() {
    private lateinit var binding: ActivityLaunchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nextPage()
        initVM()
        initClick()

    }

    private fun nextPage() {
        val backgroundImage: ImageView = findViewById(R.id.launch_img_logo)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.start)
        backgroundImage.startAnimation(slideAnimation)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent()

            intent.setClass(this@LaunchActivity, OnBoardActivity::class.java)

            startActivity(intent)
           finish()
        }, 3000)
    }

    private fun initVM() {

    }

    private fun initClick() {

    }
}