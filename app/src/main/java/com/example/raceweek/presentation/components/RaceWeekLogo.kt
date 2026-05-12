package com.example.raceweek.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.raceweek.ui.theme.Accent

@Composable
fun RaceWeekLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    color: Color = Accent
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val unit = w / 5.5f

        fun rect(col: Float, row: Float) {
            drawRect(
                color = color,
                topLeft = Offset(col * unit, row * unit),
                size = Size(unit, unit)
            )
        }

        // Row 0: cols 1, 2.5, 3.5
        rect(0.5f, 0.5f)
        rect(2f, 0.5f)
        rect(3.5f, 0.5f)
        // Row 1: cols 1.5, 3
        rect(1.25f, 1.5f)
        rect(2.75f, 1.5f)
        // Row 2: col 2.5, 3.5
        rect(2.5f, 2.5f)
        rect(3.5f, 2.5f)
        // Row 3: col 2.75
        rect(2.75f, 3.5f)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
private fun RaceWeekLogoPreview() {
    RaceWeekLogo(size = 120.dp)
}
