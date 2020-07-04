package com.kaveh.sliderview

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt

class StepperSlider @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnCustomEventListener {
        fun onChanged(progress: Float)
    }

    private val mPaintBg = Paint()
    private val mPaintProgress = Paint()
    private val mPaintIndicator = Paint()
    private val mPaintPoint = Paint()
    private var mIndicatorRadius = 26F
    private var mProgress = 0F
    private var isTouched = false
    private var stepperCount = 0
    private lateinit var mBitmap: Bitmap
    private lateinit var mListener: OnCustomEventListener
    private var mBackgroundColor = Color.parseColor("#c1c1c1")
    private var mBorderColor = Color.parseColor("#215123")
    private var mIndicatorColor = Color.parseColor("#FFFFFF")
    private var mFillPointColor = Color.parseColor("#FFFFFF")
    private var mEmptyPointColor = Color.parseColor("#000000")
    private val mBallPaint = Paint()
    private var stripesWidth = 0F
    private var ballIndicator = false
    private var indicatorPX: Float = 0F
    private var indicatorPY: Float = 0F
    private var mPath = Path()
    private var mProgressPath = Path()

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

    var stepper: Int
        get() = stepperCount
        set(value) {
            stepperCount = value
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

    var pointColorFill: Int
        get() = mFillPointColor
        set(color) {
            mFillPointColor = color
            invalidate()
        }

    var pointColorEmpty: Int
        get() = mEmptyPointColor
        set(color) {
            mEmptyPointColor = color
            invalidate()
        }

    fun setOnChangeListener(listener: OnCustomEventListener) {
        mListener = listener
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    private fun createObjects() {
        indicatorPY = height / 2F
        mIndicatorRadius = height * .5F
        mPaintBg.style = Paint.Style.FILL
        mPaintBg.strokeCap = Paint.Cap.ROUND
        mPaintBg.isAntiAlias = true
        mPaintBg.color = mBackgroundColor
        mPaintProgress.style = Paint.Style.FILL
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
        mPaintPoint.style = Paint.Style.FILL
        mPaintPoint.strokeCap = Paint.Cap.ROUND
        mPaintPoint.isAntiAlias = true

        mPath.reset()
        mPath.moveTo(mIndicatorRadius, height * .4F)
        mPath.lineTo(width * .95F, height * .24F)
        mPath.arcTo(width * .93F, height * .24F, width * .97F, height * .76F, -90F, 180F, false)
        mPath.lineTo(mIndicatorRadius, height * .6F)
        mPath.arcTo(mIndicatorRadius * .8f, height * .4F, mIndicatorRadius * 1.2f, height * .6F, 90F, 180F, false)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun editProgressPath() {
        mProgressPath.reset()
        mProgressPath.moveTo(mIndicatorRadius, height * .4F)
        mProgressPath.lineTo(progress * (width - 2 * mIndicatorRadius) + mIndicatorRadius, height * (.4F - progress * .16F))
        mProgressPath.arcTo(progress * (width - 2 * mIndicatorRadius) + mIndicatorRadius - width * .01F, height * (.4F - progress * .16F), progress * (width - 2 * mIndicatorRadius) + mIndicatorRadius + width * .01F, height * (.6F + progress * .16F), -90F, 180F, false)
        mProgressPath.lineTo(mIndicatorRadius, height * .6F)
        mProgressPath.arcTo(mIndicatorRadius * .8f, height * .4F, mIndicatorRadius * 1.2f, height * .6F, 90F, 180F, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaintBg)
        editProgressPath()
        canvas.drawPath(mProgressPath, mPaintProgress)

        indicatorPX = progress * (width - 2 * mIndicatorRadius) + mIndicatorRadius
        if (stepperCount > 0) {
            for (i in 1 until stepperCount) {
                if (i.toFloat() / stepperCount <= progress)
                    mPaintPoint.color = mFillPointColor
                else
                    mPaintPoint.color = mEmptyPointColor
                canvas.drawCircle((width - 2 * mIndicatorRadius) / (stepperCount) * i + mIndicatorRadius, height / 2F, 5F, mPaintPoint)
            }
        }
        canvas.drawCircle(indicatorPX, indicatorPY, mIndicatorRadius * .45f, mPaintIndicator)
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
                moveWithAnimation(when {
                    event.x < mIndicatorRadius -> 0F
                    event.x > width - mIndicatorRadius -> 1F
                    else -> (event.x - mIndicatorRadius) / (width - 2 * mIndicatorRadius)
                })
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
        var destination = newProgress
        if (stepperCount > 0) {
            val part = 1F / stepperCount
            destination = (newProgress / part).roundToInt() * part
        }
        val moveAnim = ObjectAnimator.ofFloat(
                this, "progress", tmp, destination)
        moveAnim.repeatCount = 0
        moveAnim.duration = 80
        moveAnim.interpolator = AccelerateInterpolator()
        moveAnim.start()
    }
}