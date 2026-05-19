package com.example.raceweek.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raceweek.R
import com.example.raceweek.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1400)
        onSplashFinished()
    }
    SplashScreen()
}

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val scale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF2C2C2C), Color(0xFF141414), Color(0xFF080808)),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_transparent),
                contentDescription = null,
                modifier = Modifier.scale(scale)
                    .size(140.dp)
            )
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                fontFamily = BreeSerif,
                fontSize = 30.sp,
                color = TextPrimary,
                letterSpacing = 2.sp,

            )
            Text(
                text = stringResource(R.string.description_app).uppercase(),
                fontSize = 11.sp,
                color = TextSecondary,
                letterSpacing = 4.sp
            )
            LoaderBar()
        }
    }
}

@Composable
private fun LoaderBar() {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(1300))
    }
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .width(60.dp)
            .height(2.dp)
            .background(BgCircle)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.value)
                .background(Accent)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080808)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}
