package com.example.moodmusic.ui.registro_estado

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmusic.data.model.EstadoAnimo
import com.example.moodmusic.data.model.UsuarioEntity
import androidx.lifecycle.ViewModelProvider
import com.example.moodmusic.ui.historial.HistorialActivity
import com.example.moodmusic.ui.perfil.PerfilActivity
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.viewmodel.EstadoAnimoViewModel
import com.example.moodmusic.viewmodel.UsuarioViewModel

class EstadoAnimoActivity : ComponentActivity() {
    
    // Usamos el ViewModel para obtener los datos del usuario de la sesión actual
    private val usuarioViewModel: UsuarioViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(UsuarioViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MoodMusicTheme {
                val context = androidx.compose.ui.platform.LocalContext.current
                val usuarioActual = usuarioViewModel.usuarioActual
                
                // Validación de seguridad: Redirigir si ya registró su emoción hoy
                LaunchedEffect(usuarioActual) {
                    if (usuarioActual != null) {
                        val user = usuarioActual
                        val db = com.example.moodmusic.data.local.database.DatabaseProvider.getDatabase(context)
                        val timeZone = java.util.TimeZone.getTimeZone("America/Bogota")
                        val cal = java.util.Calendar.getInstance(timeZone)
                        
                        val sdfDia = java.text.SimpleDateFormat("d", java.util.Locale("es", "ES")).apply { this.timeZone = timeZone }
                        val sdfMes = java.text.SimpleDateFormat("MMMM", java.util.Locale("es", "ES")).apply { this.timeZone = timeZone }
                        val sdfAnio = java.text.SimpleDateFormat("yyyy", java.util.Locale("es", "ES")).apply { this.timeZone = timeZone }
                        
                        val dia = sdfDia.format(cal.time)
                        val mes = sdfMes.format(cal.time).replaceFirstChar { it.uppercase() }
                        val anio = sdfAnio.format(cal.time)

                        val yaRegistroHoy = db.estadoAnimoDao().obtenerRegistroHoy(user.username, dia, mes, anio)
                        
                        if (yaRegistroHoy != null) {
                            // Si ya tiene registro, directo al perfil
                            val intent = Intent(this@EstadoAnimoActivity, PerfilActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                PantallaEstadoAnimo(
                    usuario = usuarioActual,
                    onGuardar = { estadoAnimo ->
                        // Ya no pasamos el usuario por intent, se recupera en la siguiente actividad
                        val intent = Intent(this, RegistrarEstadoActivity::class.java).apply {
                            putExtra("estadoAnimo", estadoAnimo)
                        }
                        startActivity(intent)
                    },
                    onVerHistorial = {
                        startActivity(Intent(this, HistorialActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaEstadoAnimo(
    viewModel: EstadoAnimoViewModel = viewModel(),
    usuario: UsuarioEntity? = null,
    onGuardar: (EstadoAnimo) -> Unit = {},
    onVerHistorial: () -> Unit = {}
) {
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "¿Cómo te sientes hoy?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tu emoción define tu energía. Elige cómo te sientes hoy y transforma ese estado en música.",
            fontSize = 15.sp,
            color = ColorSubtexto,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        viewModel.listaEstados.forEach { estado ->
            ItemEstadoAnimo(
                estado = estado,
                seleccionado = viewModel.estadoSeleccionado?.nombre == estado.nombre,
                onClick = {
                    viewModel.seleccionarEstado(estado)
                    mensajeError = ""
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color(0xFFD32F2F),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = {
                if (viewModel.estadoSeleccionado != null) {
                    viewModel.estadoSeleccionado?.let { onGuardar(it) }
                } else {
                    mensajeError = "Debe seleccionar una emoción"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor   = ColorTexto
            ),
            border = BorderStroke(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(ColorAzul, ColorMorado))
            )
        ) {
            Text(
                text = "Continuar",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onVerHistorial,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor   = ColorTexto
            ),
            border = BorderStroke(1.dp, Color(0xFFE0E0E8))
        ) {
            Text(
                text = "Ver mi historial",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ItemEstadoAnimo(
    estado: EstadoAnimo,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val baseColor = Color(estado.color)

    val borderColor = Color(
        red = (baseColor.red * 0.65f),
        green = (baseColor.green * 0.65f),
        blue = (baseColor.blue * 0.65f),
        alpha = 1f
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(
                elevation = if (seleccionado) 12.dp else 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(baseColor)
            .then(
                if (seleccionado) {
                    Modifier.border(
                        width = 4.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = estado.emoji, fontSize = 26.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = estado.nombre,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaEstadoAnimoPreview() {
    MoodMusicTheme {
        PantallaEstadoAnimo()
    }
}
