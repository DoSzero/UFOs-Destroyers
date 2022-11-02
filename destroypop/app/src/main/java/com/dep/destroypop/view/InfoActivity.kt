package com.dep.destroypop.view

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.dep.destroypop.R
import com.dep.destroypop.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setBackgroundDrawableResource(R.drawable.background)
        setToFullScreen()

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        animation.duration = 100

        findViewById<View>(R.id.activity_instructions).setOnClickListener {
            setToFullScreen()
        }

        findViewById<View>(R.id.btn_back_instructions).setOnClickListener {
            view: View ->
            view.startAnimation(animation)
            finish()
        }
    }

    private fun setToFullScreen() {
        findViewById<View>(R.id.activity_instructions).
        systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onResume() {
        super.onResume()
        setToFullScreen()
    }
}