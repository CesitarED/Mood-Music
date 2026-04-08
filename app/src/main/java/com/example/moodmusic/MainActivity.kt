package com.example.moodmusic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.ui.theme.MoodMusicTheme
import kotlinx.coroutines.delay

// -------------------------------------------------------
// Colores del tema (declarados aquí para todo el proyecto)
// -------------------------------------------------------
val ColorFondo     = Color(0xFFF0F0F5)
val ColorAzul      = Color(0xFF1DB8D4)
val ColorMorado    = Color(0xFF8B5CF6)
val ColorTexto     = Color(0xFF1A1A2E)
val ColorSubtexto  = Color(0xFF888888)
val ColorCampo     = Color(0xFFFFFFFF)
val ColorBorde     = Color(0xFFE0E0E8)
val ColorBotonGris = Color(0xFFE8E8EE)

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

// -------------------------------------------------------
// Pantalla de carga con navegación automática
// -------------------------------------------------------
@Composable
fun PantallaCarga() {

    val context = LocalContext.current

    // Navega a InicioSesionActivity después de 3 segundos
    LaunchedEffect(Unit) {
        delay(3000)
        val intent = Intent(context, InicioSesionActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    // Estado de los puntos (PDF 2 - mutableStateOf + remember)
    var puntos by remember { mutableStateOf("") }

    // Animación infinita para los puntos
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

    // Actualiza los puntos según el paso (PDF 2 - Recomposition)
    puntos = when (paso.toInt()) {
        0    -> "."
        1    -> ".."
        2    -> "..."
        else -> ""
    }

    // Column principal (PDF 1 - Layouts)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LogoSplash()

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre de la app
        Text(
            text = "MOOD & MUSIC",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Slogan
        Text(
            text = "Siente tu música",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Puntos animados (PDF 2 - estado reactivo)
        Text(
            text = puntos,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ColorAzul
        )
    }
}

// -------------------------------------------------------
// Logo para el Splash
// -------------------------------------------------------
@Composable
fun LogoSplash() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "♩", fontSize = 28.sp, color = ColorMorado)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "〜〜〜",
                fontSize = 32.sp,
                fontFamily = FontFamily.Cursive,
                color = ColorAzul
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "♪", fontSize = 28.sp, color = ColorMorado)
        }
    }
}

// -------------------------------------------------------
// Preview
// -------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaCargaPreview() {
    MoodMusicTheme {
        PantallaCarga()
    }
}