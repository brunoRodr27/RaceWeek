package com.example.raceweek.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raceweek.R
import com.example.raceweek.domain.model.AppSettings
import com.example.raceweek.domain.model.NotificationTime
import com.example.raceweek.presentation.main.IconCircle
import com.example.raceweek.ui.theme.*

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    SettingsScreen(
        settings = settings,
        onNotificationsChange = viewModel::setNotifications,
        onTimeChange = viewModel::setTime,
        onPracticesChange = viewModel::setPractices,
        onQualifyingsChange = viewModel::setQualifyings,
        onRacesChange = viewModel::setRaces,
        onBack = onBack
    )
}

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onNotificationsChange: (Boolean) -> Unit,
    onTimeChange: (NotificationTime) -> Unit,
    onPracticesChange: (Boolean) -> Unit,
    onQualifyingsChange: (Boolean) -> Unit,
    onRacesChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notificationsEnabled = settings.notifications

    Column(
        modifier = modifier.fillMaxSize().background(
            Brush.radialGradient(
                colors = listOf(Color(0xFF1A2020), Color(0xFF131313), Color(0xFF0A0A0A)),
                radius = 900f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconCircle(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Accent
                )
            }
            Text(
                text = stringResource(R.string.settings).uppercase(),
                fontFamily = BreeSerif,
                fontSize = 19.sp,
                color = TextPrimary
            )
        }

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {

            SettingsSection(title = stringResource(R.string.notifications)) {

                // Notificações (toggle principal)
                ToggleItem(
                    label = "Notificações",
                    checked = notificationsEnabled,
                    onToggle = onNotificationsChange
                )

                // Tempo de antecedência (segmented control)
                DependentItem(enabled = notificationsEnabled) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BgCard)
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 13.dp)
                    ) {
                        Text(
                            text = "Tempo de notificação",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            NotificationTime.entries.forEach { option ->
                                val selected = settings.time == option
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Accent else BgCircle)
                                        .border(
                                            1.dp,
                                            if (selected) Accent else Border,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = notificationsEnabled) {
                                            onTimeChange(option)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option.label,
                                        fontSize = 11.sp,
                                        color = if (selected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Treinos Livres
                DependentItem(enabled = notificationsEnabled) {
                    ToggleItem(
                        label = "Treinos Livres",
                        checked = settings.practices,
                        enabled = notificationsEnabled,
                        onToggle = onPracticesChange
                    )
                }

                // Classificações
                DependentItem(enabled = notificationsEnabled) {
                    ToggleItem(
                        label = "Classificações",
                        checked = settings.qualifyings,
                        enabled = notificationsEnabled,
                        onToggle = onQualifyingsChange
                    )
                }

                // Corridas
                DependentItem(enabled = notificationsEnabled) {
                    ToggleItem(
                        label = "Corridas",
                        checked = settings.races,
                        enabled = notificationsEnabled,
                        onToggle = onRacesChange
                    )
                }
            }

            SettingsSection(title = stringResource(R.string.categories)) {
                ValueItem("Formula 1", "✓ Ativo")
                ValueItem("MotoGP", "✓ Ativo")
                ValueItem("IndyCar", "✓ Ativo")
                ValueItem("WEC / Le Mans", "✓ Ativo")
                ValueItem("Formula E", "✓ Ativo")
                ValueItem("NASCAR", "Desativado", valueColor = TextMuted)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Wrapper que aplica alpha e bloqueia interação quando desabilitado
@Composable
private fun DependentItem(enabled: Boolean, content: @Composable () -> Unit) {
    Box(modifier = Modifier.alpha(if (enabled) 1f else 0.38f)) {
        content()
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 22.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 10.sp,
            color = Accent,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun ToggleItem(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onToggle(!checked) }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextPrimary)
        Toggle(checked = checked, enabled = enabled, onToggle = { onToggle(!checked) })
    }
}

@Composable
private fun Toggle(checked: Boolean, enabled: Boolean = true, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .width(38.dp)
            .height(21.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (checked) Accent else BgCircle)
            .clickable(enabled = enabled, onClick = onToggle)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
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
    SettingsScreen(
        settings = AppSettings(),
        onNotificationsChange = {},
        onTimeChange = {},
        onPracticesChange = {},
        onQualifyingsChange = {},
        onRacesChange = {},
        onBack = {}
    )
}
