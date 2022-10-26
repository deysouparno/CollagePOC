package com.example.customviewtest

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.customviewtest.databinding.ActivityPuzzleBinding
import com.example.customviewtest.layout.slant.FourSlantLayout
import com.example.customviewtest.layout.slant.ThreeSlantLayout
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

            puzzleLayout = FourSlantLayout(0)
            isTouchEnable = true;
            isNeedDrawLine = true;
            isNeedDrawOuterLine = true;
            lineSize = 4;
            lineColor = Color.BLACK;
            selectedLineColor = Color.BLACK;
            handleBarColor = Color.BLACK;
            this.setAnimateDuration(300);
        }

        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.varanasi))
        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.black_panther))
        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.elephant))
        binding.puzzle.addPiece(ContextCompat.getDrawable(this, R.drawable.tiger))
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