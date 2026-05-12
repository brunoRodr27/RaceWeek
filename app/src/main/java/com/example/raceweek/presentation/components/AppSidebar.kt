package com.example.raceweek.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.ui.theme.*

@Composable
fun AppSidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX by animateDpAsState(
        targetValue = if (isOpen) 0.dp else (-265).dp,
        animationSpec = tween(durationMillis = 350),
        label = "sidebarOffset"
    )

    Box(modifier = modifier.fillMaxSize()) {
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .clickable(onClick = onClose)
            )
        }

        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(265.dp)
                .fillMaxHeight()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF252525), Color(0xFF161616), Color(0xFF0E0E0E))
                    )
                )
                .border(
                    width = 1.dp,
                    color = BorderLight,
                    shape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
                )
                .padding(top = 60.dp, bottom = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgCard),
                        contentAlignment = Alignment.Center
                    ) {
                        RaceWeekLogo(size = 22.dp)
                    }
                    Text(
                        text = "RaceWeek",
                        fontFamily = BreeSerif,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                        .background(Border)
                )
                Spacer(modifier = Modifier.height(18.dp))

                SidebarItem(emoji = "🏎", label = "Agenda", isActive = true, onClick = onClose)
                SidebarItem(emoji = "📅", label = "Calendário", onClick = {
                    onClose()
                    onNavigateToCalendar()
                })

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                        .background(Border)
                )
                Spacer(modifier = Modifier.height(10.dp))

                SidebarItem(emoji = "🏆", label = "Campeonatos", onClick = {})
                SidebarItem(emoji = "⏱", label = "Resultados", onClick = {})
                SidebarItem(emoji = "⭐", label = "Favoritos", onClick = {})

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                        .background(Border)
                )
                Spacer(modifier = Modifier.height(10.dp))

                SidebarItem(emoji = "🔔", label = "Notificações", onClick = {})
                SidebarItem(emoji = "⚙️", label = "Configurações", onClick = {
                    onClose()
                    onNavigateToSettings()
                })
            }
        }
    }
}

@Composable
private fun SidebarItem(
    emoji: String,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isActive) Color(0x14D44020) else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text(text = emoji, fontSize = 15.sp, modifier = Modifier.width(18.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isActive) Accent else TextSecondary,
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun AppSidebarPreview() {
    AppSidebar(
        isOpen = true,
        onClose = {},
        onNavigateToCalendar = {},
        onNavigateToSettings = {}
    )
}
