package com.example.moodmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.ui.theme.MoodMusicTheme
import com.example.moodmusic.model.Usuario

// -------------------------------------------------------
// RegistroActivity
// -------------------------------------------------------
class RegistroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMusicTheme {
                PantallaRegistro(
                    onRegistrarse = { usuario ->
                        // Aquí va la navegación a HomeActivity
                        // val intent = Intent(this, HomeActivity::class.java)
                        // intent.putExtra("usuario", usuario)
                        // startActivity(intent)
                    },
                    onCancelar = {
                        finish() // Vuelve a InicioSesionActivity
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------
// Pantalla Registro
// Conceptos aplicados:
//   - Column / Row / Box  (PDF 1 - Layouts)
//   - mutableStateOf + remember  (PDF 2 - Estado y Recomposition)
//   - State Hoisting  (PDF 3)
//   - data class Usuario + Serializable  (PDF 4 - Intent)
// -------------------------------------------------------
@Composable
fun PantallaRegistro(
    onRegistrarse: (usuario: Usuario) -> Unit = {},
    onCancelar: () -> Unit = {}
) {
    // Estado de cada campo (PDF 2 - mutableStateOf + remember)
    var nombre           by remember { mutableStateOf("") }
    var apellido         by remember { mutableStateOf("") }
    var username         by remember { mutableStateOf("") }
    var edadSeleccionada by remember { mutableStateOf("") }
    var correo           by remember { mutableStateOf("") }
    var contrasena       by remember { mutableStateOf("") }
    var confirmarContra  by remember { mutableStateOf("") }
    var mostrarContra    by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }
    var expandirEdad     by remember { mutableStateOf(false) }
    var mensajeError     by remember { mutableStateOf("") }

    val opcionesEdad = (10..80).map { it.toString() }

    // Column con scroll para pantallas pequeñas
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(52.dp))

        // ---------- Logo ----------
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "♩", fontSize = 22.sp, color = ColorMorado)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Mood&Music",
                fontSize = 18.sp,
                fontFamily = FontFamily.Cursive,
                color = ColorAzul
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "♪", fontSize = 18.sp, color = ColorMorado)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- Título ----------
        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- Fila: Nombre + Apellido ----------
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
                    focusedBorderColor      = ColorMorado,
                    unfocusedBorderColor    = ColorBorde,
                    focusedContainerColor   = ColorCampo,
                    unfocusedContainerColor = ColorCampo
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Fila: Username + Edad (dropdown) ----------
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
                    focusedBorderColor      = ColorMorado,
                    unfocusedBorderColor    = ColorBorde,
                    focusedContainerColor   = ColorCampo,
                    unfocusedContainerColor = ColorCampo
                ),
                modifier = Modifier.weight(1f)
            )

            // Dropdown edad
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = edadSeleccionada,
                    onValueChange = { if (it.all { char -> char.isDigit() }) edadSeleccionada = it },
                    placeholder = { Text("Edad", color = ColorSubtexto, fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = ColorMorado,
                        unfocusedBorderColor    = ColorBorde,
                        focusedContainerColor   = ColorCampo,
                        unfocusedContainerColor = ColorCampo
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Correo ----------
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it; mensajeError = "" },
            placeholder = { Text("Correo", color = ColorSubtexto, fontSize = 14.sp) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = ColorMorado,
                unfocusedBorderColor    = ColorBorde,
                focusedContainerColor   = ColorCampo,
                unfocusedContainerColor = ColorCampo
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Contraseña ----------
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
                focusedBorderColor      = ColorMorado,
                unfocusedBorderColor    = ColorBorde,
                focusedContainerColor   = ColorCampo,
                unfocusedContainerColor = ColorCampo
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- Confirmar contraseña ----------
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
                focusedBorderColor      = ColorMorado,
                unfocusedBorderColor    = ColorBorde,
                focusedContainerColor   = ColorCampo,
                unfocusedContainerColor = ColorCampo
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ---------- Mensaje de error ----------
        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color(0xFFE24B4A),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ---------- Botón Registrarme ----------
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
                            // Crear objeto Usuario con data class (PDF 4)
                            val usuario = Usuario(
                                nombre     = nombre,
                                apellido   = apellido,
                                username   = username,
                                edad       = edadSeleccionada,
                                correo     = correo,
                                contrasena = contrasena
                            )
                            onRegistrarse(usuario)
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

        // ---------- Botón Cancelar ----------
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

// -------------------------------------------------------
// Preview
// -------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaRegistroPreview() {
    MoodMusicTheme {
        PantallaRegistro()
    }
}