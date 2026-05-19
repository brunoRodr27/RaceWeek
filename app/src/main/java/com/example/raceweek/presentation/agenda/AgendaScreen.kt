package com.example.raceweek.presentation.agenda

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.raceweek.ui.theme.Accent
import com.example.raceweek.ui.theme.BgCard
import com.example.raceweek.ui.theme.Border
import com.example.raceweek.ui.theme.BorderLight
import com.example.raceweek.ui.theme.TextMuted

@Composable
fun AgendaScreen(
    categories: List<String>,
    selectedCategory: String,
    allRaces: List<UpcomingRace>,
    heroRaceInfo: HeroRaceInfo?,
    isLoading: Boolean,
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
            val races = if (category == CATEGORY_ALL) allRaces
                        else allRaces.filter { it.categoryDescription == category }
            AgendaPageContent(
                selectedCategory = category,
                races = races,
                heroRaceInfo = if (category == CATEGORY_ALL) heroRaceInfo else null,
                isLoading = isLoading,
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
    isLoading: Boolean,
    onRaceClick: (String) -> Unit,
    onHeroExpired: () -> Unit = {}
) {
    val shimmerBrush = rememberShimmerBrush()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (isLoading) {
            if (selectedCategory == CATEGORY_ALL) {
                item {
                    SectionLabel(
                        text = stringResource(R.string.next_races),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                item { HeroCardSkeleton(shimmerBrush) }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(1.dp)
                            .background(Border)
                    )
                }
            } else {
                item {
                    SectionLabel(
                        text = "$selectedCategory · ${stringResource(R.string.next_races)}",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
            repeat(4) {
                item { RaceCardSkeleton(shimmerBrush) }
            }
        } else {
            if (selectedCategory == CATEGORY_ALL && heroRaceInfo != null) {
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
                if (selectedCategory == CATEGORY_ALL) {
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
                                text = stringResource(R.string.no_races_scheduled),
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
}

// ── Shimmer ───────────────────────────────────────────────────────────────────

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFF1E1E1E),
            Color(0xFF2D2D2D),
            Color(0xFF1E1E1E),
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 600f, 0f)
    )
}

// ── Skeletons ─────────────────────────────────────────────────────────────────

@Composable
private fun HeroCardSkeleton(brush: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Label
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            // Flag + título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
            // Localização
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Caixas de countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}

@Composable
private fun RaceCardSkeleton(brush: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
    ) {
        // Barra lateral de destaque
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(3.dp)
                .fillMaxHeight()
                .background(Accent.copy(alpha = 0.25f))
        )
        Column(
            modifier = Modifier.padding(start = 18.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Flag + badge de categoria
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
            // Nome da corrida
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            // Localização
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border)
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Metadados: data e horário
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(2) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(9.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(brush)
                        )
                        Box(
                            modifier = Modifier
                                .width(52.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(brush)
                        )
                    }
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

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

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun AgendaSkeletonPreview() {
    AgendaScreen(
        categories = listOf("Todos", "Formula 1", "MotoGP"),
        selectedCategory = "Todos",
        allRaces = emptyList(),
        heroRaceInfo = null,
        isLoading = true,
        onCategorySelect = {},
        onRaceClick = {}
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
        isLoading = false,
        onCategorySelect = {},
        onRaceClick = {}
    )
}
