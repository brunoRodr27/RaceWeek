package com.example.raceweek.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import kotlinx.coroutines.delay

@Composable
fun HeroCard(race: Race, modifier: Modifier = Modifier) {
    var days by remember { mutableStateOf("02") }
    var hours by remember { mutableStateOf("14") }
    var mins by remember { mutableStateOf("38") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BgCard)
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = "↑ PRÓXIMO EVENTO",
                fontSize = 9.sp,
                color = Accent,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${race.flag} ${race.name}",
                fontFamily = BreeSerif,
                fontSize = 21.sp,
                color = TextPrimary
            )
            Text(
                text = race.location,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CountBox(value = days, unit = "Dias")
                CountBox(value = hours, unit = "Horas")
                CountBox(value = mins, unit = "Min")
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
        race = Race(
            id = "monaco",
            category = "Formula 1",
            flag = "🇲🇨",
            name = "GP de Mônaco",
            location = "Circuit de Monaco · Monte Carlo",
            date = "Dom, 25 Mai",
            time = "14:00",
            weatherIcon = "☀️",
            temperature = "22°C",
            isHero = true
        )
    )
}
