package com.example.moodmusic.ui.historial

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmusic.data.model.EstadoAnimoEntity
import com.example.moodmusic.data.model.UsuarioEntity
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.viewmodel.HistorialViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistorialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val usuario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("usuario", UsuarioEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("usuario") as? UsuarioEntity
        }

        setContent {
            MoodMusicTheme {
                PantallaHistorial(
                    usuario = usuario,
                    onVolver = { finish() },
                    onVerDetalle = { estado ->
                        val intent = Intent(this, DetalleHistorialActivity::class.java).apply {
                            putExtra("estado", estado)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaHistorial(
    usuario: UsuarioEntity?,
    viewModel: HistorialViewModel = viewModel(),
    onVolver: () -> Unit,
    onVerDetalle: (EstadoAnimoEntity) -> Unit = {}
) {
    LaunchedEffect(usuario) {
        usuario?.let { viewModel.cargarHistorial(it.username) }
    }

    val listaEstados = viewModel.listaEstados
    var expansionDropdown by remember { mutableStateOf(false) }
    var semanaSeleccionada by remember { mutableIntStateOf(0) }

    val opcionesSemanas = listOf("Últimos 7 días", "Semana anterior", "Hace 2 semanas")

    val diasDeLaSemana = remember(semanaSeleccionada) {
        val lista = mutableListOf<Calendar>()
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.add(Calendar.WEEK_OF_YEAR, -semanaSeleccionada)

        for (i in 0..6) {
            val diaCal = cal.clone() as Calendar
            diaCal.add(Calendar.DAY_OF_YEAR, i)
            lista.add(diaCal)
        }
        lista
    }

    // Formateadores para comparar con la base de datos
    val sdfDia = SimpleDateFormat("d", Locale("es", "ES"))
    val sdfMes = SimpleDateFormat("MMMM", Locale("es", "ES"))
    val sdfAnio = SimpleDateFormat("yyyy", Locale("es", "ES"))
    val formatoDiaTexto = SimpleDateFormat("EEE d MMM", Locale("es", "ES"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 24.dp),
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
                text = "Tu historial",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTexto,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Da click en una nota para verla / editarla",
            fontSize = 15.sp,
            color = ColorSubtexto,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE0E0E8), RoundedCornerShape(12.dp))
                    .clickable { expansionDropdown = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(opcionesSemanas[semanaSeleccionada], color = ColorTexto)
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.LightGray)
                }
            }
            DropdownMenu(
                expanded = expansionDropdown,
                onDismissRequest = { expansionDropdown = false },
                modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
            ) {
                opcionesSemanas.forEachIndexed { index, opcion ->
                    DropdownMenuItem(text = { Text(opcion) }, onClick = {
                        semanaSeleccionada = index
                        expansionDropdown = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(diasDeLaSemana) { cal ->
                val diaStr = sdfDia.format(cal.time)
                val mesStr = sdfMes.format(cal.time).replaceFirstChar { it.uppercase() }
                val anioStr = sdfAnio.format(cal.time)

                // Búsqueda del estado guardado coincidiendo con el nuevo formato (nombre del mes)
                val estadoDelDia = listaEstados.find {
                    it.dia == diaStr && it.mes == mesStr && it.anio == anioStr
                }

                val fechaFormateada = formatoDiaTexto.format(cal.time).replaceFirstChar { it.uppercase() }

                ItemHistorialDia(
                    fechaLabel = fechaFormateada,
                    estado = estadoDelDia,
                    onClick = { estadoDelDia?.let { onVerDetalle(it) } }
                )
            }
        }
    }
}

@Composable
fun ItemHistorialDia(fechaLabel: String, estado: EstadoAnimoEntity?, onClick: () -> Unit) {
    val registrado = estado != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (registrado) 4.dp else 0.dp, RoundedCornerShape(16.dp))
            .background(if (registrado) Color(0xFFF0F4FF) else Color.Transparent, RoundedCornerShape(16.dp))
            .clickable(enabled = registrado) { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = fechaLabel, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ColorTexto)
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
            imageVector = if (registrado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (registrado) ColorTexto else Color.LightGray,
            modifier = Modifier.size(24.dp)
        )
        if (registrado) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(2.dp, RoundedCornerShape(14.dp))
                    .background(Color(estado!!.colorEstado), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = estado.emojiEstado, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "(${estado.nombreEstado})",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaHistorialPreview() {
    MoodMusicTheme {
        PantallaHistorial(usuario = null, onVolver = {})
    }
}
