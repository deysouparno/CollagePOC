package com.example.customviewtest

import android.graphics.PointF
import android.graphics.drawable.Drawable


data class LayoutResponse(val templates: List<LayoutTemplate>)

data class LayoutTemplate(
    val imagesTemplate: List<ImageTemplate>,
    val numberOfImages: Int = 5,
)

data class ImageTemplate(
    val height: Int,
    val width: Int,
    val constraints: List<Int>,
    val margins: List<Int> = arrayListOf(0, 0, 0, 0),
    val padding: List<Int> = arrayListOf(0, 0, 0, 0),
    val cornerRadius: Int = 0,
    val horizontalWeight: Float = -1f,
    val verticalWight: Float = -1f,
    val viewClass: String = "com.example.customviewtest.ZoomImageView" ,
    val mask: String? = null,
    val rotation: Float = 0f
)

data class Point(val x: Float, val y: Float)

data class FreeStyleImageTemplate(
    val height: Float,
    val width: Float,
    val point: Point,
    val rotation: Float = 0f,
    val viewClass: String = "com.example.customviewtest.ZoomImageView" ,
    val mask: String? = null,
)

data class StickerTemplate(
    val height: Float,
    val width: Float,
    val point: Point,
    val rotation: Float = 0f,
    val drawable: String = ""
)

data class FreeStyleLayoutTemplate(
    val background: String = "",
    val images: List<FreeStyleImageTemplate>,
    val stickers: List<StickerTemplate> = arrayListOf()
)

data class FreestyleLayouts(val layouts: List<FreeStyleLayoutTemplate>)
