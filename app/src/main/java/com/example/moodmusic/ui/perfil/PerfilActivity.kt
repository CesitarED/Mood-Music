package com.example.moodmusic.ui.perfil

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmusic.R
import com.example.moodmusic.ui.auth.InicioSesionActivity
import com.example.moodmusic.ui.avatar.SeleccionAvatarActivity
import com.example.moodmusic.ui.historial.HistorialActivity
import com.example.moodmusic.ui.main.*
import com.example.moodmusic.ui.musica.MusicaRecomendadaActivity
import com.example.moodmusic.ui.theme.MoodMusicTheme
import com.example.moodmusic.viewmodel.UsuarioViewModel

import com.example.moodmusic.ui.musica.CargaMusicaActivity

class PerfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMusicTheme {
                PantallaPerfil(
                    onVerPerfil = {
                        val intent = Intent(this, EditarPerfilActivity::class.java)
                        startActivity(intent)
                    },
                    onCambiarAvatar = {
                        val intent = Intent(this, SeleccionAvatarActivity::class.java)
                        intent.putExtra("desdePerfil", true)
                        startActivity(intent)
                    },
                    onVerHistorial = {
                        val intent = Intent(this, HistorialActivity::class.java)
                        startActivity(intent)
                    },
                    onVerMusica = {
                        val intent = Intent(this, CargaMusicaActivity::class.java)
                        startActivity(intent)
                    },
                    onCambiarPass = {
                        val intent = Intent(this, CambiarContrasenaActivity::class.java)
                        startActivity(intent)
                    },
                    onCerrarSesion = {
                        val intent = Intent(this, InicioSesionActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaPerfil(
    viewModel: UsuarioViewModel = viewModel(),
    onVerPerfil: () -> Unit,
    onCambiarAvatar: () -> Unit,
    onVerHistorial: () -> Unit,
    onVerMusica: () -> Unit,
    onCambiarPass: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    // Obtenemos el usuario del ViewModel. Gracias al Flow en el ViewModel,
    // esta variable se actualizará automáticamente cuando cambie en la DB.
    val usuario = viewModel.usuarioActual
    val listaAvatares = listOf(
        R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
        R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8
    )

    var mostrarDialogo by remember { mutableStateOf(false) }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .wrapContentHeight(),
            confirmButton = {}, // No usamos los botones por defecto para el diseño personalizado
            dismissButton = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Cerrar sesión",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¿Estás seguro de salir de la aplicación?\nNecesitarás iniciar sesión de nuevo para usar la app.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Cancelar
                        OutlinedButton(
                            onClick = { mostrarDialogo = false },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFFF4D4D)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4D4D))
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.SemiBold)
                        }
                        // Botón Cerrar sesión
                        Button(
                            onClick = {
                                viewModel.cerrarSesion()
                                onCerrarSesion()
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4D))
                        ) {
                            Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()), // Añadimos scroll por si acaso
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // El nombre ahora se actualizará solo
        Text(
            text = "Hola! @${usuario?.username ?: "..."}",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(24.dp))

        // La foto ahora se actualizará sola
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Brush.linearGradient(listOf(ColorAzul.copy(alpha = 0.3f), ColorMorado.copy(alpha = 0.3f))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val avatarRes = if (usuario != null && usuario.avatar in 0 until listaAvatares.size) {
                listaAvatares[usuario.avatar]
            } else {
                R.drawable.avatar1 // Imagen por defecto mientras carga o si no hay
            }

            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            ItemMenuPerfil("Ver perfil", onClick = onVerPerfil)
            ItemMenuPerfil("Cambiar avatar", onClick = onCambiarAvatar)
            ItemMenuPerfil("Ver historial", onClick = onVerHistorial)
            ItemMenuPerfil("Ver música recomendada", onClick = onVerMusica)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Modo oscuro", fontSize = 17.sp, color = ColorTexto)
                var isDark by remember { mutableStateOf(false) }
                Switch(
                    checked = isDark,
                    onCheckedChange = { isDark = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = ColorMorado)
                )
            }
            HorizontalDivider(color = ColorBorde, thickness = 1.dp)

            ItemMenuPerfil("Cambiar contraseña", onClick = onCambiarPass)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable { 
                        mostrarDialogo = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Cerrar sesión", fontSize = 17.sp, color = Color.Red.copy(alpha = 0.7f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Red.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ItemMenuPerfil(titulo: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = titulo, fontSize = 17.sp, color = ColorTexto)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = ColorSubtexto
            )
        }
        HorizontalDivider(color = ColorBorde, thickness = 1.dp)
    }
}
