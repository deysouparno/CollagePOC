package com.example.customviewtest

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.rotationMatrix
import androidx.core.view.setMargins
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.customviewtest.databinding.ActivityMainBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerTreatment
import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.ShapePath
import com.google.android.material.slider.Slider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.collageButton.setOnClickListener {
            startActivity(Intent(this, CollageActivity::class.java))
        }

        binding.freestyleButton.setOnClickListener {
            startActivity(Intent(this, FreestyleActivity::class.java))
        }

        binding.slantButton.setOnClickListener {
            startActivity(Intent(this, PuzzleActivity::class.java))
        }

        binding.videoCollageButton.setOnClickListener {
            startActivity(Intent(this, VideoCollageActivity::class.java))
        }

//        val v = TestView(this)
//        v.setImageDrawable(getDrawable(R.drawable.bird))
//        v.setBackgroundColor(Color.YELLOW)
//        v.setPadding(5, 5, 5, 5)


//        binding.root.addView(v)

//        (v.layoutParams as ConstraintLayout.LayoutParams).apply {
//            topToTop = binding.root.id
//            bottomToBottom = binding.root.id
//            leftToLeft = binding.root.id
//            rightToRight = binding.root.id
//
//            height = 500
//            width = 500
//        }

    }

}


class TestView(context: Context): AppCompatImageView(context) {

    private val clipPath: Path = Path()
    private val borderPath: Path = Path()
    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)



    override fun invalidate() {
        super.invalidate()
        setClipPath()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("testView", "measure called")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setClipPath()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        Log.d("testView", "dispatch draw called ${clipPath.isEmpty}")
        super.dispatchDraw(canvas)
        if (!clipPath.isEmpty) {
            canvas?.clipPath(clipPath)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d("testView", "draw called ${clipPath.isEmpty}")
        if (clipPath.isEmpty) {
            super.onDraw(canvas)
            return
        }

        canvas?.apply {
            save()
            clipPath(clipPath)
//            drawPath(clipPath, Paint().apply {
//                color = Color.WHITE
//            })

           this.drawBitmap(ContextCompat.getDrawable(context, R.drawable.elephant)!!.toBitmap(500, 500, null), 0f, 0f, null)

            restore()
        }

    }


    private fun setClipPath() {
        Log.d("testView", "set clip path called")
//        val width = measuredWidth.toFloat()
//        val height = measuredHeight.toFloat()
//        if (width <= 0 || height <= 0) return

        clipPath.reset()
//        borderPath.reset()

        clipPath.apply {
            moveTo(0f, 0f)
            lineTo(500f, 0f)
            lineTo(500f, 250f)
            lineTo(0f, 500f)
//            lineTo(0f, 0f)
        }

        clipPath.close()
//        borderPath.close()
    }

}