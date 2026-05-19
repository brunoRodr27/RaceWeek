package com.example.raceweek.presentation.calendar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.R
import com.example.raceweek.domain.model.CalendarEvent
import com.example.raceweek.presentation.utils.deviceTzAbbr
import com.example.raceweek.ui.theme.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// 10 anos para cada direção a partir do mês atual
private const val PAGER_HALF = 120

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
    val baseMonth = remember { YearMonth.now().minusMonths(PAGER_HALF.toLong()) }
    val pagerState = rememberPagerState(
        initialPage = PAGER_HALF,
        pageCount = { PAGER_HALF * 2 + 1 }
    )
    val today = remember { LocalDate.now() }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    val scope = rememberCoroutineScope()

    // Rótulo do mês derivado diretamente do estado do pager — atualiza em tempo real
    // enquanto o usuário arrasta, sem recomposição desnecessária de toda a tela.
    val monthLabel by remember {
        derivedStateOf {
            val ym = baseMonth.plusMonths(pagerState.currentPage.toLong())
            val name = ym.month
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale("pt", "BR"))
                .replaceFirstChar { it.uppercase() }
            "$name ${ym.year}"
        }
    }

    // Limpa o dia selecionado apenas quando a navegação de mês se consolida (swipe completo)
    LaunchedEffect(pagerState.settledPage) { selectedDay = null }

    val daysOfWeek = remember {
        val locale = Locale("pt", "BR")
        listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        ).map { it.getDisplayName(TextStyle.NARROW, locale) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Cabeçalho: mês/ano + botões de navegação
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
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
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
                CalNavBtn(icon = R.drawable.ic_next) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }
        }

        // Cabeçalho dos dias da semana — fixo, fora do pager
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { dow ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = dow, fontSize = 9.sp, color = TextMuted, letterSpacing = 0.5.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Grade do calendário — deslize horizontalmente para navegar entre meses.
        // beyondViewportPageCount = 1 mantém o mês anterior e o próximo compostos,
        // eliminando qualquer lag no início do arrasto.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            beyondViewportPageCount = 1
        ) { page ->
            MonthGrid(
                yearMonth = baseMonth.plusMonths(page.toLong()),
                calendarRaces = calendarRaces,
                today = today,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Seção de eventos do dia selecionado
        val eventFormatter = remember {
            DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("pt", "BR"))
        }
        val eventLabel = if (selectedDay != null)
            stringResource(R.string.events_on_date, selectedDay!!.format(eventFormatter).uppercase())
        else
            stringResource(R.string.events_select_day).uppercase()

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
                text = stringResource(R.string.no_events_this_day),
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            else -> Text(
                text = stringResource(R.string.tap_day_hint),
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

// ── Grade mensal ──────────────────────────────────────────────────────────────

@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    calendarRaces: Map<LocalDate, List<CalendarEvent>>,
    today: LocalDate,
    selectedDay: LocalDate?,
    onDaySelected: (LocalDate?) -> Unit
) {
    // DayOfWeek.value: Seg=1..Dom=7 → % 7 → Dom=0, Seg=1, ..., Sáb=6
    val firstDayIndex = yearMonth.atDay(1).dayOfWeek.value % 7
    val daysInMonth = yearMonth.lengthOfMonth()
    val prevMonthLength = yearMonth.minusMonths(1).lengthOfMonth()

    // Sempre 42 células (6 linhas × 7 colunas) para manter altura constante entre meses
    // e evitar saltos de layout durante o swipe.
    val allCells: List<Pair<Int?, Boolean>> = buildList {
        repeat(firstDayIndex) { i ->
            add(Pair(prevMonthLength - firstDayIndex + 1 + i, false))
        }
        repeat(daysInMonth) { add(Pair(it + 1, true)) }
        repeat(42 - size) { add(Pair(null, false)) }
    }

    Column {
        allCells.chunked(7).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { (day, isCurrent) ->
                    val date = if (isCurrent && day != null) yearMonth.atDay(day) else null
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
                            .clickable(enabled = isCurrent && day != null) { onDaySelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day?.toString() ?: "",
                                fontSize = 12.sp,
                                color = when {
                                    isSelected -> Color.White
                                    !isCurrent -> TextMuted.copy(alpha = 0.22f)
                                    hasRace    -> TextPrimary
                                    isToday    -> TextPrimary
                                    else       -> TextSecondary
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
    }
}

// ── Componentes ───────────────────────────────────────────────────────────────

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
                text = stringResource(R.string.time_timezone_format, event.time, tzAbbr),
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

// ── Preview ───────────────────────────────────────────────────────────────────

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
