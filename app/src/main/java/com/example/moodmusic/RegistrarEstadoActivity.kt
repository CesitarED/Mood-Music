package com.example.moodmusic

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
import com.example.moodmusic.model.DatabaseProvider
import com.example.moodmusic.model.EstadoAnimo
import com.example.moodmusic.model.EstadoAnimoEntity
import com.example.moodmusic.model.UsuarioEntity
import com.example.moodmusic.ui.theme.MoodMusicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    onVolver: () -> Unit,
    onGuardarExitoso: () -> Unit
) {
    var nota by remember { mutableStateOf("") }
    val context = LocalContext.current

    val calendario = Calendar.getInstance()
    val diaActual = calendario.get(Calendar.DAY_OF_MONTH).toString()
    val mesActual = (calendario.get(Calendar.MONTH) + 1).toString()
    val anioActual = calendario.get(Calendar.YEAR).toString()

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

        // Card de Emoción Elegida con el color completo
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
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de Nota
        OutlinedTextField(
            value = nota,
            onValueChange = { nota = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp)),
            placeholder = { Text("Escribe lo que está influyendo en tu estado...", color = Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Fila de Fecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FechaBox(label = "Día:", value = diaActual, color = ColorAzul)
            FechaBox(label = "Mes:", value = mesActual, color = Color(0xFF4A69FF))
            FechaBox(label = "Año:", value = anioActual, color = ColorMorado)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Botón Guardar
        Button(
            onClick = {
                if (usuario != null && estadoAnimo != null) {
                    val nuevoEstado = EstadoAnimoEntity(
                        username = usuario.username,
                        nombreEstado = estadoAnimo.nombre,
                        emojiEstado = estadoAnimo.emoji,
                        colorEstado = estadoAnimo.color,
                        nota = nota,
                        dia = diaActual,
                        mes = mesActual,
                        anio = anioActual,
                        fechaCompleta = System.currentTimeMillis()
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        DatabaseProvider.getDatabase(context).estadoAnimoDao().insertar(nuevoEstado)
                        CoroutineScope(Dispatchers.Main).launch {
                            onGuardarExitoso()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ColorTexto),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(ColorAzul, ColorMorado)))
        ) {
            Text("Guardar", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón Volver
        Button(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ColorTexto),
            border = BorderStroke(1.dp, Color(0xFFE0E0E8))
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
            .width(105.dp)
            .height(50.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label $value",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
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
