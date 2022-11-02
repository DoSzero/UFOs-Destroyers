package com.dep.destroypop

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.dep.destroypop.utils.PixelHelperUtils

@SuppressLint("AppCompatCustomView")
class UFOs : ImageView, AnimatorUpdateListener, Animator.AnimatorListener {

    private var mAnimator: ValueAnimator? = null
    private var mListener: UFOsListener? = null
    private var mPopped = false

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, color: Int, rawHeight: Int) : super(context) {
        mListener = context as UFOsListener

        setImageResource(R.drawable.ufo_83974)
        this.setColorFilter(color)

        layoutParams = ViewGroup.LayoutParams(
            PixelHelperUtils.pixelsToDp(rawHeight / 2, context),
            PixelHelperUtils.pixelsToDp(rawHeight, context)
        )
    }

    fun releaseUFOs(screenHeight: Int, duration: Int) {
        mAnimator = ValueAnimator()
        mAnimator!!.duration = duration.toLong()
        mAnimator!!.setFloatValues(screenHeight.toFloat(), 0f)
        mAnimator!!.interpolator = LinearInterpolator()
        mAnimator!!.setTarget(this)
        mAnimator!!.addListener(this)
        mAnimator!!.addUpdateListener(this)
        mAnimator!!.start()
    }

    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        y = valueAnimator.animatedValue as Float
    }

    override fun onAnimationStart(animator: Animator) {}
    override fun onAnimationEnd(animator: Animator) {
        if (!mPopped) mListener!!.popUFO(this, false)
    }

    override fun onAnimationCancel(animator: Animator) {}
    override fun onAnimationRepeat(animator: Animator) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mPopped && event.action == MotionEvent.ACTION_DOWN) {
            mListener!!.popUFO(this, true)
            mPopped = true
            mAnimator!!.cancel()
        }
        return super.onTouchEvent(event)
    }

    fun setPopped(isBalloonPopped: Boolean) {
        mPopped = isBalloonPopped
        if (isBalloonPopped) mAnimator!!.cancel()
    }

    interface UFOsListener {
        fun popUFO(
            UFOs: UFOs,
            userTouch: Boolean
        )
    }
}