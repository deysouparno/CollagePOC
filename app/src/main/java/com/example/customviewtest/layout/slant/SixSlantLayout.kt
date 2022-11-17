package com.example.customviewtest.layout.slant

import com.xiaopo.flying.puzzle.Line

class SixSlantLayout(private val themeCnt: Int): NumberSlantLayout(themeCnt) {
    override fun layout() {
        when (themeCnt) {
            0 -> {
                addLine(0, Line.Direction.HORIZONTAL, 0.56f, 0.2f)
                addLine(0, Line.Direction.VERTICAL, 0.65f)
                addLine(2, Line.Direction.VERTICAL, 0.35f)
                addLine(2, Line.Direction.HORIZONTAL, 0.56f, 0.4f)
                addLine(2, Line.Direction.VERTICAL, 0.6f)
            }
            1 -> {
                addLine(0, Line.Direction.HORIZONTAL, 0.56f, 0.2f)
                addLine(0, Line.Direction.VERTICAL, 0.65f)
                addLine(2, Line.Direction.VERTICAL, 0.35f)
                addLine(2, Line.Direction.HORIZONTAL, 0.56f, 0.4f)
                addLine(2, Line.Direction.VERTICAL, 0.6f)
            }
        }
    }

    override fun getThemeCount(): Int = 1
}