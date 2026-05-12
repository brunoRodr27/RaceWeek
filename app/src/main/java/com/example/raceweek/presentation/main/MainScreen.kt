package com.example.raceweek.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raceweek.presentation.agenda.AgendaScreen
import com.example.raceweek.presentation.agenda.AgendaViewModel
import com.example.raceweek.presentation.calendar.CalendarRoute
import com.example.raceweek.presentation.components.AppSidebar
import com.example.raceweek.presentation.components.BottomNavBar
import com.example.raceweek.presentation.components.BottomTab
import com.example.raceweek.ui.theme.*

@Composable
fun MainRoute(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: AgendaViewModel = hiltViewModel()
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    MainScreen(
        selectedCategory = selectedCategory,
        categories = viewModel.categories,
        races = viewModel.filteredRaces(selectedCategory),
        heroRace = viewModel.races.find { it.isHero },
        calendarRaces = viewModel.calendarRaces,
        onCategorySelect = viewModel::selectCategory,
        onRaceClick = onNavigateToDetail,
        onNavigateToSettings = onNavigateToSettings
    )
}

@Composable
fun MainScreen(
    selectedCategory: String,
    categories: List<String>,
    races: List<com.example.raceweek.domain.model.Race>,
    heroRace: com.example.raceweek.domain.model.Race?,
    calendarRaces: Map<Int, List<com.example.raceweek.domain.model.CalendarEvent>>,
    onCategorySelect: (String) -> Unit,
    onRaceClick: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(BottomTab.AGENDA) }
    var sidebarOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF2A2A2A), Color(0xFF161616), Color(0xFF0D0D0D)),
                    radius = 1200f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            TopBar(
                onMenuClick = { sidebarOpen = true },
                onSettingsClick = onNavigateToSettings
            )

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    BottomTab.AGENDA -> AgendaScreen(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        races = races,
                        heroRace = heroRace,
                        onCategorySelect = onCategorySelect,
                        onRaceClick = onRaceClick
                    )
                    BottomTab.CALENDAR -> CalendarRoute(calendarRaces = calendarRaces)
                }
            }

            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        if (sidebarOpen) {
            AppSidebar(
                isOpen = sidebarOpen,
                onClose = { sidebarOpen = false },
                onNavigateToCalendar = { selectedTab = BottomTab.CALENDAR },
                onNavigateToSettings = onNavigateToSettings
            )
        }
    }
}

@Composable
private fun TopBar(onMenuClick: () -> Unit, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconCircle(onClick = onMenuClick) {
            MenuIcon()
        }
        Text(
            text = "RACEWEEK",
            fontFamily = BreeSerif,
            fontSize = 20.sp,
            color = TextPrimary,
            letterSpacing = 1.sp
        )
        IconCircle(onClick = onSettingsClick) {
            SettingsIcon()
        }
    }
}

@Composable
fun IconCircle(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(BgCircle)
            .border(1.dp, BorderLight, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun MenuIcon() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.width(16.dp).height(1.5.dp).background(Accent))
        Box(modifier = Modifier.width(16.dp).height(1.5.dp).background(TextMuted))
        Box(modifier = Modifier.width(10.dp).height(1.5.dp).background(Accent))
    }
}

@Composable
private fun SettingsIcon() {
    Text(text = "⚙", fontSize = 16.sp, color = TextSecondary)
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        selectedCategory = "Todos",
        categories = listOf("Todos", "Formula 1", "MotoGP"),
        races = emptyList(),
        heroRace = null,
        calendarRaces = emptyMap(),
        onCategorySelect = {},
        onRaceClick = {},
        onNavigateToSettings = {}
    )
}
