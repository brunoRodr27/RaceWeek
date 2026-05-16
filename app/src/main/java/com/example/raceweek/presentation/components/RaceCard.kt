package com.example.raceweek.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.presentation.utils.toRaceDateString
import com.example.raceweek.presentation.utils.toRaceTimeString
import com.example.raceweek.ui.theme.*
import com.example.raceweek.R

@Composable
fun RaceCard(
    race: UpcomingRace,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val flagResId = remember(race.flagResName) {
        context.resources.getIdentifier(race.flagResName, "mipmap", context.packageName)
    }
    val dateStr = remember(race.raceTimestamp) { race.raceTimestamp.toRaceDateString() }
    val timeStr = remember(race.raceTimestamp) { race.raceTimestamp.toRaceTimeString() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(3.dp)
                .fillMaxHeight()
                .background(Accent)
        )
        Column(
            modifier = Modifier.padding(start = 18.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (flagResId != 0) {
                    Image(
                        painter = painterResource(id = flagResId),
                        contentDescription = race.name,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Spacer(modifier = Modifier.size(22.dp))
                }
                SeriesBadge(series = race.categoryDescription)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = race.name,
                fontFamily = BreeSerif,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = "${race.country} · ${race.location}",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Border)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    MetaItem(label = stringResource(R.string.date), value = dateStr)
                    MetaItem(label = stringResource(R.string.start), value = timeStr)
                }
            }
        }
    }
}

@Composable
fun SeriesBadge(series: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0x1FD44020))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = series,
            fontSize = 9.sp,
            color = Accent,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MetaItem(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun RaceCardPreview() {
    RaceCard(
        race = UpcomingRace(
            id = "f1_monaco_gp",
            flagResName = "ic_monaco",
            categoryDescription = "Formula 1",
            name = "GP de Mônaco",
            country = "Monte Carlo",
            location = "Circuit de Monaco",
            raceTimestamp = System.currentTimeMillis() + 9 * 24 * 60 * 60 * 1000L + 14 * 3600 * 1000L
        ),
        onClick = {}
    )
}
