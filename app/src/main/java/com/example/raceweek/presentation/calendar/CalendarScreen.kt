package com.example.raceweek.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.domain.model.CalendarEvent
import com.example.raceweek.ui.theme.*

@Composable
fun CalendarRoute(
    calendarRaces: Map<Int, List<CalendarEvent>>,
    modifier: Modifier = Modifier
) {
    CalendarScreen(calendarRaces = calendarRaces, modifier = modifier)
}

@Composable
fun CalendarScreen(
    calendarRaces: Map<Int, List<CalendarEvent>>,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    val daysOfWeek = listOf("D", "S", "T", "Q", "Q", "S", "S")
    val firstDayOfMonth = 5
    val daysInMonth = 31
    val prevMonthDays = (firstDayOfMonth - 1 downTo 0).map { 30 - it }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Maio 2026",
                fontFamily = BreeSerif,
                fontSize = 19.sp,
                color = TextPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalNavBtn(label = "‹")
                CalNavBtn(label = "›")
            }
        }

        val allCells: List<Pair<Int?, Boolean>> =
            prevMonthDays.map { Pair(it, false) } +
            (1..daysInMonth).map { Pair(it, true) }

        val totalCells = allCells.size
        val remainder = if (totalCells % 7 == 0) 0 else 7 - totalCells % 7
        val paddedCells = allCells + List(remainder) { Pair<Int?, Boolean>(null, false) }

        val rows = paddedCells.chunked(7)

        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { dow ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = dow, fontSize = 9.sp, color = TextMuted, letterSpacing = 0.5.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (day, isCurrent) ->
                    val hasRace = day != null && isCurrent && calendarRaces.containsKey(day)
                    val isSelected = day != null && isCurrent && selectedDay == day
                    val isToday = day == 4 && isCurrent

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Accent else Color.Transparent)
                            .clickable(enabled = isCurrent && day != null) { selectedDay = day },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day?.toString() ?: "",
                                fontSize = 12.sp,
                                color = when {
                                    isSelected -> Color.White
                                    !isCurrent -> TextMuted.copy(alpha = 0.22f)
                                    isToday -> TextPrimary
                                    hasRace -> TextPrimary
                                    else -> TextSecondary
                                },
                                fontWeight = if (isToday || isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                            if (hasRace) {
                                Box(
                                    modifier = Modifier.size(4.dp).clip(CircleShape)
                                        .background(if (isSelected) Color.White else Accent)
                                )
                            } else if (isToday && !isSelected) {
                                Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(TextSecondary))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (selectedDay != null) "EVENTOS · $selectedDay DE MAIO" else "EVENTOS · SELECIONE UM DIA",
            fontSize = 10.sp,
            color = TextMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val events = selectedDay?.let { calendarRaces[it] }
        if (events != null) {
            events.forEach { event ->
                CalendarEventCard(event = event)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Text(
                text = "Toque num dia com ● para ver eventos",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Composable
private fun CalendarEventCard(event: CalendarEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.width(3.dp).height(34.dp).clip(RoundedCornerShape(2.dp)).background(Accent))
        Text(text = event.flag, fontSize = 16.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = event.name, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(text = "${event.time} · BRT", fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
        }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0x1FD44020)).padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(text = event.series, fontSize = 9.sp, color = Accent)
        }
    }
}

@Composable
private fun CalNavBtn(label: String) {
    Box(
        modifier = Modifier.size(28.dp).clip(CircleShape).background(BgCircle).border(1.dp, Border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 14.sp, color = TextSecondary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun CalendarScreenPreview() {
    CalendarScreen(
        calendarRaces = mapOf(25 to listOf(CalendarEvent("🇲🇨", "GP de Mônaco", "14:00", "F1")))
    )
}
