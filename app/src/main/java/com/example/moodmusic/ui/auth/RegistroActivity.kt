package com.example.moodmusic.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.moodmusic.R
import com.example.moodmusic.ui.main.*
import com.example.moodmusic.ui.theme.*
import com.example.moodmusic.viewmodel.UsuarioViewModel

class RegistroActivity : ComponentActivity() {

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
                val registroExitoso = viewModel.registroExitoso
                val mensajeError    = viewModel.mensajeError

                LaunchedEffect(registroExitoso) {
                    if (registroExitoso) {
                        Toast.makeText(
                            this@RegistroActivity,
                            "¡Registro exitoso! Inicia sesión con tus credenciales.",
                            Toast.LENGTH_LONG
                        ).show()
                        
                        val intent = Intent(this@RegistroActivity, InicioSesionActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        viewModel.limpiarEstados()
                        finish()
                    }
                }

                PantallaRegistro(
                    mensajeErrorExterno = mensajeError,
                    onRegistrarse = { username, nombre, apellido, edad, correo, contrasena ->
                        viewModel.registrar(username, nombre, apellido, edad, correo, contrasena)
                    },
                    onCancelar = { finish() }
                )
            }
        }
    }
}

@Composable
fun PantallaRegistro(
    mensajeErrorExterno: String = "",
    onRegistrarse: (username: String, nombre: String, apellido: String,
                    edad: String, correo: String, contrasena: String) -> Unit = { _, _, _, _, _, _ -> },
    onCancelar: () -> Unit = {}
) {
    var nombre           by remember { mutableStateOf("") }
    var apellido         by remember { mutableStateOf("") }
    var username         by remember { mutableStateOf("") }
    var edadSeleccionada by remember { mutableStateOf("") }
    var correo           by remember { mutableStateOf("") }
    var contrasena       by remember { mutableStateOf("") }
    var confirmarContra  by remember { mutableStateOf("") }
    var mostrarContra    by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }
    var mensajeError     by remember { mutableStateOf("") }

    val isDark = LocalIsDarkTheme.current
    val logoRes = if (isDark) R.drawable.logo_app_oscuro else R.drawable.logo_app_claro

    LaunchedEffect(mensajeErrorExterno) {
        if (mensajeErrorExterno.isNotEmpty()) {
            mensajeError = mensajeErrorExterno
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(52.dp))

        Image(
            painter = painterResource(id = logoRes),
            contentDescription = "Logo Mood & Music",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .wrapContentHeight()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; mensajeError = "" },
                placeholder = { Text("Nombre", color = ColorSubtexto, fontSize = 14.sp) },
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
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it; mensajeError = "" },
                placeholder = { Text("Apellido", color = ColorSubtexto, fontSize = 14.sp) },
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
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; mensajeError = "" },
                placeholder = { Text("Username", color = ColorSubtexto, fontSize = 14.sp) },
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
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = edadSeleccionada,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        edadSeleccionada = it
                        mensajeError = ""
                    }
                },
                placeholder = { Text("Edad", color = ColorSubtexto, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor        = ColorTexto,
                    unfocusedTextColor      = ColorTexto,
                    focusedBorderColor      = ColorMorado,
                    unfocusedBorderColor    = ColorBorde,
                    focusedContainerColor   = ColorCampo,
                    unfocusedContainerColor = ColorCampo
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it; mensajeError = "" },
            placeholder = { Text("Correo", color = ColorSubtexto, fontSize = 14.sp) },
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it; mensajeError = "" },
            placeholder = { Text("Contraseña", color = ColorSubtexto, fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = if (mostrarContra) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { mostrarContra = !mostrarContra }) {
                    Icon(
                        imageVector = if (mostrarContra) Icons.Filled.Visibility
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmarContra,
            onValueChange = { confirmarContra = it; mensajeError = "" },
            placeholder = { Text("Confirmar contraseña", color = ColorSubtexto, fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = if (mostrarConfirmar) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { mostrarConfirmar = !mostrarConfirmar }) {
                    Icon(
                        imageVector = if (mostrarConfirmar) Icons.Filled.Visibility
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

        Spacer(modifier = Modifier.height(8.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color(0xFFE24B4A),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

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
                onClick = {
                    when {
                        nombre.isBlank() || apellido.isBlank() || username.isBlank() ||
                                edadSeleccionada.isBlank() || correo.isBlank() ||
                                contrasena.isBlank() || confirmarContra.isBlank() -> {
                            mensajeError = "Por favor completa todos los campos."
                        }
                        contrasena != confirmarContra -> {
                            mensajeError = "Las contraseñas no coinciden."
                        }
                        else -> {
                            onRegistrarse(
                                username, nombre, apellido,
                                edadSeleccionada, correo, contrasena
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = "Registrarme",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCancelar,
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
                text = "Cancelar",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaRegistroPreview() {
    MoodMusicTheme {
        PantallaRegistro()
    }
}
