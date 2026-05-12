package com.example.raceweek.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.raceweek.presentation.main.IconCircle
import com.example.raceweek.ui.theme.*

@Composable
fun SettingsRoute(onBack: () -> Unit) {
    SettingsScreen(onBack = onBack)
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().background(
            Brush.radialGradient(
                colors = listOf(Color(0xFF1A2020), Color(0xFF131313), Color(0xFF0A0A0A)),
                radius = 900f
            )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconCircle(onClick = onBack) {
                Text(text = "‹", fontSize = 20.sp, color = TextSecondary)
            }
            Text(text = "Configurações", fontFamily = BreeSerif, fontSize = 19.sp, color = TextPrimary)
        }

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            SettingsSection(title = "Notificações") {
                var alertas by remember { mutableStateOf(true) }
                var aviso by remember { mutableStateOf(true) }
                var resultados by remember { mutableStateOf(false) }

                ToggleItem("Alertas de corrida", alertas) { alertas = it }
                ToggleItem("Aviso 1h antes", aviso) { aviso = it }
                ToggleItem("Resultados ao vivo", resultados) { resultados = it }
            }

            SettingsSection(title = "Campeonatos") {
                ValueItem("Formula 1", "✓ Ativo")
                ValueItem("MotoGP", "✓ Ativo")
                ValueItem("IndyCar", "✓ Ativo")
                ValueItem("WEC / Le Mans", "✓ Ativo")
                ValueItem("Formula E", "✓ Ativo")
                ValueItem("NASCAR", "Desativado", valueColor = TextMuted)
            }

            SettingsSection(title = "Preferências") {
                ValueItem("Fuso horário", "BRT −3")
                ValueItem("Idioma", "Português")
                ValueItem("Versão", "1.0.0", valueColor = TextMuted)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 22.dp)) {
        Text(text = title.uppercase(), fontSize = 10.sp, color = Accent, letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 8.dp))
        content()
    }
}

@Composable
private fun ToggleItem(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).clip(RoundedCornerShape(12.dp))
            .background(BgCard).border(1.dp, Border, RoundedCornerShape(12.dp))
            .clickable { onToggle(!checked) }.padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextPrimary)
        Toggle(checked = checked, onToggle = { onToggle(!checked) })
    }
}

@Composable
private fun Toggle(checked: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier.width(38.dp).height(21.dp).clip(RoundedCornerShape(11.dp))
            .background(if (checked) Accent else BgCircle).clickable(onClick = onToggle)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(2.5.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun ValueItem(label: String, value: String, valueColor: Color = TextSecondary) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).clip(RoundedCornerShape(12.dp))
            .background(BgCard).border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextPrimary)
        Text(text = value, fontSize = 12.sp, color = valueColor)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(onBack = {})
}
