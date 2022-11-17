package com.example.customviewtest

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.customviewtest.databinding.ActivityFreestyleBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import org.json.JSONObject
import java.io.IOException

class FreestyleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFreestyleBinding

    private var height: Int = 0
    private var width: Int = 0

    private lateinit var drawables: ArrayList<Drawable?>
    private lateinit var stickers: ArrayList<Drawable?>


    private val layouts = arrayListOf<FreeStyleLayoutTemplate>(
        FreeStyleLayoutTemplate(
            images = arrayListOf(
                FreeStyleImageTemplate(
                    0.4f, 0.4f,
                    Point(0.1f, 0.05f),
                    0f
                ),
                FreeStyleImageTemplate(
                    0.4f, 0.4f,
                    Point(0.4f, 0.5f),
                    0f
                ),

            ),
            stickers = arrayListOf(
                StickerTemplate(
                    0.15f, 0.15f,
                    Point(0.3f, 0.2f),
                    0f,
                ),
                StickerTemplate(
                    0.15f, 0.15f,
                    Point(0.1f, 0.4f),
                    0f,
                )
            )
        ),
        FreeStyleLayoutTemplate(
            images = arrayListOf(
                FreeStyleImageTemplate(
                    0.7f, 0.5f,
                    Point(0.05f, 0.1f),
                    0f
                ),
                FreeStyleImageTemplate(
                    0.4f, 0.3f,
                    Point(0.595f, 0.6f),
                    0f
                ),

                ),
            stickers = arrayListOf(

            )
        ),

        )

    private var layoutIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFreestyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            height = windowManager.maximumWindowMetrics.bounds.height() - 50
            width = windowManager.maximumWindowMetrics.bounds.width()

        } else {
            val displayMetrics = DisplayMetrics()
            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }

        drawables = arrayListOf(
            ContextCompat.getDrawable(
                this,
                com.example.customviewtest.R.drawable.deer2
            ),
            ContextCompat.getDrawable(this, R.drawable.elephant),
            ContextCompat.getDrawable(this, R.drawable.tiger),
            ContextCompat.getDrawable(this, R.drawable.deer),
            ContextCompat.getDrawable(this, R.drawable.bird)
        )

        stickers = arrayListOf(
            ContextCompat.getDrawable(this, R.drawable.sun_sticker),
            ContextCompat.getDrawable(this, R.drawable.mushroom_sticker)
        )



        binding.nextButton.setOnClickListener {
            if (layoutIndex < layouts.size - 1) generateLayout(++layoutIndex)
            binding.radiusSlider.value = 0f
            binding.paddingSlider.value = 0f
        }

        binding.prevButton.setOnClickListener {
            if (layoutIndex > 0) generateLayout(--layoutIndex)
            binding.radiusSlider.value = 0f
            binding.paddingSlider.value = 0f
        }

        generateLayout(0)


    }

    private fun generateLayout(index: Int) {
        val layout = layouts[index]

        binding.collageContainer.removeAllViews()

        binding.layoutTv.text = "Layout ${layoutIndex + 1}"

        layout.images.forEachIndexed { i, img ->

//            val image = Class.forName(img.viewClass)
//                .getConstructor(Context::class.java).newInstance(this) as AppCompatImageView

            val imageLayout = CustomImageView(this@FreestyleActivity).apply {
                setImageDrawable(drawables[i])
                setBackgroundColor(Color.BLUE)
            }

            binding.collageContainer.addView(imageLayout)

            val imgHeight =
                if (img.height > 0) (img.height * height).toInt() else (img.width * width * -1).toInt()
            val imgWidth =
                if (img.width > 0) (img.width * width).toInt() else (img.width * height * -1).toInt()


            val params = (imageLayout.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = imgHeight
                width = imgWidth
            }
            imageLayout.layoutParams = params
            imageLayout.x = if (img.point.x > 0) img.point.x * width else img.point.x * width * -1
            imageLayout.y = if (img.point.y > 0) img.point.y * height else img.point.y * width * -1
            imageLayout.setCustomRotation(img.rotation)

//            imageLayout.setOnLongClickListener {
//                Log.d("task", "long press state: ${it.isLongClickable}")
//                if (!it.isLongClickable) return@setOnLongClickListener false
//
//                val clipText = "ClipData"
//                val item = ClipData.Item(clipText)
//                val mimetypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
//                val data = ClipData(clipText, mimetypes, item)
//
//                val dragShadowBuilder = View.DragShadowBuilder(it)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    it.startDragAndDrop(data, dragShadowBuilder, it, 0)
//                } else {
//                    it.startDrag(data, dragShadowBuilder, it, 0)
//                }
//                it.visibility = View.VISIBLE
//                true
//            }

//            imageLayout.imageview.setOnDragListener(dragListener)
        }

        layout.stickers.forEachIndexed { i, stc ->

            val sticker = FreestyleImageView(this)

            sticker.setImageDrawable(stickers[i])
            binding.collageContainer.addView(sticker)

//            val imgHeight = (stc.height * height).toInt()
//            val imgWidth = (stc.width * width).toInt()

            val imgHeight =
                if (stc.height > 0) (stc.height * height).toInt() else (stc.width * width * -1).toInt()
            val imgWidth =
                if (stc.width > 0) (stc.width * width).toInt() else (stc.width * height * -1).toInt()


            val params = (sticker.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = imgHeight
//                width = imgWidth
            }
            sticker.layoutParams = params

            sticker.layoutParams = params
            sticker.x = if (stc.point.x > 0) stc.point.x * width else stc.point.x * width * -1
            sticker.y = if (stc.point.y > 0) stc.point.y * height else stc.point.y * width * -1
            sticker.setCustomRotation(stc.rotation)
        }
    }

//    private val dragListener = View.OnDragListener { view, dragEvent ->
//        when (dragEvent.action) {
//            DragEvent.ACTION_DRAG_STARTED -> {
//                dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
//            }
//
//            DragEvent.ACTION_DRAG_ENTERED -> {
//                view.invalidate()
//                true
//            }
//
//            DragEvent.ACTION_DRAG_LOCATION -> true
//
//            DragEvent.ACTION_DRAG_EXITED -> {
//                view.invalidate()
//                true
//            }
//
//            DragEvent.ACTION_DROP -> {
//                val item = dragEvent.clipData.getItemAt(0)
//                val dragData = item.text
//                Log.d("task", dragData.toString())
//
//                view.invalidate()
//                val v = dragEvent.localState as ShapeableImageView
//                val owner = v.parent as ConstraintLayout
//                val destination = view as ShapeableImageView
//                if (owner == destination) return@OnDragListener false
////                val destinationView = destination[0]
////                owner.removeAllViews()
////                destination.removeAllViews()
////                owner.addView(destinationView)
////                destination.addView(v)
//                val od = v.drawable
//                v.setImageDrawable(destination.drawable)
//                destination.setImageDrawable(od)
//                v.visibility = View.VISIBLE
//                true
//            }
//
//
//            DragEvent.ACTION_DRAG_ENDED -> {
//                view.invalidate()
//                true
//            }
//
//            else -> false
//        }
//    }

    private fun View.setCustomRotation(deg: Float) {
//        this.animation = RotateAnimation(0f, deg).apply {
//            duration = 1
//        }

        this.animate().apply {
            rotation(deg)
            duration = 1
        }
    }

    private fun dpToPx(dim: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dim.toFloat(),
        resources.displayMetrics
    ).toInt()

}

object FreestyleLayoutParser {

    private fun getJsonDataFromAsset(
        context: Context,
        fileName: String = "freestyle.json"
    ): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


    fun getFreestyleLayouts(
        context: Context,
        size: Int = 5,
        useRemoteLayouts: Boolean = false
    ): List<FreeStyleLayoutTemplate> {
        val layoutString = getJsonDataFromAsset(context) ?: ""

        if (layoutString.isEmpty()) return emptyList()

        val parent = JSONObject(layoutString)
        val templates = parent.getJSONObject("templates")

        val converter = Gson()
        val dd = converter.fromJson(templates.toString(), FreestyleLayouts::class.java)

        return dd.layouts
    }

}