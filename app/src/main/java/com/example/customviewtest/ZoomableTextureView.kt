package com.example.customviewtest

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import java.util.*


interface OnScaleEnd {
    fun onScaleEnded(scale: List<Float?>?)
}

class ZoomableTextureView : TextureView {
//    private var context: Context
    private var minScale = 1.0f
    private var maxScale = 5.0f
    private var saveScale = 1.0f
    private var mode = 0
//    private val matrix = Matrix()
    private var mScaleDetector: ScaleGestureDetector? = null
    private var scaleEnd: OnScaleEnd? = null
    private lateinit var m: FloatArray
    private val last = PointF()
    private val start = PointF()
    private var right = 0f
    private var bottom = 0f
    public lateinit var mediaPlayer : MediaPlayer
    fun setEndListener(listener: OnScaleEnd?) {
        scaleEnd = listener
    }

    fun adjustAspectRatio(videoWidth: Int, videoHeight: Int) {
        val viewWidth = this.width
        val viewHeight = this.height
        val aspectRatio = videoHeight.toDouble() / videoWidth
        val newWidth: Int
        val newHeight: Int
        if (viewHeight > (viewWidth * aspectRatio).toInt()) {
            // limited by narrow width; restrict height
            newWidth = viewWidth
            newHeight = (viewWidth * aspectRatio).toInt()
        } else {
            // limited by short height; restrict width
            newWidth = (viewHeight / aspectRatio).toInt()
            newHeight = viewHeight
        }
        val xoff = (viewWidth - newWidth) / 2
        val yoff = (viewHeight - newHeight) / 2
        Log.v(
            "ZoomableTextureView", "video=" + videoWidth + "x" + videoHeight +
                    " view=" + viewWidth + "x" + viewHeight +
                    " newView=" + newWidth + "x" + newHeight +
                    " off=" + xoff + "," + yoff
        )
        val txform = Matrix()
        getTransform(txform)
        txform.setScale(newWidth.toFloat() / viewWidth, newHeight.toFloat() / viewHeight)
        //txform.postRotate(10);          // just for fun
        txform.postTranslate(xoff.toFloat(), yoff.toFloat())
        setTransform(txform)
    }

    fun setMinScale(scale: Float) {
        if (scale >= 1.0f && scale <= maxScale) {
            minScale = scale
        } else {
            throw RuntimeException("minScale can't be lower than 1 or larger than maxScale(" + maxScale + ")")
        }
    }

    fun setMaxScale(scale: Float) {
        if (scale >= 1.0f && scale >= minScale) {
            minScale = scale
        } else {
            throw RuntimeException("maxScale can't be lower than 1 or minScale(" + minScale + ")")
        }
    }

    constructor(context: Context, resId: Int) : super(context) {
        initView(null as AttributeSet?)
        this.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {

                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer!!.setOnPreparedListener {
                    it.isLooping = true
                    it.start()
                    it.setSurface(Surface(surface))
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) = Unit

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit

        }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView(attrs)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putFloat("minScale", minScale)
        bundle.putFloat("maxScale", maxScale)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        var state: Parcelable? = state
        if (state is Bundle) {
            val bundle = state
            minScale = bundle.getInt("minScale").toFloat()
            minScale = bundle.getInt("maxScale").toFloat()
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }

    val coordinates: List<Float>
        get() = Arrays.asList(
            saveScale, m[2], m[5], right, bottom
        )

    private fun initView(attrs: AttributeSet?) {
//        val a = context.theme.obtainStyledAttributes(attrs, styleable.ZoomableTextureView, 0, 0)
//        try {
//            minScale = a.getFloat(styleable.ZoomableTextureView_minScale, minScale)
//            maxScale = a.getFloat(styleable.ZoomableTextureView_maxScale, maxScale)
//        } finally {
//            a.recycle()
//        }
        setOnTouchListener(ZoomOnTouchListeners())
    }

    private inner class ZoomOnTouchListeners : OnTouchListener {
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            mScaleDetector!!.onTouchEvent(motionEvent)
            matrix.getValues(m)
            val x = m[2]
            val y = m[5]
            val curr = PointF(motionEvent.x, motionEvent.y)
            when (motionEvent.actionMasked) {
                0 -> {
                    last[motionEvent.x] = motionEvent.y
                    start.set(last)
                    mode = 1
                }
                1 -> mode = 0
                2 -> if (mode == 2 || mode == 1 && saveScale > minScale) {
                    var deltaX = curr.x - last.x
                    var deltaY = curr.y - last.y
                    if (y + deltaY > 0.0f) {
                        deltaY = -y
                    } else if (y + deltaY < -bottom) {
                        deltaY = -(y + bottom)
                    }
                    if (x + deltaX > 0.0f) {
                        deltaX = -x
                    } else if (x + deltaX < -right) {
                        deltaX = -(x + right)
                    }
                    matrix.postTranslate(deltaX, deltaY)
                    last[curr.x] = curr.y
                }
                3, 4 -> {}
                5 -> {
                    last[motionEvent.x] = motionEvent.y
                    start.set(last)
                    mode = 2
                }
                6 -> mode = 0
                else -> {}
            }
            setTransform(matrix)
            this@ZoomableTextureView.invalidate()
            return true
        }

        inner class ScaleListener() :
            ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                mode = 2
                return true
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                var mScaleFactor = detector.scaleFactor
                val origScale = saveScale
                saveScale = saveScale * mScaleFactor
                if (saveScale > maxScale) {
                    saveScale = maxScale
                    mScaleFactor = maxScale / origScale
                } else if (saveScale < minScale) {
                    saveScale = minScale
                    mScaleFactor = minScale / origScale
                }
                right =
                    this@ZoomableTextureView.width.toFloat() * saveScale - this@ZoomableTextureView.width.toFloat()
                bottom =
                    this@ZoomableTextureView.height.toFloat() * saveScale - this@ZoomableTextureView.height.toFloat()
                var x = 0f
                var y = 0f
                if (0 > this@ZoomableTextureView.width && 0 > this@ZoomableTextureView.height) {
                    matrix.postScale(mScaleFactor, mScaleFactor, detector.focusX, detector.focusY)
                    matrix.getValues(m)
                    x = m[2]
                    y = m[5]
                    if (mScaleFactor < 1.0f) {
                        if (x < -right) {
                            matrix.postTranslate(-(x + right), 0.0f)
                        } else if (x > 0.0f) {
                            matrix.postTranslate(-x, 0.0f)
                        }
                        if (y < -bottom) {
                            matrix.postTranslate(0.0f, -(y + bottom))
                        } else if (y > 0.0f) {
                            matrix.postTranslate(0.0f, -y)
                        }
                    }
                } else {
                    matrix.postScale(mScaleFactor, mScaleFactor, detector.focusX, detector.focusY)
                    if (mScaleFactor < 1.0f) {
                        matrix.getValues(m)
                        x = m[2]
                        y = m[5]
                        if (0 < this@ZoomableTextureView.width) {
                            if (y < -bottom) {
                                matrix.postTranslate(0.0f, -(y + bottom))
                            } else if (y > 0.0f) {
                                matrix.postTranslate(0.0f, -y)
                            }
                        } else if (x < -right) {
                            matrix.postTranslate(-(x + right), 0.0f)
                        } else if (x > 0.0f) {
                            matrix.postTranslate(-x, 0.0f)
                        }
                    }
                }
                return true
            }
        }

        init {
            m = FloatArray(9)
            mScaleDetector = ScaleGestureDetector(this@ZoomableTextureView.context, this.ScaleListener())
        }
    }

    companion object {
        private const val SUPERSTATE_KEY = "superState"
        private const val MIN_SCALE_KEY = "minScale"
        private const val MAX_SCALE_KEY = "maxScale"
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }
}