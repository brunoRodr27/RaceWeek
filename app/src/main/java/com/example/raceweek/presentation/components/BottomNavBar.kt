package com.example.raceweek.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.R
import com.example.raceweek.ui.theme.Accent
import com.example.raceweek.ui.theme.Border
import com.example.raceweek.ui.theme.TextMuted

enum class BottomTab { AGENDA, CALENDAR }

@Composable
fun BottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Border,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .background(Color(0xF7121212))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = stringResource(R.string.agenda),
                isSelected = selectedTab == BottomTab.AGENDA,
                icon = painterResource(R.drawable.ic_agenda),
                onClick = { onTabSelected(BottomTab.AGENDA) }
            )
            NavItem(
                label = stringResource(R.string.calendar),
                isSelected = selectedTab == BottomTab.CALENDAR,
                icon = painterResource(R.drawable.ic_calendar),
                onClick = { onTabSelected(BottomTab.CALENDAR) }
            )
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun NavItem(
    label: String,
    isSelected: Boolean,
    icon: Painter,
    onClick: () -> Unit
) {
    val color by animateColorAsState(
        targetValue = if (isSelected) Accent else TextMuted,
        label = "navColor"
    )
    val barWidth by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 0.dp,
        label = "barWidth"
    )

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(barWidth)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isSelected) Accent else Color.Transparent)
        )
        CompositionLocalProvider(LocalContentColor provides color) {
            Icon(painter = icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun BottomNavBarPreview() {
    BottomNavBar(selectedTab = BottomTab.AGENDA, onTabSelected = {})
}
