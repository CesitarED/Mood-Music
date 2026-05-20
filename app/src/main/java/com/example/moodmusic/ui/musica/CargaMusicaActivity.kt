package com.example.moodmusic.ui.musica

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.ui.main.ColorAzul
import com.example.moodmusic.ui.main.ColorFondo
import com.example.moodmusic.ui.main.ColorMorado
import com.example.moodmusic.ui.main.ColorTexto
import com.example.moodmusic.ui.theme.MoodMusicTheme
import kotlinx.coroutines.delay

import com.example.moodmusic.data.model.UsuarioEntity
import android.os.Build

class CargaMusicaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val usuario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("usuario", UsuarioEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("usuario") as? UsuarioEntity
        }

        val mood = intent.getStringExtra("mood") ?: "happy"

        setContent {
            MoodMusicTheme {
                PantallaCargaPacman {
                    val intent = Intent(this, MusicaRecomendadaActivity::class.java).apply {
                        putExtra("usuario", usuario)
                        putExtra("mood", mood)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

@Composable
fun PantallaCargaPacman(onFinalizar: () -> Unit) {
    // Timer de 5 segundos
    LaunchedEffect(Unit) {
        delay(5000)
        onFinalizar()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PacmanAnimation()

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Buscando música para ti...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto
        )
    }
}

@Composable
fun PacmanAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pacman")

    // Animación de la boca (ángulo de apertura)
    val bocaAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 45f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "boca"
    )

    // Animación de los puntos (desplazamiento)
    val puntoOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "puntos"
    )

    Canvas(modifier = Modifier.size(200.dp, 100.dp)) {
        val sizePacman = 80.dp.toPx()
        val centerY = size.height / 2
        val centerX = size.width / 4

        // Dibujar los puntos
        val puntoRadio = 8.dp.toPx()
        val espacioEntrePuntos = 40.dp.toPx()
        
        for (i in 0..3) {
            val x = (centerX + 60.dp.toPx()) + (i * espacioEntrePuntos) - puntoOffset.dp.toPx()
            if (x > centerX) { // Solo dibujar si está delante de pacman
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.6f),
                    radius = puntoRadio,
                    center = Offset(x, centerY)
                )
            }
        }

        // Dibujar Pacman
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4AC7FA), Color(0xFF9D59FF)), // Colores más vibrantes según la imagen
                start = Offset(centerX - sizePacman / 2, centerY),
                end = Offset(centerX + sizePacman / 2, centerY)
            ),
            startAngle = bocaAngle,
            sweepAngle = 360f - (bocaAngle * 2),
            useCenter = true,
            topLeft = Offset(centerX - sizePacman / 2, centerY - sizePacman / 2),
            size = Size(sizePacman, sizePacman),
            style = Fill
        )
    }
}
