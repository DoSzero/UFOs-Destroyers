package com.dep.destroypop

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dep.destroypop.Balloon.BalloonListener
import com.dep.destroypop.databinding.ActivityGameplayBinding
import com.dep.destroypop.utils.SoundHelper
import kotlinx.coroutines.*
import java.text.MessageFormat
import java.util.*

open class GameplayActivity : AppCompatActivity(), BalloonListener {

    private val mRandom = Random()
    private val mBalloonColors = intArrayOf(
        Color.YELLOW,
        Color.RED,
        Color.WHITE,
        Color.MAGENTA,
        Color.GREEN,
        Color.CYAN,
        Color.BLUE
    )

    var mBalloonsPerLevel = 10
    var mBalloonsPopped = 0
    var mScreenWidth = 0
    var mScreenHeight = 0
    var mLevel = 0
    var mScore = 0
    var mHeartsUsed = 0

    var mPlaying = false
    var mSound = false
    var mMusic = false
    var mGame = false
    var mGameStopped = true

    private var mScoreDisplay: TextView? = null
    private var mLevelDisplay: TextView? = null
    private var mGoButton: Button? = null
    private var mAnimation: Animation? = null

    private var mContentView: ViewGroup? = null
    private var mSoundHelper: SoundHelper? = null
    private var mMusicHelper: SoundHelper? = null

    private val mHeartImages: MutableList<ImageView> = ArrayList()
    private val mBalloons: MutableList<Balloon> = ArrayList()

    private lateinit var binding: ActivityGameplayBinding

    @SuppressLint("FindViewByIdCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameplay)

        binding = ActivityGameplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setBackgroundDrawableResource(R.drawable.background)

        //mContentView = findViewById(R.id.activity_main)
        setToFullScreen()

        val viewTreeObserver = binding.activityMain.viewTreeObserver

        mMusicHelper = SoundHelper(this)
        mMusicHelper!!.prepareMusicPlayer(this)
        val intent = intent

        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
//                    binding.activityMain
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    //mScreenWidth = mContentView.width
                    //mScreenHeight = mContentView.height

                    mScreenWidth = mContentView!!.width
                    mScreenHeight = mContentView!!.height
                }
            })
        }

        mContentView?.setOnClickListener(View.OnClickListener {
                view: View? -> setToFullScreen()
        })

        //mScoreDisplay = findViewById(R.id.score_display)
        //mLevelDisplay = findViewById(R.id.level_display)
        mHeartImages.add(findViewById(R.id.heart1))
        mHeartImages.add(findViewById(R.id.heart2))
        mHeartImages.add(findViewById(R.id.heart3))
        mHeartImages.add(findViewById(R.id.heart4))
        mHeartImages.add(findViewById(R.id.heart5))
        //mGoButton = findViewById(R.id.go_button)
        updateDisplay()

        mSoundHelper = SoundHelper(this)
        mSoundHelper!!.prepareMusicPlayer(this)

        val mAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade)
        mAnimation.duration = 100

        if (intent.hasExtra(MainActivity.SOUND)) mSound = intent.getBooleanExtra(MainActivity.SOUND, true)
        if (intent.hasExtra(MainActivity.MUSIC)) mMusic = intent.getBooleanExtra(MainActivity.MUSIC, true)

        findViewById<View>(R.id.btn_back_gameplay).setOnClickListener { view: View ->
            view.startAnimation(mAnimation)
            gameOver()
            finish()
        }

//        fun BalloonLauncher() {
//        GlobalScope.launch {
//            val dispatcher = this.coroutineContext
//            CoroutineScope(dispatcher).launch {
//
//                val minDelay = MIN_ANIMATION_DELAY.coerceAtLeast(MAX_ANIMATION_DELAY - ((0) - 1) * 500) / 2
//
////                val minDelay = Math.max(
////                    MIN_ANIMATION_DELAY,
////                    MAX_ANIMATION_DELAY - ((p0[0]?.minus(1))?.times(500)!!)
////                ) / 2
//
//                var balloonsLaunched = 0
//                while (mPlaying && balloonsLaunched < mBalloonsPerLevel) {
//                    val random = Random(Date().time)
//                    random.nextInt(mScreenWidth - 200)
//                    balloonsLaunched++
//                    try {
//                        Thread.sleep((random.nextInt(minDelay) + minDelay).toLong())
//                    } catch (e: InterruptedException) {
//                        e.printStackTrace()
//                    }
//                }
//
//                launchBalloon(0)
//
//            }
//            }
//        }

    }


    private fun setToFullScreen() {
        findViewById<View>(R.id.activity_main).systemUiVisibility =
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

    override fun onRestart() {
        super.onRestart()
        if (mGame) {
            if (mMusic) mMusicHelper!!.playMusic()
        }
    }

    private fun startGame() {
        setToFullScreen()
        mScore = 0
        mLevel = 0
        mHeartsUsed = 0
        mGameStopped = false
        mGame = true
        if (mMusic) mMusicHelper!!.playMusic()
        for (pin in mHeartImages) pin.setImageResource(R.drawable.heart)
        startLevel()
    }

    private fun startLevel() {
        mLevel++
        updateDisplay()
        BalloonLauncher().run { mLevel }

        //BalloonLauncher().execute(mLevel)
        mPlaying = true
        mBalloonsPopped = 0
        binding.goButton.visibility = View.INVISIBLE
    }

    fun BalloonLauncher() {
        GlobalScope.launch {
            val dispatcher = this.coroutineContext
            CoroutineScope(dispatcher).launch {

                val minDelay = MIN_ANIMATION_DELAY.coerceAtLeast(MAX_ANIMATION_DELAY - ((0) - 1) * 500) / 2

//                val minDelay = Math.max(
//                    MIN_ANIMATION_DELAY,
//                    MAX_ANIMATION_DELAY - ((p0[0]?.minus(1))?.times(500)!!)
//                ) / 2

                var balloonsLaunched = 0
                while (mPlaying && balloonsLaunched < mBalloonsPerLevel) {
                    val random = Random(Date().time)
                    random.nextInt(mScreenWidth - 200)
                    balloonsLaunched++
                    try {
                        Thread.sleep((random.nextInt(minDelay) + minDelay).toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                launchBalloon(0)

            }
        }
    }

    private fun finishLevel() {
        Toast.makeText(this, getString(R.string.finish_level) + mLevel, Toast.LENGTH_SHORT).show()
        mPlaying = false
        binding.goButton.text = MessageFormat.format("{0} {1}", getString(R.string.level_start), mLevel + 1)
        binding.goButton.visibility = View.VISIBLE
    }

    fun goButtonClickHandler(view: View?) {
        if (mGameStopped) startGame() else startLevel()
    }

    override fun popBalloon(balloon: Balloon, userTouch: Boolean) {
        mBalloonsPopped++
        if (mSound) mSoundHelper!!.playSound()

        mContentView!!.removeView(balloon)
        mBalloons.remove(balloon)
        if (userTouch) mScore++ else {
            mHeartsUsed++
            if (mHeartsUsed <= mHeartImages.size) mHeartImages[mHeartsUsed - 1].setImageResource(R.drawable.broken_heart)
            if (mHeartsUsed == NUMBER_OF_HEARTS) {
                gameOver()
                return
            }
        }
        updateDisplay()
        if (mBalloonsPopped == mBalloonsPerLevel) {
            finishLevel()
            mBalloonsPerLevel += 10
        }
    }

    private fun gameOver() {
        Toast.makeText(this, R.string.game_over, Toast.LENGTH_SHORT).show()
        if (mMusic) mMusicHelper!!.pauseMusic()
        mGame = false

        for (balloon in mBalloons) {
            mContentView!!.removeView(balloon)
            balloon.setPopped(true)
        }
        mBalloons.clear()
        mPlaying = false
        mGameStopped = true

        binding.goButton.setText(R.string.start_game)
        binding.goButton.visibility =View.VISIBLE

        //mGoButton!!.setText(R.string.start_game)
        //mGoButton!!.visibility = View.VISIBLE
    }

    private fun updateDisplay() {
        binding.scoreDisplay.text = mScore.toString()
        binding.levelDisplay.text = mLevel.toString()

        //mScoreDisplay!!.text = mScore.toString()
        //mLevelDisplay!!.text = mLevel.toString()
    }

     private fun launchBalloon(x: Int) {
        val balloon = Balloon(this, mBalloonColors[mRandom.nextInt(mBalloonColors.size)], 150)
        mBalloons.add(balloon)
        balloon.x = x.toFloat()
        balloon.y = (mScreenHeight + balloon.height).toFloat()

        //binding.c
        mContentView!!.addView(balloon)
        balloon.releaseBalloon(
            mScreenHeight,
            MIN_ANIMATION_DURATION.coerceAtLeast(MAX_ANIMATION_DURATION - mLevel * 1000)
        )
    }

    override fun onPause() {
        super.onPause()
        if (mGame) {
            if (mMusic) mMusicHelper!!.pauseMusic()
        }
    }

    /**
     * This class is responsible for calculating speed of balloons and x axis position of the balloon
     * @see AsyncTask
     */

//    // выполнение в одном потоке исполнителей
//    val result = async (Executors.newSingleThreadExecutor(). asCoroutineDispatcher ()) { doInBackground(*params) }
//    // выполнить в фиксированном пуле потоков Executors. В настоящее время для этой задачи используется 3 потока
//    val result = async (Executors.newFixedThreadPool(3). asCoroutineDispatcher ()) { doInBackground(*params) }


//    fun BalloonLauncher() = runBlocking { // this: CoroutineScope
//        launch {
//            doWorld()
//            launchBalloon(0)
//        }
//        println("Hello")
//    }
//
//    // this is your first suspending function
//    private suspend fun doWorld(vararg p0: Int?): Void? {
//        //if (p0.size != 1) throw AssertionError(getString(R.string.assertion_message))
//        val minDelay = Math.max(MIN_ANIMATION_DELAY, MAX_ANIMATION_DELAY - ((p0[0]?.minus(1))?.times(500)!!)) / 2
//        var balloonsLaunched = 0
//
//        val mBalloonsPerLevel = 10
//        val mScreenWidth = 0
//        val  mPlaying = true
//
//        while (mPlaying && balloonsLaunched < mBalloonsPerLevel) {
//            val random = Random(Date().time)
//            random.nextInt(mScreenWidth - 200)
//            balloonsLaunched++
//            try {
//                withContext(Dispatchers.IO) {
//                    Thread.sleep((random.nextInt(minDelay) + minDelay).toLong())
//                }
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
//        return null
//    }


//    private class BalloonLaunchCorutines : GameplayActivity(){
//        GlobalS
//    }

//    private class BalloonLauncher : AsyncTask<Int?, Int?, Void?>() {
//        override fun doInBackground(vararg p0: Int?): Void? {
//            //if (p0.size != 1) throw AssertionError(getString(R.string.assertion_message))
//            val minDelay = Math.max(
//                MIN_ANIMATION_DELAY,
//                MAX_ANIMATION_DELAY - ((p0[0]?.minus(1))?.times(500)!!)
//            ) / 2
//            var balloonsLaunched = 0
//
//
//            //val mBalloonsPerLevel = 10
//            //val mScreenWidth = 0
//            //val  mPlaying = true
//
//            while (mPlaying && balloonsLaunched < mBalloonsPerLevel) {
//                val random = Random(Date().time)
//                publishProgress(random.nextInt(mScreenWidth - 200))
//                balloonsLaunched++
//                try {
//                    Thread.sleep((random.nextInt(minDelay) + minDelay).toLong())
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//            return null
//        }

//        @Deprecated("Deprecated in Java")
//        override fun onProgressUpdate(vararg values: Int?) {
//            super.onProgressUpdate(*values)
////            launchBalloon(values[0])
//
//            values[0]?.let { launchBalloon(it) }
//        }



    companion object {
        private const val MIN_ANIMATION_DELAY = 500
        private const val MAX_ANIMATION_DELAY = 1500
        private const val MIN_ANIMATION_DURATION = 1000
        private const val MAX_ANIMATION_DURATION = 6000
        private const val NUMBER_OF_HEARTS = 5
    }

}

