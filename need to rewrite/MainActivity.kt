package com.dep.destroypop

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dep.destroypop.GameplayActivity
import com.dep.destroypop.databinding.ActivityMainBinding
import com.dep.destroypop.utils.HighScoreHelper
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
   
    private var denied = false
    private var mMusic = true
    private var mSound = true

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setBackgroundDrawableResource(R.drawable.background)
        setToFullScreen()

        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnInstructions = findViewById<Button>(R.id.btn_instructions)
        val btnInviteFriends = findViewById<Button>(R.id.btn_invite_friends)
        val btnExit = findViewById<ImageButton>(R.id.btn_exit)
        val btnMusic = findViewById<ImageButton>(R.id.btn_music)
        val btnSound = findViewById<ImageButton>(R.id.btn_sound)
  
        binding.btnLeaderboard.visibility = View.GONE
        binding.btnAchievements.visibility = View.GONE

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        animation.setDuration(100)

        binding.highScore.setText(HighScoreHelper.getTopScore(this).toString())
        btnInstructions.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    applicationContext, InstructionsActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.activity_start).setOnClickListener { view: View? -> setToFullScreen() }
        btnStart.setOnClickListener { view: View? ->
            val intent = Intent(applicationContext, GameplayActivity::class.java)
            intent.putExtra(SOUND, mSound)
            intent.putExtra(MUSIC, mMusic)
            startActivity(intent)
        }
        btnExit.setOnClickListener { view: View ->
            view.startAnimation(animation)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
        btnMusic.setOnClickListener { view: View? ->
            if (mMusic) {
                mMusic = false
                btnMusic.setBackgroundResource(R.drawable.music_note_off)
            } else {
                mMusic = true
                btnMusic.setBackgroundResource(R.drawable.music_note)
            }
        }
        btnSound.setOnClickListener { view: View? ->
            if (mSound) {
                mSound = false
                btnSound.setBackgroundResource(R.drawable.volume_off)
            } else {
                mSound = true
                btnSound.setBackgroundResource(R.drawable.volume_up)
            }
        }

        binding.btnAchievements.setOnClickListener(View.OnClickListener { view: View ->
            view.startAnimation(animation)
        })

        binding.btnLeaderboard.setOnClickListener(View.OnClickListener { view: View ->
            view.startAnimation(animation)
        })
        
        btnInviteFriends.setOnClickListener { view: View? ->
            val intent = AppInviteInvitation.IntentBuilder(getString(R.string.invite_title))
                .setMessage(getString(R.string.invite_message))
                .setDeepLink(Uri.parse(getString(R.string.dynamic_link)))
                .setCustomImage(Uri.parse(getString(R.string.email_cover)))
                .setCallToActionText(getString(R.string.invite_action))
                .build()
            startActivityForResult(intent, REQUEST_INVITE)
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

        binding.highScore.text = HighScoreHelper.getTopScore(this).toString()
        if (GoogleSignIn.getLastSignedInAccount(this) == null && isConnected && !denied) signInSilently() else if (GoogleSignIn.getLastSignedInAccount(
                this) != null && isConnected &&
            binding.btnLeaderboard.visibility == View.GONE &&
            binding.btnAchievements.visibility == View.GONE
        ) {
            binding.btnLeaderboard.visibility = View.VISIBLE
            binding.btnAchievements.visibility = View.VISIBLE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (result!!.isSuccess) {
                Toast.makeText(this, getString(R.string.signed_in), Toast.LENGTH_SHORT).show()
                binding.btnLeaderboard.visibility = View.VISIBLE
                binding.btnAchievements.visibility = View.VISIBLE
                denied = false
            } else {
                denied = true
            }
        } else if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                
            }
        }
    }


    companion object {
        const val SOUND = "SOUND"
        const val MUSIC = "MUSIC"
        private const val REQUEST_INVITE = 1
        private const val RC_SIGN_IN = 2
    }
}
