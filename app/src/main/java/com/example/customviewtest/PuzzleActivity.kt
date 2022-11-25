package com.example.customviewtest

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.customviewtest.databinding.ActivityPuzzleBinding
import com.example.customviewtest.layout.slant.SixSlantLayout
import com.example.customviewtest.layout.straight.ThreeStraightLayout
import com.xiaopo.flying.puzzle.Area
import com.xiaopo.flying.puzzle.Line
import com.xiaopo.flying.puzzle.PuzzleLayout
import com.xiaopo.flying.puzzle.PuzzlePiece

class PuzzleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPuzzleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPuzzleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.puzzle.apply {

            puzzleLayout = ThreeStraightLayout(0)
            isTouchEnable = true;
            isNeedDrawLine = true;
            isNeedDrawOuterLine = true;
            lineSize = 4;
            lineColor = Color.BLACK;
            selectedLineColor = Color.BLACK;
            handleBarColor = Color.BLACK;
            this.setAnimateDuration(300);
        }

//        PuzzlePiece()
        val cc = ContextCompat.getDrawable(this, R.drawable.varanasi)!!
//        cc.intrinsicWidth

        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.varanasi),
            getMatrix(ContextCompat.getDrawable(this, R.drawable.varanasi)!!))
        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.black_panther))
        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.elephant))
//        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.tiger))
//        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.deer2))
//        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.deer))
        binding.puzzle.puzzlePieces.forEachIndexed { index, puzzlePiece ->
            Log.d("scaleFix", "$index ${puzzlePiece.drawableBounds.height()}, ${puzzlePiece.drawableBounds.width()}")
        }

        
//        binding.root.addView(View(this).apply {
//            layoutParams = ConstraintLayout.LayoutParams(702, 1159).apply {
//                leftToLeft = binding.root.id
//                topToTop = binding.root.id
//                rightToRight = binding.root.id
//                bottomToBottom = binding.root.id
//            }
//            setBackgroundColor(Color.CYAN)
//        })
    }

    private fun getMatrix(drawable: Drawable): Matrix {
        val imageRectF = RectF(0f, 0f, drawable.intrinsicWidth.toFloat(),
            drawable.intrinsicHeight.toFloat()
        )
        val viewRectF = RectF(0f, 0f, binding.puzzle.width - 0.65f, binding.puzzle.height * 0.56f)
        return Matrix().apply {  setRectToRect(imageRectF, viewRectF, Matrix.ScaleToFit.CENTER)}
    }
}

class CustomLayout : PuzzleLayout {
    override fun setOuterBounds(bounds: RectF?) {
        TODO("Not yet implemented")
    }

    override fun layout() {
        TODO("Not yet implemented")
    }

    override fun getAreaCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getOuterLines(): MutableList<Line> {
        TODO("Not yet implemented")
    }

    override fun getLines(): MutableList<Line> {
        TODO("Not yet implemented")
    }

    override fun getOuterArea(): Area {
        TODO("Not yet implemented")
    }

    override fun update() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override fun getArea(position: Int): Area {
        TODO("Not yet implemented")
    }

    override fun width(): Float {
        TODO("Not yet implemented")
    }

    override fun height(): Float {
        TODO("Not yet implemented")
    }

    override fun setPadding(padding: Float) {
        TODO("Not yet implemented")
    }

    override fun getPadding(): Float {
        TODO("Not yet implemented")
    }

    override fun getRadian(): Float {
        return this.radian
    }

    override fun setRadian(radian: Float) {
        this.radian = radian
    }

    override fun generateInfo(): PuzzleLayout.Info {
        return this.generateInfo()
    }

    override fun setColor(color: Int) {
        this.color = color
    }

    override fun getColor(): Int = this.color

    override fun sortAreas() = Unit
}