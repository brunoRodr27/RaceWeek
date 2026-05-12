package com.example.raceweek.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.ui.theme.Accent
import com.example.raceweek.ui.theme.Border
import com.example.raceweek.ui.theme.TextMuted
import com.example.raceweek.ui.theme.TextSecondary

@Composable
fun CategoryTabs(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Border,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(categories) { cat ->
                val isActive = cat == selected
                Box(
                    modifier = Modifier
                        .clickable { onSelect(cat) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .drawBehind {
                            if (isActive) {
                                drawLine(
                                    color = Accent,
                                    start = Offset(0f, size.height + 10.dp.toPx()),
                                    end = Offset(size.width, size.height + 10.dp.toPx()),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        color = if (isActive) Accent else TextMuted,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun CategoryTabsPreview() {
    CategoryTabs(
        categories = listOf("Todos", "Formula 1", "MotoGP", "IndyCar", "Formula E", "WEC", "NASCAR"),
        selected = "Formula 1",
        onSelect = {}
    )
}
