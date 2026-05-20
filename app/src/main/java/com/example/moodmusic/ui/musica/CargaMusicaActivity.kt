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

import androidx.compose.ui.platform.LocalContext
import com.example.moodmusic.data.model.UsuarioEntity
import android.os.Build

import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModelProvider
import com.example.moodmusic.viewmodel.UsuarioViewModel

class CargaMusicaActivity : ComponentActivity() {

    private val usuarioViewModel: UsuarioViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(UsuarioViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val moodFromIntent = intent.getStringExtra("mood")
        
        setContent {
            MoodMusicTheme {
                val context = LocalContext.current
                val usuarioActual = usuarioViewModel.usuarioActual
                var moodToUse by remember { mutableStateOf(moodFromIntent) }
                
                // Si no viene un mood por intent (desde perfil), lo buscamos en la DB
                LaunchedEffect(usuarioActual) {
                    if (moodToUse == null && usuarioActual != null) {
                        val db = com.example.moodmusic.data.local.database.DatabaseProvider.getDatabase(context)
                        val timeZone = java.util.TimeZone.getTimeZone("America/Bogota")
                        val cal = java.util.Calendar.getInstance(timeZone)
                        
                        val sdfDia = java.text.SimpleDateFormat("d", java.util.Locale("es", "ES")).apply { this.timeZone = timeZone }
                        val sdfMes = java.text.SimpleDateFormat("MMMM", java.util.Locale("es", "ES")).apply { this.timeZone = timeZone }
                        val sdfAnio = java.text.SimpleDateFormat("yyyy", java.util.Locale("es", "ES")).apply { this.timeZone = timeZone }
                        
                        val dia = sdfDia.format(cal.time)
                        val mes = sdfMes.format(cal.time).replaceFirstChar { it.uppercase() }
                        val anio = sdfAnio.format(cal.time)

                        val registro = db.estadoAnimoDao().obtenerRegistroHoy(
                            usuarioActual.username,
                            dia,
                            mes,
                            anio
                        )
                        // Si existe registro hoy, usamos esa emoción. Si no, default a "feliz"
                        moodToUse = registro?.nombreEstado ?: "feliz"
                    }
                }

                PantallaCargaPacman {
                    val intent = Intent(this, MusicaRecomendadaActivity::class.java).apply {
                        // Pasamos el usuario actual y el mood detectado
                        putExtra("usuario", usuarioActual)
                        putExtra("mood", moodToUse ?: "feliz")
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
    // Usamos rememberUpdatedState para que el delay siempre ejecute la versión 
    // más reciente de la función, capturando el moodToUse actualizado.
    val currentOnFinalizar by rememberUpdatedState(onFinalizar)

    LaunchedEffect(Unit) {
        delay(5000)
        currentOnFinalizar()
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
