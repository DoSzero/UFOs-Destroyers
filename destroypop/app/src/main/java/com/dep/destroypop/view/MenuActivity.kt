package com.dep.destroypop.view

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.dep.destroypop.R
import com.dep.destroypop.databinding.ActivityMenuBinding
import com.dep.destroypop.utils.ScoreUtils
import kotlin.system.exitProcess

class MenuActivity : AppCompatActivity() {

    private var mMusic = true
    private var mSound = true

    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setBackgroundDrawableResource(R.drawable.background)
        setToFullScreen()

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        animation.duration = 100

        binding.btnAbout.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    applicationContext, InfoActivity::class.java
                )
            )
        }

        findViewById<View>(R.id.activity_start).setOnClickListener { view: View? -> setToFullScreen() }

        binding.btnStart.setOnClickListener { view: View? ->
            val intent = Intent(applicationContext, GameActivity::class.java)
            intent.putExtra(SOUND, mSound)
            intent.putExtra(MUSIC, mMusic)
            startActivity(intent)
        }

        binding.btnExit.setOnClickListener { view: View ->
            view.startAnimation(animation)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }

        binding.btnMusic.setOnClickListener { view: View? ->
            if (mMusic) {
                mMusic = false
                binding.btnMusic.setBackgroundResource(R.drawable.music_note_off)
            } else {
                mMusic = true
                binding.btnMusic.setBackgroundResource(R.drawable.music_note)
            }
        }

        binding.btnSound.setOnClickListener { view: View? ->
            if (mSound) {
                mSound = false
                binding.btnSound.setBackgroundResource(R.drawable.volume_off)
            } else {
                mSound = true
                binding.btnSound.setBackgroundResource(R.drawable.volume_up)
            }
        }

    }

    private fun setToFullScreen() {
        findViewById<View>(R.id.activity_start).systemUiVisibility =
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

    companion object {
        const val SOUND = "SOUND"
        const val MUSIC = "MUSIC"
    }
}