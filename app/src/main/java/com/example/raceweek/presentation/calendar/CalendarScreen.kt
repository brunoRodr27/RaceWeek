package com.example.raceweek.presentation.calendar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.R
import com.example.raceweek.domain.model.CalendarEvent
import com.example.raceweek.presentation.utils.deviceTzAbbr
import com.example.raceweek.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarRoute(
    calendarRaces: Map<LocalDate, List<CalendarEvent>>,
    modifier: Modifier = Modifier
) {
    CalendarScreen(calendarRaces = calendarRaces, modifier = modifier)
}

@Composable
fun CalendarScreen(
    calendarRaces: Map<LocalDate, List<CalendarEvent>>,
    modifier: Modifier = Modifier
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = remember { LocalDate.now() }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }

    val daysOfWeek = listOf("D", "S", "T", "Q", "Q", "S", "S")

    // DayOfWeek.value: Mon=1..Sun=7 → % 7 → Sun=0, Mon=1, ..., Sat=6
    val firstDayIndex = currentYearMonth.atDay(1).dayOfWeek.value % 7
    val daysInMonth = currentYearMonth.lengthOfMonth()
    val prevMonthLength = currentYearMonth.minusMonths(1).lengthOfMonth()

    val allCells: List<Pair<Int?, Boolean>> = run {
        val prev = if (firstDayIndex > 0)
            (prevMonthLength - firstDayIndex + 1..prevMonthLength).map { Pair(it, false) }
        else emptyList()
        val curr = (1..daysInMonth).map { Pair(it, true) }
        prev + curr
    }
    val remainder = if (allCells.size % 7 == 0) 0 else 7 - allCells.size % 7
    val rows = (allCells + List(remainder) { Pair<Int?, Boolean>(null, false) }).chunked(7)

    val monthLabel = remember(currentYearMonth) {
        val name = currentYearMonth.month
            .getDisplayName(TextStyle.FULL_STANDALONE, Locale("pt", "BR"))
            .replaceFirstChar { it.uppercase() }
        "$name ${currentYearMonth.year}"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Cabeçalho: mês/ano + navegação
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthLabel,
                fontFamily = BreeSerif,
                fontSize = 19.sp,
                color = TextPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CalNavBtn(icon = R.drawable.ic_back) {
                    currentYearMonth = currentYearMonth.minusMonths(1)
                    selectedDay = null
                }
                CalNavBtn(icon = R.drawable.ic_next) {
                    currentYearMonth = currentYearMonth.plusMonths(1)
                    selectedDay = null
                }
            }
        }

        // Dias da semana
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { dow ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = dow, fontSize = 9.sp, color = TextMuted, letterSpacing = 0.5.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Grade do calendário
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (day, isCurrent) ->
                    val date = if (isCurrent && day != null) currentYearMonth.atDay(day) else null
                    val hasRace = date != null && calendarRaces.containsKey(date)
                    val isSelected = date != null && date == selectedDay
                    val isToday = date == today

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Accent else Color.Transparent)
                            .clickable(enabled = isCurrent && day != null) { selectedDay = date },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day?.toString() ?: "",
                                fontSize = 12.sp,
                                color = when {
                                    isSelected  -> Color.White
                                    !isCurrent  -> TextMuted.copy(alpha = 0.22f)
                                    hasRace     -> TextPrimary
                                    isToday     -> TextPrimary
                                    else        -> TextSecondary
                                },
                                fontWeight = if (isToday || isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                            when {
                                hasRace -> Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White else Accent)
                                )
                                isToday && !isSelected -> Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .clip(CircleShape)
                                        .background(TextSecondary)
                                )
                                else -> Spacer(modifier = Modifier.size(4.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Seção de eventos
        val eventFormatter = remember { DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("pt", "BR")) }
        val eventLabel = if (selectedDay != null)
            "EVENTOS · ${selectedDay!!.format(eventFormatter).uppercase()}"
        else
            "EVENTOS · SELECIONE UM DIA"

        Text(
            text = eventLabel,
            fontSize = 10.sp,
            color = TextMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val events = selectedDay?.let { calendarRaces[it] }
        when {
            !events.isNullOrEmpty() -> events.forEach { event ->
                CalendarEventCard(event = event)
                Spacer(modifier = Modifier.height(8.dp))
            }
            selectedDay != null -> Text(
                text = "Nenhum evento neste dia.",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            else -> Text(
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
    val context = LocalContext.current
    val flagResId = remember(event.flagResName) {
        context.resources.getIdentifier(event.flagResName, "mipmap", context.packageName)
    }
    val tzAbbr = remember { deviceTzAbbr() }
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
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Accent)
        )
        if (flagResId != 0) {
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.name,
                fontSize = 12.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = event.sessionLabel,
                fontSize = 10.sp,
                color = Accent,
                modifier = Modifier.padding(top = 1.dp)
            )
            Text(
                text = "${event.time} · $tzAbbr",
                fontSize = 10.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0x1FD44020))
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(text = event.series, fontSize = 9.sp, color = Accent)
        }
    }
}

@Composable
private fun CalNavBtn(@DrawableRes icon: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(BgCircle)
            .border(1.dp, Border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Accent
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun CalendarScreenPreview() {
    val today = LocalDate.now()
    CalendarScreen(
        calendarRaces = mapOf(
            today.minusDays(2) to listOf(
                CalendarEvent("ic_monaco", "GP de Mônaco", "10:30", "F1", "Treino Livre 1", 0L),
                CalendarEvent("ic_monaco", "GP de Mônaco", "14:00", "F1", "Treino Livre 2", 1L)
            ),
            today.minusDays(1) to listOf(
                CalendarEvent("ic_monaco", "GP de Mônaco", "11:30", "F1", "Treino Livre 3", 2L),
                CalendarEvent("ic_monaco", "GP de Mônaco", "15:00", "F1", "Classificação", 3L)
            ),
            today to listOf(
                CalendarEvent("ic_monaco", "GP de Mônaco", "14:00", "F1", "Corrida", 4L)
            ),
            today.plusDays(7) to listOf(
                CalendarEvent("ic_spain", "GP da Espanha", "15:00", "F1", "Corrida", 5L)
            )
        )
    )
}
