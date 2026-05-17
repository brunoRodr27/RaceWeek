package com.example.raceweek.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.raceweek.domain.model.HeroRaceInfo
import com.example.raceweek.presentation.utils.toCorrectEpochMillis
import com.example.raceweek.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HeroCard(
    heroRace: HeroRaceInfo,
    onClick: () -> Unit,
    onExpired: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val correctRaceEpoch = remember(heroRace.raceTimestamp, heroRace.timezone) {
        heroRace.raceTimestamp.toCorrectEpochMillis(heroRace.timezone)
    }

    // Dispara onExpired ao zerar o countdown, ou imediatamente se o evento já passou
    LaunchedEffect(correctRaceEpoch) {
        val remaining = correctRaceEpoch - System.currentTimeMillis()
        if (remaining > 0) delay(remaining)
        onExpired()
    }

    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        // Sincroniza com a virada de minuto do relógio do dispositivo
        delay(60_000L - System.currentTimeMillis() % 60_000L)
        currentTime = System.currentTimeMillis()
        while (true) {
            delay(60_000L)
            currentTime = System.currentTimeMillis()
        }
    }

    val diff = (correctRaceEpoch - currentTime).coerceAtLeast(0L)
    val totalMinutes = diff / 60_000L
    val days = totalMinutes / (60 * 24)
    val hours = (totalMinutes % (60 * 24)) / 60
    val mins = totalMinutes % 60

    val flagResId = remember(heroRace.flagResName) {
        context.resources.getIdentifier(heroRace.flagResName, "mipmap", context.packageName)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(BgCard)
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.next_event).uppercase(),
                fontSize = 9.sp,
                color = Accent,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (flagResId != 0) {
                    Image(
                        painter = painterResource(id = flagResId),
                        contentDescription = heroRace.name,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = heroRace.name,
                    fontFamily = BreeSerif,
                    fontSize = 21.sp,
                    color = TextPrimary
                )
            }
            Text(
                text = "${heroRace.country} · ${heroRace.location}",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CountBox(
                    value = days.toString().padStart(2, '0'),
                    unit = stringResource(R.string.days)
                )
                CountBox(
                    value = hours.toString().padStart(2, '0'),
                    unit = stringResource(R.string.hours)
                )
                CountBox(
                    value = mins.toString().padStart(2, '0'),
                    unit = stringResource(R.string.minutes)
                )
            }
        }
    }
}

@Composable
fun CountBox(value: String, unit: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x4D000000))
            .border(1.dp, Border, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .defaultMinSize(minWidth = 50.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontFamily = BreeSerif,
                fontSize = 19.sp,
                color = Accent
            )
            Text(
                text = unit.uppercase(),
                fontSize = 9.sp,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun HeroCardPreview() {
    HeroCard(
        heroRace = HeroRaceInfo(
            id = "f1_monaco_gp",
            flagResName = "ic_monaco",
            name = "GP de Mônaco",
            country = "Monte Carlo",
            location = "Circuit de Monaco",
            raceTimestamp = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000L + 14 * 60 * 60 * 1000L
        ),
        onClick = {}
    )
}
