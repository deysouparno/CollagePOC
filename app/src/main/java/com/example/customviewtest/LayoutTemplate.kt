package com.example.customviewtest


data class LayoutResponse(val templates: List<LayoutTemplate>)

data class LayoutTemplate(val numberOfImages: Int, val imagesTemplate: List<ImageTemplate>)

data class ImageTemplate(
    val height: Int,
    val width: Int,
    val constraints: List<Int>,
    val margins: List<Int> = arrayListOf(0, 0, 0, 0),
    val padding: List<Int> = arrayListOf(0, 0, 0, 0),
    val cornerRadius: Int = 0,
    val horizontalWeight: Float = -1f,
    val verticalWight: Float = -1f
)
