package com.example.customviewtest

import `in`.simplifiedbytes.maskedimageview.PngMaskImageView
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.customviewtest.databinding.ActivityCollageBinding
import com.google.android.material.shape.CornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapePath
import kotlin.math.abs


class CollageActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCollageBinding

    private var layoutIndex = 1

    private val images = ArrayList<ImageViewContainer>()
    private val layoutParams = ArrayList<ConstraintLayout.LayoutParams>()
    private lateinit var drawables: ArrayList<Drawable?>
    var xDown = 0f;
    var yDown = 0f

    private val PARENT = 100
    private val NO_CONSTRAINT = 500

    private var radius = MutableLiveData<Float>(0f)
    private var margin = MutableLiveData<Int>(0)

    private val displayMetrics = DisplayMetrics()

    private var height: Int = 0
    private var width: Int = 0

    // on below line we are getting height
    // and width using display metrics.
//    val height = displayMetrics.heightPixels
//    val width = displayMetrics.widthPixels


    private val touchListener = object : View.OnTouchListener {
        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            if (view == null || motionEvent == null)
                return true
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    xDown = motionEvent.x
                    yDown = motionEvent.y
                }

                MotionEvent.ACTION_MOVE -> {
                    view.x = view.x + motionEvent.x - xDown
                    view.y = view.y + motionEvent.y - yDown
                }

                MotionEvent.ACTION_UP -> view.performClick()

            }

            return true
        }

    }

    private val layouts = arrayListOf<LayoutTemplate>(
        LayoutTemplate(

            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2)),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 3)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 3, -PARENT)),
                ImageTemplate(0, 0, arrayListOf(2, 1, -PARENT, -PARENT)),
                ImageTemplate(200, 200, arrayListOf(-PARENT, -PARENT, -PARENT, -PARENT)
                , viewClass = "in.simplifiedbytes.maskedimageview.PngMaskImageView")
            ),
        ),


        LayoutTemplate(

            arrayListOf(
                ImageTemplate(
                    0,
                    0,
                    arrayListOf(-PARENT, -PARENT, -PARENT, 1),
                    horizontalWeight = 1f,
                    verticalWight = 1f
                ),
                ImageTemplate(
                    0, 0, arrayListOf(-PARENT, 0, 2, 3), horizontalWeight = 1f,
                    verticalWight = 1f
                ),
                ImageTemplate(
                    0, 0, arrayListOf(1, -1, -PARENT, -1), horizontalWeight = 1f,
                    verticalWight = 1f
                ),
                ImageTemplate(
                    0, 0, arrayListOf(-PARENT, 1, 4, -PARENT), horizontalWeight = 1f,
                    verticalWight = 1f
                ),
                ImageTemplate(
                    0, 0, arrayListOf(3, 2, -PARENT, -PARENT), horizontalWeight = 1f,
                    verticalWight = 1f
                )
            ),
        ),

        LayoutTemplate(

            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2)),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 3)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 1, 4)),
                ImageTemplate(0, 0, arrayListOf(2, 1, -PARENT, -PARENT)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 2, 3, -PARENT))
            ),
        ),


        LayoutTemplate(

            arrayListOf(
                ImageTemplate(
                    0,
                    0,
                    arrayListOf(-PARENT, -PARENT, 1, 2),
                    horizontalWeight = 1f,
                    verticalWight = 2f
                ),
                ImageTemplate(
                    0,
                    0,
                    arrayListOf(0, -PARENT, -PARENT, 3),
                    horizontalWeight = 2f,
                    verticalWight = 2f
                ),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 3, -PARENT), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(2, 1, 4, -PARENT), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(3, 1, -PARENT, -PARENT), verticalWight = 1f)
            ),
        ),

        LayoutTemplate(

            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2), horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 2), horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, -PARENT, 3)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 2, 4, -PARENT), horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(3, 2, -PARENT, -PARENT), horizontalWeight = 2f)
            ),
        ),

        LayoutTemplate(

            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 3)),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, 2, 3)),
                ImageTemplate(0, 0, arrayListOf(1, -PARENT, -PARENT, 4)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 4, -PARENT)),
                ImageTemplate(0, 0, arrayListOf(3, -3, -PARENT, -PARENT))
            ),
        ),

        LayoutTemplate(
            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 3), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, 2, 4), verticalWight = 1.25f),
                ImageTemplate(0, 0, arrayListOf(1, -PARENT, -PARENT, 6), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 1, 5), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(3, 1, 2, 6), verticalWight = 0.75f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 3, 6, -PARENT), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(5, 4, -PARENT, -PARENT), verticalWight = 1f),
            ),
            numberOfImages = 7,
        ),

        LayoutTemplate(
            arrayListOf(
                ImageTemplate(
                    300,
                    0,
                    arrayListOf(-PARENT, -PARENT, 1, NO_CONSTRAINT),
                    horizontalWeight = 5f
                ),
                ImageTemplate(
                    0,
                    0,
                    arrayListOf(0, -PARENT, -PARENT, 7),
                    verticalWight = 3f,
                    horizontalWeight = 3f
                ),
                ImageTemplate(400, 100, arrayListOf(-PARENT, NO_CONSTRAINT, NO_CONSTRAINT, 5)),
                ImageTemplate(0, 180, arrayListOf(2, 0, NO_CONSTRAINT, 4)),
                ImageTemplate(0, 180, arrayListOf(2, 3, NO_CONSTRAINT, 6)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 1, 6, -PARENT), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(5, 1, 7, -PARENT), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(6, 1, -PARENT, -PARENT), verticalWight = 1f),
            ),
            numberOfImages = 8,
        ),

        LayoutTemplate(
            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2)),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 3)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 3, -PARENT)),
                ImageTemplate(0, 0, arrayListOf(2, 1, -PARENT, -PARENT)),
                ImageTemplate(
                    300,
                    300,
                    arrayListOf(-PARENT, -PARENT, -PARENT, -PARENT),
                    rotation = 45f
                ),
                ImageTemplate(200, 200, arrayListOf(-PARENT, -PARENT, -PARENT, -PARENT)),
            ),
            numberOfImages = 6,
        ),

        LayoutTemplate(
            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 3), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, 2, 4), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(1, -PARENT, -PARENT, 6), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 4, 7), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(3, 1, 6, 5), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(3, 4, 6, 8), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(4, 2, -PARENT, 9), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 3, 8, -PARENT), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(7, 5, 9, -PARENT), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(8, 6, -PARENT, -PARENT), verticalWight = 2f),
            ),
            numberOfImages = 10,
        ),

        LayoutTemplate(
            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 4), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 3, 6), verticalWight = 1f, horizontalWeight = 3f),
                ImageTemplate(0, 0, arrayListOf(2, 0, 4, 7), verticalWight = 1f, horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(3, 1, 5, 8), verticalWight = 1f, horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(4, 1, -PARENT, 9), verticalWight = 1f, horizontalWeight = 3f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 2, 7, -PARENT), verticalWight = 1f, horizontalWeight = 3f),
                ImageTemplate(0, 0, arrayListOf(6, 3, 8, -PARENT), verticalWight = 1f, horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(7, 4, 9, -PARENT), verticalWight = 1f, horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(8, 5, -PARENT, -PARENT), verticalWight = 1f, horizontalWeight = 3f),
            ),
            numberOfImages = 10,
        ),

        LayoutTemplate(
            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 7), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 3, 8), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(2, 0, 5, 4), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(2, 3, 6, 8), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(3, 1, 7, 6), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(4, 5, 7, 9), verticalWight = 1f),
                ImageTemplate(0, 0, arrayListOf(5, 1, -PARENT, 9), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 2, 9, -PARENT), verticalWight = 2f),
                ImageTemplate(0, 0, arrayListOf(8, 7, -PARENT, -PARENT), verticalWight = 2f),
            ),
            numberOfImages = 10,
        ),

        LayoutTemplate(
            arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2), verticalWight = 2f, horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 4), verticalWight = 2f, horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 3, 5), verticalWight = 1f, horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(2, 1, 4, 6), verticalWight = 1f, horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(3, 1, -PARENT, 7), verticalWight = 1f, horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 2, 6, 8), verticalWight = 1f, horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(5, 3, 7, 8), verticalWight = 1f, horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(6, 4, -PARENT, 9), verticalWight = 1f, horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 5, 9, -PARENT), verticalWight = 2f, horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(8, 7, -PARENT, -PARENT), verticalWight = 2f, horizontalWeight = 1f),
            ),
            numberOfImages = 10,
        )
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCollageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            height = windowManager.maximumWindowMetrics.bounds.height()
            width = windowManager.maximumWindowMetrics.bounds.width()
        } else {
            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }

        drawables = arrayListOf(
            ContextCompat.getDrawable(this, R.drawable.deer2),
            ContextCompat.getDrawable(this, R.drawable.elephant),
            ContextCompat.getDrawable(this, R.drawable.tiger),
            ContextCompat.getDrawable(this, R.drawable.deer),
            ContextCompat.getDrawable(this, R.drawable.bird),
            ContextCompat.getDrawable(this, R.drawable.red_panda),
            ContextCompat.getDrawable(this, R.drawable.black_panther),
            ContextCompat.getDrawable(this, R.drawable.varanasi),
            ContextCompat.getDrawable(this, R.drawable.varanasi),
            ContextCompat.getDrawable(this, R.drawable.varanasi),
        )

        binding.layoutTv.text = "Layout ${layoutIndex + 1}"

//        generateLayout(layoutIndex)
//        loadLayout2()
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


        binding.radiusSlider.addOnChangeListener { slider, value, fromUser ->
            radius.postValue(value * 100f)
        }

        binding.paddingSlider.addOnChangeListener { slider, value, fromUser ->
            margin.postValue((value * 50).toInt())
        }

        binding.puzzle.apply {

        }






    }


    private fun getId(index: Int) = when (index) {
        0 -> R.id.image_1
        1 -> R.id.image_2
        2 -> R.id.image_3
        3 -> R.id.image_4
        4 -> R.id.image_5
        5 -> R.id.image_6
        6 -> R.id.image_7
        7 -> R.id.image_8
        8 -> R.id.image_9
        9 -> R.id.image_10
        else -> binding.collageContainer.id
    }

    private fun getImageViewClass(type: Int)  {
        val img = PngMaskImageView(this)
        img.setMask(ContextCompat.getDrawable(this, R.drawable.pip_bottle)!!);

    }


    private fun generateLayout(index: Int, flag: Boolean = true) {

        binding.layoutTv.text = "Layout ${layoutIndex + 1}"
        val template = layouts[index]
        if (flag) loadImageViews(template)


        layoutParams.forEachIndexed { i, params ->
            Log.d("constratints", "image: ${i + 1}")
            params.apply {
                height = dpToInt(template.imagesTemplate[i].height)
                width = dpToInt(template.imagesTemplate[i].width)

                if (template.imagesTemplate[i].constraints[0] != NO_CONSTRAINT) {
                    if (template.imagesTemplate[i].constraints[0] < 0)
                        leftToLeft = getId(-1 * template.imagesTemplate[i].constraints[0])
                    else leftToRight =
                        getId(template.imagesTemplate[i].constraints[0])
                }
                if (template.imagesTemplate[i].constraints[1] != NO_CONSTRAINT) {
                    if (template.imagesTemplate[i].constraints[1] < 0)
                        topToTop = getId(-1 * template.imagesTemplate[i].constraints[1])
                    else topToBottom =
                        getId(template.imagesTemplate[i].constraints[1])
                }
                if (template.imagesTemplate[i].constraints[2] != NO_CONSTRAINT) {
                    if (template.imagesTemplate[i].constraints[2] < 0)
                        rightToRight = getId(-1 * template.imagesTemplate[i].constraints[2])
                    else rightToLeft =
                        getId(template.imagesTemplate[i].constraints[2])
                }

                if (template.imagesTemplate[i].constraints[3] != NO_CONSTRAINT) {
                    if (template.imagesTemplate[i].constraints[3] < 0)
                        bottomToBottom = getId(-1 * template.imagesTemplate[i].constraints[3])
                    else bottomToTop =
                        getId(template.imagesTemplate[i].constraints[3])
                }

                horizontalWeight = template.imagesTemplate[i].horizontalWeight
                verticalWeight = template.imagesTemplate[i].verticalWight

                if (template.imagesTemplate[i].rotation != 0f) {
                    images[i].animate().apply {
                        rotation(template.imagesTemplate[i].rotation)
                        duration = 1
                    }
//                    images[i].imageview.animate().apply {
//                        rotation(-template.imagesTemplate[i].rotation)
//                        duration = 1
//                    }
                }


                images[i].imageview.background = ColorDrawable(Color.BLUE)

                images[i].imageview.background = ShapeDrawable(RectShape()).apply {
                    colorFilter = PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
                }

            }


        }

    }

    private fun addDragAndDrop(image: AppCompatImageView) {
        image.setOnLongClickListener {
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

        image.setOnDragListener(dragListener)
    }

    private fun loadImageViews(layout: LayoutTemplate) {
        images.clear()
        layoutParams.clear()
        binding.collageContainer.removeAllViews()

        for (i in 1..layout.numberOfImages) {
            val imageLayout = ImageViewContainer(this)
            when (i) {
                1 -> imageLayout.id = R.id.image_1
                2 -> imageLayout.id = R.id.image_2
                3 -> imageLayout.id = R.id.image_3
                4 -> imageLayout.id = R.id.image_4
                5 -> imageLayout.id = R.id.image_5
                6 -> imageLayout.id = R.id.image_6
                7 -> imageLayout.id = R.id.image_7
                8 -> imageLayout.id = R.id.image_8
                9 -> imageLayout.id = R.id.image_9
                10 -> imageLayout.id = R.id.image_10
            }

            imageLayout.imageview = Class.forName(layout.imagesTemplate[i-1].viewClass)
                .getConstructor(Context::class.java).newInstance(this) as AppCompatImageView
            imageLayout.setUpImageView()
            images.add(imageLayout)
            binding.collageContainer.addView(imageLayout)
            imageLayout.imageview.setImageDrawable(drawables[i - 1])



            if (layout.imagesTemplate[i-1].viewClass.contains("Zoom")) {
                radius.observe(this) {
                    (imageLayout.imageview as ZoomImageView).shapeAppearanceModel = ShapeAppearanceModel().toBuilder()
                        .setAllCornerSizes(it)
                        .build();
                }
            }

            if (layout.imagesTemplate[i-1].viewClass.contains("Mask")) {
                if (layout.imagesTemplate[i-1].mask != null)
                    Glide.with(this).load(layout.imagesTemplate[i-1].mask).into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            (imageLayout.imageview as PngMaskImageView).setMask(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    });
                (imageLayout.imageview as PngMaskImageView)
                    .setMask(ContextCompat.getDrawable(this, R.drawable.pip_bottle)!!)
                imageLayout.setCardBackgroundColor(Color.WHITE)
            }

            margin.observe(this) { m ->
                imageLayout.setContentPadding(m, m, m, m)
            }

            val arr = Array(26){_ -> false}

            val s = ""
            s.forEach {
                arr[it - 'a']
            }


            layoutParams.add(imageLayout.layoutParams as ConstraintLayout.LayoutParams)

            imageLayout.imageview.setOnLongClickListener {
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

            imageLayout.imageview.setOnDragListener(dragListener)


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
                val v = dragEvent.localState as AppCompatImageView
                val owner = v.parent as ConstraintLayout
                val destination = view as AppCompatImageView
                if (owner == destination) return@OnDragListener false
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


    private fun getNewGuideline(context: Context, percentage: Float): Guideline {
        val guideline = Guideline(context)
        val orientation: Int
        val height: Int
        val width: Int

        binding.collageContainer.addView(guideline)

        if (percentage > 0) {
            orientation = ConstraintLayout.LayoutParams.HORIZONTAL
            height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            width = ConstraintLayout.LayoutParams.MATCH_PARENT
        } else {
            orientation = ConstraintLayout.LayoutParams.VERTICAL
            height = ConstraintLayout.LayoutParams.MATCH_PARENT
            width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        }
        val lp = guideline.layoutParams as ConstraintLayout.LayoutParams
        lp.apply {
            this.width = width
            this.height = height
            this.orientation = orientation
            guidePercent = abs(percentage)
        }
//        lp.orientation = orientation
//        lp.guidePercent = abs(percentage)

        guideline.layoutParams = lp
        return guideline
    }


    private fun dpToInt(dim: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dim.toFloat(),
        resources.displayMetrics
    ).toInt()
}


class ConcaveRoundedCornerTreatment : CornerTreatment() {

    override fun getCornerPath(
        shapePath: ShapePath,
        angle: Float,
        interpolation: Float,
        radius: Float
    ) {
        val interpolatedRadius = radius * interpolation + 10
        shapePath.reset(0f, interpolatedRadius, ANGLE_LEFT, ANGLE_LEFT - angle)
        shapePath.addArc(
            -interpolatedRadius,
            -interpolatedRadius,
            interpolatedRadius,
            interpolatedRadius,
            ANGLE_BOTTOM,
            -angle
        )
    }

    companion object {
        const val ANGLE_LEFT = 180f
        const val ANGLE_BOTTOM = 90f
    }
}
