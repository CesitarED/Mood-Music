package com.example.moodmusic

import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.moodmusic.model.EstadoAnimoEntity
import com.example.moodmusic.ui.theme.MoodMusicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetalleHistorialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val estado = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("estado", EstadoAnimoEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("estado") as? EstadoAnimoEntity
        }

        setContent {
            MoodMusicTheme {
                PantallaDetalleHistorial(
                    estadoInicial = estado,
                    onVolver = { finish() }
                )
            }
        }
    }
}

@Composable
fun PantallaDetalleHistorial(
    estadoInicial: EstadoAnimoEntity?,
    onVolver: () -> Unit
) {
    var estado by remember { mutableStateOf(estadoInicial) }
    var nuevaNota by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = CoroutineScope(Dispatchers.Main)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, ColorMorado.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { onVolver() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    tint = ColorMorado,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Text(
                text = "Detalle",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTexto,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        estado?.let { est ->
            val gradiente = Brush.linearGradient(
                colors = listOf(Color(0xFFE0F7FA), Color(0xFFE1BEE7))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .background(gradiente, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${est.dia}/${est.mes}/${est.anio}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTexto
                )
                
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ColorTexto,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cajón de emoción con color completo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(Color(est.colorEstado), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = est.emojiEstado, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "(${est.nombreEstado})",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (est.nota.isEmpty()) "(Sin nota)" else est.nota,
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nuevaNota,
            onValueChange = { nuevaNota = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp)),
            placeholder = { Text("(Nueva nota)", color = Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (estado != null && nuevaNota.isNotEmpty()) {
                    val estadoActualizado = estado!!.copy(nota = nuevaNota)
                    CoroutineScope(Dispatchers.IO).launch {
                        DatabaseProvider.getDatabase(context).estadoAnimoDao().insertar(estadoActualizado)
                        scope.launch {
                            estado = estadoActualizado
                            nuevaNota = ""
                            Toast.makeText(context, "Nota actualizada", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(52.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ColorTexto),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(ColorAzul, ColorMorado)))
        ) {
            Text("Editar", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaDetalleHistorialPreview() {
    MoodMusicTheme {
        PantallaDetalleHistorial(
            estadoInicial = EstadoAnimoEntity(
                username = "test",
                nombreEstado = "Feliz",
                emojiEstado = "😊",
                colorEstado = 0xFF26C6DA,
                nota = "Hoy fue un gran día",
                dia = "7",
                mes = "4",
                anio = "2024",
                fechaCompleta = System.currentTimeMillis()
            ),
            onVolver = {}
        )
    }
}
