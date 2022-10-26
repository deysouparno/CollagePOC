package com.example.customviewtest.layout.slant

import com.xiaopo.flying.puzzle.Line

class FourSlantLayout(private val themeCnt: Int): NumberSlantLayout(themeCnt) {
    override fun layout() {
        when (themeCnt) {
            0 -> {
                addLine(0, Line.Direction.VERTICAL, 0.99f, 0.01f)
                addLine(0, Line.Direction.HORIZONTAL, 0.01f, 0.5f)
                addLine(1, Line.Direction.HORIZONTAL, 0.5f, 0.99f)
            }
        }
    }

    override fun getThemeCount(): Int = 1
}