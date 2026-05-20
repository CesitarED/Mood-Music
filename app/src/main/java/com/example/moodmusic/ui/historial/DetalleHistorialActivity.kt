package com.example.moodmusic.ui.historial

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
import com.example.moodmusic.data.local.database.DatabaseProvider
import com.example.moodmusic.data.model.EstadoAnimoEntity
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.ui.main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.moodmusic.data.remote.api.MusicApiService
import com.example.moodmusic.data.repository.MusicRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    var notaEditable by remember { mutableStateOf(estadoInicial?.nota ?: "") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = LocalIsDarkTheme.current

    val musicRepository: MusicRepository by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MusicApiService::class.java)
        MusicRepository(api)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // Cabecera
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .background(ColorCampo, RoundedCornerShape(12.dp))
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
            val gradiente = if (isDark) {
                Brush.linearGradient(colors = listOf(Color(0xFF1E2A4A), Color(0xFF2D1E4A)))
            } else {
                Brush.linearGradient(colors = listOf(Color(0xFFE0F7FA), Color(0xFFE1BEE7)))
            }

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

                // Cajón de emoción con su color original
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
                            text = est.nombreEstado,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Editar nota personal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTexto,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notaEditable,
                    onValueChange = { notaEditable = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .background(ColorCampo, RoundedCornerShape(16.dp)),
                    placeholder = { Text("¿Quieres añadir algo más?", color = ColorSubtexto) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ColorTexto,
                        unfocusedTextColor = ColorTexto,
                        focusedBorderColor = ColorMorado.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = ColorCampo,
                        unfocusedContainerColor = ColorCampo
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Editar (Guardar cambios)
        Button(
            onClick = {
                val currentEstado = estado
                if (currentEstado != null) {
                    val estadoActualizado = currentEstado.copy(nota = notaEditable)
                    scope.launch(Dispatchers.IO) {
                        // Guardar en Room
                        DatabaseProvider.getDatabase(context).estadoAnimoDao().insertar(estadoActualizado)
                        
                        // Guardar en Firestore si tiene ID
                        currentEstado.firestoreId?.let { id ->
                            musicRepository.actualizarNotaCloud(id, notaEditable)
                        }

                        scope.launch(Dispatchers.Main) {
                            estado = estadoActualizado
                            Toast.makeText(context, "Registro actualizado", Toast.LENGTH_SHORT).show()
                            onVolver()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(52.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorCampo, contentColor = ColorTexto),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(ColorAzul, ColorMorado)))
        ) {
            Text("Guardar cambios", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
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
                fechaCompleta = System.currentTimeMillis(),
                avatar = 1
            ),
            onVolver = {}
        )
    }
}
