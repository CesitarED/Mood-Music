package com.example.moodmusic

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmusic.model.EstadoAnimo
import com.example.moodmusic.model.UsuarioEntity
import com.example.moodmusic.ui.theme.MoodMusicTheme
import com.example.moodmusic.viewmodel.EstadoAnimoViewModel

// -------------------------------------------------------
// EstadoAnimoActivity
// Pantalla donde el usuario selecciona cómo se siente hoy
// -------------------------------------------------------
class EstadoAnimoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Recibir el objeto UsuarioEntity desde el Intent (Android 13+)
        val usuario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("usuario", UsuarioEntity::class.java)
        } else {
            intent.getSerializableExtra("usuario") as? UsuarioEntity
        }

        setContent {
            MoodMusicTheme {
                PantallaEstadoAnimo(
                    usuario = usuario,
                    onGuardar = { estadoAnimo ->
                        // Navega a HistorialActivity (próximo paso)
                    },
                    onVerHistorial = {
                        // Navega a HistorialActivity
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------
// Pantalla Estado de Ánimo
// Conceptos aplicados:
//   - Column / LazyColumn  (PDF 1 - Layouts)
//   - mutableStateOf + remember  (PDF 2 - Estado)
//   - State Hoisting  (PDF 3)
//   - ViewModel + mutableStateOf  (PDF 4 - ViewModel)
// -------------------------------------------------------
@Composable
fun PantallaEstadoAnimo(
    viewModel: EstadoAnimoViewModel = viewModel(),
    usuario: UsuarioEntity? = null,
    onGuardar: (EstadoAnimo) -> Unit = {},
    onVerHistorial: () -> Unit = {}
) {
    // Estado de error local
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(56.dp))

        // ---------- Saludo ----------
        usuario?.let {
            Text(
                text = "Hola, ${it.nombre}",
                fontSize = 18.sp,
                color = ColorMorado,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ---------- Título ----------
        Text(
            text = "¿Cómo te sientes hoy?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Subtítulo ----------
        Text(
            text = "Tu emoción define tu energía. Elige cómo te sientes hoy y transforma ese estado en música.",
            fontSize = 14.sp,
            color = ColorSubtexto,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ---------- Lista de estados de ánimo ----------
        // Recorre la lista del ViewModel (State Hoisting - PDF 3)
        viewModel.listaEstados.forEach { estado ->
            ItemEstadoAnimo(
                estado = estado,
                // Resalta si está seleccionado (PDF 2 - Recomposition)
                seleccionado = viewModel.estadoSeleccionado?.nombre == estado.nombre,
                onClick = {
                    // UI envía evento → ViewModel actualiza estado (PDF 3)
                    viewModel.seleccionarEstado(estado)
                    mensajeError = ""
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ---------- Mensaje de error ----------
        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color(0xFFE24B4A),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Botón Guardar ----------
        Button(
            onClick = {
                if (viewModel.guardarEstado()) {
                    viewModel.estadoSeleccionado?.let { onGuardar(it) }
                } else {
                    mensajeError = "Por favor selecciona cómo te sientes."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor   = ColorTexto
            ),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Text(
                text = "Guardar",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Botón Ver historial ----------
        Button(
            onClick = onVerHistorial,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor   = ColorTexto
            ),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Text(
                text = "Ver mi historial",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// -------------------------------------------------------
// Item de estado de ánimo (Card clickeable)
// Basado en PDF - Cards en listas
// State Hoisting: recibe estado y evento desde el padre
// -------------------------------------------------------
@Composable
fun ItemEstadoAnimo(
    estado: EstadoAnimo,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    // Escala visual cuando está seleccionado
    val alpha = if (seleccionado) 1f else 0.85f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(estado.color).copy(alpha = alpha))
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = estado.nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            // Indicador de seleccionado
            if (seleccionado) {
                Text(
                    text = "✓",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------
// Preview
// -------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaEstadoAnimoPreview() {
    MoodMusicTheme {
        PantallaEstadoAnimo()
    }
}