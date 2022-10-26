package com.example.customviewtest

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
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
                    0.3f, 0.4f,
                    Point(80f, 100f),
                    -8f
                ),
                FreeStyleImageTemplate(
                    0.3f, 0.4f,
                    Point(600f, 220f),
                    6f
                ),
                FreeStyleImageTemplate(
                    0.3f, 0.4f,
                    Point(80f, 1000f),
                    8f
                ),
                FreeStyleImageTemplate(
                    0.3f, 0.4f,
                    Point(600f, 1200f),
                    -2f
                )
            ),
            stickers = arrayListOf(
                StickerTemplate(
                    0.15f, 0.15f,
                    Point(600f, 1200f),
                    0f,
                ),
                StickerTemplate(
                    0.15f, 0.15f,
                    Point(600f, 1500f),
                    0f,
                )
            )
        ),

        FreeStyleLayoutTemplate(
            images = arrayListOf(
                FreeStyleImageTemplate(
                    0.15f, 0.8f,
                    Point(80f, 80f),
                    0f
                ),
                FreeStyleImageTemplate(
                    0.3f, 0.4f,
                    Point(600f, 600f),
                    0f
                ),
                FreeStyleImageTemplate(
                    0.3f, 0.4f,
                    Point(80f, 800f),
                    0f
                ),
                FreeStyleImageTemplate(
                    0.15f, 0.8f,
                    Point(120f, 1600f),
                    0f
                )
            )
        ),

        FreeStyleLayoutTemplate(
            images = arrayListOf(
                FreeStyleImageTemplate(
                    0.6f, 0.5f,
                    Point(80f, 80f),
                    0f
                ),
                FreeStyleImageTemplate(
                    0.35f, 0.45f,
                    Point(500f, 1000f),
                    0f
                ),

                )
        ),

        FreeStyleLayoutTemplate(
            images = arrayListOf(
                FreeStyleImageTemplate(
                    0.15f, 0.8f,
                    Point(80f, 80f),
                    0f
                ),
                FreeStyleImageTemplate(
                    0.15f, 0.8f,
                    Point(80f, 550f),
                    2f
                ),

                FreeStyleImageTemplate(
                    0.15f, 0.8f,
                    Point(80f, 1100f),
                    -2f
                ),

                )
        ),

        FreeStyleLayoutTemplate(
            images = arrayListOf(
                FreeStyleImageTemplate(
                    0.45f, 0.45f,
                    Point(80f, 80f),
                    0f,

                    ),
                FreeStyleImageTemplate(
                    0.45f, 0.45f,
                    Point(500f, 1000f),
                    0f,
                ),

                )
        ),

        )

    private var layoutIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFreestyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            height = windowManager.maximumWindowMetrics.bounds.height()
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

            val imageLayout = Class.forName(img.viewClass)
                .getConstructor(Context::class.java).newInstance(this) as AppCompatImageView

            binding.collageContainer.addView(imageLayout)

            val image = ImageViewContainer(this).apply {
                imageview.layoutParams = ViewGroup.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
            }


            image.imageview.setImageDrawable(drawables[i])
            binding.collageContainer.addView(image)

            val imgHeight =
                if (height > 0) (img.height * height).toInt() else (img.width * width).toInt()
            val imgWidth =
                if (width > 0) (img.width * width).toInt() else (img.width * height).toInt()


            val params = (image.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = imgHeight
                width = imgWidth
            }
            image.layoutParams = params
            image.x = img.point.x
            image.y = img.point.y
            image.setCustomRotation(img.rotation)

            image.imageview.setOnLongClickListener {
                Log.d("task", "long press state: ${it.isLongClickable}")
                if (!it.isLongClickable) return@setOnLongClickListener false

                val clipText = "ClipData"
                val item = ClipData.Item(clipText)
                val mimetypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(clipText, mimetypes, item)

                val dragShadowBuilder = View.DragShadowBuilder(it)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    it.startDragAndDrop(data, dragShadowBuilder, it, 0)
                } else {
                    it.startDrag(data, dragShadowBuilder, it, 0)
                }
                it.visibility = View.VISIBLE
                true
            }

            image.imageview.setOnDragListener(dragListener)
        }

        layout.stickers.forEachIndexed { i, stc ->

            val sticker = AppCompatImageView(this)

            sticker.setImageDrawable(stickers[i])
            binding.collageContainer.addView(sticker)

            val imgHeight = (stc.height * height).toInt()
            val imgWidth = (stc.width * width).toInt()

            val params = (sticker.layoutParams as ConstraintLayout.LayoutParams).apply {
                height = imgHeight
                width = imgWidth
//                topToTop = binding.collageContainer.id
//                leftToLeft = binding.collageContainer.id
            }
            sticker.layoutParams = params
            sticker.x = stc.point.x
            sticker.y = stc.point.y
            sticker.setCustomRotation(stc.rotation)
        }
    }

    private val dragListener = View.OnDragListener { view, dragEvent ->
        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION -> true

            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }

            DragEvent.ACTION_DROP -> {
                val item = dragEvent.clipData.getItemAt(0)
                val dragData = item.text
                Log.d("task", dragData.toString())

                view.invalidate()
                val v = dragEvent.localState as ShapeableImageView
                val owner = v.parent as ConstraintLayout
                val destination = view as ShapeableImageView
                if (owner == destination) return@OnDragListener false
//                val destinationView = destination[0]
//                owner.removeAllViews()
//                destination.removeAllViews()
//                owner.addView(destinationView)
//                destination.addView(v)
                val od = v.drawable
                v.setImageDrawable(destination.drawable)
                destination.setImageDrawable(od)
                v.visibility = View.VISIBLE
                true
            }


            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }

            else -> false
        }
    }

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