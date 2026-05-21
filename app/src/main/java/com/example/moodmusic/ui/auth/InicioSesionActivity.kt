package com.example.moodmusic.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.moodmusic.R
import com.example.moodmusic.ui.avatar.SeleccionAvatarActivity
import com.example.moodmusic.ui.main.*
import com.example.moodmusic.ui.registro_estado.EstadoAnimoActivity
import com.example.moodmusic.ui.historial.HistorialActivity
import com.example.moodmusic.ui.perfil.PerfilActivity
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.viewmodel.UsuarioViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class InicioSesionActivity : ComponentActivity() {

    private val viewModel: UsuarioViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(UsuarioViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MoodMusicTheme {
                val loginExitoso = viewModel.loginExitoso
                val mensajeError = viewModel.mensajeError

                LaunchedEffect(loginExitoso, viewModel.usuarioActual) {
                    if (loginExitoso && viewModel.usuarioActual != null) {
                        val user = viewModel.usuarioActual!!
                        CoroutineScope(Dispatchers.IO).launch {
                            if (user.avatar == -1) {
                                val intent = Intent(this@InicioSesionActivity, SeleccionAvatarActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                val timeZone = TimeZone.getTimeZone("America/Bogota")
                                val cal = Calendar.getInstance(timeZone)
                                
                                val sdfDia = SimpleDateFormat("d", Locale("es", "ES"))
                                val sdfMes = SimpleDateFormat("MMMM", Locale("es", "ES"))
                                val sdfAnio = SimpleDateFormat("yyyy", Locale("es", "ES"))
                                
                                sdfDia.timeZone = timeZone
                                sdfMes.timeZone = timeZone
                                sdfAnio.timeZone = timeZone

                                val diaActual = sdfDia.format(cal.time)
                                val mesActual = sdfMes.format(cal.time).replaceFirstChar { it.uppercase() }
                                val anioActual = sdfAnio.format(cal.time)

                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                val registroHoyExiste = if (uid != null) {
                                    val snapshot = FirebaseFirestore.getInstance()
                                        .collection("usuarios")
                                        .document(uid)
                                        .collection("historial")
                                        .whereEqualTo("dia", diaActual)
                                        .whereEqualTo("mes", mesActual)
                                        .whereEqualTo("anio", anioActual)
                                        .limit(1)
                                        .get()
                                        .await()

                                    !snapshot.isEmpty
                                } else {
                                    false
                                }

                                val intent = if (registroHoyExiste) {
                                    Intent(this@InicioSesionActivity, PerfilActivity::class.java)
                                } else {
                                    Intent(this@InicioSesionActivity, EstadoAnimoActivity::class.java)
                                }
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                            viewModel.limpiarEstados()
                            finish()
                        }
                    }
                }

                PantallaLogin(
                    mensajeErrorExterno = mensajeError,
                    onIniciarSesion = { nombreOUsername, contrasena ->
                        viewModel.login(nombreOUsername, contrasena)
                    },
                    onRegistrarse = {
                        startActivity(Intent(this, RegistroActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaLogin(
    mensajeErrorExterno: String = "",
    onIniciarSesion: (nombre: String, contrasena: String) -> Unit = { _, _ -> },
    onRegistrarse: () -> Unit = {}
) {
    var nombre            by remember { mutableStateOf("") }
    var contrasena        by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var mensajeError      by remember { mutableStateOf("") }

    LaunchedEffect(mensajeErrorExterno) {
        if (mensajeErrorExterno.isNotEmpty()) {
            mensajeError = mensajeErrorExterno
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LogoOnda()

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "¡Bienvenido!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicie sesión para continuar",
            fontSize = 14.sp,
            color = ColorSubtexto
        )

        Spacer(modifier = Modifier.height(32.dp))

        CampoTexto(
            valor = nombre,
            onValorChange = { nombre = it; mensajeError = "" },
            placeholder = "Ingrese su usuario o correo"
        )

        Spacer(modifier = Modifier.height(14.dp))

        CampoContrasena(
            valor = contrasena,
            onValorChange = { contrasena = it; mensajeError = "" },
            mostrar = mostrarContrasena,
            onToggleMostrar = { mostrarContrasena = !mostrarContrasena }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color(0xFFE24B4A),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        BotonGradiente(
            texto = "Iniciar sesión",
            onClick = {
                when {
                    nombre.isBlank() || contrasena.isBlank() -> {
                        mensajeError = "Por favor completa todos los campos."
                    }
                    else -> onIniciarSesion(nombre, contrasena)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        BotonSecundario(
            texto = "Registrarse",
            onClick = onRegistrarse
        )
    }
}

@Composable
fun LogoOnda() {
    val isDark = LocalIsDarkTheme.current
    val logoRes = if (isDark) R.drawable.logo_app_oscuro else R.drawable.logo_app_claro
    
    Image(
        painter = painterResource(id = logoRes),
        contentDescription = "Logo Mood & Music",
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .wrapContentHeight()
    )
}

@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        placeholder = { Text(text = placeholder, color = ColorSubtexto, fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor        = ColorTexto,
            unfocusedTextColor      = ColorTexto,
            focusedBorderColor      = ColorMorado,
            unfocusedBorderColor    = ColorBorde,
            focusedContainerColor   = ColorCampo,
            unfocusedContainerColor = ColorCampo
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CampoContrasena(
    valor: String,
    onValorChange: (String) -> Unit,
    mostrar: Boolean,
    onToggleMostrar: () -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        placeholder = { Text("Ingrese la contraseña", color = ColorSubtexto, fontSize = 14.sp) },
        singleLine = true,
        visualTransformation = if (mostrar) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleMostrar) {
                Icon(
                    imageVector = if (mostrar) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = ColorSubtexto
                )
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor        = ColorTexto,
            unfocusedTextColor      = ColorTexto,
            focusedBorderColor      = ColorMorado,
            unfocusedBorderColor    = ColorBorde,
            focusedContainerColor   = ColorCampo,
            unfocusedContainerColor = ColorCampo
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BotonGradiente(
    texto: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(ColorAzul, ColorMorado)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = texto,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorBotonGris,
            contentColor   = ColorTexto
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaLoginPreview() {
    MoodMusicTheme {
        PantallaLogin()
    }
}
