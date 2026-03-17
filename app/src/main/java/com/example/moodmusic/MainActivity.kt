package com.example.moodmusic

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.ui.theme.MoodMusicTheme
import kotlinx.coroutines.delay

// -------------------------------------------------------
// Colores del tema
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
                PantallaCarga(
                    onCargaTerminada = {
                        // Navegación entre Activities con Intent
                        val intent = Intent(this, InicioSesion::class.java)
                        startActivity(intent)
                        // Cerramos el splash para que no quede en el back stack
                        finish()
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------
// Pantalla de carga
// -------------------------------------------------------
@Composable
fun PantallaCarga(
    onCargaTerminada: () -> Unit = {}
) {
    // Estado reactivo con remember y mutableStateOf
    var puntos by remember { mutableStateOf("") }

    // LaunchedEffect ejecuta código suspendido una sola vez
    // cuando el composable entra en pantalla
    LaunchedEffect(Unit) {
        delay(3000L) // espera 3 segundos
        onCargaTerminada()
    }

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

    //  Recomposition: actualiza los puntos según el paso
    puntos = when (paso.toInt()) {
        0    -> "."
        1    -> ".."
        2    -> "..."
        else -> ""
    }

    // Layouts: Column principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LogoSplash()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MOOD & MUSIC",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Siente tu música",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Estado reactivo: puntos animados
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
                text = "Mood&Music",
                fontSize = 18.sp,
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