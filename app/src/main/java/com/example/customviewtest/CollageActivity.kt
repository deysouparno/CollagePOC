package com.example.customviewtest

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.customviewtest.databinding.ActivityCollageBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerTreatment
import com.google.android.material.shape.EdgeTreatment
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

    private var radius = MutableLiveData<Float>(0f)
    private var margin = MutableLiveData<Int>(0)

    val displayMetrics = DisplayMetrics()

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
            5, arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 50)),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 50)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 50, 3, -PARENT)),
                ImageTemplate(0, 0, arrayListOf(2, 50, -PARENT, -PARENT)),
                ImageTemplate(200, 200, arrayListOf(-PARENT, -PARENT, -PARENT, -PARENT))
            ),
            guideLines = arrayListOf(0.5f)
        ),


        LayoutTemplate(
            5, arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, -PARENT, 50)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 50, 2, 51)),
                ImageTemplate(0, 0, arrayListOf(1, 50, -PARENT, 51)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 51, 4, -PARENT)),
                ImageTemplate(0, 0, arrayListOf(3, 51, -PARENT, -PARENT))
            ),
            guideLines = arrayListOf(1 / 3f, 2 / 3f)
        ),

        LayoutTemplate(
            5, arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2)),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 3)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, 1, 4)),
                ImageTemplate(0, 0, arrayListOf(2, 1, -PARENT, -PARENT)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 2, 3, -PARENT))
            ),
            guideLines = arrayListOf(-0.5f)
        ),


        LayoutTemplate(
            5, arrayListOf(
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
            guideLines = arrayListOf(2 / 3f)
        ),

        LayoutTemplate(
            5, arrayListOf(
                ImageTemplate(0, 0, arrayListOf(-PARENT, -PARENT, 1, 2), horizontalWeight = 2f),
                ImageTemplate(0, 0, arrayListOf(0, -PARENT, -PARENT, 2), horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 0, -PARENT, 3)),
                ImageTemplate(0, 0, arrayListOf(-PARENT, 2, 4, -PARENT), horizontalWeight = 1f),
                ImageTemplate(0, 0, arrayListOf(3, 2, -PARENT, -PARENT), horizontalWeight = 2f)
            ),
            guideLines = arrayListOf(1 / 3f, 2 / 3f)
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
            ContextCompat.getDrawable(this, R.drawable.bird)
        )

        binding.layoutTv.text = "Layout ${layoutIndex + 1}"

        generateLayout(layoutIndex)
//        loadLayout2()
        binding.nextButton.setOnClickListener {
            if (layoutIndex < layouts.size - 1) generateLayout(++layoutIndex)
        }

        binding.prevButton.setOnClickListener {
            if (layoutIndex > 0) generateLayout(--layoutIndex)
        }


        binding.radiusSlider.addOnChangeListener { slider, value, fromUser ->
            radius.postValue(value * 100f)
//            generateLayout(layoutIndex)
        }

        binding.paddingSlider.addOnChangeListener { slider, value, fromUser ->
            margin.postValue((value * 50).toInt())
//            generateLayout(layoutIndex)
        }


        margin.observe(this) { m ->

            images.forEach {
                it.setPadding(m, m, m, m)
            }
        }

        radius.observe(this) {

            images.forEach { container ->
                container.imageview.shapeAppearanceModel = ShapeAppearanceModel().toBuilder()
                    .setAllCornerSizes(it)
                    .setBottomEdge(EdgeTreatment())
                    .build();
            }

        }


    }


    private fun getId(index: Int) = when (index) {
        0 -> R.id.image_1
        1 -> R.id.image_2
        2 -> R.id.image_3
        3 -> R.id.image_4
        4 -> R.id.image_5
        50 -> R.id.guide_1
        51 -> R.id.guide_2
        52 -> R.id.guide_3
        53 -> R.id.guide_4
        else -> binding.collageContainer.id
    }


    private fun generateLayout(index: Int, flag: Boolean = true) {

        binding.layoutTv.text = "Layout ${layoutIndex + 1}"
        val template = layouts[index]
        if (flag) loadImageViews(5)

        template.guideLines.forEachIndexed { id, f ->
            val guideline = getNewGuideline(this, f)
            guideline.id = getId(id + 50)
        }

        layoutParams.forEachIndexed { i, params ->
            Log.d("constratints", "image: ${i + 1}")
            params.apply {
                height = dpToInt(template.imagesTemplate[i].height)
                width = dpToInt(template.imagesTemplate[i].width)

                if (template.imagesTemplate[i].constraints[0] < 0)
                    leftToLeft = getId(template.imagesTemplate[i].constraints[0])
                else leftToRight =
                    getId(template.imagesTemplate[i].constraints[0])

                if (template.imagesTemplate[i].constraints[1] < 0)
                    topToTop = getId(template.imagesTemplate[i].constraints[1])
                else topToBottom =
                    getId(template.imagesTemplate[i].constraints[1])

                if (template.imagesTemplate[i].constraints[2] < 0)
                    rightToRight = getId(template.imagesTemplate[i].constraints[2])
                else rightToLeft =
                    getId(template.imagesTemplate[i].constraints[2])

                if (template.imagesTemplate[i].constraints[3] < 0)
                    bottomToBottom = getId(template.imagesTemplate[i].constraints[3])
                else bottomToTop =
                    getId(template.imagesTemplate[i].constraints[3])

                horizontalWeight = template.imagesTemplate[i].horizontalWeight
                verticalWeight = template.imagesTemplate[i].verticalWight

            }


        }

    }

    private fun loadImageViews(num: Int) {
        images.clear()
        layoutParams.clear()
        binding.collageContainer.removeAllViews()

        for (i in 1..num) {
            val imageLayout =
                ImageViewContainer(this).apply { imageview = ZoomImageView(this@CollageActivity) }
            imageLayout.setUpImageView()
            when (i) {
                1 -> imageLayout.id = R.id.image_1
                2 -> imageLayout.id = R.id.image_2
                3 -> imageLayout.id = R.id.image_3
                4 -> imageLayout.id = R.id.image_4
                5 -> imageLayout.id = R.id.image_5
            }

            images.add(imageLayout)
            binding.collageContainer.addView(imageLayout)
            imageLayout.imageview.setImageDrawable(drawables[i - 1])


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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
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


//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }