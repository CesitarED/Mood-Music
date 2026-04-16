package com.example.moodmusic.ui.perfil

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodmusic.R
import com.example.moodmusic.ui.main.*
import com.example.moodmusic.ui.theme.MoodMusicTheme
import com.example.moodmusic.viewmodel.UsuarioViewModel

class EditarPerfilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMusicTheme {
                PantallaEditarPerfil(onVolver = { finish() })
            }
        }
    }
}

@Composable
fun PantallaEditarPerfil(
    viewModel: UsuarioViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val usuario = viewModel.usuarioActual
    val context = LocalContext.current
    val listaAvatares = listOf(
        R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
        R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8
    )

    // Estados de los campos
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var passwordConfirmacion by remember { mutableStateOf("") }

    // Controlar si ya cargamos los datos iniciales
    var datosCargados by remember { mutableStateOf(false) }

    // Cargar datos iniciales cuando el usuario esté disponible
    LaunchedEffect(usuario) {
        if (usuario != null && !datosCargados) {
            nombre = usuario.nombre
            apellido = usuario.apellido
            username = usuario.username
            edad = usuario.edad
            correo = usuario.correo
            datosCargados = true
        }
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

        // Botón Volver
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
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    Brush.linearGradient(listOf(ColorAzul.copy(alpha = 0.2f), ColorMorado.copy(alpha = 0.2f))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val avatarRes = if (usuario != null && usuario.avatar in listaAvatares.indices) {
                listaAvatares[usuario.avatar]
            } else R.drawable.avatar1

            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${usuario?.nombre ?: ""} ${usuario?.apellido ?: ""}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Editar Información:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTexto,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            CajaTextoEdicion(nombre, "Nombre", Modifier.weight(1f)) { nombre = it }
            Spacer(modifier = Modifier.width(12.dp))
            CajaTextoEdicion(apellido, "Apellido", Modifier.weight(1f)) { apellido = it }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            CajaTextoEdicion(username, "Username", Modifier.weight(1f)) { username = it }
            Spacer(modifier = Modifier.width(12.dp))
            CajaTextoEdicion(edad, "Edad", Modifier.weight(1f), isDropdown = true) { edad = it }
        }

        Spacer(modifier = Modifier.height(12.dp))
        CajaTextoEdicion(correo, "Correo", Modifier.fillMaxWidth()) { correo = it }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = ColorBorde.copy(alpha = 0.5f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Confirmar cambios con tu contraseña:",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorMorado,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        CajaTextoEdicion(
            valor = passwordConfirmacion,
            label = "Contraseña actual",
            modifier = Modifier.fillMaxWidth(),
            isPassword = true
        ) { passwordConfirmacion = it }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Guardar
        Button(
            onClick = {
                if (passwordConfirmacion != usuario?.contrasena) {
                    Toast.makeText(context, "La contraseña es incorrecta", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.actualizarPerfil(nombre, apellido, username, edad, correo)
                    Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    onVolver()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(52.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(ColorAzul, ColorMorado)))
        ) {
            Text("Guardar", color = ColorTexto, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun CajaTextoEdicion(
    valor: String,
    label: String,
    modifier: Modifier = Modifier,
    isDropdown: Boolean = false,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(1.dp, ColorBorde, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (valor.isEmpty()) {
            Text(label, color = Color.LightGray, fontSize = 14.sp)
        }
        
        BasicTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = ColorTexto,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        )

        if (isDropdown) {
            Icon(
                painter = painterResource(id = android.R.drawable.arrow_down_float),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd).size(14.dp),
                tint = Color.LightGray
            )
        }
    }
}
