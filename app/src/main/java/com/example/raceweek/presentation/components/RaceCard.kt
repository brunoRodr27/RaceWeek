package com.example.raceweek.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.domain.model.Race
import com.example.raceweek.ui.theme.*

@Composable
fun RaceCard(
    race: Race,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                Text(text = race.flag, fontSize = 20.sp)
                SeriesBadge(series = race.category)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = race.name,
                fontFamily = BreeSerif,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = race.location,
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
                    MetaItem(label = "Data", value = race.date)
                    MetaItem(label = "Largada", value = race.time)
                }
                WeatherBadge(icon = race.weatherIcon, temp = race.temperature)
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

@Composable
fun WeatherBadge(icon: String, temp: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x0AFFFFFF))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 13.sp)
        Text(text = temp, fontSize = 11.sp, color = TextSecondary)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF161616)
@Composable
private fun RaceCardPreview() {
    RaceCard(
        race = Race(
            id = "monaco",
            category = "Formula 1",
            flag = "🇲🇨",
            name = "GP de Mônaco",
            location = "Circuit de Monaco · Monte Carlo",
            date = "Dom, 25 Mai",
            time = "14:00",
            weatherIcon = "☀️",
            temperature = "22°C"
        ),
        onClick = {}
    )
}
