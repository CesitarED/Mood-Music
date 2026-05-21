package com.example.moodmusic.ui.musica

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.moodmusic.data.local.database.DatabaseProvider
import com.example.moodmusic.ui.theme.ColorAzul
import com.example.moodmusic.ui.theme.ColorCampo
import com.example.moodmusic.ui.theme.ColorFondo
import com.example.moodmusic.ui.theme.ColorMorado
import com.example.moodmusic.ui.theme.ColorTexto
import com.example.moodmusic.ui.theme.MoodMusicTheme
import com.example.moodmusic.viewmodel.UsuarioViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.delay

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

                LaunchedEffect(usuarioActual) {
                    if (moodToUse == null && usuarioActual != null) {
                        val db = DatabaseProvider.getDatabase(context)
                        val timeZone = TimeZone.getTimeZone("America/Bogota")
                        val cal = Calendar.getInstance(timeZone)

                        val sdfDia = SimpleDateFormat("d", Locale("es", "ES")).apply {
                            this.timeZone = timeZone
                        }
                        val sdfMes = SimpleDateFormat("MMMM", Locale("es", "ES")).apply {
                            this.timeZone = timeZone
                        }
                        val sdfAnio = SimpleDateFormat("yyyy", Locale("es", "ES")).apply {
                            this.timeZone = timeZone
                        }

                        val dia = sdfDia.format(cal.time)
                        val mes = sdfMes.format(cal.time).replaceFirstChar { it.uppercase() }
                        val anio = sdfAnio.format(cal.time)

                        val registro = db.estadoAnimoDao().obtenerRegistroHoy(
                            usuarioActual.username,
                            dia,
                            mes,
                            anio
                        )
                        moodToUse = registro?.nombreEstado ?: "feliz"
                    }
                }

                PantallaCargaMusica(
                    hasConnection = { context.hasInternetConnection() },
                    onFinalizar = {
                        val intent = Intent(this, MusicaRecomendadaActivity::class.java).apply {
                            putExtra("usuario", usuarioActual)
                            putExtra("mood", moodToUse ?: "feliz")
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

private fun Context.hasInternetConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

@Composable
fun PantallaCargaMusica(
    hasConnection: () -> Boolean,
    onFinalizar: () -> Unit
) {
    var sinConexion by remember { mutableStateOf(!hasConnection()) }
    var reintento by remember { mutableIntStateOf(0) }
    val currentOnFinalizar by rememberUpdatedState(onFinalizar)

    LaunchedEffect(sinConexion, reintento) {
        if (!sinConexion) {
            delay(2600)
            if (hasConnection()) {
                currentOnFinalizar()
            } else {
                sinConexion = true
            }
        }
    }

    if (sinConexion) {
        PantallaSinConexion(
            onReintentar = {
                sinConexion = !hasConnection()
                reintento++
            }
        )
    } else {
        PantallaCargaPacman()
    }
}

@Composable
fun PantallaSinConexion(onReintentar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SleepingCloudIllustration()

        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = "No hay conexion a internet",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorTexto,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onReintentar,
            modifier = Modifier
                .width(170.dp)
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorCampo,
                contentColor = ColorTexto
            ),
            border = BorderStroke(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(listOf(ColorAzul, ColorMorado))
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            Text(
                text = "Reintentar",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SleepingCloudIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "sleeping_cloud")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Canvas(
        modifier = Modifier
            .size(width = 170.dp, height = 120.dp)
            .offset(y = floatOffset.dp)
    ) {
        val strokeWidth = 4.dp.toPx()
        val cloudBrush = Brush.linearGradient(
            colors = listOf(ColorAzul.copy(alpha = 0.75f), ColorMorado.copy(alpha = 0.8f)),
            start = Offset(20.dp.toPx(), 40.dp.toPx()),
            end = Offset(size.width - 20.dp.toPx(), size.height - 20.dp.toPx())
        )

        val cloud = Path().apply {
            moveTo(38.dp.toPx(), 78.dp.toPx())
            cubicTo(20.dp.toPx(), 78.dp.toPx(), 18.dp.toPx(), 48.dp.toPx(), 48.dp.toPx(), 48.dp.toPx())
            cubicTo(58.dp.toPx(), 25.dp.toPx(), 94.dp.toPx(), 25.dp.toPx(), 106.dp.toPx(), 48.dp.toPx())
            cubicTo(130.dp.toPx(), 43.dp.toPx(), 148.dp.toPx(), 58.dp.toPx(), 148.dp.toPx(), 78.dp.toPx())
            cubicTo(148.dp.toPx(), 96.dp.toPx(), 128.dp.toPx(), 98.dp.toPx(), 38.dp.toPx(), 98.dp.toPx())
            cubicTo(24.dp.toPx(), 98.dp.toPx(), 22.dp.toPx(), 78.dp.toPx(), 38.dp.toPx(), 78.dp.toPx())
        }

        drawPath(
            path = cloud,
            brush = cloudBrush,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawArc(
            color = ColorMorado.copy(alpha = 0.8f),
            startAngle = 25f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(70.dp.toPx(), 58.dp.toPx()),
            size = Size(16.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = ColorMorado.copy(alpha = 0.8f),
            startAngle = 25f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(100.dp.toPx(), 58.dp.toPx()),
            size = Size(16.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = ColorAzul.copy(alpha = 0.8f),
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(83.dp.toPx(), 72.dp.toPx()),
            size = Size(18.dp.toPx(), 10.dp.toPx()),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.rgb(141, 92, 246)
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT,
                    android.graphics.Typeface.BOLD
                )
            }
            paint.textSize = 22.sp.toPx()
            drawText("Z", 42.dp.toPx(), 28.dp.toPx(), paint)
            paint.textSize = 16.sp.toPx()
            drawText("z", 59.dp.toPx(), 19.dp.toPx(), paint)
            paint.textSize = 12.sp.toPx()
            drawText("z", 73.dp.toPx(), 14.dp.toPx(), paint)
        }
    }
}

@Composable
fun PantallaCargaPacman() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PacmanAnimation()

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Buscando musica para ti...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PacmanAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pacman")

    val mouthAngle by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 48f,
        animationSpec = infiniteRepeatable(
            animation = tween(320, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mouth"
    )

    val dotOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(620, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dots"
    )

    val bob by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(760, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    Canvas(modifier = Modifier.size(width = 260.dp, height = 130.dp)) {
        val pacmanSize = 88.dp.toPx()
        val centerY = size.height / 2 + bob.dp.toPx()
        val centerX = 70.dp.toPx()
        val dotRadius = 7.dp.toPx()
        val dotGap = 42.dp.toPx()
        val firstDotX = centerX + 78.dp.toPx()

        for (i in 0..4) {
            val x = firstDotX + (i * dotGap) - dotOffset.dp.toPx()
            if (x > centerX + 35.dp.toPx() && x < size.width - 8.dp.toPx()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorMorado.copy(alpha = 0.9f), ColorAzul.copy(alpha = 0.55f)),
                        center = Offset(x, centerY),
                        radius = dotRadius * 2.2f
                    ),
                    radius = dotRadius,
                    center = Offset(x, centerY)
                )
            }
        }

        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(ColorAzul, ColorMorado),
                start = Offset(centerX - pacmanSize / 2, centerY),
                end = Offset(centerX + pacmanSize / 2, centerY)
            ),
            startAngle = mouthAngle,
            sweepAngle = 360f - (mouthAngle * 2),
            useCenter = true,
            topLeft = Offset(centerX - pacmanSize / 2, centerY - pacmanSize / 2),
            size = Size(pacmanSize, pacmanSize),
            style = Fill
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.92f),
            radius = 5.dp.toPx(),
            center = Offset(centerX + 8.dp.toPx(), centerY - 23.dp.toPx())
        )
    }
}
