package com.dep.destroypop.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.dep.destroypop.UFOs;
import com.dep.destroypop.R;
import com.dep.destroypop.utils.SoundUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements UFOs.UFOsListener {

    private static final int
    MIN_ANIMATION_DELAY = 500,
    MAX_ANIMATION_DELAY = 1500,
    MIN_ANIMATION_DURATION = 1000,
    MAX_ANIMATION_DURATION = 6000,
    NUMBER_OF_HEARTS = 5;

    private final Random mRandom = new Random();
    private final int[] mBalloonColors = {
            Color.YELLOW,
            Color.RED,
            Color.WHITE,
            Color.MAGENTA,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE
    };

    private int
            mUFOPerLevel = 10,
            mUFOPopped,
            mScreenWidth,
            mScreenHeight,
            mLevel,
            mScore,
            mHeartsUsed;

    private boolean
            mPlaying,
            mSound,
            mMusic,
            mGame,
            mGameStopped = true;


    private TextView mScoreDisplay, mLevelDisplay;
    private Button mGoButton;
    private ViewGroup mContentView;
    private SoundUtils mSoundUtils, mMusicHelper;

    private final List<ImageView> mHeartImages = new ArrayList<>();
    private final List<UFOs> mUFOs = new ArrayList<>();
    private Animation mAnimation;

    @SuppressLint("FindViewByIdCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().setBackgroundDrawableResource(R.drawable.background);

        mContentView = findViewById(R.id.activity_main);
        setToFullScreen();

        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        mMusicHelper = new SoundUtils(this);
        mMusicHelper.prepareMusicPlayer(this);
        Intent intent = getIntent();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mContentView.getWidth();
                    mScreenHeight = mContentView.getHeight();
                }
            });
        }

        mContentView.setOnClickListener(view -> setToFullScreen());

        mScoreDisplay = findViewById(R.id.score_display);
        mLevelDisplay = findViewById(R.id.level_display);

        mHeartImages.add(findViewById(R.id.heart1));
        mHeartImages.add(findViewById(R.id.heart2));
        mHeartImages.add(findViewById(R.id.heart3));
        mHeartImages.add(findViewById(R.id.heart4));
        mHeartImages.add(findViewById(R.id.heart5));

        mGoButton = findViewById(R.id.go_button);
        updateDisplay();

        mSoundUtils = new SoundUtils(this);
        mSoundUtils.prepareMusicPlayer(this);

        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        mAnimation.setDuration(100);

        if (intent.hasExtra(MenuActivity.SOUND))
            mSound = intent.getBooleanExtra(MenuActivity.SOUND, true);

        if (intent.hasExtra(MenuActivity.MUSIC))
            mMusic = intent.getBooleanExtra(MenuActivity.MUSIC, true);

        findViewById(R.id.btn_back_gameplay).setOnClickListener(view -> {
            view.startAnimation(mAnimation);
            gameOver();
            finish();
        });
    }

    private void setToFullScreen() {
        findViewById(R.id.activity_main).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mGame) {
            if (mMusic) mMusicHelper.playMusic();
        }
    }

    private void startGame() {
        setToFullScreen();

        mScore = 0;
        mLevel = 0;
        mHeartsUsed = 0;
        mGameStopped = false;
        mGame = true;
        if (mMusic) mMusicHelper.playMusic();
        for (ImageView pin : mHeartImages) pin.setImageResource(R.drawable.heart);
        startLevel();
    }

    private void startLevel() {
        mLevel++;

        updateDisplay();
        new UFOsLauncher().execute(mLevel);
        mPlaying = true;
        mUFOPopped = 0;
        mGoButton.setVisibility(View.INVISIBLE);
    }

    private void finishLevel() {
        Toast.makeText(this, getString(R.string.finish_level) + mLevel, Toast.LENGTH_SHORT).show();
        mPlaying = false;
        mGoButton.setText(MessageFormat.format("{0} {1}", getString(R.string.level_start), mLevel + 1));
        mGoButton.setVisibility(View.VISIBLE);
    }


    public void goButtonClickHandler(View view) {
        if (mGameStopped) startGame();
        else startLevel();
    }


    @Override
    public void popUFO(@NonNull UFOs UFOs, boolean userTouch) {
        mUFOPopped++;
        if (mSound) mSoundUtils.playSound();
        mContentView.removeView(UFOs);
        mUFOs.remove(UFOs);
        if (userTouch) mScore++;
        else {
            mHeartsUsed++;
            if (mHeartsUsed <= mHeartImages.size())
                mHeartImages.get(mHeartsUsed - 1).setImageResource(R.drawable.heart_broken);
            if (mHeartsUsed == NUMBER_OF_HEARTS) {
                gameOver();
                return;
            }
        }
        updateDisplay();

        if (mUFOPopped == mUFOPerLevel) {
            finishLevel();
            mUFOPerLevel += 10;
        }
    }


    private void gameOver() {
        Toast.makeText(this, R.string.game_over, Toast.LENGTH_SHORT).show();
        if (mMusic) mMusicHelper.pauseMusic();
        mGame = false;

        for (UFOs UFOs : mUFOs) {
            mContentView.removeView(UFOs);
            UFOs.setPopped(true);
        }

        mUFOs.clear();
        mPlaying = false;
        mGameStopped = true;
        mGoButton.setText(R.string.start_game);

        mGoButton.setVisibility(View.VISIBLE);
    }


    private void updateDisplay() {
        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));
    }


    private void launchUFOs(int x) {
        UFOs UFOs = new UFOs(this, mBalloonColors[mRandom.nextInt(mBalloonColors.length)], 150);
        mUFOs.add(UFOs);
        UFOs.setX(x);
        UFOs.setY(mScreenHeight + UFOs.getHeight());
        mContentView.addView(UFOs);
        UFOs.releaseUFOs(mScreenHeight, Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000)));
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mGame) {
            if (mMusic) mMusicHelper.pauseMusic();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class UFOsLauncher extends AsyncTask<Integer, Integer, Void> {

        @Nullable
        @Override
        protected Void doInBackground(@NonNull Integer... params) {
            if (params.length != 1) throw new AssertionError(getString(R.string.assertion_message));
            int minDelay = Math.max(MIN_ANIMATION_DELAY, (MAX_ANIMATION_DELAY - ((params[0] - 1) * 500))) / 2;
            int balloonsLaunched = 0;

            while (mPlaying && balloonsLaunched < mUFOPerLevel) {
                Random random = new Random(new Date().getTime());
                publishProgress(random.nextInt(mScreenWidth - 200));
                balloonsLaunched++;

                try {
                    Thread.sleep(random.nextInt(minDelay) + minDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            launchUFOs(values[0]);
        }
    }
}