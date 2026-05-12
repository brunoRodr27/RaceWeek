package com.example.raceweek.presentation.agenda

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.domain.model.Race
import com.example.raceweek.presentation.components.CategoryTabs
import com.example.raceweek.presentation.components.HeroCard
import com.example.raceweek.presentation.components.RaceCard
import com.example.raceweek.ui.theme.TextMuted

@Composable
fun AgendaScreen(
    categories: List<String>,
    selectedCategory: String,
    races: List<Race>,
    heroRace: Race?,
    onCategorySelect: (String) -> Unit,
    onRaceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        CategoryTabs(
            categories = categories,
            selected = selectedCategory,
            onSelect = onCategorySelect
        )
        LazyColumn(
            contentPadding = PaddingValues(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (selectedCategory == "Todos" && heroRace != null) {
                item {
                    SectionLabel(
                        text = "Próxima Corrida",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                item { HeroCard(race = heroRace) }
            }

            item {
                SectionLabel(
                    text = if (selectedCategory == "Todos") "Agenda da Semana"
                    else "$selectedCategory · Próximas Corridas",
                    modifier = Modifier.padding(top = if (selectedCategory == "Todos") 4.dp else 12.dp)
                )
            }

            if (races.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text(text = "🏁", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nenhuma corrida agendada\npara esta categoria.",
                                fontSize = 13.sp,
                                color = TextMuted
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
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        color = TextMuted,
        letterSpacing = 2.sp,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 0.dp)
            .padding(bottom = 10.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun AgendaScreenPreview() {
    val races = listOf(
        Race(
            id = "monaco",
            category = "Formula 1",
            flag = "🇲🇨",
            name = "GP de Mônaco",
            location = "Circuit de Monaco",
            date = "Dom, 25 Mai",
            time = "14:00",
            weatherIcon = "☀️",
            temperature = "22°C",
            isHero = true
        )
    )
    AgendaScreen(
        categories = listOf("Todos", "Formula 1", "MotoGP"),
        selectedCategory = "Todos",
        races = races,
        heroRace = races.first(),
        onCategorySelect = {},
        onRaceClick = {}
    )
}
