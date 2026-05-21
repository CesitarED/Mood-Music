package com.example.moodmusic.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.R
import com.example.moodmusic.ui.auth.InicioSesionActivity
import com.example.moodmusic.ui.theme.*
import kotlinx.coroutines.delay

// -------------------------------------------------------
// MainActivity - Pantalla de carga (Splash)
// -------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMusicTheme {
                PantallaCarga()
            }
        }
    }
}

@Composable
fun PantallaCarga() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000)
        // Ahora siempre enviamos al login al iniciar la app
        val intent = Intent(context, InicioSesionActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    var puntos by remember { mutableStateOf("") }
    val infiniteTransition = rememberInfiniteTransition(label = "puntos")
    val paso by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 3f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "paso"
    )

    puntos = when (paso.toInt()) {
        0    -> "."
        1    -> ".."
        2    -> "..."
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoSplash()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Siente tu música",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTexto
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = puntos,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ColorAzul
        )
    }
}

@Composable
fun LogoSplash() {
    val isDark = LocalIsDarkTheme.current
    val logoRes = if (isDark) R.drawable.logo_app_oscuro else R.drawable.logo_app_claro

    Image(
        painter = painterResource(id = logoRes),
        contentDescription = "Logo Mood & Music",
        modifier = Modifier
            .width(280.dp)
            .wrapContentHeight()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaCargaPreview() {
    MoodMusicTheme {
        PantallaCarga()
    }
}
