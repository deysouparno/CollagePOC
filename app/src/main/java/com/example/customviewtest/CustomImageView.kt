package com.example.customviewtest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView


class ZoomImageView : ShapeableImageView, View.OnTouchListener,
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    //shared constructing
    private var mContext: Context? = null
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null

    var mMatrix: Matrix? = null
    private var mMatrixValues: FloatArray? = null
    var mode = NONE

    // Scales
    var mSaveScale = 1f
    var mMinScale = 1f
    var mMaxScale = 4f

    // view dimensions
    var origWidth = 0f
    var origHeight = 0f
    var viewWidth = 0
    var viewHeight = 0
    private var mLast = PointF()
    private var mStart = PointF()

    constructor(context: Context) : super(context) {
        sharedConstructing(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        sharedConstructing(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    )

    private fun sharedConstructing(context: Context) {
        super.setClickable(true)
        mContext = context
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mMatrix = Matrix()
        mMatrixValues = FloatArray(9)
        imageMatrix = mMatrix
        scaleType = ScaleType.MATRIX
        mGestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            this@ZoomImageView.isLongClickable = false
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = detector.scaleFactor
            val prevScale = mSaveScale
            mSaveScale *= mScaleFactor
            if (mSaveScale > mMaxScale) {
                mSaveScale = mMaxScale
                mScaleFactor = mMaxScale / prevScale
            } else if (mSaveScale < mMinScale) {
                mSaveScale = mMinScale
                mScaleFactor = mMinScale / prevScale
            }
            if (origWidth * mSaveScale <= viewWidth
                || origHeight * mSaveScale <= viewHeight
            ) {
                mMatrix!!.postScale(
                    mScaleFactor, mScaleFactor, viewWidth / 2.toFloat(),
                    viewHeight / 2.toFloat()
                )
            } else {
                mMatrix!!.postScale(
                    mScaleFactor, mScaleFactor,
                    detector.focusX, detector.focusY
                )
            }
            fixTranslation()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            super.onScaleEnd(detector)
            this@ZoomImageView.isLongClickable = true
        }
    }

    private fun fitToScreen() {
        mSaveScale = 1f
        val scale: Float
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) return
        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        val scaleX = viewWidth.toFloat() / imageWidth.toFloat()
        val scaleY = viewHeight.toFloat() / imageHeight.toFloat()
        scale = Math.max(scaleX, scaleY)
        mMatrix!!.setScale(scale, scale)

        // Center the image
        var redundantYSpace = (viewHeight.toFloat()
                - scale * imageHeight.toFloat())
        var redundantXSpace = (viewWidth.toFloat()
                - scale * imageWidth.toFloat())
        redundantYSpace /= 2.toFloat()
        redundantXSpace /= 2.toFloat()
        mMatrix!!.postTranslate(redundantXSpace, redundantYSpace)
        origWidth = viewWidth - 2 * redundantXSpace
        origHeight = viewHeight - 2 * redundantYSpace
        imageMatrix = mMatrix
    }

    fun fixTranslation() {
        mMatrix!!.getValues(mMatrixValues) //put matrix values into a float array so we can analyze
        val transX =
            mMatrixValues!![Matrix.MTRANS_X] //get the most recent translation in x direction
        val transY =
            mMatrixValues!![Matrix.MTRANS_Y] //get the most recent translation in y direction
        val fixTransX = getFixTranslation(transX, viewWidth.toFloat(), origWidth * mSaveScale)
        val fixTransY = getFixTranslation(transY, viewHeight.toFloat(), origHeight * mSaveScale)
        if (fixTransX != 0f || fixTransY != 0f) mMatrix!!.postTranslate(fixTransX, fixTransY)
    }

    private fun getFixTranslation(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) { // case: NOT ZOOMED
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else { //CASE: ZOOMED
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        if (trans < minTrans) { // negative x or y translation (down or to the right)
            return -trans + minTrans
        }
        if (trans > maxTrans) { // positive x or y translation (up or to the left)
            return -trans + maxTrans
        }
        return 0F
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0F
        } else delta
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (mSaveScale == 1f) {

            fitToScreen()
        }
    }

    /*
        Ontouch
     */
    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        mScaleDetector!!.onTouchEvent(event)
        mGestureDetector!!.onTouchEvent(event)

        val currentPoint = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLast.set(currentPoint)
                mStart.set(mLast)
                mode = DRAG
                isLongClickable = true
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                val dx = currentPoint.x - mLast.x
                val dy = currentPoint.y - mLast.y
                if (dx > 5 || dy > 5) isLongClickable = false
                val fixTransX = getFixDragTrans(dx, viewWidth.toFloat(), origWidth * mSaveScale)
                val fixTransY = getFixDragTrans(dy, viewHeight.toFloat(), origHeight * mSaveScale)
                mMatrix!!.postTranslate(fixTransX, fixTransY)
                fixTranslation()
                mLast[currentPoint.x] = currentPoint.y

            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
            }

        }
        imageMatrix = mMatrix
        return false
    }

    /*
        GestureListener
     */
    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    /*
        onDoubleTap
     */
    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        fitToScreen()
        return false
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        return false
    }

    companion object {

        // Image States
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }
}

class CustomImageView(context: Context) : ShapeableImageView(context),
    GestureDetector.OnGestureListener, View.OnTouchListener {

    private var xDown = 0f;
    private var yDown = 0f
    private var mScaleFactor = 1.0f

    private var mContext: Context? = null
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null

    var mMatrix: Matrix? = null
    private var mMatrixValues: FloatArray? = null
    var mode = ZoomImageView.NONE

    // Scales
    var mSaveScale = 1f
    var mMinScale = 1f
    var mMaxScale = 4f
    var origWidth = 0f
    var origHeight = 0f

    private var mLast = PointF()
    private var mStart = PointF()

//    override fun onScale(p0: ScaleGestureDetector?): Boolean {
//        mScaleFactor *= p0?.scaleFactor ?: 1f
//        mScaleFactor = Math.max(
//            0.1f,
//            Math.min(mScaleFactor, 2f)
//        )
//        this.scaleX = mScaleFactor
//        this.scaleY = mScaleFactor
//        return true
//    }
//
//    override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
//        return false
//    }
//
//    override fun onScaleEnd(p0: ScaleGestureDetector?) {
//    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = detector.scaleFactor
            val prevScale = mSaveScale
            mSaveScale *= mScaleFactor
            if (mSaveScale > mMaxScale) {
                mSaveScale = mMaxScale
                mScaleFactor = mMaxScale / prevScale
            } else if (mSaveScale < mMinScale) {
                mSaveScale = mMinScale
                mScaleFactor = mMinScale / prevScale
            }

            (this@CustomImageView.layoutParams as ConstraintLayout.LayoutParams).apply {
                width = (width * mScaleFactor).toInt()
                height = (height * mScaleFactor).toInt()
            }

            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            super.onScaleEnd(detector)
        }
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {

        mScaleDetector!!.onTouchEvent(motionEvent)
        mGestureDetector!!.onTouchEvent(motionEvent)

        if (view == null || motionEvent == null) {
            return false
        }
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                xDown = motionEvent.x
                yDown = motionEvent.y
                this.isClickable = false
            }

            MotionEvent.ACTION_MOVE -> {
                val diffX = motionEvent.x - xDown
                val diffY = motionEvent.y - yDown
                view.x = view.x + diffX
                view.y = view.y + diffY

                if (diffX > width / 2 || diffY > height / 2) {
                    this.performLongClick()
                }

            }

            MotionEvent.ACTION_UP -> {
                this.isClickable = true
            }
        }

        return false
    }


    init {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mMatrix = Matrix()
        mMatrixValues = FloatArray(9)
        imageMatrix = mMatrix
        mGestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)
    }

}

class ImageViewContainer(context: Context): MaterialCardView(context) {
    private var layout: ConstraintLayout
    lateinit var imageview: AppCompatImageView

    init {
        this.layout = ConstraintLayout(context)
        this.addView(layout)
        this.layout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.cardElevation = 0f

    }


    @SuppressLint("ClickableViewAccessibility")
    fun setUpImageView() {
        this.layout.addView(imageview)
        this.imageview.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    }

}

class FreestyleImageView(context: Context) : AppCompatImageView(context) {
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f

    private var saveX = 0f
    private var saveY = 0f

    private var isScalling = false

    init {
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        this.scaleType = ScaleType.FIT_CENTER
        setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                mScaleGestureDetector?.onTouchEvent(event);
                if (event?.pointerCount == 2 ) return false
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        saveX = event.x
                        saveY = event.y
                        this@FreestyleImageView.bringToFront()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val diffX = event.x - saveX
                        val diffY = event.y - saveY
                        this@FreestyleImageView.x = this@FreestyleImageView.x + diffX
                        this@FreestyleImageView.y = this@FreestyleImageView.y + diffY

                    }
                }

                return true;
            }
        })

//        setMask(ContextCompat.getDrawable(context, R.drawable.pip_bottle)!!)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (gainFocus) {
            this.setBackgroundColor(Color.parseColor("#66000000"))
        } else {
            this.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = 0.4f.coerceAtLeast(mScaleFactor.coerceAtMost(10.0f))
            scaleX = mScaleFactor
            scaleY = mScaleFactor
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            isScalling = true
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            isScalling = false
            super.onScaleEnd(detector)
        }
    }

    fun callSuperTouchEvent(event: MotionEvent) {
        super.onTouchEvent(event)
    }

}



