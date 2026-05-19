package com.example.raceweek.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.raceweek.R
import com.example.raceweek.domain.model.AppSettings
import com.example.raceweek.domain.model.Category
import com.example.raceweek.domain.model.NotificationTime
import com.example.raceweek.presentation.main.IconCircle
import com.example.raceweek.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val categories by viewModel.categories.collectAsState()
    SettingsScreen(
        settings = settings,
        categories = categories,
        onNotificationsChange = viewModel::setNotifications,
        onTimeChange = viewModel::setTime,
        onPracticesChange = viewModel::setPractices,
        onQualifyingsChange = viewModel::setQualifyings,
        onRacesChange = viewModel::setRaces,
        onCategoryToggle = viewModel::toggleCategory,
        onCategoryReorder = viewModel::reorderCategories,
        onBack = onBack
    )
}

@Composable
fun SettingsScreen(
    settings: AppSettings,
    categories: List<Category>,
    onNotificationsChange: (Boolean) -> Unit,
    onTimeChange: (NotificationTime) -> Unit,
    onPracticesChange: (Boolean) -> Unit,
    onQualifyingsChange: (Boolean) -> Unit,
    onRacesChange: (Boolean) -> Unit,
    onCategoryToggle: (id: Int, active: Boolean) -> Unit,
    onCategoryReorder: (orderedIds: List<Int>) -> Unit,
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

                ToggleItem(
                    label = stringResource(R.string.notifications),
                    checked = notificationsEnabled,
                    onToggle = onNotificationsChange
                )

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
                            text = stringResource(R.string.notification_time_label),
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
                                        .border(1.dp, if (selected) Accent else Border, RoundedCornerShape(8.dp))
                                        .clickable(enabled = notificationsEnabled) { onTimeChange(option) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (option) {
                                            NotificationTime.TWO_HOURS -> stringResource(R.string.notif_advance_2h)
                                            NotificationTime.ONE_HOUR -> stringResource(R.string.notif_advance_1h)
                                            NotificationTime.THIRTY_MIN -> stringResource(R.string.notif_advance_30min)
                                        },
                                        fontSize = 11.sp,
                                        color = if (selected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                DependentItem(enabled = notificationsEnabled) {
                    ToggleItem(
                        label = stringResource(R.string.practices),
                        checked = settings.practices,
                        enabled = notificationsEnabled,
                        onToggle = onPracticesChange
                    )
                }

                DependentItem(enabled = notificationsEnabled) {
                    ToggleItem(
                        label = stringResource(R.string.qualifyings),
                        checked = settings.qualifyings,
                        enabled = notificationsEnabled,
                        onToggle = onQualifyingsChange
                    )
                }

                DependentItem(enabled = notificationsEnabled) {
                    ToggleItem(
                        label = stringResource(R.string.races),
                        checked = settings.races,
                        enabled = notificationsEnabled,
                        onToggle = onRacesChange
                    )
                }

            }

            SettingsSection(title = stringResource(R.string.categories)) {
                if (categories.isNotEmpty()) {
                    ReorderableCategoryList(
                        categories = categories,
                        onToggle = onCategoryToggle,
                        onReorder = onCategoryReorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ── Drag-to-reorder ──────────────────────────────────────────────────────────

private val ITEM_HEIGHT = 56.dp
private val ITEM_GAP = 6.dp

@Composable
private fun ReorderableCategoryList(
    categories: List<Category>,
    onToggle: (id: Int, active: Boolean) -> Unit,
    onReorder: (orderedIds: List<Int>) -> Unit
) {
    val density = LocalDensity.current
    val itemTotalPx = with(density) { (ITEM_HEIGHT + ITEM_GAP).toPx() }

    // Lista de trabalho local para feedback visual imediato
    var workingList by remember(categories) { mutableStateOf(categories) }
    var draggingId by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // Sincroniza com DB somente quando não estiver arrastando
    LaunchedEffect(categories) {
        if (draggingId == -1) workingList = categories
    }

    Column(verticalArrangement = Arrangement.spacedBy(ITEM_GAP)) {
        val draggingIdx = workingList.indexOfFirst { it.id == draggingId }
        val targetIdx = if (draggingIdx >= 0) {
            (draggingIdx + (dragOffset / itemTotalPx).roundToInt())
                .coerceIn(0, workingList.size - 1)
        } else -1

        workingList.forEachIndexed { index, category ->
            val isDragging = category.id == draggingId

            val offsetY = when {
                isDragging -> dragOffset
                draggingIdx >= 0 && targetIdx >= 0 -> when {
                    index > draggingIdx && index <= targetIdx -> -itemTotalPx
                    index < draggingIdx && index >= targetIdx -> +itemTotalPx
                    else -> 0f
                }
                else -> 0f
            }

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer { translationY = offsetY }
            ) {
                CategoryRow(
                    category = category,
                    onToggle = { active -> onToggle(category.id, active) },
                    dragModifier = Modifier.pointerInput(category.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingId = category.id
                                dragOffset = 0f
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                dragOffset += amount.y
                            },
                            onDragEnd = {
                                val idx = workingList.indexOfFirst { it.id == draggingId }
                                val tgt = if (idx >= 0) {
                                    (idx + (dragOffset / itemTotalPx).roundToInt())
                                        .coerceIn(0, workingList.size - 1)
                                } else -1
                                if (idx >= 0 && tgt >= 0 && idx != tgt) {
                                    val newList = workingList.toMutableList()
                                    newList.add(tgt, newList.removeAt(idx))
                                    workingList = newList
                                    onReorder(newList.map { it.id })
                                }
                                draggingId = -1
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                draggingId = -1
                                dragOffset = 0f
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    onToggle: (Boolean) -> Unit,
    dragModifier: Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ITEM_HEIGHT)
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ícone de arrastar (2×3 pontos)
        Column(
            modifier = dragModifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            repeat(3) {
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(TextMuted)
                        )
                    }
                }
            }
        }

        Text(
            text = category.description.ifBlank { category.name },
            fontSize = 13.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )

        Toggle(
            checked = category.active,
            onToggle = { onToggle(!category.active) }
        )
    }
}

// ── Componentes compartilhados ────────────────────────────────────────────────

@Composable
private fun DependentItem(enabled: Boolean, content: @Composable () -> Unit) {
    Box(modifier = Modifier.alpha(if (enabled) 1f else 0.38f)) { content() }
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

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        settings = AppSettings(),
        categories = listOf(
            Category(1, "f1", true, "Formula 1", 0),
            Category(2, "motogp", true, "MotoGP", 1),
            Category(3, "indycar", false, "IndyCar", 2)
        ),
        onNotificationsChange = {},
        onTimeChange = {},
        onPracticesChange = {},
        onQualifyingsChange = {},
        onRacesChange = {},
        onCategoryToggle = { _, _ -> },
        onCategoryReorder = {},
        onBack = {}
    )
}
