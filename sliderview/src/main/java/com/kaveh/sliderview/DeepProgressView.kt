package com.kaveh.sliderview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.abs

class DeepProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnCustomEventListener {
        fun onChanged(progress: Float)
    }

    private var oval = RectF()
    private val mPaintBg = Paint()
    private val mPaintBgShadow = Paint()
    private val mPaintProgress = Paint()
    private val mPaintIndicator = Paint()
    private val mClickEffectPaint = Paint()
    private var mEffectRadius = 0F
    private var strokeWidth = 40F
    private var mIndicatorRadius = 26F
    private var mRectF = RectF()
    private var mProgress = 0F
    private var isTouched = false
    private var isRotateEffectEnabled = false
    private lateinit var mBitmap: Bitmap
    private lateinit var mListener: OnCustomEventListener
    private var mBackgroundColor = Color.parseColor("#c1c1c1")
    private var mBorderColor = Color.parseColor("#c1c1c1")
    private var mIndicatorColor = Color.parseColor("#03DAC5")
    private var mEffectColor = Color.parseColor("#F43636")
    private var mProgressColor = Color.parseColor("#6200EE")
    private val mBallPaint = Paint()
    private var stripesWidth = 0F
    private var ballIndicator = false
    private var indicatorPX: Float = 0F
    private var indicatorPY: Float = 0F
    private var progressLayer: ClipDrawable? = null
    private var startSpace = 1

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        stripesWidth = width / 45F
        createObjects()
    }

    /**
     * set progress.
     *
     * @param progress Should be 0 ~ 1.
     */
    private var progress: Float
        get() = mProgress
        set(progress) {
            if (mProgress != progress && (progress >= 0 || progress <= 1)) {
                progressLayer?.level = when {
                    progress == 0F -> 0
                    progress == 1F -> 10000
                    else -> (progress * (10000 - 2 * startSpace) + startSpace).toInt()
                }
                mProgress = progress
                if (::mListener.isInitialized) {
                    mListener.onChanged(mProgress)
                }
                invalidate()
            }
        }

    var rotateEffectEnabled: Boolean
        get() = isRotateEffectEnabled
        set(value) {
            isRotateEffectEnabled = value
        }

    var effectRadius: Float
        get() = mEffectRadius
        set(radius) {
            if (mEffectRadius != radius) {
                mEffectRadius = radius
                invalidate()
            }
        }

    var barRadius: Float
        get() = strokeWidth
        set(value) {
            strokeWidth = value
        }

    var bgColor: Int
        get() = mBackgroundColor
        set(color) {
            mBackgroundColor = color
            invalidate()
        }

    var accentColor: Int
        get() = mBorderColor
        set(color) {
            mBorderColor = color
            invalidate()
        }

    var indicatorColor: Int
        get() = mIndicatorColor
        set(color) {
            mIndicatorColor = color
            invalidate()
        }

    var effectColor: Int
        get() = mEffectColor
        set(color) {
            mEffectColor = color
            invalidate()
        }

    fun setIndicatorBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
    }

    fun setOnChangeListener(listener: OnCustomEventListener) {
        mListener = listener
    }

    @SuppressLint("ResourceAsColor")
    private fun createObjects() {
        indicatorPY = height / 2F
        mIndicatorRadius = height * .5F
        startSpace = (mIndicatorRadius / width * 10000).toInt()
        oval.left = strokeWidth
        oval.top = strokeWidth
        oval.right = width.toFloat() - strokeWidth
        oval.bottom = height * 2F - strokeWidth
        mPaintBg.style = Paint.Style.STROKE
        mPaintBg.strokeWidth = 25.0F
        mPaintBg.strokeCap = Paint.Cap.ROUND
        mPaintBg.isAntiAlias = true
        mPaintBg.color = mBackgroundColor
        mPaintBgShadow.strokeWidth = 1F
        mPaintBgShadow.color = Color.TRANSPARENT
        mPaintBgShadow.setShadowLayer(10.0f, 0f, 0f, Color.BLACK)
        mPaintProgress.style = Paint.Style.STROKE
        mPaintProgress.strokeWidth = 3.0F
        mPaintProgress.strokeCap = Paint.Cap.ROUND
        mPaintProgress.isAntiAlias = true
        mPaintProgress.color = mProgressColor
        mPaintIndicator.style = Paint.Style.FILL
        mPaintIndicator.strokeCap = Paint.Cap.ROUND
        mPaintIndicator.isAntiAlias = true
        mPaintIndicator.color = mIndicatorColor
        mPaintIndicator.setShadowLayer(mIndicatorRadius / 3, 0.0f, 0.0f, Color.BLACK)
        mBallPaint.style = Paint.Style.STROKE
        mBallPaint.strokeCap = Paint.Cap.ROUND
        mBallPaint.strokeWidth = 1F
        mBallPaint.isAntiAlias = true
        mBallPaint.color = Color.MAGENTA
        mClickEffectPaint.style = Paint.Style.STROKE
        mClickEffectPaint.strokeWidth = 5.0F
        mClickEffectPaint.strokeCap = Paint.Cap.ROUND
        mClickEffectPaint.isAntiAlias = true
        mClickEffectPaint.color = mEffectColor

        val bgDrawable: LayerDrawable = ContextCompat.getDrawable(context, R.drawable.progress_bg) as LayerDrawable
        val bg = bgDrawable.findDrawableByLayerId(R.id.barBg) as GradientDrawable?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bg?.setTint(mBackgroundColor)
        }
        val border = bgDrawable.findDrawableByLayerId(R.id.borderBg) as GradientDrawable?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            border?.setTint(mBorderColor)
        }
        progressLayer = bgDrawable.findDrawableByLayerId(R.id.progress) as ClipDrawable?

        this.background = bgDrawable
    }

    override fun onDraw(canvas: Canvas) {
        if (::mBitmap.isInitialized)
        //TODO check bitmap
            canvas.drawBitmap(mBitmap, progress * width - mBitmap.width / 2, height / 2F - mBitmap.height / 2, mPaintIndicator)
        else {
            indicatorPX = progress * (width - 2 * mIndicatorRadius) + mIndicatorRadius
            canvas.drawCircle(indicatorPX, indicatorPY, mIndicatorRadius * .88f, mPaintIndicator)
        }
        if (rotateEffectEnabled) {
            var tt = getDistance(progress * width + mIndicatorRadius)
            mRectF.set(progress * width, 0F, progress * width + mIndicatorRadius, height.toFloat())
            while (tt < mIndicatorRadius * 2) {
                if (tt > mIndicatorRadius) {
                    mRectF.set(progress * width + mIndicatorRadius - tt, 0F, progress * width - (mIndicatorRadius - tt), height.toFloat())
                    canvas.drawArc(mRectF, 90F, 180F, true, mBallPaint)
                } else {
                    mRectF.set(progress * width - (mIndicatorRadius - tt), 0F, progress * width + mIndicatorRadius - tt, height.toFloat())
                    canvas.drawArc(mRectF, 90F, -180F, true, mBallPaint)
                }
                tt += stripesWidth
            }
        }
    }

    private fun getDistance(x: Float): Float {
        ballIndicator = (x / stripesWidth).toInt() % 2 == 0
        return (x - ((x / stripesWidth).toInt() * stripesWidth))
    }

    private fun isTouchedNear(x: Float) {
        if (abs(indicatorPX - x) < mIndicatorRadius + 5) {
            isTouched = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouchedNear(event.x)
                if (isRotateEffectEnabled)
                    startAnimation()
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouched) {
                    progress = when {
                        event.x < mIndicatorRadius -> 0F
                        event.x > width - mIndicatorRadius -> 1F
                        else -> (event.x - mIndicatorRadius) / (width - 2 * mIndicatorRadius)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isTouched) {
                    moveWithAnimation(when {
                        event.x < mIndicatorRadius -> 0F
                        event.x > width - mIndicatorRadius -> 1F
                        else -> (event.x - mIndicatorRadius) / (width - 2 * mIndicatorRadius)
                    })
                }
                isTouched = false
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }
            else -> {
            }
        }
        return super.onTouchEvent(event)
    }

    private fun moveWithAnimation(newProgress: Float) {
        val tmp = progress
        val moveAnim = ObjectAnimator.ofFloat(
                this, "progress", tmp, newProgress)
        moveAnim.repeatCount = 0
        moveAnim.duration = 80
        moveAnim.interpolator = AccelerateInterpolator()
        moveAnim.start()
    }

    private fun startAnimation() {
        val clickEffectAnim = ObjectAnimator.ofFloat(
                this, "effectRadius", mIndicatorRadius, mIndicatorRadius + 20)
        clickEffectAnim.repeatCount = 1
        clickEffectAnim.repeatMode = ValueAnimator.REVERSE
        clickEffectAnim.duration = 150
        clickEffectAnim.interpolator = LinearInterpolator()
        clickEffectAnim.start()
        clickEffectAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mEffectRadius = 0f
                clickEffectAnim.removeAllListeners()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }
}