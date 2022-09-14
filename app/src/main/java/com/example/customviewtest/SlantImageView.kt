package com.example.customviewtest

import android.content.Context
import android.graphics.*
import androidx.appcompat.widget.AppCompatImageView

class SlantImageView(
    context: Context,
) : AppCompatImageView(context) {

    private var topLeft: PointF = PointF()
    private var topRight: PointF = PointF()
    private var bottomLeft: PointF = PointF()
    private var bottomRight: PointF = PointF()

    constructor(
        context: Context,
        topLeft: PointF,
        topRight: PointF,
        bottomLeft: PointF,
        bottomRight: PointF
    ) : this(context) {
        this.topLeft = topLeft
        this.topRight = topRight
        this.bottomLeft = bottomLeft
        this.bottomRight = bottomRight
    }


    private val clipPath: Path = Path()
    private val borderPath: Path = Path()
    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

//    var start: Direction = NONE
//    var end: Direction = NONE
//    var distance: Float = 0f

    var borderEnabled: Boolean = false
    var borderSize: Float = 0f
    var borderColor: Int = Color.BLACK

    override fun invalidate() {
        super.invalidate()
        setClipPath()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setClipPath()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (!clipPath.isEmpty) {
            canvas?.clipPath(clipPath)
        }
        super.dispatchDraw(canvas)
    }

    override fun onDraw(canvas: Canvas?) {
//        if (clipPath.isEmpty) {
//            super.onDraw(canvas)
//            return
//        }

        canvas?.apply {
            save()
            clipPath(clipPath)
            super.onDraw(this)
            if (!borderPath.isEmpty) {
                drawPath(borderPath, borderPaint)
            }
            restore()
        }
    }

    private fun setClipPath() {
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()
        if (width <= 0 || height <= 0) return

        clipPath.reset()
        borderPath.reset()

        clipPath.apply {
            moveTo(topLeft.x, topLeft.y)
            lineTo(topRight.x, topRight.y)
        }


        clipPath.close()
        borderPath.close()
    }

//    private fun isTopOrLeft(): Boolean = end == TOP || end == LEFT

//    private fun createTopPath(width: Float, height: Float) {
//        if (isTopOrLeft()) {
//            clipPath.apply {
//                moveTo(0f, 0f)
//                lineTo(width, distance)
//                lineTo(width, height)
//                lineTo(0f, height)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(0f, 0f)
//                    lineTo(width, distance)
//                }
//            }
//        } else {
//            clipPath.apply {
//                moveTo(0f, distance)
//                lineTo(width, 0f)
//                lineTo(width, height)
//                lineTo(0f, height)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(0f, distance)
//                    lineTo(width, 0f)
//                }
//            }
//        }
//    }
//
//    private fun createBottomPath(width: Float, height: Float) {
//        if (isTopOrLeft()) {
//            clipPath.apply {
//                moveTo(0f, 0f)
//                lineTo(width, 0f)
//                lineTo(width, height - distance)
//                lineTo(0f, height)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(0f, height)
//                    lineTo(width, height - distance)
//                }
//            }
//        } else {
//            clipPath.apply {
//                moveTo(0f, 0f)
//                lineTo(width, 0f)
//                lineTo(width, height)
//                lineTo(0f, height - distance)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(0f, height - distance)
//                    lineTo(width, height)
//                }
//            }
//        }
//    }
//
//    private fun createLeftPath(width: Float, height: Float) {
//        if (isTopOrLeft()) {
//            clipPath.apply {
//                moveTo(distance, 0f)
//                lineTo(width, 0f)
//                lineTo(width, height)
//                lineTo(0f, height)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(distance, 0f)
//                    lineTo(0f, height)
//                }
//            }
//        } else {
//            clipPath.apply {
//                moveTo(0f, 0f)
//                lineTo(width, 0f)
//                lineTo(width, height)
//                lineTo(distance, height)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(0f, 0f)
//                    lineTo(distance, height)
//                }
//            }
//        }
//    }
//
//    private fun createRightPath(width: Float, height: Float) {
//        if (isTopOrLeft()) {
//            clipPath.apply {
//                moveTo(0f, 0f)
//                lineTo(width, 0f)
//                lineTo(width - distance, height)
//                lineTo(0f, height)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(width, 0f)
//                    lineTo(width - distance, height)
//                }
//            }
//        } else {
//            clipPath.apply {
//                moveTo(0f, 0f)
//                lineTo(width - distance, 0f)
//                lineTo(width, height)
//                lineTo(0f, height)
//            }
//
//            if (borderEnabled) {
//                borderPath.apply {
//                    moveTo(width - distance, 0f)
//                    lineTo(width, height)
//                }
//            }
//        }
//    }
}