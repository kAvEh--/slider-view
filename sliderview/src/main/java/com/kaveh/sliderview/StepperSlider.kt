package com.kaveh.sliderview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class StepperSlider @JvmOverloads constructor(
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
    private var mIndicatorColor = Color.parseColor("#FFFFFF")
    private var mEffectColor = Color.parseColor("#F43636")
    private val mBallPaint = Paint()
    private var stripesWidth = 0F
    private var ballIndicator = false
    private var indicatorPX: Float = 0F
    private var indicatorPY: Float = 0F
    private var mPath = Path()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    private fun createObjects() {
        indicatorPY = height / 2F
        mIndicatorRadius = height * .5F
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
        mPaintProgress.color = mBorderColor
        mPaintIndicator.style = Paint.Style.STROKE
        mPaintIndicator.strokeWidth = 14F
        mPaintIndicator.strokeCap = Paint.Cap.ROUND
        mPaintIndicator.isAntiAlias = true
        mPaintIndicator.color = mIndicatorColor
        mPaintIndicator.setShadowLayer(mIndicatorRadius / 3, 0.0f, 7.0f, Color.GRAY)
        mBallPaint.style = Paint.Style.STROKE
        mBallPaint.strokeCap = Paint.Cap.ROUND
        mBallPaint.strokeWidth = 1F
        mBallPaint.isAntiAlias = true
        mBallPaint.color = Color.MAGENTA
        mClickEffectPaint.style = Paint.Style.FILL
        mClickEffectPaint.strokeWidth = 5.0F
        mClickEffectPaint.strokeCap = Paint.Cap.ROUND
        mClickEffectPaint.isAntiAlias = true
        mClickEffectPaint.color = mEffectColor

        mPath.reset()
        mPath.moveTo(mIndicatorRadius, height * .4F)
        mPath.lineTo(width * .95F, height * .2F)
        mPath.arcTo(width * .93F, height * .2F, width * .97F, height * .8F, -90F, 180F, false)
        mPath.lineTo(mIndicatorRadius, height * .6F)
        mPath.arcTo(mIndicatorRadius * .8f, height * .4F, mIndicatorRadius * 1.2f, height * .6F, 90F, 180F, false)

//        val bgDrawable: LayerDrawable = ContextCompat.getDrawable(context, R.drawable.progress_bg) as LayerDrawable
//        val bg = bgDrawable.findDrawableByLayerId(R.id.barBg) as GradientDrawable?
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            bg?.setTint(mBackgroundColor)
//        }
//        val border = bgDrawable.findDrawableByLayerId(R.id.borderBg) as GradientDrawable?
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            border?.setTint(mBorderColor)
//        }
//        this.background = bgDrawable
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mClickEffectPaint)
        if (::mBitmap.isInitialized)
        //TODO check bitmap
            canvas.drawBitmap(mBitmap, progress * width - mBitmap.width / 2, height / 2F - mBitmap.height / 2, mPaintIndicator)
        else {
            indicatorPX = progress * (width - 2 * mIndicatorRadius) + mIndicatorRadius
            canvas.drawCircle(indicatorPX, indicatorPY, mIndicatorRadius * .45f, mPaintIndicator)
        }
    }

    private fun getDistance(x: Float): Float {
        ballIndicator = (x / stripesWidth).toInt() % 2 == 0
        return (x - ((x / stripesWidth).toInt() * stripesWidth))
    }

    private fun isTouchedNear(x: Float) {
        if (Math.abs(indicatorPX - x) < mIndicatorRadius + 5) {
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