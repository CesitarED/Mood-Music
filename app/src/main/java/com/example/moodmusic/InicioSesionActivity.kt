package com.example.moodmusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmusic.ui.theme.MoodMusicTheme


// InicioSesion Activity
// -------------------------------------------------------
class InicioSesionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMusicTheme {
                PantallaLogin(
                    onIniciarSesion = { nombre, contrasena ->
                        // Aquí va la navegación a la siguiente Activity
                        // Ejemplo:
                        // val intent = Intent(this, HomeActivity::class.java)
                        // startActivity(intent)
                    },
                    onRegistrarse = {
                        // Aquí va la navegación a la Activity de registro
                        // Ejemplo:
                        // val intent = Intent(this, RegistroActivity::class.java)
                        // startActivity(intent)
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------
// Pantalla Login
// -------------------------------------------------------
@Composable
fun PantallaLogin(
    onIniciarSesion: (nombre: String, contrasena: String) -> Unit = { _, _ -> },
    onRegistrarse: () -> Unit = {}
) {
    var nombre            by remember { mutableStateOf("") }
    var contrasena        by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var mensajeError      by remember { mutableStateOf("") }

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
            onValorChange = {
                nombre = it
                mensajeError = ""
            },
            placeholder = "Ingrese el nombre"
        )

        Spacer(modifier = Modifier.height(14.dp))

        CampoContrasena(
            valor = contrasena,
            onValorChange = {
                contrasena = it
                mensajeError = ""
            },
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
                if (nombre.isBlank() || contrasena.isBlank()) {
                    mensajeError = "Por favor completa todos los campos."
                } else {
                    onIniciarSesion(nombre, contrasena)
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

// -------------------------------------------------------
// Logo
// -------------------------------------------------------
@Composable
fun LogoOnda() {
    Box(
        modifier = Modifier.size(width = 200.dp, height = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

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

        }
    }
}

// -------------------------------------------------------
// Campo de texto reutilizable
// -------------------------------------------------------
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        placeholder = {
            Text(text = placeholder, color = ColorSubtexto, fontSize = 14.sp)
        },
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
}

// -------------------------------------------------------
// Campo contraseña con ojo
// -------------------------------------------------------
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
        placeholder = {
            Text("Ingrese la contraseña", color = ColorSubtexto, fontSize = 14.sp)
        },
        singleLine = true,
        visualTransformation = if (mostrar) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleMostrar) {
                Icon(
                    imageVector = if (mostrar) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff,
                    contentDescription = if (mostrar) "Ocultar contraseña"
                    else "Mostrar contraseña",
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
}

// -------------------------------------------------------
// Botón gradiente
// -------------------------------------------------------
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

// -------------------------------------------------------
// Botón secundario
// -------------------------------------------------------
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

// -------------------------------------------------------
// Preview
// -------------------------------------------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PantallaLoginPreview() {
    MoodMusicTheme {
        PantallaLogin()
    }
}