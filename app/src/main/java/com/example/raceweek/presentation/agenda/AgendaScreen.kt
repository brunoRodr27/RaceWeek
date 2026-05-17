package com.example.raceweek.presentation.agenda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.raceweek.R
import com.example.raceweek.domain.model.HeroRaceInfo
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.presentation.components.CategoryTabs
import com.example.raceweek.presentation.components.HeroCard
import com.example.raceweek.presentation.components.RaceCard
import com.example.raceweek.ui.theme.Border
import com.example.raceweek.ui.theme.TextMuted

@Composable
fun AgendaScreen(
    categories: List<String>,
    selectedCategory: String,
    allRaces: List<UpcomingRace>,
    heroRaceInfo: HeroRaceInfo?,
    onCategorySelect: (String) -> Unit,
    onRaceClick: (String) -> Unit,
    onHeroExpired: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = categories.indexOf(selectedCategory).coerceAtLeast(0),
        pageCount = { categories.size.coerceAtLeast(1) }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.settledPage) {
        val category = categories.getOrNull(pagerState.settledPage) ?: return@LaunchedEffect
        onCategorySelect(category)
    }

    Column(modifier = modifier.fillMaxSize()) {
        CategoryTabs(
            categories = categories,
            selected = categories.getOrNull(pagerState.currentPage) ?: selectedCategory,
            onSelect = { category ->
                val index = categories.indexOf(category)
                if (index >= 0) scope.launch { pagerState.scrollToPage(index) }
            }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) { page ->
            val category = categories.getOrNull(page) ?: return@HorizontalPager
            val races = if (category == "Todos") allRaces
                        else allRaces.filter { it.categoryDescription == category }
            AgendaPageContent(
                selectedCategory = category,
                races = races,
                heroRaceInfo = if (category == "Todos") heroRaceInfo else null,
                onRaceClick = onRaceClick,
                onHeroExpired = onHeroExpired
            )
        }
    }
}

@Composable
private fun AgendaPageContent(
    selectedCategory: String,
    races: List<UpcomingRace>,
    heroRaceInfo: HeroRaceInfo?,
    onRaceClick: (String) -> Unit,
    onHeroExpired: () -> Unit = {}
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (selectedCategory == "Todos" && heroRaceInfo != null) {
            val hero = heroRaceInfo
            item {
                SectionLabel(
                    text = stringResource(R.string.next_races),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            item {
                HeroCard(
                    heroRace = hero,
                    onClick = { onRaceClick(hero.id) },
                    onExpired = onHeroExpired
                )
            }
        }

        item {
            if (selectedCategory == "Todos") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                        .background(Border)
                )
            } else {
                SectionLabel(
                    text = "$selectedCategory · " + stringResource(R.string.next_races),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        if (races.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_finish),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma corrida agendada\npara esta categoria.",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(races) { race ->
                RaceCard(
                    race = race,
                    onClick = { onRaceClick(race.id) }
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        color = TextMuted,
        letterSpacing = 2.sp,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .padding(bottom = 10.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun AgendaScreenPreview() {
    val races = listOf(
        UpcomingRace(
            id = "f1_monaco_gp",
            flagResName = "ic_monaco",
            categoryDescription = "Formula 1",
            name = "GP de Mônaco",
            country = "Monte Carlo",
            location = "Circuit de Monaco",
            raceTimestamp = System.currentTimeMillis() + 9 * 24 * 60 * 60 * 1000L
        )
    )
    AgendaScreen(
        categories = listOf("Todos", "Formula 1", "MotoGP"),
        selectedCategory = "MotoGP",
        allRaces = races,
        heroRaceInfo = HeroRaceInfo(
            id = "f1_monaco_gp",
            flagResName = "ic_monaco",
            name = "GP de Mônaco",
            country = "Monte Carlo",
            location = "Circuit de Monaco",
            raceTimestamp = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000L
        ),
        onCategorySelect = {},
        onRaceClick = {}
    )
}
