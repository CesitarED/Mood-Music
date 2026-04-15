package com.example.moodmusic.ui.registro_estado

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmusic.data.model.EstadoAnimo
import com.example.moodmusic.data.model.UsuarioEntity
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.viewmodel.EstadoAnimoViewModel
import java.text.SimpleDateFormat
import java.util.*

class RegistrarEstadoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val usuario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("usuario", UsuarioEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("usuario") as? UsuarioEntity
        }

        val estadoAnimo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("estadoAnimo", EstadoAnimo::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("estadoAnimo") as? EstadoAnimo
        }

        setContent {
            MoodMusicTheme {
                PantallaRegistrarEstado(
                    usuario = usuario,
                    estadoAnimo = estadoAnimo,
                    onVolver = { finish() },
                    onGuardarExitoso = {
                        Toast.makeText(this, "Estado guardado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaRegistrarEstado(
    usuario: UsuarioEntity?,
    estadoAnimo: EstadoAnimo?,
    viewModel: EstadoAnimoViewModel = viewModel(),
    onVolver: () -> Unit,
    onGuardarExitoso: () -> Unit
) {
    var nota by remember { mutableStateOf("") }
    val context = LocalContext.current

    val timeZone = TimeZone.getTimeZone("America/Bogota")
    val calendario = Calendar.getInstance(timeZone)

    val sdfDia = SimpleDateFormat("d", Locale("es", "ES"))
    val sdfMes = SimpleDateFormat("MMMM", Locale("es", "ES"))
    val sdfAnio = SimpleDateFormat("yyyy", Locale("es", "ES"))

    sdfDia.timeZone = timeZone
    sdfMes.timeZone = timeZone
    sdfAnio.timeZone = timeZone

    val diaActual = sdfDia.format(calendario.time)
    val mesActual = sdfMes.format(calendario.time).replaceFirstChar { it.uppercase() }
    val anioActual = sdfAnio.format(calendario.time)

    val colorFondoCajon = estadoAnimo?.let { Color(it.color) } ?: Color(0xFFF0F0F0)

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
            text = "Registrar estado",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(6.dp, RoundedCornerShape(20.dp))
                .background(colorFondoCajon, RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = estadoAnimo?.emoji ?: "❓", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = estadoAnimo?.nombre ?: "Emoción elegida",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Nota personal (opcional)",
            fontSize = 18.sp,
            color = ColorSubtexto,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nota,
            onValueChange = { nota = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp)),
            placeholder = { Text("Escribe lo que está influyendo en tu estado...") },
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FechaBox("Día:", diaActual, ColorAzul)
            FechaBox("Mes:", mesActual, Color(0xFF4A69FF))
            FechaBox("Año:", anioActual, ColorMorado)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (usuario != null && estadoAnimo != null) {
                    viewModel.seleccionarEstado(estadoAnimo)
                    viewModel.guardarEstado(usuario.username, nota, usuario.avatar) {
                        onGuardarExitoso()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error: Faltan datos del usuario o emoción",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = ColorTexto
            ),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(ColorAzul, ColorMorado))
            )
        ) {
            Text("Guardar", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = ColorTexto
            )
        ) {
            Text("Volver", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun FechaBox(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .width(115.dp)
            .height(50.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label $value",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaRegistrarEstadoPreview() {
    MoodMusicTheme {
        PantallaRegistrarEstado(
            usuario = null,
            estadoAnimo = EstadoAnimo("Feliz", 0xFF26C6DA, "😊"),
            onVolver = {},
            onGuardarExitoso = {}
        )
    }
}
