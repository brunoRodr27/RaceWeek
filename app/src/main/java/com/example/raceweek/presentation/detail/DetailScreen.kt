package com.example.raceweek.presentation.detail

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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raceweek.R
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.presentation.agenda.AgendaViewModel
import com.example.raceweek.presentation.utils.toRaceDateString
import com.example.raceweek.presentation.utils.toRaceTimeString
import com.example.raceweek.presentation.utils.toSessionTimeString
import com.example.raceweek.presentation.utils.toSessionDisplayName
import com.example.raceweek.ui.theme.*

@Composable
fun DetailRoute(
    raceId: String,
    onBack: () -> Unit,
    viewModel: AgendaViewModel = hiltViewModel()
) {
    val race = viewModel.getRaceById(raceId)
    if (race != null) {
        DetailScreen(race = race, onBack = onBack)
    }
}

@Composable
fun DetailScreen(
    race: UpcomingRace,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val flagResId = remember(race.flagResName) {
        context.resources.getIdentifier(race.flagResName, "mipmap", context.packageName)
    }
    val dateStr = remember(race.raceTimestamp) { race.raceTimestamp.toRaceDateString() }
    val timeStr = remember(race.raceTimestamp) { race.raceTimestamp.toRaceTimeString() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF2A2020), Color(0xFF151515), Color(0xFF0A0A0A)),
                    radius = 900f
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.clickable(onClick = onBack).padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CompositionLocalProvider() {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Accent)
                }
                Text(
                    text = stringResource(R.string.back),
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (flagResId != 0) {
                    Image(
                        painter = painterResource(id = flagResId),
                        contentDescription = race.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = race.name,
                    fontFamily = BreeSerif,
                    fontSize = 22.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${race.country} · ${race.location}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color(0x1FD44020))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = race.categoryDescription.uppercase(),
                        fontSize = 9.sp,
                        color = Accent,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        label = stringResource(R.string.date),
                        value = dateStr,
                        modifier = Modifier.weight(1f))
                    StatCard(
                        label = stringResource(R.string.start),
                        value = timeStr,
                        modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        label = stringResource(R.string.laps),
                        value = race.laps?.toString() ?: "–",
                        modifier = Modifier.weight(1f))
                    StatCard(
                        label = stringResource(R.string.weather),
                        value = "",
                        modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.schedule).uppercase(),
                    fontSize = 10.sp,
                    color = TextMuted,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val regularSessions = race.sessions.filter { it.key != "race" }
                val raceSession = race.sessions.find { it.key == "race" }

                regularSessions.forEach { session ->
                    SessionItem(
                        name = session.key.toSessionDisplayName(),
                        time = session.timestamp.toSessionTimeString()
                    )
                }

                if (raceSession != null) {
                    val raceTimeStr = raceSession.timestamp.toSessionTimeString()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = raceSession.key.toSessionDisplayName(), fontSize = 13.sp, color = Accent, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = raceTimeStr, fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(BgCard)
            .border(1.dp, Border, RoundedCornerShape(12.dp)).padding(12.dp)
    ) {
        Text(text = label.uppercase(), fontSize = 9.sp, color = TextMuted, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = value, fontFamily = BreeSerif, fontSize = 17.sp, color = TextPrimary)
    }
}

@Composable
private fun SessionItem(name: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp).drawBehind {
            val y = size.height
            drawLine(color = Border, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1.dp.toPx())
        },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontSize = 13.sp, color = TextPrimary)
        Text(text = time, fontSize = 11.sp, color = TextMuted)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun DetailScreenPreview() {
    DetailScreen(
        race = UpcomingRace(
            id = "f1_monaco_gp",
            flagResName = "ic_monaco",
            categoryDescription = "Formula 1",
            name = "GP de Mônaco",
            country = "Monte Carlo",
            location = "Circuit de Monaco",
            raceTimestamp = System.currentTimeMillis() + 9 * 24 * 60 * 60 * 1000L + 14 * 3600 * 1000L
        ),
        onBack = {}
    )
}
